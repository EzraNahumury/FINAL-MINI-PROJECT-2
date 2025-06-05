package org.ukdw.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Hyperlink;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.ukdw.managers.UserManager;
import org.ukdw.utils.DBConnectionManager;

import java.io.IOException;

public class LoginView {
    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Button btnLogin;

    @FXML
    private Hyperlink lblForgot;

    private UserManager userManager;

    public LoginView() {
        userManager = new UserManager(DBConnectionManager.getConnection());
    }

    public void onKeyPressEvent(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            login();
        }
    }

    public void handleLoginAction(ActionEvent actionEvent) {
        login();
    }

    private void login() {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Login Error", "Please enter both username and password.");
            return;
        }

        boolean isAuthenticated = userManager.authenticateUser(username, password);
        SessionManager.getInstance().login(username, password);
        if (isAuthenticated) {
            try {
                // Load main view
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/ukdw/main-view.fxml"));
                Parent mainViewRoot = loader.load();

                // Get the current stage from any control
                Stage stage = (Stage) txtUsername.getScene().getWindow();

                // Set main view
                stage.setTitle("UKDW Room Booking System - Main");
                stage.setScene(new Scene(mainViewRoot));
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to load main view: " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}