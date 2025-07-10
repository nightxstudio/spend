package com.example.spendwise.helpers;

import android.content.Context;

import com.example.spendwise.models.Expense;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExpenseStorageHelper {

    private static final String FILE_NAME = "expenses.json";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

    // -------------------------------------------------------------
    // 📁 Used in Expense entry feature (e.g., Add/Edit Expense screen)
    // -------------------------------------------------------------

    // Save a new expense to internal storage
    public static void saveExpense(Context context, Expense expense) {
        List<Expense> currentExpenses = getExpenses(context);
        currentExpenses.add(expense);

        JSONArray jsonArray = new JSONArray();
        for (Expense e : currentExpenses) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("category", e.getCategory());
                obj.put("note", e.getNote());
                obj.put("date", e.getDate());
                obj.put("amount", e.getAmount());
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
            jsonArray.put(obj);
        }

        try (FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE)) {
            fos.write(jsonArray.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load all expenses from local storage
    public static List<Expense> getExpenses(Context context) {
        List<Expense> expenseList = new ArrayList<>();
        try (FileInputStream fis = context.openFileInput(FILE_NAME)) {
            byte[] data = new byte[fis.available()];
            fis.read(data);
            String json = new String(data);

            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                Expense e = new Expense(
                        obj.getString("category"),
                        obj.getString("note"),
                        obj.getString("date"),
                        obj.getInt("amount")
                );
                expenseList.add(e);
            }
        } catch (IOException | JSONException ignored) {}
        return expenseList;
    }

    // -------------------------------------------------------------
    // 🏆 Used in AchievementsFragment (logic to determine achievements)
    // -------------------------------------------------------------

    // Total number of expenses logged
    public static int getTotalExpenses(Context context) {
        return getExpenses(context).size();
    }

    // Calculates daily logging streak (e.g., for Daily Logger)
    public static int getLoggingStreak(Context context) {
        List<String> dates = new ArrayList<>();
        for (Expense e : getExpenses(context)) {
            if (!dates.contains(e.getDate())) {
                dates.add(e.getDate());
            }
        }

        // Sort by newest date first
        dates.sort((d1, d2) -> {
            try {
                return sdf.parse(d2).compareTo(sdf.parse(d1));
            } catch (ParseException e) {
                return 0;
            }
        });

        int streak = 1;
        Calendar cal = Calendar.getInstance();

        for (int i = 0; i < dates.size() - 1; i++) {
            try {
                cal.setTime(sdf.parse(dates.get(i)));
                cal.add(Calendar.DATE, -1);
                String expectedPrev = sdf.format(cal.getTime());

                if (expectedPrev.equals(dates.get(i + 1))) {
                    streak++;
                } else {
                    break;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return streak;
    }

    // Calculates number of days user stayed under budget
    public static int getBudgetStreak(Context context, int budgetPerDay) {
        Map<String, Integer> dailyTotal = new HashMap<>();
        for (Expense e : getExpenses(context)) {
            String date = e.getDate();
            dailyTotal.put(date, dailyTotal.getOrDefault(date, 0) + e.getAmount());
        }

        int count = 0;
        for (int amount : dailyTotal.values()) {
            if (amount <= budgetPerDay) count++;
        }
        return count;
    }

    // Checks if user used all categories at least once
    public static boolean usedAllCategories(Context context, List<String> allCategories) {
        Set<String> used = new HashSet<>();
        for (Expense e : getExpenses(context)) {
            used.add(e.getCategory());
        }
        return used.containsAll(allCategories);
    }

    // Compare total spending this month vs last month
    public static int getSavingsComparedToLastMonth(Context context) {
        Calendar cal = Calendar.getInstance();
        int currentMonth = cal.get(Calendar.MONTH);
        int currentYear = cal.get(Calendar.YEAR);

        cal.add(Calendar.MONTH, -1);
        int lastMonth = cal.get(Calendar.MONTH);
        int lastYear = cal.get(Calendar.YEAR);

        int currentTotal = getExpensesForMonth(context, currentMonth, currentYear);
        int lastTotal = getExpensesForMonth(context, lastMonth, lastYear);

        return Math.max(0, lastTotal - currentTotal);  // No negative savings
    }

    // Helper: Total expenses in a specific month/year
    private static int getExpensesForMonth(Context context, int month, int year) {
        int total = 0;
        for (Expense e : getExpenses(context)) {
            try {
                Date d = sdf.parse(e.getDate());
                Calendar cal = Calendar.getInstance();
                cal.setTime(d);
                if (cal.get(Calendar.MONTH) == month && cal.get(Calendar.YEAR) == year) {
                    total += e.getAmount();
                }
            } catch (ParseException ignored) {}
        }
        return total;
    }
}
