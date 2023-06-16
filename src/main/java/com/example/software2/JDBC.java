package com.example.software2;
import com.example.software2.models.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * The class object to handle the raw connection to the SQL database
 * @author Nicholas Young
 */
public class JDBC {

    /**
     * Validate the user credentials, and get the user's id
     * @param userName the entered username
     * @param password the entered user password
     * @return Either -1, for no user found with that combination, or the user's Id
     */
    public Integer validateUser(String userName, String password) {
        // TODO database call to check if user exists and get the id

        System.out.println("database validating user");

        // set to -1 if no record exists
        return 1;
    }

    /**
     * Get all of a user's appointments
     * @param userId the user's primary ID
     * @return a list of `AppointmentInstance`s that represent all of a user's appointments
     */
    public ObservableList<AppointmentInstance> getUserAppointments (Integer userId) {
        ObservableList<AppointmentInstance> return_list = FXCollections.observableArrayList();

        System.out.println("database getting all user appointments");

        ZonedDateTime start = ZonedDateTime.now().plusMinutes(5).withZoneSameInstant(ZoneOffset.UTC).plusHours(0);
        ZonedDateTime end =  ZonedDateTime.now().plusMinutes(15).withZoneSameInstant(ZoneOffset.UTC).plusHours(2);
        return_list.add(new AppointmentInstance(5, "Test Appointment", "A test appointment", "Mt. Rainier",
                1, "Preliminary", start, end,
                1, 1));

        return return_list;
    }

    public void createAppointment(AppointmentInstance appointment) {
        // TODO database call to create a new appointment

        System.out.println("database create new appointment");
    }

    public void editAppointment(AppointmentInstance appointment) {
        //TODO database call to update an appointment

        System.out.println("database update appointment");
    }

    public String deleteAppointment(AppointmentInstance appointment) {
        // TODO database call to delete this appointment ID
        System.out.println("Pretend I made a database call to delete appointing: " + appointment.getId());
        return "Sucess";
    }

    public ObservableList<CustomerInstance> getCustomers() {
        // TODO database call to get all customers
        ObservableList<CustomerInstance> return_list = FXCollections.observableArrayList();

        System.out.println("database getting all customers");

        return_list.add(new CustomerInstance(1, "Roberta", "space", "-1", "3-3-3",
                ZonedDateTime.now(ZoneId.systemDefault()), "bob", ZonedDateTime.now(ZoneId.systemDefault()), "Nick",
                1));

        return return_list;
    }

    public void createCustomer(CustomerInstance customer) {
        // TODO database call to create new customer
        System.out.println("database create new customer");
    }

    public void editCustomer(CustomerInstance customer) {
        // TODO database call to update customer
        System.out.println("database update customer record");
    }

    public boolean tryDeleteCustomer(CustomerInstance customer) {
        // TODO database call to check if customer has any associated appointments
        boolean can_delete = false;
        System.out.println("database trying to delete customer");

        return can_delete;

    }

    public ObservableList<ContactInstance> getContacts() {
        // TODO database call to get all contacts

        System.out.println("database getting all contacts");

        ObservableList<ContactInstance> return_list = FXCollections.observableArrayList();
        return_list.add(new ContactInstance(1, "Bob", "bob@bob.com"));
        return return_list;
    }

    public ObservableList<DivisionInstance> getDivisions() {
        // TODO database call to get all divisions

        System.out.println("database getting all divisions");

        ObservableList<DivisionInstance> return_list = FXCollections.observableArrayList();
        return_list.add(new DivisionInstance(1, "Georgia", 1));
        return_list.add(new DivisionInstance(2, "Georgina", 2));
        return return_list;
    }

    public ObservableList<CountryInstance> getCountries() {
        // TODO database calls to get all countries

        System.out.println("database getting all countries");

        ObservableList<CountryInstance> return_list = FXCollections.observableArrayList();
        return_list.add(new CountryInstance(1, "Amurica"));
        return_list.add(new CountryInstance(2, "Canadur"));
        return return_list;
    }

    public ObservableList<Rpt_AppointmentTypePerMonthRow> Rpt_AppointmentsPerMonth(LocalDate filterStart, LocalDate filterEnd) {
        // TODO database call, grouped by month and type, returning count of appointment ids

        System.out.println("database getting appointments per month report");

        ObservableList<Rpt_AppointmentTypePerMonthRow> return_list = FXCollections.observableArrayList();
        return_list.add(new Rpt_AppointmentTypePerMonthRow(5, "July", "preliminary"));
        return return_list;
    }
}
