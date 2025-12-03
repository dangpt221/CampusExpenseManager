package com.example.campusexpensesmanagermer.Models;

public class BudgetItem {
    private int id;
    private int budgetId;
    private String categoryName;
    private double allocatedAmount;
    private double spentAmount;
    private String createdAt;

    // Constructor rỗng
    public BudgetItem() {
        this.spentAmount = 0;
    }

    // Constructor đầy đủ
    public BudgetItem(int budgetId, String categoryName, double allocatedAmount) {
        this.budgetId = budgetId;
        this.categoryName = categoryName;
        this.allocatedAmount = allocatedAmount;
        this.spentAmount = 0;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getBudgetId() { return budgetId; }
    public void setBudgetId(int budgetId) { this.budgetId = budgetId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public double getAllocatedAmount() { return allocatedAmount; }
    public void setAllocatedAmount(double allocatedAmount) { this.allocatedAmount = allocatedAmount; }

    public double getSpentAmount() { return spentAmount; }
    public void setSpentAmount(double spentAmount) { this.spentAmount = spentAmount; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    // Helper methods
    public double getRemaining() {
        return allocatedAmount - spentAmount;
    }

    public int getProgressPercentage() {
        if (allocatedAmount == 0) return 0;
        return (int) ((spentAmount / allocatedAmount) * 100);
    }

    public boolean isExceeded() {
        return spentAmount > allocatedAmount;
    }

    @Override
    public String toString() {
        return "BudgetItem{" +
                "id=" + id +
                ", budgetId=" + budgetId +
                ", categoryName='" + categoryName + '\'' +
                ", allocatedAmount=" + allocatedAmount +
                ", spentAmount=" + spentAmount +
                '}';
    }
}