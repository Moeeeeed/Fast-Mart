package com.example.a2.models;

public class Order {
    private String buyerId;
    private String summary;
    private double totalPrice;
    private long timestamp;

    // Empty constructor for Firebase
    public Order() {
    }

    public Order(String buyerId, String summary, double totalPrice, long timestamp) {
        this.buyerId = buyerId;
        this.summary = summary;
        this.totalPrice = totalPrice;
        this.timestamp = timestamp;
    }

    // Getters and Setters using camelCase
    public String getBuyerId() { return buyerId; }
    public void setBuyerId(String buyerId) { this.buyerId = buyerId; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
