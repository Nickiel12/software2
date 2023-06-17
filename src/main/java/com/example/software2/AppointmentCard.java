package com.example.software2;

import com.example.software2.models.AppointmentInstance;
import com.example.software2.models.ContactInstance;
import com.example.software2.models.CustomerInstance;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

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
    public void setAppointment(AppointmentInstance appointment, AppState state) {
        String contactName = null;
        for (ContactInstance contact: state.getContacts()) {
            if (Objects.equals(contact.id(), appointment.getContactId())) {
                contactName = contact.contactName();
            }
        }
        String customerName = null;
        for (CustomerInstance customer: state.getCustomers()) {
            if (Objects.equals(customer.getId(), appointment.getCustomerId())) {
                customerName = customer.getCustomerName();
            }
        }

        this.idLabel.setText(appointment.getId().toString());
        this.dateLabel.setText(appointment.getStartTime().format(DateTimeFormatter.ofPattern("MM/dd")));
        String details = "Title: " + appointment.getTitle() + "\n" +
                "Location: " + appointment.getLocation() + "\n" +
                "Contact: " + contactName + "\n" +
                "Customer: " + customerName + "\n" +
                "Type: " + appointment.getType() + "\n" +
                "Start Time: " + appointment.getStartTime().withZoneSameInstant(state.getCurrentZone()).format(DateTimeFormatter.ofPattern("HH:mm MM/dd/yy")) + "\n" +
                "End Time: " + appointment.getEndTime().withZoneSameInstant(state.getCurrentZone()).format(DateTimeFormatter.ofPattern("HH:mm MM/dd/yy")) + "\n" +
                appointment.getDescription() + "\n";

        this.detailsLabel.setText(details);
    }
}
