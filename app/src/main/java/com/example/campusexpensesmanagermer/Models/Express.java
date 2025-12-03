package com.example.campusexpensesmanagermer.Models;

public class Express {
    private int id;
    private String title;
    private double amount;
    private String categoryName;
    private int userId;
    private String date;
    private String note;
    private int status;

    // Constructor rỗng
    public Express() {
    }

    // Constructor với 4 tham số (được sử dụng)
    public Express(String title, double amount, String categoryName, int userId) {
        this.title = title;
        this.amount = amount;
        this.categoryName = categoryName;
        this.userId = userId;
        this.status = 1;
    }

    // Constructor với date
    public Express(String title, double amount, String categoryName, int userId, String date) {
        this.title = title;
        this.amount = amount;
        this.categoryName = categoryName;
        this.userId = userId;
        this.date = date;
        this.status = 1;
    }

    // Constructor đầy đủ
    public Express(int id, String title, double amount, String categoryName, int userId, String date, String note, int status) {
        this.id = id;
        this.title = title;
        this.amount = amount;
        this.categoryName = categoryName;
        this.userId = userId;
        this.date = date;
        this.note = note;
        this.status = status;
    }

    // Getters and Setters
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Express{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", amount=" + amount +
                ", categoryName='" + categoryName + '\'' +
                ", userId=" + userId +
                ", date='" + date + '\'' +
                ", note='" + note + '\'' +
                ", status=" + status +
                '}';
    }
}