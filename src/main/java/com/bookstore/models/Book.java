package com.bookstore.models;

import java.util.Objects;

public class Book {
    private Integer id;
    private String title;
    private Integer authorId;
    private String isbn;
    private Integer publicationYear;
    private Double price;
    private Integer stock;

    // Default constructor (required for JAX-RS JSON deserialization)
    public Book() {}

    // Parameterized constructor (initializes all attributes)
    public Book(Integer id, String title, Integer authorId, String isbn, 
                Integer publicationYear, Double price, Integer stock) {
        this.id = id;
        this.title = title;
        this.authorId = authorId;
        this.isbn = isbn;
        this.publicationYear = publicationYear;
        this.price = price;
        this.stock = stock;
    }

    // Getters and Setters for all attributes
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Integer getAuthorId() { return authorId; }
    public void setAuthorId(Integer authorId) { this.authorId = authorId; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public Integer getPublicationYear() { return publicationYear; }
    public void setPublicationYear(Integer publicationYear) { 
        this.publicationYear = publicationYear; 
    }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    // Optional: Override equals and hashCode for data integrity
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(id, book.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}