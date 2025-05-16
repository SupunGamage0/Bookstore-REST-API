package com.bookstore.models;

import java.util.HashMap;
import java.util.Map;

public class Cart {
    private Integer customerId; // Added to associate cart with a customer
    private Map<Integer, Integer> items = new HashMap<>(); // bookId -> quantity

    // Getters and Setters
    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }
    public Map<Integer, Integer> getItems() { return items; }
    public void addItem(Integer bookId, Integer quantity) { items.put(bookId, quantity); }
    public void removeItem(Integer bookId) { items.remove(bookId); }
    public void updateItem(Integer bookId, Integer quantity) { items.replace(bookId, quantity); }
}