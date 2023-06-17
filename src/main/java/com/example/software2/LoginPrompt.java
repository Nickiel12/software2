package com.example.software2;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ResourceBundle;

/**
 * The controller for the LoginPrompt fxml window
 * Presents a UI for entering and validating user credentials before
 * using the app
 * @author Nicholas Young
 */
public class LoginPrompt {

    private Stage currentStage;

    /**
     * Update the LoginPrompt's stage, so it can close it by itself
     * @param stage the LoginPrompt's stage
     */
    public void setCurrentStage(Stage stage) {
        currentStage = stage;
    }
    @FXML
    private Text LabelUserName;
    @FXML
    private Text LabelPassword;
    @FXML
    private TextField FieldUserName;
    @FXML
    private TextField FieldPassword;
    @FXML
    private Button LoginButton;
    @FXML
    private Text LabelZone;

    private AppState state;

    public void localize() {
        ResourceBundle bundle = ResourceBundle.getBundle("LoginPrompt", state.getCurrentLocale());

        currentStage.setTitle(bundle.getString("windowTitle"));

        LabelUserName.setText(bundle.getString("usernameLabel"));
        FieldUserName.setPromptText(bundle.getString("usernamePrompt"));
        LabelPassword.setText(bundle.getString("passwordLabel"));
        FieldPassword.setPromptText(bundle.getString("passwordPrompt"));

        LoginButton.setText(bundle.getString("loginText"));
        LabelZone.setText(bundle.getString("currentRegion") + " " + state.getCurrentZone().getDisplayName(TextStyle.FULL, state.getCurrentLocale()));
    }

    /**
     * Set the application state
     * @param newState the application's state
     */
    public void setState(AppState newState) {
        state = newState;
        localize();
    }

    /**
     * Handles the press of the Login button and validates the entered credentials
     */
    public void handleLogin() {
        ResourceBundle bundle = ResourceBundle.getBundle("LoginPrompt", state.getCurrentLocale());

        String userName = FieldUserName.getText();
        String password = FieldPassword.getText();

        Integer userId = state.getDatabaseConnection().validateUser(userName, password);

        Path login_activity = Paths.get(".\\login_activity.txt");
        if (userId == -1){
            Alert alert = new Alert(Alert.AlertType.WARNING, bundle.getString("loginFailAlert"), ButtonType.OK);
            alert.showAndWait();

            try {
                Files.writeString(login_activity, "User '" + userName + "' tried to log in at " +
                                ZonedDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss a MM/dd/yyyy")),
                        StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (IOException e) {
                System.out.println("There was an error saving login_activity.txt\n" + e);
            }
        } else {
            // successful login
            state.setLogInUser(userName, userId);
            try {
                Files.writeString(login_activity, "User '" + userName + "' successfully logged in at " +
                        ZonedDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss a MM/dd/yyyy")),
                        StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (IOException e) {
                System.out.println("There was an error saving login_activity.txt\n" + e);
            }
        }
        closeModal();
    }

    /**
     * A Convenience function to close the window
     */
    private void closeModal() {
        this.currentStage.close();
    }

}
