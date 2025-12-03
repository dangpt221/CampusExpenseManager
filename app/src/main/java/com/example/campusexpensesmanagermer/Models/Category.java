package com.example.campusexpensesmanagermer.Models;

public class Category {
    private int id;
    private int userId;
    private String name;
    private String icon;
    private String color;
    private int isDefault;
    private String createdAt;
    private String updatedAt;

    public Category() {}

    public Category(int userId, String name, String icon, String color, int isDefault) {
        this.userId = userId;
        this.name = name;
        this.icon = icon;
        this.color = color;
        this.isDefault = isDefault;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public int getIsDefault() { return isDefault; }
    public void setIsDefault(int isDefault) { this.isDefault = isDefault; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return name;
    } 
}