package com.example.campusexpensesmanagermer.Models;

public class ExpenseReport {
    private String categoryName;
    private double totalAmount;
    private int transactionCount;
    private double percentage;

    public ExpenseReport() {
    }

    public ExpenseReport(String categoryName, double totalAmount, int transactionCount) {
        this.categoryName = categoryName;
        this.totalAmount = totalAmount;
        this.transactionCount = transactionCount;
    }

    // Getters and Setters
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(int transactionCount) {
        this.transactionCount = transactionCount;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }
}