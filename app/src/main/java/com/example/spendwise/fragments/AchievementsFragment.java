package com.example.spendwise.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spendwise.R;
import com.example.spendwise.adapters.AchievementsAdapter;
import com.example.spendwise.helpers.ExpenseStorageHelper;
import com.example.spendwise.models.AchievementModel;

import java.text.SimpleDateFormat;
import java.util.*;

public class AchievementsFragment extends Fragment {

    private RecyclerView recyclerView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_achievements, container, false);
        recyclerView = view.findViewById(R.id.recycler_achievements);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new AchievementsAdapter(generateAchievements()));
        return view;
    }

    private List<AchievementModel> generateAchievements() {
        List<AchievementModel> list = new ArrayList<>();
        Context context = getContext();
        ExpenseStorageHelper helper = new ExpenseStorageHelper();
        String today = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        int streakDays = helper.getLoggingStreak(context);
        int budgetDays = helper.getBudgetStreak(context, 300);
        int totalLogged = helper.getTotalExpenses(context);
        boolean usedAll = helper.usedAllCategories(context, getAllCategories());
        int savings = helper.getSavingsComparedToLastMonth(context);

        list.add(new AchievementModel(
                R.drawable.ic_trophy,
                "Daily Logger",
                "Logged " + streakDays + " days in a row",
                10,
                streakDays >= 1,
                streakDays >= 1 ? today : ""
        ));

        list.add(new AchievementModel(
                R.drawable.ic_trophy,
                "Budget Keeper",
                "Spent under ₹300 on " + budgetDays + " day(s)",
                10,
                budgetDays >= 1,
                budgetDays >= 1 ? today : ""
        ));

        list.add(new AchievementModel(
                R.drawable.ic_trophy,
                "Tracker Starter",
                "Logged " + totalLogged + " expenses",
                10,
                totalLogged >= 1,
                totalLogged >= 1 ? today : ""
        ));

        list.add(new AchievementModel(
                R.drawable.ic_trophy,
                "Category Master",
                "Used all categories once",
                15,
                usedAll,
                usedAll ? today : ""
        ));

        list.add(new AchievementModel(
                R.drawable.ic_trophy,
                "Smart Saver",
                "Saved ₹" + savings + " vs last month",
                15,
                savings > 0,
                savings > 0 ? today : ""
        ));

        // 🔁 Later: Add more here with version gating if needed

        return list;
    }

    private List<String> getAllCategories() {
        return Arrays.asList("Food", "Transport", "Entertainment", "Health", "Education", "Utilities", "Other");
    }
}
