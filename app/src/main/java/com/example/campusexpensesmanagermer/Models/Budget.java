package com.example.campusexpensesmanagermer.Models;

public class Budget {
    private int id;
    private int userId;
    private String name;              // tên ngân sách
    private double money;             // target_amount
    private String description;       // note
    private int status;               // trạng thái
    private String createdAt;
    private String updatedAt;

    private int year;                 // năm
    private int month;                // tháng
    private double spent;             // tiền đã chi (tính toán từ expenses)

    // Constructor
    public Budget() {
        this.status = 1;
        this.spent = 0;
    }

    public Budget(int userId, String name, double money, int status) {
        this.userId = userId;
        this.name = name;
        this.money = money;
        this.status = status;
        this.spent = 0;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public double getSpent() {
        return spent;
    }

    public void setSpent(double spent) {
        this.spent = spent;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    // Helper methods
    public double getRemaining() {
        return money - spent;
    }

    public int getProgressPercentage() {
        if (money == 0) return 0;
        return (int) ((spent / money) * 100);
    }

    public boolean isExceeded() {
        return spent > money;
    }

    public double getAmount() {
        return money;
    }

    @Override
    public String toString() {
        return "Budget{" +
                "id=" + id +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", money=" + money +
                ", year=" + year +
                ", month=" + month +
                ", spent=" + spent +
                ", status=" + status +
                '}';
    }
}