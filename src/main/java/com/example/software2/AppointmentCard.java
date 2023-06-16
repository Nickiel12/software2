package com.example.software2;

import com.example.software2.models.AppointmentInstance;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * The controller for the card object shown on the Main screen
 * It takes an AppointmentInstance and updates the shown values
 * @author Nicholas Young
 */
public class AppointmentCard {
    @FXML
    private Label idLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Label detailsLabel;

    /**
     * Set the appointment instance that this card represents
     * @param appointment the appointment instance to pull information from
     */
    public void setAppointment(AppointmentInstance appointment, ZoneId appTimeZone) {
        this.idLabel.setText(appointment.getId().toString());
        this.dateLabel.setText(appointment.getStartTime().format(DateTimeFormatter.ofPattern("MM/dd")));
        StringBuilder details = new StringBuilder();
        details.append("Title: ").append(appointment.getTitle()).append("\n");
        details.append("Location: ").append(appointment.getLocation()).append("\n");
        details.append("Contact: ").append("TODO").append("\n");
        details.append("Customer: ").append("TODO").append("\n");
        details.append("Type: ").append(appointment.getType()).append("\n");
        details.append("Start Time: ").append(appointment.getStartTime().withZoneSameInstant(appTimeZone).format(DateTimeFormatter.ofPattern("HH:mm MM/dd/yy"))).append("\n");
        details.append("End Time: ").append(appointment.getEndTime().withZoneSameInstant(appTimeZone).format(DateTimeFormatter.ofPattern("HH:mm MM/dd/yy"))).append("\n");
        details.append(appointment.getDescription()).append("\n");

        this.detailsLabel.setText(details.toString());
    }
}
