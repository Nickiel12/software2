package com.example.software2;

import com.example.software2.models.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * The object that represents the state of the application. Contains objects that
 * need to persist and be handed around to different classes.
 * @author Nicholas Young
 */
public class AppState {

    private JDBC databaseConnection;

    /**
     * Get the current database connection
     * @return the current database connection
     */
    public JDBC getDatabaseConnection() {
        return databaseConnection;
    }

    /**
     * Set a new database connection
     * @param databaseConnection the new database connection
     */
    public void setDatabaseConnection(JDBC databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    private boolean userHasLoggedIn;
    private String username;
    private Integer userId;

    /**
     * Set the logged-in user's information
     * @param username the user's username
     * @param userId the user's associated primary key
     */
    public void setLogInUser(String username, Integer userId) {
        userHasLoggedIn = true;
        this.userId = userId;
        this.username = username;
    }

    /**
     * Get the logged-in user's username
     * @return the user's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Get the user's primary key userId
     * @return the user's primary key userId
     */
    public Integer getUserId() {return userId;}

    /**
     * Get if the user has successfully logged into the app
     * @return a boolean representing if there has been a successful login
     */
    public boolean appIsLoggedIn() {
        return userHasLoggedIn;
    }

    private ZoneId currentZone;

    /**
     * Set the current timezone for the application
     * @param zone the new timezone for the application
     */
    public void setCurrentZone(ZoneId zone) {
        currentZone = zone;
    }

    /**
     * Get the current application configured timezone
     * @return the application's currently configured timezone
     */
    public ZoneId getCurrentZone() {
        return currentZone;
    }

    private Locale currentLocale;

    /**
     * Set the application's configured locale
     * @param locale the application's new locale
     */
    public void setCurrentLocale(Locale locale) {
        currentLocale = locale;
    }

    /**
     * Get the application's currently configured locale
     * @return the appliction's currently configured locale
     */
    public Locale getCurrentLocale() {
        return currentLocale;
    }

    private ObservableList<AppointmentInstance> appointments;

    /**
     * Get the current list of cached appointments
     * @return the currently cached appointments
     */
    public ObservableList<AppointmentInstance> getAppointments() {
        return appointments;
    }

    /**
     * Update the cached appointments for this user from the database
     */
    public void updateAppointments() {
        this.appointments.clear();
        this.appointments.addAll(this.databaseConnection.getUserAppointments(this.userId));

        for (AppointmentInstance appointment:
             this.appointments) {
            appointment.setLocalTime(this.currentZone);
        }
    }

    private ObservableList<ContactInstance> contacts;

    /**
     * Get the cached contacts
     * @return the currently cached contacts
     */
    public ObservableList<ContactInstance> getContacts() {
        return contacts;
    }

    /**
     * Trigger an update of all contacts from the database
     */
    public void updateContacts() {
        this.contacts.removeAll();
        this.contacts.addAll( this.databaseConnection.getContacts());
    }

    private ObservableList<CustomerInstance> customers;

    /**
     * Get the ObservableList of customers that is cached
     * @return the currently cached instances of customers
     */
    public ObservableList<CustomerInstance> getCustomers() {
        return customers;
    }

    /**
     * A convenience function to update the list of customers from the database
     */
    public void updateCustomers() {
        this.customers.clear();
        this.customers.addAll(this.databaseConnection.getCustomers());
    }

    private DateTimeFormatter dateFormat;

    /**
     * Get the format used for full timedate values
     * @return the datetime format used for full date-times
     */
    public DateTimeFormatter getDateFormat() {
        return this.dateFormat;
    }

    private ObservableList<DivisionInstance> divisions;

    /**
     * Get the currently cached Divisions as an ObservableList
     * @return the currently cached Divisions list
     */
    public ObservableList<DivisionInstance> getDivisions() {
        return divisions;
    }

    /**
     * Convenience function to have the database update the cached divisions
     */
    public void updateDivisions() {
        this.divisions.clear();
        this.divisions.addAll(this.databaseConnection.getDivisions());
    }

    private final ObservableList<CountryInstance> countries;

    /**
     * Get the currently cached Countries as an ObservableList
     * @return the currently cached Countries list
     */
    public ObservableList<CountryInstance> getCountries() {
        return this.countries;
    }

    /**
     * Convenience function to have the database update the cached Countries
     */
    public void updateCountries() {
        this.countries.clear();
        this.countries.addAll(this.databaseConnection.getCountries());
    }

    /**
     * Create a new AppState object, with all empty fields
     */
    public AppState() {
        this.userId = null;
        this.username = null;
        this.userHasLoggedIn = false;
        this.currentLocale = null;
        this.currentZone = null;
        this.databaseConnection = new JDBC();
        this.appointments = FXCollections.observableArrayList();
        this.contacts = FXCollections.observableArrayList();
        this.customers = FXCollections.observableArrayList();
        this.divisions = FXCollections.observableArrayList();
        this.countries = FXCollections.observableArrayList();
        this.dateFormat = DateTimeFormatter.ofPattern("hh:mm a MM/dd/yy");
    }

}
