package com.example.spendwise.models;

public class Expense {
    private String category;
    private String note;
    private String date;
    private int amount;

    public Expense(String category, String note, String date, int amount) {
        this.category = category;
        this.note = note;
        this.date = date;
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public String getNote() {
        return note;
    }

    public String getDate() {
        return date;
    }

    public int getAmount() {
        return amount;
    }
}
