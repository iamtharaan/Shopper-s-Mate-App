package com.smallacademy.userroles;

public class PaymentHistoryModel {
    private String date;
    private String time;
    private double amount;

    public PaymentHistoryModel(String date, String time, double amount) {
        this.date = date;
        this.time = time;
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public double getAmount() {
        return amount;
    }
}