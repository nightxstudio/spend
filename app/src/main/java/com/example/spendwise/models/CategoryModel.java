package com.example.spendwise.models;

public class CategoryModel {
    private String name;
    private int iconResId;
    private boolean isCustom;

    public CategoryModel(String name, int iconResId, boolean isCustom) {
        this.name = name;
        this.iconResId = iconResId;
        this.isCustom = isCustom;
    }

    public String getName() {
        return name;
    }

    public int getIconResId() {
        return iconResId;
    }

    public boolean isCustom() {
        return isCustom;
    }
}
