package com.example.spendwise.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.spendwise.R;
import com.example.spendwise.helpers.ExpenseStorageHelper;
import com.example.spendwise.models.Expense;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BudgetFragment extends Fragment {

    private TextView textLimit, textStats;
    private ProgressBar progressBar;
    private MaterialButton editButton;

    private int budgetLimit = 2000;
    private int totalSpent = 0;
    private final String BUDGET_FILE = "budget.json";

    public BudgetFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_budget, container, false);

        textLimit = view.findViewById(R.id.text_budget_limit);
        textStats = view.findViewById(R.id.text_budget_stats);
        progressBar = view.findViewById(R.id.progress_budget);
        editButton = view.findViewById(R.id.button_edit_budget);

        loadBudget();
        calculateSpent();
        updateUI();

        editButton.setOnClickListener(v -> showEditBudgetDialog());

        return view;
    }

    private void loadBudget() {
        try (FileInputStream fis = requireContext().openFileInput(BUDGET_FILE)) {
            byte[] data = new byte[fis.available()];
            fis.read(data);
            String json = new String(data);
            JSONObject obj = new JSONObject(json);
            budgetLimit = obj.getInt("limit");
        } catch (IOException | JSONException ignored) {}
    }

    private void saveBudget(int limit) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("limit", limit);
            try (FileOutputStream fos = requireContext().openFileOutput(BUDGET_FILE, Context.MODE_PRIVATE)) {
                fos.write(obj.toString().getBytes());
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    private void showEditBudgetDialog() {
        EditText input = new EditText(getContext());
        input.setHint("New limit (₹)");
        input.setInputType(InputType.TYPE_CLASS_NUMBER);

        new AlertDialog.Builder(getContext())
                .setTitle("Edit Budget Limit")
                .setMessage("Set your monthly budget")
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    String value = input.getText().toString().trim();
                    if (!TextUtils.isEmpty(value)) {
                        budgetLimit = Integer.parseInt(value);
                        saveBudget(budgetLimit);
                        updateUI();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void calculateSpent() {
        totalSpent = 0;
        List<Expense> expenses = ExpenseStorageHelper.getExpenses(getContext());
        String currentMonth = new SimpleDateFormat("MM-yyyy", Locale.getDefault())
                .format(Calendar.getInstance().getTime());

        for (Expense e : expenses) {
            String[] parts = e.getDate().split("-");
            if (parts.length == 3) {
                String monthYear = parts[1] + "-" + parts[2];
                if (monthYear.equals(currentMonth)) {
                    totalSpent += e.getAmount();
                }
            }
        }
    }

    private void updateUI() {
        textLimit.setText("₹" + budgetLimit);
        textStats.setText("₹" + totalSpent + " spent of ₹" + budgetLimit);

        int percent = budgetLimit == 0 ? 0 : (int) ((totalSpent * 100.0f) / budgetLimit);
        progressBar.setMax(100);
        progressBar.setProgress(Math.min(percent, 100));
    }
}
