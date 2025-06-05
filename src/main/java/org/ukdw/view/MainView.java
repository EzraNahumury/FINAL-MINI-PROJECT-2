package org.ukdw.view;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.ukdw.RoomBookSystem;
import org.ukdw.Start;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainView implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    /**
     * Handle action related to input (in this case specifically only responds to
     * keyboard event CTRL-A).
     *
     * @param event Input event.
     */
    @FXML
    private void handleKeyInput(final InputEvent event) {
        if (event instanceof KeyEvent) {
            final KeyEvent keyEvent = (KeyEvent) event;
            if (keyEvent.isControlDown() && keyEvent.getCode() == KeyCode.A) {

            }
        }
    }

    @FXML
    private void doExit(final ActionEvent event) {
        Platform.exit();
    }

    @FXML
    private void doOnlineManual(final ActionEvent event) {
        try {
            Desktop.getDesktop().browse(new URL("https://www.ukdw.ac.id").toURI());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (URISyntaxException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void doAbout(final ActionEvent event) {

    }

    @FXML
    private void handleGedungAction(final ActionEvent event) throws IOException {
        Start.setRoot("gedung-view", "", false);
    }

    @FXML
    private void handleUserAction(final ActionEvent event) throws IOException {
        Start.setRoot("user-view", "", false);
    }

    @FXML
    private void handleBookingAction(final ActionEvent event) throws IOException {
        Start.setRoot("pemesanan-view", "", false);
    }

    @FXML
    private void handleRuanganAction(final ActionEvent event) throws IOException {
        Start.setRoot("ruangan-view", "", false);
    }

    @FXML
    private void doLogout(final ActionEvent event) throws IOException {
        SessionManager.getInstance().logout();
        if (!SessionManager.getInstance().isLoggedIn()) {
            Start.setRoot("login-view", "Login", false);
        }
    }

    public void handleLaporanAction(ActionEvent actionEvent) throws IOException {
        Start.setRoot("laporan-pemesanan-view", "", false);
    }
}
