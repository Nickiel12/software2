package com.example.software2;

import com.example.software2.models.*;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;
import java.util.Objects;

/**
 * The controller for the Main window of the application
 * @author Nicholas Young
 */
public class Main {
    private AppState state;

    private Stage primaryStage;
    private boolean closeStageOnSet = false;

    /**
     * Set the Stage that runs this object, also checks if the login prompt resulted
     * in a failure, and said to close the app
     * @param stage the stage object that contains the Main window
     */
    public void setStage(Stage stage) {
        primaryStage = stage;
        if (closeStageOnSet) {
            primaryStage.close();
        }
    }

    // D_ fxml objects exist on the "Dashboard" tab, hence the 'D_' prefix
    @FXML
    private Label D_UserLoggedIn;
    @FXML
    private Label D_CurTimezone;
    @FXML
    private ScrollPane D_AppointmentsPane;
    @FXML
    private HBox D_AppointmentsHBox;

    /**
     * The function called at class creation, before the main window is shown.
     * Used to handle setting up the AppState, getting the regions, and displaying the login screen
     * @throws IOException Throws an IOException if the LoginPrompt.fxml file is not in the correct location
     */
    public void initialize() throws IOException {
        state = new AppState();
        state.setCurrentZone(ZoneId.systemDefault());
        state.setCurrentLocale(Locale.getDefault());
        handleLogin();

        state.updateContacts();
        state.updateCustomers();
        state.updateCountries();
        state.updateDivisions();

        D_UserLoggedIn.setText("Welcome: " + state.getUsername());
        D_CurTimezone.setText("Current Timezone: " + state.getCurrentZone());

        D_AppointmentsHBox.setSpacing(10);
        D_AppointmentsPane.setFitToWidth(true);
        D_AppointmentsPane.setFitToHeight(false);
        D_AppointmentsPane.setPrefViewportHeight(200);
        state.getAppointments().addListener(new ListChangeListener<AppointmentInstance>() {
            @Override
            public void onChanged(Change<? extends AppointmentInstance> change) {
                // this method will cause lag as it rebuild the whole list
                // but only if there is a very large number of appointments.
                // A good future optimization
                D_AppointmentsHBox.getChildren().removeAll();
                for (int i = 0; i < state.getAppointments().size(); i++) {

                    FXMLLoader cardLoader = new FXMLLoader(getClass().getResource("AppointmentCard.fxml"));
                    Parent card = null;
                    try {
                        card = cardLoader.load();
                        AppointmentCard cardController = cardLoader.getController();
                        cardController.setAppointment(state.getAppointments().get(i), state);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    D_AppointmentsHBox.getChildren().add(card);
                }
            }
        });

        initializeCalendar();
        initializeCustomersView();

        state.updateAppointments();
    }

    /**
     * Check if there is an appointment within the next 15 minutes,
     * and display an alert if there is
     */
    public void checkUpcoming() {
        AppointmentInstance appointment = null;
        ZonedDateTime now = ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC);
        ZonedDateTime later = now.plusMinutes(15);
        for(AppointmentInstance item: state.getAppointments()) {
            if (item.getStartTime().isAfter(now) && item.getStartTime().isBefore(later)) {
                appointment = item;
                break;
            }
        }
        if (appointment != null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "You have an upcoming appointment!" +
                    "\nId: " + appointment.getId() +
                     "\nTime: " + appointment.getStartTimeLocal().format(DateTimeFormatter.ofPattern("hh:mm a")) + " -> " +
                    appointment.getEndTimeLocal().format(DateTimeFormatter.ofPattern("hh:mm a")) +
                     "\nDate: " + appointment.getStartTimeLocal().format(DateTimeFormatter.ofPattern("MM/dd/yy"))
            , ButtonType.OK);
            alert.showAndWait();
        }
    }


    @FXML
    private RadioButton C_MonthRadio;
    @FXML
    private RadioButton C_WeekRadio;
    private ToggleGroup C_MonthWeekToggle;
    @FXML
    private Button C_LeftNav;
    @FXML
    private Button C_RightNav;
    @FXML
    private Label C_NavLabel;
    @FXML
    private TableView C_Table;
    private LocalDate C_FilterStart;
    private LocalDate C_FilterEnd;

    @FXML
    private Button createAppointment;
    @FXML
    private Button editAppointment;
    @FXML
    private Button deleteAppointment;

    private FilteredList<AppointmentInstance> calendarAppointments;
    /**
     * Create the calendar table found on the calendar tab
     */
    public void initializeCalendar() {
        calendarAppointments = new FilteredList<>(state.getAppointments());
        C_MonthWeekToggle = new ToggleGroup();
        C_MonthRadio.setToggleGroup(C_MonthWeekToggle);
        C_WeekRadio.setToggleGroup(C_MonthWeekToggle);

        C_MonthWeekToggle.selectedToggleProperty().addListener((observable, oldVal, newVal) -> onCalendarToggle());
        C_MonthWeekToggle.selectToggle(C_MonthRadio);

        TableColumn<AppointmentInstance, Integer> appIDColumn = new TableColumn<>("Id");
        appIDColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        appIDColumn.setPrefWidth(20);
        TableColumn<AppointmentInstance, String> appTitleColumn = new TableColumn<>("Title");
        appTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        appTitleColumn.setPrefWidth(90);
        TableColumn<AppointmentInstance, String> appTypeColumn = new TableColumn<>("Type");
        appTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        appTypeColumn.setPrefWidth(90);
        TableColumn<AppointmentInstance, String> appDescColumn = new TableColumn<>("Description");
        appDescColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        appDescColumn.setPrefWidth(150);
        TableColumn<AppointmentInstance, Integer> appCustomerColumn = new TableColumn<>("Customer");
        appCustomerColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        appCustomerColumn.setPrefWidth(30);
        TableColumn<AppointmentInstance, String> appLocationColumn = new TableColumn<>("Location");
        appLocationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        appLocationColumn.setPrefWidth(80);
        TableColumn<AppointmentInstance, String> appContactColumn = new TableColumn<>("Contact");
        appContactColumn.setCellValueFactory(appointment -> {
            for(ContactInstance item: state.getContacts()) {
                if (Objects.equals(item.id(), appointment.getValue().getContactId())) {
                    return new ReadOnlyObjectWrapper<>(item.contactName());
                }
            }
            return new ReadOnlyObjectWrapper<>(null);
        });
        appContactColumn.setPrefWidth(80);
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
        TableColumn<AppointmentInstance, Integer> appUserId = new TableColumn<>("User ID");
        appUserId.setCellValueFactory(new PropertyValueFactory<>("id"));
        appUserId.setPrefWidth(30);

        C_Table.getColumns().addAll(appIDColumn, appTitleColumn, appTypeColumn, appDescColumn, appLocationColumn,
                 appContactColumn, appStartTime, appEndTime, appCustomerColumn, appUserId);
        C_Table.setItems(calendarAppointments);

        C_Table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            editAppointment.setDisable(newSelection == null);
            deleteAppointment.setDisable(newSelection == null);
        });
    }

    /**
     * Handle the press of the rightward calendar navigation, moving the start
     * and end times based on the toggle's value
     */
    public void onCalendarRightNav() {
        if (C_MonthWeekToggle.getSelectedToggle() == C_MonthRadio) {
            C_FilterStart = C_FilterStart.plusMonths(1);
            C_FilterEnd = C_FilterEnd.plusMonths(1);
        } else {
            C_FilterStart = C_FilterStart.plusWeeks(1);
            C_FilterEnd = C_FilterEnd.plusWeeks(1);
        }
        filterCalendar();
    }

    /**
     * Handle the press of the leftward calendar navigation, moving the start
     * and end times based on the toggle's value
     */
    public void onCalendarLeftNav() {
        if (C_MonthWeekToggle.getSelectedToggle() == C_MonthRadio) {
            C_FilterStart = C_FilterStart.minusMonths(1);
            C_FilterEnd = C_FilterEnd.minusMonths(1);
        } else {
            C_FilterStart = C_FilterStart.minusWeeks(1);
            C_FilterEnd = C_FilterEnd.minusWeeks(1);
        }
        filterCalendar();
    }

    /**
     * Handle the change of the calendar toggle group. This group controls whether the
     * calendar table shows a month's worth, or a weeks worth of appointments
     */
    private void onCalendarToggle() {
        LocalDate today = LocalDate.now();
        if (C_MonthWeekToggle.getSelectedToggle() == C_MonthRadio) {
            C_FilterStart = today.withDayOfMonth(1);
            C_FilterEnd = C_FilterStart.plusMonths(1);
        } else {
            C_FilterStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
            C_FilterEnd = C_FilterStart.plusWeeks(1);
        }
        filterCalendar();
    }

    /**
     * A convenience function to update the calendar table items filtered list predicate,
     * and changes the navigation label that shows the current filter's range
     */
    private void filterCalendar() {
        if (C_MonthWeekToggle.getSelectedToggle() == C_MonthRadio) {
            C_NavLabel.setText(C_FilterStart.getMonth().getDisplayName(TextStyle.SHORT, state.getCurrentLocale())
                    + " - " +
                    C_FilterEnd.getMonth().getDisplayName(TextStyle.SHORT, state.getCurrentLocale())
            );
        } else {

            C_NavLabel.setText(DateTimeFormatter.ofPattern("MM/dd").format(C_FilterStart)
                    + " - " +
                    DateTimeFormatter.ofPattern("MM/dd").format(C_FilterEnd)
            );
        }
        calendarAppointments.setPredicate( appointment -> appointment.getStartTime().isAfter(C_FilterStart.atStartOfDay(state.getCurrentZone()))
                        && appointment.getStartTime().isBefore(C_FilterEnd.atStartOfDay(state.getCurrentZone())));
    }

    /**
     * Handle the press of the Delete appointment button.
     * Confirms the user wishes to delete that appointment, and displays any errors
     */
    public void onRemoveAppointmentPress() {
        AppointmentInstance appointment = (AppointmentInstance) C_Table.getSelectionModel().getSelectedItem();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this appointment?"
                + "\nId: " + appointment.getId() + "\nType: " + appointment.getType(), ButtonType.YES, ButtonType.CANCEL);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            String result = state.getDatabaseConnection().deleteAppointment(appointment);
            Alert alert1 = new Alert(Alert.AlertType.INFORMATION, result, ButtonType.OK);
            alert1.showAndWait();

            state.updateAppointments();
        }
    }

    /**
     * Handles the press of the Edit appointment button. It finds the selected appointment in the
     * calendar table, creates a new AddAppointment window, and calls setAppointment to change it
     * to the editing state. Finally, it checks if the new window returned a valid result, and updates the list
     * @throws IOException throws an IOException if the AddAppointment.fxml file is not in the correct location
     */
    public void onEditAppointmentPress() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AddAppointment.class.getResource("AddAppointment.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = new Stage();
        stage.setTitle("Edit New Appointment");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(primaryStage);
        stage.setScene(new Scene(root));

        AppointmentInstance appointment = (AppointmentInstance) C_Table.getSelectionModel().getSelectedItem();
        AddAppointment controller = fxmlLoader.getController();
        controller.setCustomers(state.getCustomers());
        controller.setContacts(state.getContacts());
        controller.setZone(state.getCurrentZone());
        controller.setAppointment(appointment, state);
        controller.setStage(stage);
        controller.initialize();

        stage.showAndWait();
        if (!controller.wasCanceled()) {
            state.getDatabaseConnection().editAppointment(controller.getAppointment());
            state.updateAppointments();
        }
    }

    /**
     * Handles the press of the Create appointment button. It finds the selected appointment in the
     * calendar table, creates a new AddAppointment window. Finally, it checks if the new window returned a valid result
     * and updates the list
     * @throws IOException throws an IOException if the AddAppointment.fxml file is not in the correct location
     */
    public void onCreateAppointmentPress() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AddAppointment.class.getResource("AddAppointment.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = new Stage();
        stage.setTitle("Create New Appointment");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(primaryStage);
        stage.setScene(new Scene(root));

        AppointmentInstance appointment = (AppointmentInstance) C_Table.getSelectionModel().getSelectedItem();
        AddAppointment controller = fxmlLoader.getController();
        controller.setCustomers(state.getCustomers());
        controller.setContacts(state.getContacts());
        controller.setZone(state.getCurrentZone());
        controller.setStage(stage);
        controller.initialize();

        stage.showAndWait();

        if (!controller.wasCanceled()) {
            appointment = controller.getAppointment();
            appointment.setUserId(state.getUserId());

            state.getDatabaseConnection().createAppointment(appointment, state.getUsername());
            state.updateAppointments();
        }
    }


    @FXML
    public TableView Cst_TableView;
    @FXML
    public Button Cst_EditButton;
    @FXML
    public Button Cst_CreateButton;
    @FXML
    public Button Cst_DeleteButton;

    /**
     * Set up the Customer table and set all the listeners
     */
    public void initializeCustomersView() {

        TableColumn<CustomerInstance, Integer> cst_idColumn = new TableColumn<>("Id");
        cst_idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        cst_idColumn.setPrefWidth(20);

        TableColumn<CustomerInstance, String> cst_nameColumn = new TableColumn<>("Name");
        cst_nameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        cst_nameColumn.setPrefWidth(100);

        TableColumn<CustomerInstance, String> cst_phoneColumn = new TableColumn<>("Phone Number");
        cst_phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        cst_phoneColumn.setPrefWidth(50);

        TableColumn<CustomerInstance, String> cst_country = new TableColumn<>("Country");

        cst_country.setCellValueFactory(data -> {
            DivisionInstance division = null;
            for (DivisionInstance item : state.getDivisions()) {
                if (Objects.equals(item.id(), data.getValue().getDivisionId())) {
                    division = item;
                }
            }

            if (division == null) {
                return new ReadOnlyObjectWrapper<>(null);
            }

            for (CountryInstance item : state.getCountries()) {
                if (Objects.equals(item.id(), division.country_id())) {
                    return new ReadOnlyObjectWrapper<>(item.country());
                }
            }
            return new ReadOnlyObjectWrapper<>(null);
        });
        cst_country.setPrefWidth(50);

        TableColumn<CustomerInstance, String> cst_division = new TableColumn<>("Division");
        cst_division.setCellValueFactory(data -> {
            for (DivisionInstance item : state.getDivisions()) {
                if (Objects.equals(item.id(), data.getValue().getDivisionId())) {
                    return new ReadOnlyObjectWrapper<>(item.division());
                }
            }
            return new ReadOnlyObjectWrapper<>(null);
        });
        cst_division.setPrefWidth(50);

        TableColumn<CustomerInstance, String> cst_address = new TableColumn<>("Address");
        cst_address.setCellValueFactory(new PropertyValueFactory<>("address"));
        cst_address.setPrefWidth(100);

        Cst_TableView.getColumns().addAll(cst_idColumn, cst_nameColumn, cst_phoneColumn, cst_country, cst_division, cst_address);
        Cst_TableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                Cst_EditButton.setDisable(newSelection == null);
                Cst_DeleteButton.setDisable(newSelection == null);
            });
        Cst_TableView.setItems(state.getCustomers());
    }

    /**
     * Handle the customer Edit button. Opens a new AddCustomer window, and calls setCustomer to
     * change it to an editing window. Finally, it checks that the result is valid, and updates the lists
     * @throws IOException throws an IOException if the AddCustomer.fxml file is not in the correct location
     */
    public void onCustomerEditPress() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AddAppointment.class.getResource("AddCustomer.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = new Stage();
        stage.setTitle("Edit Customer Info");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(primaryStage);
        stage.setScene(new Scene(root));

        CustomerInstance customer = (CustomerInstance) Cst_TableView.getSelectionModel().getSelectedItem();
        AddCustomer controller = fxmlLoader.getController();
        controller.setStage(stage);
        controller.setEditingUser(state.getUsername());
        controller.setCountries(state.getCountries());
        controller.setDivisions(state.getDivisions());
        controller.setCustomer(customer, state);

        stage.showAndWait();
        if (!controller.wasCanceled()) {
            state.getDatabaseConnection().editCustomer(controller.getCustomer());
            state.updateCustomers();
        }
    }

    /**
     * Handle the customer Create button. Opens a new AddCustomer window, Finally, it checks that the result is valid, and updates the lists
     * @throws IOException throws an IOException if the AddCustomer.fxml file is not in the correct location
     */
    public void onCustomerCreatePress() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AddAppointment.class.getResource("AddCustomer.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = new Stage();
        stage.setTitle("Create Customer Info");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(primaryStage);
        stage.setScene(new Scene(root));

        AddCustomer controller = fxmlLoader.getController();
        controller.setStage(stage);
        controller.setEditingUser(state.getUsername());
        controller.setCountries(state.getCountries());
        controller.setDivisions(state.getDivisions());

        stage.showAndWait();
        if (!controller.wasCanceled()) {
            state.getDatabaseConnection().createCustomer(controller.getCustomer());
            state.updateCustomers();
        }
    }

    /**
     * Handle the customer Delete button press. Gets the selected customer from the table,
     * and confirms with the user that they want to delete it. It then makes a database call
     * to ensure there are no associated appointments that will stop the delete call
     */
    public void onCustomerDeletePress() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete this customer record?\nThis action cannot be undone",
                ButtonType.YES, ButtonType.CANCEL);
        alert.showAndWait();

        CustomerInstance customer = (CustomerInstance) Cst_TableView.getSelectionModel().getSelectedItem();

        if (alert.getResult() == ButtonType.YES) {
            boolean did_delete = state.getDatabaseConnection().tryDeleteCustomer(customer);
            if (!did_delete) {
                Alert alert1 = new Alert(Alert.AlertType.ERROR,
                        "Delete failed!\nYou must delete all appointments that this customer has before you can delete the customer",
                        ButtonType.OK);
                alert1.showAndWait();
            } else {
                state.updateCustomers();
            }
        }

    }

    @FXML
    private Button Rpt_AppTypeMnth;
    @FXML
    private Button Rpt_ContactSched;
    @FXML
    private Button Rpt_CustByDiv;

    public void onRptAppoinmentTypePerMnth() throws IOException  {
        state.updateCustomers();
        state.updateCountries();
        state.updateDivisions();

        FXMLLoader fxmlLoader = new FXMLLoader(LoginPrompt.class.getResource("Rpt_AppointmentTypesPerMonth.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(primaryStage);
        stage.setScene(new Scene(root));

        Rpt_AppointmentTypesPerMonth controller = (Rpt_AppointmentTypesPerMonth) fxmlLoader.getController();
        controller.setStage(stage);
        controller.setApplicationState(state);

        stage.showAndWait();
    }

    public void onRptContactSchedule() throws IOException  {
        state.updateCustomers();
        state.updateCountries();
        state.updateDivisions();

        FXMLLoader fxmlLoader = new FXMLLoader(LoginPrompt.class.getResource("Rpt_ContactSchedule.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(primaryStage);
        stage.setScene(new Scene(root));

        Rpt_ContactSchedule controller = (Rpt_ContactSchedule) fxmlLoader.getController();
        controller.setStage(stage);
        controller.setApplicationState(state);

        stage.showAndWait();
    }

    public void onRptCustomerByDivision() throws IOException  {
        state.updateCustomers();
        state.updateCountries();
        state.updateDivisions();

        FXMLLoader fxmlLoader = new FXMLLoader(LoginPrompt.class.getResource("Rpt_CustomerByDivision.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(primaryStage);
        stage.setScene(new Scene(root));

        Rpt_CustomerByDivision controller = (Rpt_CustomerByDivision) fxmlLoader.getController();
        controller.setStage(stage);
        controller.setApplicationState(state);

        stage.showAndWait();
    }


    /**
     * Opens the login window, and either closes the application, or updates the state with
     * the user information
     * @throws IOException throws an IOException if the LoginPrompt.fxml file is not in the correct location
     */
    public void handleLogin() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LoginPrompt.class.getResource("LoginPrompt.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(primaryStage);
        stage.setScene(new Scene(root));

        LoginPrompt loginPrompt = fxmlLoader.getController();
        loginPrompt.setCurrentStage(stage);
        loginPrompt.setState(state);

        stage.showAndWait();

        if (!state.appIsLoggedIn()) {
            closeStageOnSet = true;
        }
    }
}
