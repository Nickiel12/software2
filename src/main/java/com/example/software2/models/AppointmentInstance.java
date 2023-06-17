package com.example.software2.models;

import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * The object that represents an Appointment row in the database
 * @author Nicholas Young
 */
public class AppointmentInstance {
    private final Integer id;

    /**
     * Get the persistent appointment id
     * @return the persistent primary key for this appointment
     */
    public Integer getId() {
        return this.id;
    }

    private String title;

    /**
     * Update the title of the appointment
     * @param newTitle the new appointment title
     */
    public void setTitle(String newTitle) {
        this.title = newTitle;
    }

    /**
     * Get the appointment's title
     * @return the appointment title
     */
    public String getTitle() {
        return title;
    }

    private String description;

    /**
     * Update the appointment's description
     * @param newDescription the new appointment description
     */
    public void setDescription(String newDescription) {
        description = newDescription;
    }

    /**
     * Get the appointment's description
     * @return the appointment description
     */
    public String getDescription() {
        return description;
    }

    private String location;

    /**
     * Update the appointment location
     * @param newLocation the new appointment location
     */
    public void setLocation(String newLocation) {
        this.location = newLocation;
    }

    /**
     * Get the appointment's location
     * @return the appointment location
     */
    public String getLocation() {
        return location;
    }

    private Integer contactId;

    /**
     * Set a new contact ID. This ID is assumed to be a valid
     * foreign key to the Contacts table.
     * @param newContactId the new contact entry's ID
     */
    public void setContactId(Integer newContactId) {
        this.contactId = newContactId;
    }

    /**
     * Get the contact ID. It is assumed that this is a valid foreign
     * key for Contacts
     * @return the associated contact id
     */
    public Integer getContactId() {
        return contactId;
    }

    private String type;

    /**
     * Update the appointment type
     * @param type the new appointment type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Get the appointment type
     * @return the appointment type
     */
    public String getType() {
        return type;
    }

    private ZonedDateTime startTime;

    /**
     * Get the appointment's start date and time in UTC
     * @return the appointment's start time in UTC
     */
    public ZonedDateTime getStartTime() {
        return startTime;
    }

    /**
     * Update the appointment's start time and date. Use ZoneOffset.UTC
     * @param startTime the appointment's new start time and date. Assumed to be ZoneOffset.UTC timezone
     */
    public void setStartTime(ZonedDateTime startTime) {
        this.startTime = startTime;
    }

    /**
     * Get the startTime in the configured application timezone
     */
    public ZonedDateTime getStartTimeLocal() {
        return this.startTime.withZoneSameInstant(this.localTime);
    }

    private ZonedDateTime endTime;

    /**
     * Get the appointment's end date and time in UTC
     * @return the appointment's end time in UTC
     */
    public ZonedDateTime getEndTime() {
        return endTime;
    }

    /**
     * Update the appointment's end time and date. Use ZoneOffset.UTC
     * @param endTime the appointment's new end time and date. Assumed to be ZoneOffset.UTC timezone
     */
    public void setEndTime(ZonedDateTime endTime) {
        this.endTime = endTime;
    }

    /**
     * Get the endTime in the configured application timezone
     */
    public ZonedDateTime getEndTimeLocal() {
        return this.endTime.withZoneSameInstant(this.localTime);
    }
    private Integer customerId;

    /**
     * Get the appointment's associated customer foreign key id
     * @return foreign key associated customer id
     */
    public Integer getCustomerId() {
        return customerId;
    }

    /**
     * Update the appointment's customer. Assumed to be a valid foreign key for Customers
     * @param customerId the new customer id
     */
    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    private Integer userId;

    /**
     * Get the appointment's user ID, aka the appointment's 'owner'
     * @return the scheduled user's id
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * Update the appointment's associated user ID. Assumed to be a valid foreign key.
     * @param userId the new foreign key for the appointment's user
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public ZoneId localTime;

    /**
     * Set the configured application timezone
     * @param localTime the new timezone
     */
    public void setLocalTime(ZoneId localTime) {
        this.localTime = localTime;
    }

    /**
     * Create a new appointment instance
     * @param id the constant unique primary key in the database
     * @param title the appointment's title
     * @param description the appointment's description
     * @param location the appointment's location
     * @param contactId the associated contact's unique Id
     * @param type the appointment type
     * @param startTime the appointment's start date and time in UTC
     * @param endTime the appointment's end date and time in UTC
     * @param customerId the associated customer's unique ID
     * @param userId the appointment's associated user's unique ID
     */
    public AppointmentInstance(Integer id, String title, String description, String location, Integer contactId,
                               String type, ZonedDateTime startTime, ZonedDateTime endTime, Integer customerId,
                               Integer userId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.contactId = contactId;
        this.type = type;
        this.startTime = startTime;
        this.endTime = endTime;
        this.customerId = customerId;
        this.userId = userId;
    }
}
