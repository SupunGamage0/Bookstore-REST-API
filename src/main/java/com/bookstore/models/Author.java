package com.bookstore.models;

import java.util.Objects;

public class Author {
    private Integer id;
    private String firstName; // Changed from "name" to "firstName" and "lastName"
    private String lastName;
    private String biography;

    public Author() {}

    public Author(Integer id, String firstName, String lastName, String biography) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.biography = biography;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getBiography() { return biography; }
    public void setBiography(String biography) { this.biography = biography; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Author author = (Author) o;
        return Objects.equals(id, author.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}