package com.example.software2.models;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * The object that represents a Customer object from the database.
 * @author Nicholas Young
 */
public class CustomerInstance {
    private final Integer id;

    /**
     * Get the customer's unique id
     * @return the customer's primary key
     */
    public Integer getId() {
        return id;
    }

    private String customerName;

    /**
     * Get the customer's name
     * @return the customer name
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Update the customer's name
     * @param customerName the new customer name
     */
    public void setCustomerName(String customerName, String editingUserName) {
        lastUpdatedBy = editingUserName;
        lastUpdate = ZonedDateTime.now(ZoneOffset.UTC);
        this.customerName = customerName;
    }

    private String address;

    /**
     * Get the customer's full address
     * @return the customer's full address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Update the customer's full address
     * @param address the customer's new address
     */
    public void setAddress(String address, String editingUserName) {
        lastUpdate = ZonedDateTime.now(ZoneOffset.UTC);
        lastUpdatedBy = editingUserName;
        this.address = address;
    }

    private String postalCode;

    /**
     * Get the customer's postal code
     * @return the customer's postal code
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Update the customer's postal code
     * @param postalCode the customer's new postal code
     */
    public void setPostalCode(String postalCode, String editingUserName) {
        lastUpdate = ZonedDateTime.now(ZoneOffset.UTC);
        lastUpdatedBy = editingUserName;
        this.postalCode = postalCode;
    }

    private String phoneNumber;

    /**
     * Get the customer's phone number
     * @return the customer's phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Update the customer's phone number
     * @param phoneNumber the customer's new phone number
     */
    public void setPhoneNumber(String phoneNumber, String editingUserName) {
        lastUpdate = ZonedDateTime.now(ZoneOffset.UTC);
        lastUpdatedBy = editingUserName;
        this.phoneNumber = phoneNumber;
    }

    private final ZonedDateTime createDate;

    /**
     * Get the customer record's create date, Read only
     * @return  the customer entry's creation date
     */
    public ZonedDateTime getCreateDate() {
        return createDate;
    }

    private String createdBy;

    /**
     * Get the customer record's creating user id
     * @return the user id that created this record
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the user that created the customer. Only works if the customer does
     * not already have a created user
     * @param userName the new username
     */
    public void setCreatedBy(String userName) {
        if (createdBy == null) {
            createdBy = userName;
        }
    }

    private ZonedDateTime lastUpdate;

    /**
     * Get the customer record's last update time in UTC
     * @return the customer record's last update time in UTC
     */
    public ZonedDateTime getLastUpdate() {
        return lastUpdate;
    }

    private String lastUpdatedBy;

    /**
     * Get the username of the last user to update this record
     * @return the username of the last user to update this record
     */
    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    private Integer divisionId;

    /**
     * Get the division id foreign key for this customer
     * @return the foreign key division id for this customer
     */
    public Integer getDivisionId() {
        return divisionId;
    }

    /**
     * Update the customer's division id. Assumed to be a valid primary key in first-level divisions
     * @param divisionId the new customer division id
     */
    public void setDivisionId(Integer divisionId, String editingUserName) {
        lastUpdate = ZonedDateTime.now(ZoneOffset.UTC);
        lastUpdatedBy = editingUserName;
        this.divisionId = divisionId;
    }

    /**
     * Create a new CustomerInstance to represent a customer entry in the database
     * @param id the customer's id
     * @param customerName the customer's name
     * @param address the customer's address
     * @param postalCode the customer's postal code
     * @param phoneNumber the customer's phone number
     * @param createDate the customer record's create date
     * @param createdBy the customer record's original creator
     * @param lastUpdate the customer record's last update date
     * @param lastUpdatedBy the last user to update the customer record
     * @param divisionId the customer's division Primary Key
     */
    public CustomerInstance(Integer id, String customerName, String address, String postalCode, String phoneNumber, ZonedDateTime createDate, String createdBy, ZonedDateTime lastUpdate, String lastUpdatedBy, Integer divisionId) {
        this.id = id;
        this.customerName = customerName;
        this.address = address;
        this.postalCode = postalCode;
        this.phoneNumber = phoneNumber;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdatedBy = lastUpdatedBy;
        this.divisionId = divisionId;
    }
}

