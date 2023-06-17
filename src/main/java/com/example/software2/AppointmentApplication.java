package com.example.software2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * The entry point for the Appointment scheduling application
 * @author Nicholas Young
 */
public class AppointmentApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Main.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 607, 450);
        stage.setTitle("Appointment Scheduling Utility");
        stage.setScene(scene);
        stage.show();

        Main main = fxmlLoader.getController();
        main.setStage(stage);
    }
}
