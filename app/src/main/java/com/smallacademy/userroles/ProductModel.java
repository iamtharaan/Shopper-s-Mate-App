package com.smallacademy.userroles;

import java.io.Serializable;

public class ProductModel implements Serializable {

    private String key;
    private String name;
    private String barcode;
    private String category;
    private String imageUrl;
    private double price;
    private String description;

    // Default constructor required for Firebase
    public ProductModel() {
    }

    public ProductModel(String name, String barcode, String category, double price, String description, String imageUrl) {
        this.name = name;
        this.barcode = barcode;
        this.category = category;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}