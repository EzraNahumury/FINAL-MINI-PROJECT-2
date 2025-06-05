package org.ukdw.view;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.ukdw.data.Gedung;
import org.ukdw.data.Pemesanan;
import org.ukdw.data.Ruangan;
import org.ukdw.managers.GedungManager;
import org.ukdw.managers.PemesananManager;
import org.ukdw.managers.RuanganManager;
import org.ukdw.utils.DBConnectionManager;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class LaporanPemesananView implements Initializable {
    @FXML
    private ComboBox<Gedung> gedungComboBox;
    @FXML
    private DatePicker tanggalAwalPicker;
    @FXML
    private DatePicker tanggalAkhirPicker;
    @FXML
    private TableView<RoomUsageData> laporanTable;
    @FXML
    private TableColumn<RoomUsageData, String> ruanganColumn;
    @FXML
    private TableColumn<RoomUsageData, Integer> jumlahColumn;
    @FXML
    private Label totalLabel;

    private Connection connection;
    private GedungManager gedungManager;
    private RuanganManager ruanganManager;
    private PemesananManager pemesananManager;
    private ObservableList<RoomUsageData> roomUsageDataList = FXCollections.observableArrayList();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Class to hold room usage data for the table
    public static class RoomUsageData {
        private final SimpleStringProperty ruanganName;
        private final SimpleIntegerProperty jumlahPemesanan;

        public RoomUsageData(String ruanganName, int jumlahPemesanan) {
            this.ruanganName = new SimpleStringProperty(ruanganName);
            this.jumlahPemesanan = new SimpleIntegerProperty(jumlahPemesanan);
        }

        public String getRuanganName() {
            return ruanganName.get();
        }

        public SimpleStringProperty ruanganNameProperty() {
            return ruanganName;
        }

        public int getJumlahPemesanan() {
            return jumlahPemesanan.get();
        }

        public SimpleIntegerProperty jumlahPemesananProperty() {

            return jumlahPemesanan;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize database connection and managers
        connection = DBConnectionManager.getConnection();
        gedungManager = new GedungManager(connection);
        ruanganManager = new RuanganManager(connection);
        pemesananManager = new PemesananManager(connection);

        // Set up date pickers with default values (current month)
        LocalDate now = LocalDate.now();
        LocalDate firstDayOfMonth = now.withDayOfMonth(1);
        LocalDate lastDayOfMonth = now.withDayOfMonth(now.lengthOfMonth());
        tanggalAwalPicker.setValue(firstDayOfMonth);
        tanggalAkhirPicker.setValue(lastDayOfMonth);

        // Load buildings in ComboBox
        loadGedungComboBox();

        // Set up the table columns
        configureTableColumns();

    }

    private void loadGedungComboBox() {
        try {
            List<Gedung> gedungList = getAllGedung();
            ObservableList<Gedung> gedungObservableList = FXCollections.observableArrayList(gedungList);

            // Add an "All Buildings" option at the top
            Gedung allGedung = new Gedung(-1, "Semua Gedung", "");
            gedungObservableList.add(0, allGedung);

            gedungComboBox.setItems(gedungObservableList);
            gedungComboBox.getSelectionModel().selectFirst();

            // Set custom cell factory to display building names in dropdown
            gedungComboBox.setCellFactory(param -> new ListCell<Gedung>() {
                @Override
                protected void updateItem(Gedung gedung, boolean empty) {
                    super.updateItem(gedung, empty);
                    if (empty || gedung == null) {
                        setText(null);
                    } else {
                        setText(gedung.getNama());
                    }
                }
            });

            // Set custom button cell to display selected building name
            gedungComboBox.setButtonCell(new ListCell<Gedung>() {
                @Override
                protected void updateItem(Gedung gedung, boolean empty) {
                    super.updateItem(gedung, empty);
                    if (empty || gedung == null) {
                        setText(null);
                    } else {
                        setText(gedung.getNama());
                    }
                }
            });
        } catch (Exception e) {
            showAlert("Error loading buildings: " + e.getMessage());
        }
    }

    private List<Gedung> getAllGedung() throws SQLException {
        List<Gedung> gedungList = new ArrayList<>();
        String query = "SELECT * FROM gedung";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nama = resultSet.getString("nama");
                String alamat = resultSet.getString("alamat");
                gedungList.add(new Gedung(id, nama, alamat));
            }
        }
        return gedungList;
    }

    private void configureTableColumns() {
        ruanganColumn.setCellValueFactory(cellData -> cellData.getValue().ruanganNameProperty());
        jumlahColumn.setCellValueFactory(cellData -> cellData.getValue().jumlahPemesananProperty().asObject());

        laporanTable.setItems(roomUsageDataList);
    }

    @FXML
    public void handleTampilkanLaporan(ActionEvent actionEvent) {
        if (tanggalAwalPicker.getValue() == null || tanggalAkhirPicker.getValue() == null) {
            showAlert("Silakan pilih rentang tanggal terlebih dahulu.");
            return;
        }

        if (tanggalAwalPicker.getValue().isAfter(tanggalAkhirPicker.getValue())) {
            showAlert("Tanggal awal tidak boleh lebih besar dari tanggal akhir.");
            return;
        }

        try {
            // Clear previous data
            roomUsageDataList.clear();

            // Get selected building (if any)
            Gedung selectedGedung = gedungComboBox.getValue();

            // Get date range
            String startDate = tanggalAwalPicker.getValue().format(formatter);
            String endDate = tanggalAkhirPicker.getValue().format(formatter);

            // Generate report
            generateReport(selectedGedung, startDate, endDate);

        } catch (Exception e) {
            showAlert("Error generating report: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void generateReport(Gedung selectedGedung, String startDate, String endDate) throws SQLException {
        Map<Integer, String> roomNames = getAllRoomNames();
        Map<Integer, Integer> roomBookingCounts = new HashMap<>();

        // SQL query based on if a specific building is selected
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT r.id, r.nama, COUNT(p.id) AS booking_count ")
                .append("FROM ruangan r ")
                .append("LEFT JOIN pemesanan p ON r.id = p.ruangan_id ")
                .append("AND ((p.checkindate BETWEEN ? AND ?) OR (p.checkoutdate BETWEEN ? AND ?)) ");

        // Add building filter if a specific building is selected
        if (selectedGedung != null && selectedGedung.getId() != -1) {
            queryBuilder.append("WHERE r.gedung_id = ? ");
        }

        queryBuilder.append("GROUP BY r.id, r.nama ");
        queryBuilder.append("ORDER BY booking_count DESC, r.nama ASC");

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryBuilder.toString())) {
            preparedStatement.setString(1, startDate);
            preparedStatement.setString(2, endDate);
            preparedStatement.setString(3, startDate);
            preparedStatement.setString(4, endDate);

            // Set building ID parameter if specific building is selected
            if (selectedGedung != null && selectedGedung.getId() != -1) {
                preparedStatement.setInt(5, selectedGedung.getId());
            }

            ResultSet resultSet = preparedStatement.executeQuery();
            int totalBookings = 0;

            // Process results and add to the observable list
            while (resultSet.next()) {
                String roomName = resultSet.getString("nama");
                int bookingCount = resultSet.getInt("booking_count");

                // Add to room usage data list
                roomUsageDataList.add(new RoomUsageData(roomName, bookingCount));
                totalBookings += bookingCount;
            }

            // Update the total label
            totalLabel.setText(String.valueOf(totalBookings));
        }
    }

    private Map<Integer, String> getAllRoomNames() throws SQLException {
        Map<Integer, String> roomNames = new HashMap<>();
        String query = "SELECT id, nama FROM ruangan";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("nama");
                roomNames.put(id, name);
            }
        }
        return roomNames;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}