package com.example.newbeemall.model;

public class CategoryInfo {
    private int categoryId;
    private String categoryName;
    private int categoryLevel;
    private int parentId;
    private String categoryIcon;

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public int getCategoryLevel() { return categoryLevel; }
    public void setCategoryLevel(int categoryLevel) { this.categoryLevel = categoryLevel; }
    public int getParentId() { return parentId; }
    public void setParentId(int parentId) { this.parentId = parentId; }
    public String getCategoryIcon() { return categoryIcon; }
    public void setCategoryIcon(String categoryIcon) { this.categoryIcon = categoryIcon; }
}
