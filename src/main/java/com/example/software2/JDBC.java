package com.example.software2;
import com.example.software2.models.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.*;

/**
 * The class object to handle the raw connection to the SQL database
 * @author Nicholas Young
 */
public class JDBC {
    private static final String protocol = "jdbc";
    private static final String vendor = ":mysql:";
    private static final String location = "//localhost/";
    private static final String databaseName = "client_schedule";
    private static final String jdbcUrl = protocol + vendor + location + databaseName + "?connectionTimeZone = SERVER"; // local
    private static final String driver = "com.mysql.jdbc.Driver";
    private static final String userName = "sqlUser";
    private static String password = "Passw0rd!";
    private static Connection connection = null;
    private static PreparedStatement preparedStatement;

    public static void makeConnection() {
        try {
            connection = DriverManager.getConnection(jdbcUrl, userName, password);
            //System.out.println("Connection Successful");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static Connection getConnection() { return connection; }

    public static void closeConnection() {
        try {
            connection.close();
            //System.out.println("Connection Closed");
        } catch (SQLException e ) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void makePreparedStatement(String sqlStatement, Connection conn) throws SQLException {
        if (conn != null) preparedStatement = conn.prepareStatement(sqlStatement);
        else System.out.println("Prepared Statement Creation Failed!");
    }

    public static PreparedStatement getPreparedStatement() throws SQLException {
        if (preparedStatement != null) return preparedStatement;
        else System.out.println("Null reference to Prepared Statement");
        return null;
    }

    /**
     * Validate the user credentials, and get the user's id
     * @param userName the entered username
     * @param password the entered user password
     * @return Either -1, for no user found with that combination, or the user's Id
     */
    public Integer validateUser(String userName, String password) {
        ResultSet rs = null;

        try {
            makeConnection();
            makePreparedStatement("SELECT COUNT(User_ID) AS HasAccount FROM client_schedule.users WHERE User_Name='" + userName + "' AND Password='" + password + "'", getConnection());
            rs = preparedStatement.executeQuery();

            while(rs.next()) {
                int has_account = rs.getInt("HasAccount");
                if (has_account == 1) {
                    return 1;
                }
            }
        } catch (SQLException e) {
            System.out.println("Sql Error: " + e.getMessage());
        } finally {
            closeConnection();
        }

        return -1;
    }

    /**
     * Get all of a user's appointments
     * @param userId the user's primary ID
     * @return a list of `AppointmentInstance`s that represent all of a user's appointments
     */
    public ObservableList<AppointmentInstance> getUserAppointments (Integer userId) {
        ObservableList<AppointmentInstance> return_list = FXCollections.observableArrayList();

        ResultSet rs = null;
        try {
            makeConnection();
            makePreparedStatement("SELECT Appointment_ID, Title, Description, Location, Type, Start, End, " +
                    "Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID " +
                    "FROM client_schedule.appointments WHERE User_ID=" + userId, getConnection());
            rs = preparedStatement.executeQuery();

            while(rs.next()) {
                Integer id = rs.getInt("Appointment_ID");
                String title = rs.getString("Title");
                String description = rs.getString("Description");
                String location = rs.getString("Location");
                String type = rs.getString("Type");
                Timestamp startTime = rs.getTimestamp("Start");
                Timestamp endTime = rs.getTimestamp("End");
                //Timestamp createTime = rs.getTimestamp("Create_Date");
                //String createBy = rs.getString("Create_By");
                //Timestamp last_update = rs.getTimestamp("Last_Update");
                //String last_update_by = rs.getString("Last_Updated_By");
                Integer customerId = rs.getInt("Customer_ID");
                Integer userID = rs.getInt("User_ID");
                Integer contactId = rs.getInt("Contact_ID");

                return_list.add(new AppointmentInstance(id, title, description, location, contactId, type,
                        startTime.toInstant().atZone(ZoneOffset.UTC), endTime.toInstant().atZone(ZoneOffset.UTC),
                        customerId, userID));

            }
        } catch (SQLException e) {
            System.out.println("Sql Error: " + e.getMessage());
        } finally {
            closeConnection();
        }

        System.out.println("database getting all user appointments");
        return return_list;
    }

    public void createAppointment(AppointmentInstance appointment, String userName) {
        try {
            makeConnection();
            makePreparedStatement("INSERT INTO client_schedule.appointments (Title, Description, Location, Type, Start, End, " +
                    "Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID) VALUES (" +
                    "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", getConnection());

            preparedStatement.setString(1, appointment.getTitle());
            preparedStatement.setString(2, appointment.getDescription());
            preparedStatement.setString(3, appointment.getLocation());
            preparedStatement.setString(4, appointment.getType());
            preparedStatement.setTimestamp(5, Timestamp.from(appointment.getStartTime().toInstant()));
            preparedStatement.setTimestamp(6, Timestamp.from(appointment.getEndTime().toInstant()));
            preparedStatement.setTimestamp(7, Timestamp.from(ZonedDateTime.now(ZoneOffset.UTC).toInstant()));
            preparedStatement.setString(8, userName);
            preparedStatement.setTimestamp(9, Timestamp.from(ZonedDateTime.now(ZoneOffset.UTC).toInstant()));
            preparedStatement.setString(10, userName);
            preparedStatement.setInt(11, appointment.getCustomerId());
            preparedStatement.setInt(12, appointment.getContactId());
            preparedStatement.setInt(13, appointment.getUserId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Sql Error: " + e.getMessage());
        } finally {
            closeConnection();
        }
        System.out.println("database update appointment");
    }

    public void editAppointment(AppointmentInstance appointment) {
        try {
            makeConnection();
            makePreparedStatement("UPDATE client_schedule.appointments SET Title=?, Description=?, Location=?," +
                    " Type=?, Start=?, End=?, " +
                    "Last_Update=?, Last_Updated_By=?, Customer_ID=?, Contact_ID=? WHERE Appointment_ID=?", getConnection());

            preparedStatement.setInt(11, appointment.getId());

            preparedStatement.setString(1, appointment.getTitle());
            preparedStatement.setString(2, appointment.getDescription());
            preparedStatement.setString(3, appointment.getLocation());
            preparedStatement.setString(4, appointment.getType());
            preparedStatement.setTimestamp(5, Timestamp.from(appointment.getStartTime().toInstant()));
            preparedStatement.setTimestamp(6, Timestamp.from(appointment.getEndTime().toInstant()));
            preparedStatement.setTimestamp(7, Timestamp.from(ZonedDateTime.now(ZoneOffset.UTC).toInstant()));
            preparedStatement.setString(8, userName);
            preparedStatement.setInt(9, appointment.getCustomerId());
            preparedStatement.setInt(10, appointment.getContactId());

            int rowsAffected = preparedStatement.executeUpdate();
            System.out.println("Update for id: '" + appointment.getId() + "' affected '" + rowsAffected + "' rows");
        } catch (SQLException e) {
            System.out.println("Sql Error: " + e.getMessage());
        } finally {
            closeConnection();
        }
        System.out.println("database update appointment");
    }

    public String deleteAppointment(AppointmentInstance appointment) {
        try {
            makeConnection();
            makePreparedStatement("DELETE FROM client_schedule.appointments WHERE Appointment_ID=?", getConnection());

            preparedStatement.setInt(1, appointment.getId());

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected + " appointment deleted";
        } catch (SQLException e) {
            System.out.println("Sql Error: " + e.getMessage());
        } finally {
            closeConnection();
        }
        return "Success";
    }

    public boolean appointmentConflictDuring(ZonedDateTime startTime, ZonedDateTime endTime, Integer customerID) {

        ResultSet rs = null;
        try {
            makeConnection();
            makePreparedStatement("SELECT COUNT(Appointment_ID) AS ConflictCount FROM client_schedule.appointments" +
                    " WHERE Customer_ID=? AND ((Start BETWEEN ? AND ?) OR (End BETWEEN ? AND ?))", getConnection());

            preparedStatement.setInt(1, customerID);
            preparedStatement.setTimestamp(2, Timestamp.from(startTime.toInstant()));
            preparedStatement.setTimestamp(4, Timestamp.from(startTime.toInstant()));
            preparedStatement.setTimestamp(3, Timestamp.from(endTime.toInstant()));
            preparedStatement.setTimestamp(5, Timestamp.from(endTime.toInstant()));

            rs = preparedStatement.executeQuery();

            while(rs.next()) {
                int count = rs.getInt("ConflictCount");
                if (count > 0) {
                    return true;
                }
            }

        } catch (SQLException e) {
            System.out.println("Sql Error: " + e.getMessage());
        } finally {
            closeConnection();
        }
        return false;
    }

    public ObservableList<CustomerInstance> getCustomers() {
        ObservableList<CustomerInstance> return_list = FXCollections.observableArrayList();

        ResultSet rs = null;
        try {
            makeConnection();
            makePreparedStatement("SELECT Customer_ID, Customer_Name, Address, Postal_Code, Phone, Create_Date," +
                    "Created_By, Last_Update, Last_Updated_By, Division_ID FROM client_schedule.customers", getConnection());
            rs = preparedStatement.executeQuery();

            while(rs.next()) {
                Integer id = rs.getInt("Customer_ID");
                String name = rs.getString("Customer_Name");
                String address = rs.getString("Address");
                String postalCode = rs.getString("Postal_Code");
                String phone = rs.getString("Phone");
                Timestamp createTime = rs.getTimestamp("Create_Date");
                String createBy = rs.getString("Created_By");
                Timestamp lastUpDate = rs.getTimestamp("Last_Update");
                String lastUpdateBy = rs.getString("Last_Updated_By");
                Integer divisionId = rs.getInt("Division_ID");

                return_list.add(new CustomerInstance(id, name, address, postalCode, phone, createTime.toInstant().atZone(ZoneOffset.UTC),
                        createBy, lastUpDate.toInstant().atZone(ZoneOffset.UTC), lastUpdateBy, divisionId));

            }
        } catch (SQLException e) {
            System.out.println("Sql Error: " + e.getMessage());
        } finally {
            closeConnection();
        }
        System.out.println("database getting all customers");
        return return_list;
    }

    public void createCustomer(CustomerInstance customer) {
        try {
            makeConnection();
            makePreparedStatement("INSERT INTO client_schedule.customers (Customer_Name, Address, Postal_Code, Phone, Create_Date," +
                    "Created_By, Last_Update, Last_Updated_By, Division_ID) VALUES (" +
                            " ?, ?, ?, ?, ?, ?, ?, ?, ?)", getConnection());


            preparedStatement.setString(1, customer.getCustomerName());
            preparedStatement.setString(2, customer.getAddress());
            preparedStatement.setString(3, customer.getPostalCode());
            preparedStatement.setString(4, customer.getPhoneNumber());
            preparedStatement.setTimestamp(5, Timestamp.from(ZonedDateTime.now(ZoneOffset.UTC).toInstant()));
            preparedStatement.setString(6, customer.getCreatedBy());
            preparedStatement.setTimestamp(7, Timestamp.from(customer.getLastUpdate().toInstant()));
            preparedStatement.setString(8, customer.getLastUpdatedBy());
            preparedStatement.setInt(9, customer.getDivisionId());

            int rowsAffected = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Sql Error: " + e.getMessage());
        } finally {
            closeConnection();
        }
        System.out.println("database create new customer");
    }

    public void editCustomer(CustomerInstance customer) {
        try {
            makeConnection();
            makePreparedStatement("UPDATE client_schedule.customers SET Customer_Name=?, Address=?, Postal_Code=?," +
                    " Phone=?, Last_Update=?, Last_Updated_By=?, Division_ID=? WHERE Customer_ID=?", getConnection());

            preparedStatement.setInt(8, customer.getId());

            preparedStatement.setString(1, customer.getCustomerName());
            preparedStatement.setString(2, customer.getAddress());
            preparedStatement.setString(3, customer.getPostalCode());
            preparedStatement.setString(4, customer.getPhoneNumber());
            preparedStatement.setTimestamp(5, Timestamp.from(customer.getLastUpdate().toInstant()));
            preparedStatement.setString(6, customer.getLastUpdatedBy());
            preparedStatement.setInt(7, customer.getDivisionId());

            int rowsAffected = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Sql Error: " + e.getMessage());
        } finally {
            closeConnection();
        }
        System.out.println("database update customer record");
    }

    public boolean tryDeleteCustomer(CustomerInstance customer) {
        System.out.println("database trying to delete customer");

        try {
            makeConnection();

            ResultSet rs = null;
            makePreparedStatement("SELECT COUNT(Appointment_ID) AS AppointmentCount FROM client_schedule.appointments WHERE Customer_ID=" + customer.getId(), getConnection());

            rs = preparedStatement.executeQuery();

            while(rs.next()) {
                int associatedCount = rs.getInt("AppointmentCount");
                if (associatedCount > 0) {
                    closeConnection();
                    return false;
                }
            }

            makePreparedStatement("DELETE FROM client_schedule.customers WHERE Customer_ID=?" + customer.getId(), getConnection());

            int rowsAffected = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Sql Error: " + e.getMessage());
        } finally {
            closeConnection();
        }

        return true;
    }

    public ObservableList<ContactInstance> getContacts() {
        ObservableList<ContactInstance> return_list = FXCollections.observableArrayList();

        ResultSet rs = null;
        try {
            makeConnection();
            makePreparedStatement("SELECT Contact_ID, Contact_Name, Email FROM client_schedule.contacts", getConnection());
            rs = preparedStatement.executeQuery();

            while(rs.next()) {

                int id = rs.getInt("Contact_ID");
                String name = rs.getString("Contact_Name");
                String email = rs.getString("Email");

                return_list.add(new ContactInstance(id, name, email));
            }
        } catch (SQLException e) {
            System.out.println("Sql Error: " + e.getMessage());
        } finally {
            closeConnection();
        }
        System.out.println("database getting all contacts");

        return return_list;
    }

    public ObservableList<DivisionInstance> getDivisions() {
        ObservableList<DivisionInstance> return_list = FXCollections.observableArrayList();

        ResultSet rs = null;
        try {
            makeConnection();
            makePreparedStatement("SELECT Division_ID, Division, Country_ID FROM client_schedule.first_level_divisions", getConnection());
            rs = preparedStatement.executeQuery();

            while(rs.next()) {
                int id = rs.getInt("Division_ID");
                String name = rs.getString("Division");
                int countryID = rs.getInt("Country_ID");

                return_list.add(new DivisionInstance(id, name, countryID));
            }
        } catch (SQLException e) {
            System.out.println("Sql Error: " + e.getMessage());
        } finally {
            closeConnection();
        }

        System.out.println("database getting all divisions");

        return return_list;
    }

    public ObservableList<CountryInstance> getCountries() {
        ObservableList<CountryInstance> return_list = FXCollections.observableArrayList();

        ResultSet rs = null;
        try {
            makeConnection();
            makePreparedStatement("SELECT Country_ID, Country FROM client_schedule.countries", getConnection());
            rs = preparedStatement.executeQuery();

            while(rs.next()) {
                int id = rs.getInt("Country_ID");
                String name = rs.getString("Country");

                return_list.add(new CountryInstance(id, name));
            }
        } catch (SQLException e) {
            System.out.println("Sql Error: " + e.getMessage());
        } finally {
            closeConnection();
        }

        System.out.println("database getting all countries");
        return return_list;
    }

    public ObservableList<Rpt_AppointmentTypePerMonthRow> Rpt_AppointmentsPerMonth(LocalDate filterStart, LocalDate filterEnd) {
        ObservableList<Rpt_AppointmentTypePerMonthRow> return_list = FXCollections.observableArrayList();

        ResultSet rs = null;
        try {
            makeConnection();
            makePreparedStatement("SELECT COUNT(Appointment_ID) AS AppointmentCount, DATE_FORMAT(Start, '%Y-%m') AS Month," +
                            "Type FROM client_schedule.appointments WHERE Start BETWEEN ? AND ? GROUP BY Month, Type", getConnection());

            preparedStatement.setTimestamp(1, Timestamp.from(filterStart.atStartOfDay().toInstant(ZoneOffset.UTC)));
            preparedStatement.setTimestamp(2, Timestamp.from(filterEnd.atStartOfDay().toInstant(ZoneOffset.UTC)));

            rs = preparedStatement.executeQuery();

            while(rs.next()) {
                int appointmentCount = rs.getInt("AppointmentCount");
                String month = rs.getString("Month");
                String appointmentType = rs.getString("Type");

                System.out.println(":" + appointmentType + ":");

                return_list.add(new Rpt_AppointmentTypePerMonthRow(appointmentCount, month, appointmentType));
            }
        } catch (SQLException e) {
            System.out.println("Sql Error: " + e.getMessage());
        } finally {
            closeConnection();
        }
        System.out.println("database getting appointments per month report");
        return return_list;
    }
}
