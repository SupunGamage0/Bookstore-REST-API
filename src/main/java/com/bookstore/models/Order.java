package com.bookstore.models;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Order {
    private Integer id;
    private Integer customerId;
    private Map<Integer, Integer> items; // bookId -> quantity
    private Date orderDate;

    // Constructor
    public Order() {
        this.items = new HashMap<>();
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }
    public Map<Integer, Integer> getItems() { return items; }
    public void setItems(Map<Integer, Integer> items) { this.items = items; }
    public Date getOrderDate() { return orderDate; }
    public void setOrderDate(Date orderDate) { this.orderDate = orderDate; }
}