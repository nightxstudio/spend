package com.example.spendwise.models;

public class AchievementModel {
    private final int iconResId;
    private final String title;
    private final String subtitle;
    private final int points;
    private final boolean isEarned;
    private final String dateEarned;

    public AchievementModel(int iconResId, String title, String subtitle, int points, boolean isEarned, String dateEarned) {
        this.iconResId = iconResId;
        this.title = title;
        this.subtitle = subtitle;
        this.points = points;
        this.isEarned = isEarned;
        this.dateEarned = dateEarned;
    }

    public int getIconResId() { return iconResId; }
    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public int getPoints() { return points; }
    public boolean isEarned() { return isEarned; }
    public String getDateEarned() { return dateEarned; }
}
