package com.smallacademy.userroles;

import android.os.Parcel;
import android.os.Parcelable;

public class CatProductModel implements Parcelable  {

    private String name;
    private String barcode;
    private String category;
    private String imageUrl;
    private double price;
    private String description;

    public CatProductModel() {
        // Default constructor required for Firebase
    }

    public CatProductModel(String name, String barcode, String category, double price, String description, String imageUrl) {
        this.name = name;
        this.barcode = barcode;
        this.category = category;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeDouble(price);
        dest.writeString(barcode);
        dest.writeString(description);
        dest.writeString(category);
        dest.writeString(imageUrl);
    }

    protected CatProductModel(Parcel in) {
        name = in.readString();
        price = in.readDouble();
        barcode = in.readString();
        description = in.readString();
        category = in.readString();
        imageUrl = in.readString();
    }

    public static final Creator<CatProductModel> CREATOR = new Creator<CatProductModel>() {
        @Override
        public CatProductModel createFromParcel(Parcel in) {
            return new CatProductModel(in);
        }

        @Override
        public CatProductModel[] newArray(int size) {
            return new CatProductModel[size];
        }
    };
}

