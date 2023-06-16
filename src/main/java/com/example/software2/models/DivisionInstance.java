package com.example.software2.models;

/**
 * The data record object for read only rows in the Divisions database table
 * @param id the primary key of the Division
 * @param division the Division's human legible name
 * @param country_id the Division's parent countries' primary key
 */
public record DivisionInstance(Integer id, String division, Integer country_id) {

}
