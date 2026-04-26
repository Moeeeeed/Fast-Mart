package com.example.a2.models;

public class product {
    private int id;
    private String name;
    private String price;
    private String description;
    private int imageResId; 
    private String imageUrl; // Added for Bonus marks
    private boolean isFavourite;

    // Required empty constructor for Firebase
    public product() {
    }

    // Constructor for local resources (original)
    public product(int id, String name, String price, String description, int imageResId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.imageResId = imageResId;
        this.isFavourite = false; 
    }

    // Constructor for Firebase URLs (Bonus)
    public product(int id, String name, String price, String description, String imageUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
        this.isFavourite = false;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getPrice() { return price; }
    public String getDescription() { return description; }
    public int getImageResId() { return imageResId; }
    public String getImageUrl() { return imageUrl; }
    public boolean isFavourite() { return isFavourite; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPrice(String price) { this.price = price; }
    public void setDescription(String description) { this.description = description; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setFavourite(boolean favourite) { isFavourite = favourite; }
}
