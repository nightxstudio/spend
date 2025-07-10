package com.example.spendwise.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spendwise.R;
import com.example.spendwise.models.CategoryModel;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Context context;
    private List<CategoryModel> categoryList;
    private CategoryClickListener listener;

    public interface CategoryClickListener {
        void onCategorySelected(String categoryName);
        void onAddCustomCategory();
    }

    public CategoryAdapter(Context context, List<CategoryModel> categoryList, CategoryClickListener listener) {
        this.context = context;
        this.categoryList = categoryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category_icon, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        CategoryModel category = categoryList.get(position);
        holder.label.setText(category.getName());
        holder.icon.setImageResource(category.getIconResId());

        holder.itemView.setOnClickListener(v -> {
            if (category.getName().equals("+ Custom")) {
                listener.onAddCustomCategory(); // Opens input dialog
            } else {
                listener.onCategorySelected(category.getName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView label;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.image_icon);
            label = itemView.findViewById(R.id.text_label);
        }
    }
}
