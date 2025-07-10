package com.example.spendwise.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.spendwise.R;
import com.example.spendwise.models.Expense;

import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private List<Expense> expenseList;

    public ExpenseAdapter(List<Expense> expenseList) {
        this.expenseList = expenseList;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense_record, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenseList.get(position);
        holder.category.setText(expense.getCategory());
        holder.note.setText(expense.getNote());
        holder.date.setText(expense.getDate());
        holder.amount.setText("₹" + expense.getAmount());
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView category, note, date, amount;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.text_category);
            note = itemView.findViewById(R.id.text_note);
            date = itemView.findViewById(R.id.text_date);
            amount = itemView.findViewById(R.id.text_amount);
        }
    }
}
