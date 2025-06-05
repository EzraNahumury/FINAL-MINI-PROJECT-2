package org.ukdw;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.ukdw.view.SessionManager;

import java.io.IOException;

public class Start extends Application {
    private static Stage primaryStage;


    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        primaryStage.setTitle("Login");
        if (SessionManager.getInstance().isLoggedIn()) {
            primaryStage.setScene(new Scene(loadFXML("main-view")));
        } else {
            primaryStage.setScene(new Scene(loadFXML("login-view")));
        }
        primaryStage.show();
    }



    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RoomBookSystem.class
                .getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void setRoot(String fxml, String title, boolean isResizeable)
            throws IOException {
        primaryStage.getScene().setRoot(loadFXML(fxml));
        primaryStage.sizeToScene();
        primaryStage.setResizable(isResizeable);
        if (title != null) {
            primaryStage.setTitle(title);
        }
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}