package com.example.software2;

import com.example.software2.models.CountryInstance;
import com.example.software2.models.CustomerInstance;
import com.example.software2.models.DivisionInstance;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * The controller for the form to create and edit Customers.
 * It is created as a Create form, and is changed to an Edit form when
 * a CustomerInstance is set with setCustomer
 * @author Nicholas Young
 */
public class AddCustomer {
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
    private Label nameLabel;
    @FXML
    private TextField nameTextField;
    @FXML
    private Label phoneLabel;
    @FXML
    private TextField phoneTextField;
    @FXML
    private Label postalCodeLabel;
    @FXML
    private TextField postalCodeTextField;
    @FXML
    private Label countryLabel;
    @FXML
    private ComboBox<CountryInstance> countryComboBox;
    @FXML
    private Label divisionLabel;
    @FXML
    private ComboBox<DivisionInstance> divisionComboBox;
    @FXML
    private Label addressLabel;
    @FXML
    private TextField addressTextField;
    @FXML
    private Button submitButton;
    @FXML
    private Button cancelButton;

    private CustomerInstance customer;

    private String editingUser;

    /**
     * Set the userId of the user editing the appointment
     * @param editingUser the userId of the editing user
     */
    public void setEditingUser(String editingUser) {
        this.editingUser = editingUser;
    }

    /**
     * The function called after the screen has been loaded, and sets
     * defaults, listeners, and other hooks on UI elements
     */
    public void initialize() {
        if (customer == null) {
            customer = new CustomerInstance(-1, null, null, null, null,
                    ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC), null, ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC),
                    null, -1);
        }

        divisionComboBox.setCellFactory(new Callback<>() {
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

        divisionComboBox.setConverter(new StringConverter<>() {
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

        countryComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> filterDivisions());
    }

    /**
     * Set the CustomerInstance that the user is editing
     * @param customer the CustomerInstance the user wants to edit
     * @param state the application AppState. Only getters are used in this function
     */
    public void setCustomer(CustomerInstance customer, AppState state) {
        idTextField.setText(customer.getId().toString());
        nameTextField.setText(customer.getCustomerName());
        phoneTextField.setText(customer.getPhoneNumber());
        postalCodeTextField.setText(customer.getPostalCode());
        addressTextField.setText(customer.getAddress());

        DivisionInstance division = null;
        for(DivisionInstance item: state.getDivisions()) {
            if (Objects.equals(item.id(), customer.getDivisionId())) {
                division = item;
                break;
            }
        }

        if (division != null) {
            for (CountryInstance item: state.getCountries()) {
                if (Objects.equals(item.id(), division.country_id())) {
                    countryComboBox.getSelectionModel().select(item);
                    break;
                }
            }
            // This has to go after assigning country because of the predicate
            divisionComboBox.getSelectionModel().select(division);
        }


        this.customer = customer;
    }

    /**
     * Get the finished CustomerInstance. Use `wasCanceled` to check if this
     * function will return an updated/valid instance
     * @return the submitted CustomerInstance
     */
    public CustomerInstance getCustomer() {
        return this.customer;
    }

    private FilteredList<DivisionInstance> divisions;

    /**
     * Set the DivisionInstances that the form will display
     * @param divisions a list of DivisionInstances used in the combo box
     */
    public void setDivisions(ObservableList<DivisionInstance> divisions) {
        this.divisions = new FilteredList<>(divisions);
        divisionComboBox.setItems(this.divisions);
    }

    /**
     * Set the CountryInstances that the form will display
     * @param countries a list of CountryInstances used in the combo box
     */
    public void setCountries(ObservableList<CountryInstance> countries) {
        countryComboBox.setItems(countries);
    }

    /**
     * An internal function that updates the predicate used to filter the
     * divisions combo box when a country is selected
     * LAMBDA USAGE: line 229 - uses a lambda to cut down on the number of lines required to do a simple comparison.
     * Without this lambda usage, at least 10 more lines would be added to this file for a private boolean function and
     * associated JavaDoc comments, which would make the code harder to understand when quickly reading the function content
     */
    private void filterDivisions() {
        CountryInstance country = countryComboBox.getSelectionModel().getSelectedItem();
        if (country != null) {
            divisions.setPredicate((division) -> Objects.equals(division.country_id(), country.id()));
        }
    }

    /**
     * The handler for the submit button.
     * This function validates all form values make sense, and there won't be
     * parsing issues, and will show an error box if there is an issue.
     * If all validations succeed, update the CustomerInstance that will be returned
     * and close the stage (window)
     */
    public void onSubmitButton() {

        if (customer.getCreatedBy() == null) {
            customer.setCreatedBy(editingUser);
        }

        boolean isValid = true;
        String name = nameTextField.getText();
        if (name == null) {
            nameTextField.setStyle("-fx-border-color: #FF0000");
            isValid = false;
        } else {
            customer.setCustomerName(name, editingUser);
            nameTextField.setStyle("-fx-border-color: darkgrey");
        }

        String phone = phoneTextField.getText();
        if (phone == null) {
            phoneTextField.setStyle("-fx-border-color: #FF0000");
            isValid = false;
        } else {
            customer.setPhoneNumber(phone, editingUser);
            phoneTextField.setStyle("-fx-border-color: darkgrey");
        }

        String postalCode = postalCodeTextField.getText();
        if (postalCode == null) {
            postalCodeTextField.setStyle("-fx-border-color: #FF0000");
            isValid = false;
        } else {
            customer.setPostalCode(postalCode, editingUser);
            postalCodeTextField.setStyle("-fx-border-color: darkgrey");
        }

        DivisionInstance division = divisionComboBox.getSelectionModel().getSelectedItem();
        if (division == null) {
            divisionComboBox.setStyle("-fx-border-color: #FF0000");
            isValid = false;
        } else {
            customer.setDivisionId(division.id(), editingUser);
            divisionComboBox.setStyle("-fx-border-color: darkgrey");
        }

        String address = addressTextField.getText();
        if (address == null) {
            addressTextField.setStyle("-fx-border-color: #FF0000");
            isValid = false;
        } else {
            customer.setAddress(address, editingUser);
            addressTextField.setStyle("-fx-border-color: darkgrey");
        }


        if (!isValid) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Some fields are not correct\nPlease fix them and try again", ButtonType.OK);
            alert.showAndWait();
        } else {
            currentStage.close();
        }

    }

    private boolean wasCanceled = false;

    /**
     * Get if the cancel button was pressed
     * @return whether the cancel button was pressed
     */
    public boolean wasCanceled() {
        return wasCanceled;
    }

    /**
     * The handler for the Cancel button.
     * It sets the wasCanceled variable, and closes the stage.
     */
    public void onCancelButton() {
        wasCanceled = true;
        currentStage.close();
    }

}
