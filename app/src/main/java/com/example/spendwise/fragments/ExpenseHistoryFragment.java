package com.example.spendwise.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spendwise.R;
import com.example.spendwise.adapters.ExpenseAdapter;
import com.example.spendwise.helpers.ExpenseStorageHelper;
import com.example.spendwise.models.Expense;

import java.util.Collections;
import java.util.List;

public class ExpenseHistoryFragment extends Fragment {

    public ExpenseHistoryFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expense_history, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_expenses);
        List<Expense> expenses = ExpenseStorageHelper.getExpenses(getContext());

        // Optional: Sort by date (latest first)
        Collections.reverse(expenses);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new ExpenseAdapter(expenses));

        return view;
    }
}

