package org.ukdw.view;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.ukdw.data.Gedung;
import org.ukdw.managers.GedungManager;
import org.ukdw.utils.DBConnectionManager;

import java.net.URL;
import java.sql.Connection;
import java.util.List;
import java.util.ResourceBundle;

public class GedungView implements Initializable {

    @FXML private TextField idTextField;
    @FXML private TextField titleTextField;
    @FXML private TextArea descriptionTextArea;
    @FXML private TableView<Gedung> table;
    @FXML private TableColumn<Gedung, Integer> tblColId;
    @FXML private TableColumn<Gedung, String> tblColTitle;
    @FXML private TableColumn<Gedung, String> tblColDescription;
    @FXML private TextField searchBox;

    private GedungManager gedungManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DBConnectionManager.createTables();

        Connection connection = DBConnectionManager.getConnection();
        if (connection == null) {
            System.out.println("Failed to connect to the database.");
            return;
        }
        gedungManager = new GedungManager(connection);

        tblColId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        tblColTitle.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNama()));
        tblColDescription.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAlamat()));
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        loadGedungs();

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                idTextField.setText(String.valueOf(newVal.getId()));
                titleTextField.setText(newVal.getNama());
                descriptionTextArea.setText(newVal.getAlamat());
            }
        });

        searchBox.textProperty().addListener((obs, oldText, newText) -> {
            if (newText == null || newText.trim().isEmpty()) {
                loadGedungs();
            } else {
                List<Gedung> filtered = gedungManager.searchGedungByKeyword(newText.trim());
                table.getItems().setAll(filtered);
            }
        });
    }

    public void handleAddAction(ActionEvent actionEvent) {
        String nama = titleTextField.getText().trim();
        String alamat = descriptionTextArea.getText().trim();

        if (nama.isEmpty() || alamat.isEmpty()) {
            showAlert("Nama dan alamat tidak boleh kosong.");
            return;
        }

        // ✅ Validasi nama gedung tidak boleh duplikat
        if (gedungManager.isNamaGedungExist(nama)) {
            showAlert("Nama gedung sudah terdaftar.");
            return;
        }

        if (gedungManager.addGedung(nama, alamat)) {
            loadGedungs();
            clearFields();
        } else {
            showAlert("Gagal menambahkan gedung.");
        }
    }

    public void handleDeleteAction(ActionEvent actionEvent) {
        if (idTextField.getText().isEmpty()) {
            showAlert("Silakan pilih gedung yang akan dihapus.");
            return;
        }
        int id = Integer.parseInt(idTextField.getText());
        if (gedungManager.deleteGedung(id)) {
            loadGedungs();
            clearFields();
        } else {
            showAlert("Gagal menghapus gedung.");
        }
    }

    public void handleEditAction(ActionEvent actionEvent) {
        if (idTextField.getText().isEmpty()) {
            showAlert("Silakan pilih gedung yang akan diubah.");
            return;
        }

        String newNama = idTextField.getText().trim();
        String newAlamat = descriptionTextArea.getText().trim();

        if (newNama.isEmpty() || newAlamat.isEmpty()) {
            showAlert("Nama dan alamat tidak boleh kosong.");
            return;
        }

        int id = Integer.parseInt(idTextField.getText());

        // Cek jika nama gedung diubah ke nama yang sudah ada dan beda ID
        Gedung existing = gedungManager.findByNama(newNama);
        if (existing != null && existing.getId() != id) {
            showAlert("Nama gedung sudah digunakan oleh gedung lain.");
            return;
        }

        if (gedungManager.editGedung(id, newNama, newAlamat)) {
            loadGedungs();
            clearFields();
        } else {
            showAlert("Gagal mengubah gedung.");
        }
    }

    public void handleClearSearchText(ActionEvent actionEvent) {
        searchBox.clear();
        clearFields();
        loadGedungs();
    }

    private void loadGedungs() {
        List<Gedung> gedungs = gedungManager.getAllGedung();
        table.getItems().setAll(gedungs);
    }

    private void clearFields() {
        idTextField.clear();
        titleTextField.clear();
        descriptionTextArea.clear();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
        alert.showAndWait();
    }
}