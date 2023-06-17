package com.example.software2;

import com.example.software2.models.Rpt_AppointmentTypePerMonthRow;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.LocalDate;

public class Rpt_AppointmentTypesPerMonth {

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
    }

    @FXML
    private DatePicker filterStart;
    private LocalDate filterStartMonth;
    @FXML
    private DatePicker filterEnd;
    private LocalDate filterEndMonth;
    @FXML
    private TableView reportTable;

    public void initialize() {
        filterStart.setValue(LocalDate.now().withDayOfMonth(1));
        filterStartMonth = LocalDate.now().withDayOfMonth(1);
        filterEndMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

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
        rptCount.setCellValueFactory(new PropertyValueFactory<>("count"));
        rptCount.setPrefWidth(50);

        TableColumn<Rpt_AppointmentTypePerMonthRow, String> rptMonth = new TableColumn<>("Month");
        rptMonth.setCellValueFactory(new PropertyValueFactory<>("month"));
        rptMonth.setPrefWidth(100);

        TableColumn<Rpt_AppointmentTypePerMonthRow, String> rptType = new TableColumn<>("Type");
        rptType.setCellValueFactory(new PropertyValueFactory<>("type"));
        rptType.setPrefWidth(100);

        reportTable.getColumns().addAll(rptCount, rptMonth, rptType);
    }

    private void updateReport() {
        reportTable.setItems(state.getDatabaseConnection().Rpt_AppointmentsPerMonth(filterStartMonth, filterEndMonth));
    }
}
