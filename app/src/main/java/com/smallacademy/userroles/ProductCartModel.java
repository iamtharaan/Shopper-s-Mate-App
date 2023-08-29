package com.smallacademy.userroles;

public class ProductCartModel {
    private String name;
    private double price;
    private String barcode;
    private String description;
    private String category;
    private String imageUrl;
    private int quantity;

    public ProductCartModel() {
        // Default constructor required for Firebase database
    }

    public ProductCartModel(String name, double price, String barcode, String description, String category, String imageUrl, int quantity) {
        this.name = name;
        this.price = price;
        this.barcode = barcode;
        this.description = description;
        this.category = category;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
