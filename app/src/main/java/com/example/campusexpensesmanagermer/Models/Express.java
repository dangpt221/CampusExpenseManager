package com.example.campusexpensesmanagermer.Models;

public class Express {
    private int id;
    private String title;
    private double amount;
    private String categoryName;
    private int userId;

    public Express() {
    }

    public Express(String title, double amount, String categoryName, int userId) {
        this.title = title;
        this.amount = amount;
        this.categoryName = categoryName;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
