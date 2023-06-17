package com.example.software2;

import com.example.software2.models.Rpt_AppointmentTypePerMonthRow;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.LocalDate;

/**
 * Controller for the Appointment Types Per Month report window
 * @author Nicholas Young
 */
public class Rpt_AppointmentTypesPerMonth {
    private AppState state;

    /**
     * Set the application state to populate the report
     * @param appState the current application state
     */
    public void setApplicationState(AppState appState) {
        state = appState;
        updateReport();
    }

    @FXML
    private DatePicker filterStart;
    private LocalDate filterStartMonth;
    @FXML
    private DatePicker filterEnd;
    private LocalDate filterEndMonth;
    @FXML
    private TableView<Rpt_AppointmentTypePerMonthRow> reportTable;

    /**
     * Initialize all of the table columns, defaults, and event hooks for the Appointment Types Per Month report window
     */
    public void initialize() {
        filterStartMonth = LocalDate.now().withDayOfMonth(1);
        filterStart.setValue(filterStartMonth);
        filterEndMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        filterEnd.setValue(filterEndMonth);

        filterStart.setOnAction(actionEvent -> {
            LocalDate date = filterStart.getValue();
            filterStartMonth = date.withDayOfMonth(1);
            filterStart.setValue(filterStartMonth);
            updateReport();
        });

        filterEnd.setOnAction(actionEvent -> {
            LocalDate date = filterEnd.getValue();
            filterEndMonth = date.withDayOfMonth(date.lengthOfMonth());
            filterEnd.setValue(filterEndMonth);
            updateReport();
        });

        TableColumn<Rpt_AppointmentTypePerMonthRow, Integer> rptCount = new TableColumn<>("Count");
        rptCount.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().count()));
        rptCount.setPrefWidth(50);

        TableColumn<Rpt_AppointmentTypePerMonthRow, String> rptMonth = new TableColumn<>("Month");
        rptMonth.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().month()));
        rptMonth.setPrefWidth(100);

        TableColumn<Rpt_AppointmentTypePerMonthRow, String> rptType = new TableColumn<>("Type");
        rptType.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().appointmentType()));
        rptType.setPrefWidth(100);

        reportTable.getColumns().addAll(rptCount, rptMonth, rptType);
    }

    private void updateReport() {
        reportTable.setItems(state.getDatabaseConnection().Rpt_AppointmentsPerMonth(filterStartMonth, filterEndMonth));
    }
}
