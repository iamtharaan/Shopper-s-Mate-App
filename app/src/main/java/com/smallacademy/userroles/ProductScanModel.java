package com.smallacademy.userroles;

import android.os.Parcel;
import android.os.Parcelable;

public class ProductScanModel implements Parcelable {
    private String barcode;
    private String category;
    private String description;
    private String imageUrl;
    private String name;
    private double price;

    public ProductScanModel() {
        // Default constructor
    }

    public ProductScanModel(String barcode, String category, String description, String imageUrl, String name, double price) {
        this.barcode = barcode;
        this.category = category;
        this.description = description;
        this.imageUrl = imageUrl;
        this.name = name;
        this.price = price;
    }

    protected ProductScanModel(Parcel in) {
        barcode = in.readString();
        category = in.readString();
        description = in.readString();
        imageUrl = in.readString();
        name = in.readString();
        price = in.readDouble();
    }

    public static final Creator<ProductScanModel> CREATOR = new Creator<ProductScanModel>() {
        @Override
        public ProductScanModel createFromParcel(Parcel in) {
            return new ProductScanModel(in);
        }

        @Override
        public ProductScanModel[] newArray(int size) {
            return new ProductScanModel[size];
        }
    };

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(barcode);
        dest.writeString(category);
        dest.writeString(description);
        dest.writeString(imageUrl);
        dest.writeString(name);
        dest.writeDouble(price);
    }
}
