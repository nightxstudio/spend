package com.example.spendwise.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.spendwise.R;
import com.example.spendwise.helpers.ExpenseStorageHelper;
import com.example.spendwise.models.Expense;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.formatter.ValueFormatter;


import java.text.SimpleDateFormat;
import java.util.*;

public class AnalyticsFragment extends Fragment {

    private PieChart pieChart;
    private BarChart barChart;
    private Button btnDaily, btnWeekly;
    private LinearLayout categorySummaryContainer;

    public AnalyticsFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_analytics, container, false);

        pieChart = view.findViewById(R.id.pieChart);
        categorySummaryContainer = view.findViewById(R.id.category_summary_container);
        loadPieChartData();

        barChart = view.findViewById(R.id.barChart);
        btnDaily = view.findViewById(R.id.btn_daily);
        btnWeekly = view.findViewById(R.id.btn_weekly);

        btnDaily.setOnClickListener(v -> loadBarChartData(true));
        btnWeekly.setOnClickListener(v -> loadBarChartData(false));

        // Default view = Daily
        loadBarChartData(true);


        return view;
    }

    private void loadPieChartData() {
        List<Expense> expenses = ExpenseStorageHelper.getExpenses(getContext());

        String currentMonth = new SimpleDateFormat("MM-yyyy", Locale.getDefault())
                .format(Calendar.getInstance().getTime());

        Map<String, Integer> categoryTotals = new HashMap<>();
        int total = 0;

        for (Expense e : expenses) {
            String[] parts = e.getDate().split("-");
            if (parts.length == 3) {
                String monthYear = parts[1] + "-" + parts[2];
                if (monthYear.equals(currentMonth)) {
                    int amt = e.getAmount();
                    total += amt;
                    categoryTotals.put(e.getCategory(),
                            categoryTotals.getOrDefault(e.getCategory(), 0) + amt);
                }
            }
        }

        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : categoryTotals.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(14f);
        dataSet.setValueTextColor(Color.WHITE);

        PieData pieData = new PieData(dataSet);

        pieChart.setData(pieData);
        pieChart.setUsePercentValues(true);
        pieChart.setDrawEntryLabels(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.animateY(800);

        Legend legend = pieChart.getLegend();
        legend.setEnabled(false); // Hide built-in legend

        pieChart.invalidate();

        renderCategorySummary(categoryTotals);
    }

    private void loadBarChartData(boolean isDaily) {
        List<Expense> expenses = ExpenseStorageHelper.getExpenses(getContext());

        Map<String, Integer> dataMap = new LinkedHashMap<>();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf;

        if (isDaily) {
            sdf = new SimpleDateFormat("dd-MM", Locale.getDefault());
            for (Expense e : expenses) {
                String date = e.getDate();
                if (!dataMap.containsKey(date)) dataMap.put(date, 0);
                dataMap.put(date, dataMap.get(date) + e.getAmount());
            }
        } else {
            // Weekly: Week 1, Week 2...
            sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            for (Expense e : expenses) {
                try {
                    Date date = sdf.parse(e.getDate());
                    cal.setTime(date);
                    int week = cal.get(Calendar.WEEK_OF_MONTH);
                    String weekKey = "Week " + week;
                    if (!dataMap.containsKey(weekKey)) dataMap.put(weekKey, 0);
                    dataMap.put(weekKey, dataMap.get(weekKey) + e.getAmount());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        int index = 0;

        for (Map.Entry<String, Integer> entry : dataMap.entrySet()) {
            entries.add(new BarEntry(index, entry.getValue()));
            labels.add(entry.getKey());
            index++;
        }

        BarDataSet dataSet = new BarDataSet(entries, isDaily ? "Daily Spend" : "Weekly Spend");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);

        BarData barData = new BarData(dataSet);

        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getXAxis().setLabelRotationAngle(-45f); // Tilted labels
        barChart.getXAxis().setGranularity(1f); // Ensure one label per bar
        barChart.getXAxis().setGranularityEnabled(true);
        barChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= 0 && index < labels.size()) {
                    return labels.get(index);
                }
                return "";
            }
        });


        barChart.animateY(1000);
        barChart.invalidate();
    }


    private void renderCategorySummary(Map<String, Integer> data) {
        categorySummaryContainer.removeAllViews();

        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            TextView summary = new TextView(getContext());
            summary.setText("• " + entry.getKey() + ": ₹" + entry.getValue());
            summary.setTextSize(14f);
            summary.setTextColor(getResources().getColor(R.color.text_dark));
            summary.setPadding(0, 4, 0, 4);
            categorySummaryContainer.addView(summary);
        }
    }
}
