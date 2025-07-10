package com.example.spendwise.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spendwise.R;
import com.example.spendwise.adapters.CategoryAdapter;
import com.example.spendwise.helpers.ExpenseStorageHelper;
import com.example.spendwise.models.CategoryModel;
import com.example.spendwise.models.Expense;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

public class AddExpenseFragment extends Fragment {

    private RecyclerView categoryRecycler;
    private EditText editAmount, editNote, editDate;
    private MaterialButton buttonAddExpense;

    private String selectedCategory = null;
    private final List<CategoryModel> categoryList = new ArrayList<>();
    private final String FILE_NAME = "categories.json";

    public AddExpenseFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_expense, container, false);

        categoryRecycler = view.findViewById(R.id.recycler_categories);
        editAmount = view.findViewById(R.id.edit_amount);
        editNote = view.findViewById(R.id.edit_note);
        editDate = view.findViewById(R.id.edit_date);
        buttonAddExpense = view.findViewById(R.id.button_add_expense);

        setupCategoryRecycler();
        setupDatePicker();
        setupButtonListener();

        return view;
    }

    private void setupCategoryRecycler() {
        categoryList.clear();

        // Predefined categories
        categoryList.add(new CategoryModel("Food", R.drawable.ic_food, false));
        categoryList.add(new CategoryModel("Travel", R.drawable.ic_travel, false));
        categoryList.add(new CategoryModel("Mobile", R.drawable.ic_mobile, false));
        categoryList.add(new CategoryModel("Bills", R.drawable.ic_bills, false));
        categoryList.add(new CategoryModel("Shopping", R.drawable.ic_shopping, false));

        // Load custom categories
        List<String> customNames = loadCustomCategories();
        for (String name : customNames) {
            categoryList.add(new CategoryModel(name, R.drawable.ic_custom_tag, true));
        }

        // + Add Custom category
        categoryList.add(new CategoryModel("+ Custom", R.drawable.ic_add_custom, true));

        CategoryAdapter adapter = new CategoryAdapter(getContext(), categoryList, new CategoryAdapter.CategoryClickListener() {
            @Override
            public void onCategorySelected(String categoryName) {
                selectedCategory = categoryName;
                Toast.makeText(getContext(), "Selected: " + categoryName, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAddCustomCategory() {
                promptAddCustomCategory();
            }
        });

        categoryRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoryRecycler.setAdapter(adapter);
    }

    private void promptAddCustomCategory() {
        EditText input = new EditText(getContext());
        input.setHint("Enter new category");
        input.setInputType(android.text.InputType.TYPE_CLASS_TEXT);

        new androidx.appcompat.app.AlertDialog.Builder(getContext())
                .setTitle("New Category")
                .setMessage("Create a new custom category")
                .setView(input)
                .setPositiveButton("Add", (dialog, which) -> {
                    String newCategory = input.getText().toString().trim();
                    if (!TextUtils.isEmpty(newCategory)) {
                        List<String> existing = loadCustomCategories();
                        if (existing.contains(newCategory)) {
                            Toast.makeText(getContext(), "Category already exists", Toast.LENGTH_SHORT).show();
                        } else {
                            saveCustomCategory(newCategory);
                            setupCategoryRecycler();
                            Toast.makeText(getContext(), "Added: " + newCategory, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void setupDatePicker() {
        editDate.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            new DatePickerDialog(getContext(), (view, y, m, d) -> {
                String dateStr = String.format("%02d-%02d-%d", d, m + 1, y);
                editDate.setText(dateStr);
            }, year, month, day).show();
        });
    }

    private void setupButtonListener() {
        buttonAddExpense.setOnClickListener(v -> {
            String amountStr = editAmount.getText().toString().trim();
            String noteStr = editNote.getText().toString().trim();
            String dateStr = editDate.getText().toString().trim();

            if (TextUtils.isEmpty(amountStr) || TextUtils.isEmpty(noteStr) || TextUtils.isEmpty(dateStr) || selectedCategory == null) {
                Toast.makeText(getContext(), "Please fill all fields and select a category", Toast.LENGTH_SHORT).show();
                return;
            }

            int amount;
            try {
                amount = Integer.parseInt(amountStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Enter a valid number for amount", Toast.LENGTH_SHORT).show();
                return;
            }

            Expense expense = new Expense(selectedCategory, noteStr, dateStr, amount);
            ExpenseStorageHelper.saveExpense(getContext(), expense);

            Toast.makeText(getContext(), "Expense saved!", Toast.LENGTH_SHORT).show();

            // Reset fields
            editAmount.setText("");
            editNote.setText("");
            editDate.setText("");
            selectedCategory = null;
            setupCategoryRecycler();
        });
    }

    private void saveCustomCategory(String name) {
        List<String> categories = loadCustomCategories();
        categories.add(name);

        JSONArray array = new JSONArray(new HashSet<>(categories)); // prevent duplicates
        try (FileOutputStream fos = requireContext().openFileOutput(FILE_NAME, Context.MODE_PRIVATE)) {
            fos.write(array.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> loadCustomCategories() {
        List<String> list = new ArrayList<>();
        try (FileInputStream fis = requireContext().openFileInput(FILE_NAME)) {
            byte[] bytes = new byte[fis.available()];
            fis.read(bytes);
            String json = new String(bytes);

            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                list.add(array.getString(i));
            }
        } catch (IOException | JSONException ignored) {}
        return list;
    }
}
