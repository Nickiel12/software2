package com.example.software2;

import com.example.software2.models.AppointmentInstance;
import com.example.software2.models.ContactInstance;
import com.example.software2.models.DivisionInstance;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.util.Objects;

/**
 * The controller for the Contact Schedule report window.
 * Shows a report containing all upcoming appointments
 * @author Nicholas Young
 */
public class Rpt_ContactSchedule {

    private Stage currentStage;

    /**
     * Set the Stage of the object so it can close it by itself
     * @param newStage the stage this controller is displaying to
     */
    public void setStage(Stage newStage) {
        currentStage = newStage;
    }

    private AppState state;

    /**
     * Set the application state to populate the report
     * @param appState the current application state
     */
    public void setApplicationState(AppState appState) {
        state = appState;
        filteredAppointments = new FilteredList<>(state.getAppointments());
        // On first load, don't show any results till contact has been selected
        filteredAppointments.setPredicate(appointmentInstance -> false);
        reportTable.setItems(filteredAppointments);
        updateContacts();
    }

    @FXML
    private ComboBox<ContactInstance> contactSelection;
    @FXML
    private TableView<AppointmentInstance> reportTable;

    private void updateContacts() {
        contactSelection.setItems(state.getContacts());
    }

    private void populateReport() {
        ContactInstance contact = contactSelection.getSelectionModel().getSelectedItem();
        if (contact != null) {
            filteredAppointments.setPredicate(appointmentInstance -> Objects.equals(appointmentInstance.getContactId(), contact.id()));
        }
    }

    private FilteredList<AppointmentInstance> filteredAppointments;

    /**
     * Set up the tables and set any event hooks
     */
    public void initialize() {

        contactSelection.setCellFactory(new Callback<ListView<ContactInstance>, ListCell<ContactInstance>>() {
            @Override
            public ListCell<ContactInstance> call(ListView listView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(ContactInstance contact, boolean b) {
                        super.updateItem(contact, b);
                        if (contact != null) {
                            setText(contact.contactName());
                        } else {
                            setText(null);
                        }
                    }
                };
            }
        });
        contactSelection.setConverter(new StringConverter<ContactInstance>() {
            @Override
            public String toString(ContactInstance contactInstance) {
                if (contactInstance != null) {
                    return contactInstance.contactName();
                } else {
                    return null;
                }
            }

            @Override
            public ContactInstance fromString(String s) {
                return null;
            }
        });

        contactSelection.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            populateReport();
        });

        TableColumn<AppointmentInstance, Integer> appIdColumn = new TableColumn<>("Id");
        appIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        appIdColumn.setPrefWidth(20);
        TableColumn<AppointmentInstance, String> appTitleColumn = new TableColumn<>("Title");
        appTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        appTitleColumn.setPrefWidth(90);
        TableColumn<AppointmentInstance, String> appTypeColumn = new TableColumn<>("Type");
        appTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        appTypeColumn.setPrefWidth(90);
        TableColumn<AppointmentInstance, String> appDescColumn = new TableColumn<>("Description");
        appDescColumn.setPrefWidth(150);
        TableColumn<AppointmentInstance, Integer> appCustomerColumn = new TableColumn<>("Customer");
        appCustomerColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        appCustomerColumn.setPrefWidth(30);

        TableColumn<AppointmentInstance, String> appStartTime = new TableColumn<>("Start Time");
        appStartTime.setCellValueFactory(data -> {
            return new ReadOnlyObjectWrapper<>(state.getDateFormat().format(
                    data.getValue().getStartTime().withZoneSameInstant(state.getCurrentZone())
            ));
        });
        appStartTime.setPrefWidth(125);
        TableColumn<AppointmentInstance, String> appEndTime = new TableColumn<>("End Time");
        appEndTime.setCellValueFactory(data -> {
            return new ReadOnlyObjectWrapper<>(state.getDateFormat().format(
                    data.getValue().getEndTime().withZoneSameInstant(state.getCurrentZone())
            ));
        });
        appStartTime.setPrefWidth(125);

        reportTable.getColumns().addAll(appIdColumn, appTitleColumn, appTypeColumn, appDescColumn, appStartTime, appEndTime, appCustomerColumn);
    }
}
