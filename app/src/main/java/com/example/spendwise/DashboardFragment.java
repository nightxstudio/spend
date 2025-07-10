package com.example.spendwise;

import android.os.Bundle;
import android.view.*;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spendwise.adapters.ExpenseAdapter;
import com.example.spendwise.helpers.ExpenseStorageHelper;
import com.example.spendwise.models.Expense;

import java.text.SimpleDateFormat;
import java.util.*;

public class DashboardFragment extends Fragment {

    private RecyclerView recentExpensesRecycler;

    public DashboardFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Initialize Views
        recentExpensesRecycler = view.findViewById(R.id.recycler_recent_expenses);
        recentExpensesRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        // Load Data
        loadRecentExpenses();
        updateQuickStats(view);

        return view;
    }

    private void loadRecentExpenses() {
        List<Expense> expenses = ExpenseStorageHelper.getExpenses(getContext());

        // Sort by date (latest first)
        Collections.sort(expenses, (e1, e2) -> {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                Date d1 = sdf.parse(e1.getDate());
                Date d2 = sdf.parse(e2.getDate());
                return d2.compareTo(d1);
            } catch (Exception e) {
                return 0;
            }
        });

        // Limit to latest 5 expenses (optional)
        if (expenses.size() > 5) {
            expenses = expenses.subList(0, 5);
        }

        ExpenseAdapter adapter = new ExpenseAdapter(expenses);
        recentExpensesRecycler.setAdapter(adapter);
    }

    private void updateQuickStats(View view) {
        List<Expense> expenses = ExpenseStorageHelper.getExpenses(getContext());

        int todayTotal = 0, thisWeekTotal = 0, lastWeekTotal = 0;

        Calendar now = Calendar.getInstance();
        Calendar startOfWeek = (Calendar) now.clone();
        startOfWeek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        Calendar startOfLastWeek = (Calendar) startOfWeek.clone();
        startOfLastWeek.add(Calendar.WEEK_OF_YEAR, -1);

        Calendar endOfLastWeek = (Calendar) startOfWeek.clone();
        endOfLastWeek.add(Calendar.DAY_OF_YEAR, -1);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        for (Expense expense : expenses) {
            try {
                Date expenseDate = sdf.parse(expense.getDate());
                Calendar expCal = Calendar.getInstance();
                expCal.setTime(expenseDate);

                if (isSameDay(expCal, now)) {
                    todayTotal += expense.getAmount();
                }

                if (!expCal.before(startOfWeek) && !expCal.after(now)) {
                    thisWeekTotal += expense.getAmount();
                }

                if (!expCal.before(startOfLastWeek) && !expCal.after(endOfLastWeek)) {
                    lastWeekTotal += expense.getAmount();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Set values to quick stats views
        ((TextView) view.findViewById(R.id.text_today_amount)).setText("₹" + todayTotal);
        ((TextView) view.findViewById(R.id.text_week_amount)).setText("₹" + thisWeekTotal);
        ((TextView) view.findViewById(R.id.text_last_week_amount)).setText("₹" + lastWeekTotal);
    }

    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }
}
