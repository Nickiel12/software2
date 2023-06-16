package com.example.software2.models;

/**
 * The object that represents a read only Contact object from the database
 */
public record ContactInstance(Integer id, String contactName, String email) {
    /**
     * Get the contact's primary key id
     *
     * @return the contact's database primary key id
     */
    @Override
    public Integer id() {
        return id;
    }

    /**
     * Get the contact's name
     *
     * @return the contact's name
     */
    @Override
    public String contactName() {
        return contactName;
    }

    /**
     * Get the contact's email address
     *
     * @return the contact's email address
     */
    @Override
    public String email() {
        return email;
    }
}
