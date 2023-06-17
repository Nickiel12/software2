package com.example.software2;

import com.example.software2.models.CountryInstance;
import com.example.software2.models.CustomerInstance;
import com.example.software2.models.DivisionInstance;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.util.Objects;

/**
 * Controller for the Customers By Division report window
 * @author Nicholas
 */
public class Rpt_CustomerByDivision {

    private AppState state;

    /**
     * Set the application state to populate the report
     * @param appState the current application state
     */
    public void setApplicationState(AppState appState) {
        state = appState;

        reportItems = new FilteredList<>(state.getCustomers());
        // Default to an empty table
        reportItems.setPredicate(item -> false);
        reportTable.setItems(reportItems);

        filteredDivisions = new FilteredList<>(state.getDivisions());
        divisionCombobox.setItems(filteredDivisions);
        countryComboBox.setItems(state.getCountries());
    }

    private FilteredList<CustomerInstance> reportItems;
    private FilteredList<DivisionInstance> filteredDivisions;

    @FXML
    private TableView<CustomerInstance> reportTable;
    @FXML
    private ComboBox<DivisionInstance> divisionCombobox;
    @FXML
    private ComboBox<CountryInstance> countryComboBox;

    /**
     * Initialize all of the table columns and hooks for the Customers By Division form
     */
    public void initialize() {

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

        reportTable.getColumns().addAll(cst_division, cst_idColumn, cst_nameColumn, cst_phoneColumn, cst_country, cst_address);

        divisionCombobox.setCellFactory(new Callback<>() {
            @Override
            public ListCell<DivisionInstance> call(ListView<DivisionInstance> divisionInstanceListView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(DivisionInstance division, boolean b) {
                        super.updateItem(division, b);
                        if (division != null) {
                            setText(division.division());
                        } else {
                            setText(null);
                        }
                    }
                };
            }
        });

        divisionCombobox.setConverter(new StringConverter<>() {
            @Override
            public String toString(DivisionInstance divisionInstance) {
                if (divisionInstance != null) {
                    return divisionInstance.division();
                } else {
                    return null;
                }
            }

            @Override
            public DivisionInstance fromString(String s) {
                return null;
            }
        });
        countryComboBox.setCellFactory(new Callback<>() {
            @Override
            public ListCell<CountryInstance> call(ListView<CountryInstance> countryInstanceListView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(CountryInstance country, boolean b) {
                        super.updateItem(country, b);
                        if (country != null) {
                            setText(country.country());
                        } else {
                            setText(null);
                        }
                    }
                };
            }
        });
        countryComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(CountryInstance countryInstance) {
                if (countryInstance != null) {
                    return countryInstance.country();
                } else {
                    return null;
                }
            }

            @Override
            public CountryInstance fromString(String s) {
                return null;
            }
        });

        countryComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                filteredDivisions.setPredicate((division) -> Objects.equals(division.country_id(), newSelection.id()));
            }
        });

        divisionCombobox.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                reportItems.setPredicate(item -> Objects.equals(item.getDivisionId(), newSelection.id()));
            }
        });
    }

}
