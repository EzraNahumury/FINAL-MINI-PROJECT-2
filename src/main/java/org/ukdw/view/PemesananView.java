package org.ukdw.view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import org.ukdw.data.Pemesanan;
import org.ukdw.managers.PemesananManager;
import org.ukdw.utils.DBConnectionManager;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

public class PemesananView implements Initializable {
    private FilteredList<Pemesanan> pemesananFilteredList;
    public ChoiceBox<String> RuanganSelect;
    public TextField EmailPemesan;
    public DatePicker TglCheckIn;
    public DatePicker TglCheckOut;
    public TextField JamCheckIn;
    public TextField JamCheckOut;
    public TextField searchBox;
    public TableView<Pemesanan> table;
    public TableColumn<Pemesanan, Integer> colId;
    public TableColumn<Pemesanan, String> colIdStudent;
    public TableColumn<Pemesanan, String> colIdKelas;
    public TableColumn<Pemesanan, String> colIdKelas1;

    private PemesananManager pemesananManager;
    Pemesanan selectedPemesanan;

    private Connection conn = DBConnectionManager.getConnection();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        pemesananManager = new PemesananManager(DBConnectionManager.getConnection());

        // Ambil data dari DB
        ObservableList<Pemesanan> data = FXCollections.observableArrayList(pemesananManager.getAllPemesanan());

        // Buat FilteredList untuk pencarian
        pemesananFilteredList = new FilteredList<>(data, p -> true);

        // Binding kolom tabel ke field Pemesanan
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colIdStudent.setCellValueFactory(new PropertyValueFactory<>("userEmail"));
        colIdKelas.setCellValueFactory(new PropertyValueFactory<>("idRuangan"));
        colIdKelas1.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));

        // Isi tabel
        table.setItems(pemesananFilteredList);

        // Isi ChoiceBox dummy ruangan
        List<String> ruanganList = pemesananManager.getAllRuangan();
        RuanganSelect.setItems(FXCollections.observableArrayList(ruanganList));

        // Tambahkan filter pencarian
        searchBox.textProperty().addListener((observable, oldValue, newValue) -> {
            pemesananFilteredList.setPredicate(pemesanan -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                return pemesanan.getUserEmail().toLowerCase().contains(lowerCaseFilter) ||
                        String.valueOf(pemesanan.getIdRuangan()).contains(lowerCaseFilter) ||
                        pemesanan.getCheckInDate().toLowerCase().contains(lowerCaseFilter) ||
                        pemesanan.getCheckOutDate().toLowerCase().contains(lowerCaseFilter);
            });
        });
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedPemesanan = newSelection;

                // (Opsional) Tampilkan di form jika kamu ingin bisa edit
                EmailPemesan.setText(newSelection.getUserEmail());
                RuanganSelect.setValue(newSelection.getIdRuangan() + ""); // bisa disesuaikan formatnya
                TglCheckIn.setValue(LocalDate.parse(newSelection.getCheckInDate()));
                TglCheckOut.setValue(LocalDate.parse(newSelection.getCheckOutDate()));
                JamCheckIn.setText(newSelection.getCheckInTime());
                JamCheckOut.setText(newSelection.getCheckOutTime());
            }
        });

    }

    private void reloadTableData() {
        ObservableList<Pemesanan> newData = FXCollections.observableArrayList(pemesananManager.getAllPemesanan());
        pemesananFilteredList = new FilteredList<>(newData, p -> true);
        table.setItems(pemesananFilteredList);

        searchBox.textProperty().addListener((observable, oldValue, newValue) -> {
            pemesananFilteredList.setPredicate(pemesanan -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                return pemesanan.getUserEmail().toLowerCase().contains(lowerCaseFilter) ||
                        String.valueOf(pemesanan.getIdRuangan()).contains(lowerCaseFilter) ||
                        pemesanan.getCheckInDate().toLowerCase().contains(lowerCaseFilter) ||
                        pemesanan.getCheckOutDate().toLowerCase().contains(lowerCaseFilter);
            });
        });
    }



    public void handleClearSearchText(ActionEvent actionEvent) {

    }

    public void handleAddAction(ActionEvent actionEvent) {
        String[] parts = RuanganSelect.getValue().split(" - ");
        int idRuangan = Integer.parseInt(parts[0]);
        String userEmail = EmailPemesan.getText();
        String checkInDate = TglCheckIn.getValue().toString();
        String checkOutDate = TglCheckOut.getValue().toString();
        String checkInTime = JamCheckIn.getText();
        String checkOutTime = JamCheckOut.getText();


        if (!pemesananManager.isEmailValid(userEmail)) {
            showAlert("Gagal","Format email tidak valid.");
            return;
        }
        if (!pemesananManager.isTimeValid(checkInTime) || !pemesananManager.isTimeValid(checkOutTime)) {
            showAlert("Gagal","Format jam harus HH:MM.");
            return;
        }
        if (!pemesananManager.isCheckOutAfterCheckIn(checkInTime, checkOutTime)) {
            showAlert("Gagal","Check-out harus lebih lambat dari check-in.");
            return;
        }
        if (!LocalDate.parse(checkOutDate).isAfter(LocalDate.parse(checkInDate))) {
            showAlert("Gagal","Check-out harus lebih lambat dari check-in.");
            return;
        }
        if (!pemesananManager.isRoomAvailable(checkInDate, checkInTime, getBookedSlots())) {
            showAlert("Gagal","Ruangan sudah dipesan pada tanggal dan jam tersebut.");
            return;
        }
        boolean success = pemesananManager.addPemesanan(userEmail, idRuangan, checkInDate, checkOutDate, checkInTime, checkOutTime);
        if (success) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Berhasil");
            alert.setHeaderText(null);
            alert.setContentText("Pemesanan berhasil ditambahkan!");
            alert.showAndWait();

            reloadTableData(); // <-- penting: update isi tabel

            // Opsional: reset form
            EmailPemesan.clear();
            RuanganSelect.setValue(null);
            TglCheckIn.setValue(null);
            TglCheckOut.setValue(null);
            JamCheckIn.clear();
            JamCheckOut.clear();

        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Gagal");
            alert.setHeaderText(null);
            alert.setContentText("Terjadi kesalahan saat menambahkan pemesanan.");
            alert.showAndWait();
        }
    }

    private Set<String> getBookedSlots() {
        Set<String> booked = new HashSet<>();
        List<Pemesanan> semuaPemesanan = pemesananManager.getAllPemesanan(); // ambil dari database/service

        for (Pemesanan p : semuaPemesanan) {
            String key = p.getCheckInDate() + "_" + p.getCheckInTime(); // Pastikan format "YYYY-MM-DD_HH:MM"
            booked.add(key);
        }
        return booked;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public void handleBatalAction(ActionEvent actionEvent) {
        selectedPemesanan = table.getSelectionModel().getSelectedItem();

        if (selectedPemesanan != null) {
            int id = selectedPemesanan.getId();
            boolean success = pemesananManager.deletePemesanan(id);

            if (success) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Pemesanan berhasil dihapus.");
                alert.showAndWait();
                bersihkan();
                reloadTableData();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Gagal menghapus pemesanan.");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Pilih data terlebih dahulu!");
            alert.showAndWait();
        }
    }


    private ObservableList<Pemesanan> getObservableList() {
        return (ObservableList<Pemesanan>) pemesananFilteredList.getSource();
    }

    private void bersihkan() {
        EmailPemesan.clear();
        TglCheckIn.setValue(null);
        TglCheckOut.setValue(null);
        JamCheckIn.clear();
        JamCheckOut.clear();
        RuanganSelect.getSelectionModel().clearSelection();
        searchBox.clear();
        table.getSelectionModel().clearSelection();
    }


    public void handleEditAction(ActionEvent actionEvent) {
        selectedPemesanan = table.getSelectionModel().getSelectedItem();

        if (selectedPemesanan != null) {
            try {
                String[] parts = RuanganSelect.getValue().split(" - ");
                int idRuangan = Integer.parseInt(parts[0]);
                String userEmail = EmailPemesan.getText();
                String checkInDate = TglCheckIn.getValue().toString();
                String checkOutDate = TglCheckOut.getValue().toString();
                String checkInTime = JamCheckIn.getText();
                String checkOutTime = JamCheckOut.getText();

                boolean success = pemesananManager.editPemesanan(
                        selectedPemesanan.getId(),
                        userEmail, idRuangan,
                        checkInDate, checkOutDate,
                        checkInTime, checkOutTime
                );

                if (success) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Sukses");
                    alert.setHeaderText(null);
                    alert.setContentText("Pemesanan berhasil diperbarui.");
                    alert.showAndWait();

                    reloadTableData();
                    bersihkan();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Gagal memperbarui pemesanan.");
                    alert.showAndWait();
                }
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Data tidak valid atau belum dipilih.");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Silakan pilih data yang ingin diedit.");
            alert.showAndWait();
        }
    }

}