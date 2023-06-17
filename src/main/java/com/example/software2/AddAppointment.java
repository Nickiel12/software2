package com.example.software2;

import com.example.software2.models.AppointmentInstance;
import com.example.software2.models.ContactInstance;
import com.example.software2.models.CustomerInstance;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

/**
 * The controller for the form to create and edit Appointments.
 * It is created as a Create form, and is changed to an Edit form when
 * an AppointmentInstance is applied with setAppointment
 * @author Nicholas Young
 */
public class AddAppointment {
    private Stage currentStage;

    /**
     * Set the stage so the window can close itself
     * @param stage the window's stage
     */
    public void setStage(Stage stage) {
        currentStage = stage;
    }

    @FXML
    private Label idLabel;
    @FXML
    private TextField idTextField;
    @FXML
    private Label titleLabel;
    @FXML
    private TextField titleTextField;
    @FXML
    private Label locationLabel;
    @FXML
    private TextField locationTextField;
    @FXML
    private Label typeLabel;
    @FXML
    private TextField typeTextField;
    @FXML
    private Label custLabel;
    @FXML
    private ComboBox<CustomerInstance> custComboBox;
    @FXML
    private Label contactLabel;
    @FXML
    private ComboBox<ContactInstance> contactComboBox;
    @FXML
    private Label startTimeLabel;
    @FXML
    private TextField startTimeTimeTextField;
    @FXML
    private DatePicker startTimeDatePicker;
    @FXML
    private Label endTimeLabel;
    @FXML
    private TextField endTimeTimeTextField;
    @FXML
    private Label descLabel;
    @FXML
    private TextArea descTextArea;
    @FXML
    private Button submitButton;
    @FXML
    private Button cancelButton;

    /**
     * Change the addAppointment into an edit appointment pane by assigning an appointment to edit
     * @param appointment the AppointmentInstance the user wants to edit
     * @param state the application state
     */
    public void setAppointment(AppointmentInstance appointment, AppState state) {
        currentZone = state.getCurrentZone();

        idTextField.setText(appointment.getId().toString());
        titleTextField.setText(appointment.getTitle());
        locationTextField.setText(appointment.getLocation());
        typeTextField.setText(appointment.getType());
        descTextArea.setText(appointment.getDescription());

        DateTimeFormatter hourFormat = DateTimeFormatter.ofPattern("hh:mm a");
        startTimeTimeTextField.setText(hourFormat.format(appointment.getStartTimeLocal()));
        startTimeDatePicker.setValue(appointment.getStartTimeLocal().toLocalDate());

        endTimeTimeTextField.setText(hourFormat.format(appointment.getEndTimeLocal()));

        CustomerInstance customer = null;
        for (CustomerInstance item : state.getCustomers()) {
            if (Objects.equals(item.getId(), appointment.getCustomerId())) {
                customer = item;
                break;
            }
        }
        if (customer != null) {
            custComboBox.getSelectionModel().select(customer);
        }

        ContactInstance contact = null;
        for (ContactInstance item : state.getContacts()) {
            if (Objects.equals(item.id(), appointment.getContactId())) {
                contact = item;
                break;
            }
        }
        if (customer != null) {
            contactComboBox.getSelectionModel().select(contact);
        }

        this.appointment = appointment;
    }

    /**
     * Set the list of customers the user can see in the dropdown
     * @param customers the ObservableList of CustomerInstances
     */
    public void setCustomers(ObservableList<CustomerInstance> customers) {
        custComboBox.setItems(customers);
    }

    /**
     * Set the list of contacts the user can see in the dropdown
     * @param contacts the ObservableList of ContactInstances
     */
    public void setContacts(ObservableList<ContactInstance> contacts) {
        contactComboBox.setItems(contacts);
    }

    /**
     * After the scene has been built, initialize the listeners and factories
     */
    public void initialize() {
        // Note that initialize is always called before any `setAppointment` could be called
        if (appointment == null) {
            appointment = new AppointmentInstance(-1, "", "", "", -1,
                    "", null, null, -1, -1);

            // Set defaults so the user knows what format to enter their time in as
            DateTimeFormatter hourFormat = DateTimeFormatter.ofPattern("hh:mm a");
            startTimeTimeTextField.setText(LocalTime.now().format(hourFormat));
            endTimeTimeTextField.setText(LocalTime.now().format(hourFormat));
        }
        custComboBox.setCellFactory(new Callback<>() {
            @Override
            public ListCell<CustomerInstance> call(ListView<CustomerInstance> customerInstanceListView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(CustomerInstance customerInstance, boolean b) {
                        super.updateItem(customerInstance, b);
                        if (customerInstance != null) {
                            setText(customerInstance.getCustomerName());
                        } else {
                            setText(null);
                        }
                    }
                };
            }
        });
        custComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(CustomerInstance customerInstance) {
                if (customerInstance != null) {
                    return customerInstance.getCustomerName();
                } else {
                    return null;
                }
            }

            @Override
            public CustomerInstance fromString(String s) {
                return null;
            }
        });

        contactComboBox.setCellFactory(new Callback<>() {
            @Override
            public ListCell<ContactInstance> call(ListView<ContactInstance> contactInstanceListView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(ContactInstance contactInstance, boolean b) {
                        super.updateItem(contactInstance, b);
                        if (contactInstance != null) {
                            setText(contactInstance.contactName());
                        } else {
                            setText(null);
                        }
                    }
                };
            }
        });
        contactComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(ContactInstance contactInstance) {
                if (contactInstance != null) {
                    return contactInstance.contactName();
                }
                return null;
            }

            @Override
            public ContactInstance fromString(String s) {
                return null;
            }
        });

    }

    private AppointmentInstance appointment;

    /**
     * Get the completed form's appointment object
     * @return the update/created appointment resulting from the form
     */
    public AppointmentInstance getAppointment() {
        return appointment;
    }

    private ZoneId currentZone;

    /**
     * Set the application's zone for timezone formatting
     * @param zone the ZoneId the application is configured for local
     */
    public void setZone(ZoneId zone) {
        currentZone = zone;
    }

    private JDBC databaseConn;

    /**
     * Set the database connection so the form can check for conflicting appointment
     * @param newConn the database connection
     */
    public void setDatabaseConnection(JDBC newConn) {
        databaseConn = newConn;
    }

    /**
     * Handle the Submit button on the addAppointment form.
     * Does last minute input validation and prepared the value for getAppointment
     */
    public void onSubmit() {

        if (titleTextField.getText() == null || titleTextField.getText().isEmpty()) {
            titleTextField.setStyle("-fx-border-color: #FF0000");

            Alert alert = new Alert(Alert.AlertType.ERROR, "A title is required\nPlease fix the issue and try again", ButtonType.OK);
            alert.showAndWait();
            return;
        } else {
            appointment.setTitle(titleTextField.getText());
        }

        appointment.setType(typeTextField.getText());
        appointment.setLocation(locationTextField.getText());
        appointment.setDescription(descTextArea.getText());

        if (validateContactCombBox()) {
            appointment.setContactId(contactComboBox.getValue().id());
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "The Contact box is not filled out\nPlease fill it out and try again", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        if (validateCustCombBox()){
            appointment.setCustomerId(custComboBox.getValue().getId());
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "The Customer box is not filled out\nPlease fill it out and try again", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        if (!(onStartTimeFocusChange() && onEndTimeFocusChange())) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "One of your times were not correct\nPlease fix the issue and try again", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        DateTimeFormatter hourFormat = DateTimeFormatter.ofPattern("hh:mm a");
        LocalTime startTime = LocalTime.parse(startTimeTimeTextField.getText(), hourFormat);
        LocalTime endTime = LocalTime.parse(endTimeTimeTextField.getText(), hourFormat);

        if (!startTime.isBefore(endTime)) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "You cannot end the appointment before it starts\nPlease fix the issue and try again", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        ZonedDateTime startDateTime = null;
        ZonedDateTime endDateTime = null;
        boolean didError = false;
        try {
            startDateTime = ZonedDateTime.of(startTimeDatePicker.getValue().atStartOfDay(), currentZone)
                    .withHour(startTime.getHour())
                    .withMinute(startTime.getMinute())
                    .withSecond(startTime.getSecond())
                    .withZoneSameInstant(ZoneOffset.UTC);
        } catch (DateTimeParseException | NullPointerException e) {
            startTimeDatePicker.setStyle("-fx-border-color: #FF0000");
            didError = true;
        }

        try {
            endDateTime = ZonedDateTime.of(startTimeDatePicker.getValue().atStartOfDay(), currentZone)
                    .withHour(endTime.getHour())
                    .withMinute(endTime.getMinute())
                    .withSecond(endTime.getSecond())
                    .withZoneSameInstant(ZoneOffset.UTC);
        } catch (DateTimeParseException | NullPointerException e) {
            startTimeDatePicker.setStyle("-fx-border-color: #FF0000");
            didError = true;
        }

        if (startDateTime != null && endDateTime != null) {

            if (databaseConn.appointmentConflictDuring(startDateTime, endDateTime, appointment.getCustomerId())) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "This customer already has an appointment at that time!", ButtonType.OK);
                alert.showAndWait();
                return;
            }

            ZonedDateTime businessHoursStart = startTimeDatePicker.getValue().atStartOfDay(ZoneId.of("America/New_York")).withHour(8).withMinute(0).withSecond(0);
            ZonedDateTime businessHoursEnd = startTimeDatePicker.getValue().atStartOfDay(ZoneId.of("America/New_York")).withHour(22).withMinute(0).withSecond(0);
            if (startDateTime.isBefore(businessHoursStart) || startDateTime.isAfter(businessHoursEnd)
                || endDateTime.isBefore(businessHoursStart) || endDateTime.isAfter(businessHoursEnd) ) {

                Alert alert = new Alert(Alert.AlertType.WARNING, "The entered hours are outside of business hours\n" +
                        "Are you sure you want to create this appointment?", ButtonType.YES, ButtonType.CANCEL);
                alert.showAndWait();

                if (alert.getResult() != ButtonType.YES) {
                    return;
                }
            }
        }

        if (didError) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "One of your dates were entered incorrectly\nPlease fix the issue and try again", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        if (startDateTime != null && endDateTime != null) {
            appointment.setStartTime(startDateTime);
            appointment.setEndTime(endDateTime);
        }

        appointment.setLocalTime(currentZone);

        currentStage.close();
    }

    /**
     * Validate that the user entered a valid time format, highlighting the box in red
     * if the format is not valid
     * @return a boolean representing if the time is in a valid format, and is safe to process
     */
    public boolean onStartTimeFocusChange() {
        DateTimeFormatter hourFormat = DateTimeFormatter.ofPattern("hh:mm a");
        try {
            String time = startTimeTimeTextField.getText();
            if (time != null && !time.isEmpty()) {
                LocalTime.parse(time, hourFormat);
            }
            startTimeTimeTextField.setStyle("-fx-border-color: darkgrey");
            return true;
        } catch (DateTimeParseException e) {
            startTimeTimeTextField.setStyle("-fx-border-color: #FF0000");
            return false;
        }
    }

    /**
     * Validate that the user entered a valid time format, highlighting the box in red
     * if the format is not valid
     * @return a boolean representing if the time is in a valid format, and is safe to process
     */
    public boolean onEndTimeFocusChange() {
        DateTimeFormatter hourFormat = DateTimeFormatter.ofPattern("hh:mm a");
        try {
            String time = endTimeTimeTextField.getText();
            if (time != null && !time.isEmpty()) {
                LocalTime.parse(time, hourFormat);
            }
            endTimeTimeTextField.setStyle("-fx-border-color: darkgrey");
            return true;
        } catch (DateTimeParseException e) {
            endTimeTimeTextField.setStyle("-fx-border-color: #FF0000");
            return false;
        }
    }

    /**
     * Validate that the user chose a valid Customer from the Customer Combo Box,
     * highlighting the box in red if the input is not valid
     * @return a boolean representing if the input is valid, and is safe to process
     */
    public boolean validateCustCombBox() {
        if (custComboBox.getValue() == null) {
            custComboBox.setStyle("-fx-border-color: #FF0000");
            return false;
        }
        custComboBox.setStyle("-fx-border-color: darkgrey");
        return true;
    }

    /**
     * Validate that the user chose a valid Contact from the Contact Combo Box,
     * highlighting the box in red if the input is not valid
     * @return a boolean representing if the input is valid, and is safe to process
     */
    public boolean validateContactCombBox() {
        if (contactComboBox.getValue() == null) {
            contactComboBox.setStyle("-fx-border-color: #FF0000");
            return false;
        }
        contactComboBox.setStyle("-fx-border-color: darkgrey");
        return true;
    }

    private boolean wasCanceled = false;

    /**
     * Get whether the form was canceled
     * @return if the form was canceled
     */
    public boolean wasCanceled() {
        return wasCanceled;
    }

    /**
     * handle the cancel button on the addAppointments form
     */
    public void onCancel() {
        wasCanceled = true;
        this.currentStage.close();
    }
}
