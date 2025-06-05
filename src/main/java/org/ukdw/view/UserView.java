package org.ukdw.view;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.ukdw.data.User;
import org.ukdw.managers.UserManager;
import org.ukdw.utils.DBConnectionManager;

import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class UserView implements Initializable {

    @FXML public TextField usernameTxtFld;
    @FXML public PasswordField passwordTxtFld;
    @FXML public TextField emailTxtFld;
    @FXML public Button btnTambah;
    @FXML public Button btnHapus;
    @FXML public Button btnUbah;
    @FXML public TextField searchBox;
    @FXML public TableView<User> table;
    @FXML public TableColumn<User, String> tblColUsername;
    @FXML public TableColumn<User, String> tblColEmail;

    private ObservableList<User> userObservableList;
    private FilteredList<User> userFilteredList;

    private UserManager userManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Connection connection = DBConnectionManager.getConnection();
        userManager = new UserManager(connection);
        userObservableList = FXCollections.observableArrayList(userManager.getAllUsers());
        userFilteredList = new FilteredList<>(userObservableList, p -> true);

        setupTable();
        setupListeners();
        table.setItems(userFilteredList);
    }

    private void setupTable() {
        tblColUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        tblColEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
    }

    private void setupListeners() {
        table.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends User> obs, User oldUser, User newUser) -> {
                    if (newUser != null) {
                        usernameTxtFld.setText(newUser.getUsername());
                        emailTxtFld.setText(newUser.getEmail());
                        passwordTxtFld.setText(newUser.getPassword());
                    }
                });

        searchBox.textProperty().addListener((obs, oldText, newText) -> {
            userFilteredList.setPredicate(createPredicate(newText));
        });
    }

    private Predicate<User> createPredicate(String searchText) {
        return user -> {
            if (searchText == null || searchText.isEmpty()) return true;
            return user.getUsername().toLowerCase().contains(searchText.toLowerCase()) ||
                    user.getEmail().toLowerCase().contains(searchText.toLowerCase());
        };
    }

    public void handleAddAction(ActionEvent actionEvent) {
        String email = emailTxtFld.getText();
        String username = usernameTxtFld.getText();
        String password = passwordTxtFld.getText();

        if (!userManager.isValidEmail(email)) {
            showAlert("Email format tidak valid.");
            return;
        }

        if (!userManager.isStrongPassword(password)) {
            showAlert("Password harus terdiri dari huruf, angka, dan simbol, minimal 8 karakter.");
            return;
        }

        if (userManager.isUsernameOrEmailExist(username, email)) {
            showAlert("Username atau Email sudah digunakan.");
            return;
        }

        User newUser = new User(email, username, password);
        if (userManager.registerUser(email, username, password)) {
            userObservableList.add(newUser);
            showAlert("User berhasil ditambahkan.");
            clearForm();
        } else {
            showAlert("Gagal menambahkan user.");
        }
    }


    public void handleEditAction(ActionEvent actionEvent) {
        User selectedUser = table.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("Pilih user yang akan diubah.");
            return;
        }

        String email = emailTxtFld.getText();
        String username = usernameTxtFld.getText();
        String password = passwordTxtFld.getText();

        if (!email.equalsIgnoreCase(selectedUser.getEmail())) {
            showAlert("Email tidak boleh diubah.");
            return;
        }

        if (!userManager.isValidEmail(email)) {
            showAlert("Format email tidak valid.");
            return;
        }

        if (!userManager.isStrongPassword(password)) {
            showAlert("Password harus terdiri dari huruf, angka, dan simbol, minimal 8 karakter.");
            return;
        }

        User updatedUser = new User(email, username, password);
        if (isUserChanged(selectedUser, updatedUser)) {
            if (userManager.updateProfile(email, username, password)) {
                updateUser(selectedUser, updatedUser);
                showAlert("User berhasil diperbarui.");
                clearForm();
            } else {
                showAlert("Gagal memperbarui user.");
            }
        } else {
            showAlert("Tidak ada perubahan.");
        }
    }


    public void handleDeleteAction(ActionEvent actionEvent) {
        User selectedUser = table.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            if (userManager.deleteUser(selectedUser.getEmail())) {
                userObservableList.remove(selectedUser);
                showAlert("User deleted successfully");
                clearForm();
            } else {
                showAlert("Failed to delete user.");
            }
        }
    }

    public void handleClearSearchText(ActionEvent actionEvent) {
        searchBox.clear();
    }

    private boolean isUserChanged(User oldUser, User newUser) {
        return !oldUser.getUsername().equalsIgnoreCase(newUser.getUsername()) ||
                !oldUser.getPassword().equals(newUser.getPassword());
    }

    private void clearForm() {
        emailTxtFld.clear();
        usernameTxtFld.clear();
        passwordTxtFld.clear();
        table.getSelectionModel().clearSelection();
        table.setItems(userFilteredList);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.show();
    }

    private void updateUser(User oldUser, User newUser) {
        int index = userObservableList.indexOf(oldUser);
        if (index >= 0) {
            userObservableList.set(index, newUser);
        }
    }
}