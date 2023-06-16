package com.example.software2.models;

/**
 * The data record object for read only rows in the Countries database table
 * @param id the country's primary ID
 * @param country the country's human legible name
 */
public record CountryInstance(Integer id, String country) {

}
