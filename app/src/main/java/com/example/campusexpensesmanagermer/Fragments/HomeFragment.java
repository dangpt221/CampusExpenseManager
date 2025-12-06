package com.example.campusexpensesmanagermer.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusexpensesmanagermer.Activities.Expenses.AddExpressActivity;
import com.example.campusexpensesmanagermer.Adapters.ExpressAdapter;
import com.example.campusexpensesmanagermer.Models.Budget;
import com.example.campusexpensesmanagermer.Models.ExpenseReport;
import com.example.campusexpensesmanagermer.Models.Express;
import com.example.campusexpensesmanagermer.R;
import com.example.campusexpensesmanagermer.Repositories.BudgetRepository;
import com.example.campusexpensesmanagermer.Repositories.ExpressRepository;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private static final String PREFS_NAME = "CampusExpensesPrefs";

    // Views
    private TextView tvWelcome;
    private TextView tvTotalBudget;
    private TextView tvSpent;
    private TextView tvBalance;
    private RecyclerView rvRecentExpenses;
    private FloatingActionButton fabAddExpense;
    private PieChart pieChart;
    private BarChart barChart;
    private MaterialButton btnPieChart;
    private MaterialButton btnBarChart;

    // Data
    private ExpressRepository expressRepository;
    private BudgetRepository budgetRepository;
    private ExpressAdapter adapter;
    private int userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        getUserId();
        initRepositories();
        loadData();
        setupRecyclerView();
        setupCharts();
        setupFab();
    }

    private void initViews(View view) {
        tvWelcome = view.findViewById(R.id.tv_welcome);
        tvTotalBudget = view.findViewById(R.id.tv_total_budget);
        tvSpent = view.findViewById(R.id.tv_spent);
        tvBalance = view.findViewById(R.id.tv_balance);
        rvRecentExpenses = view.findViewById(R.id.rv_recent_expenses);
        fabAddExpense = view.findViewById(R.id.fab_add_expense);
        pieChart = view.findViewById(R.id.pie_chart);
        barChart = view.findViewById(R.id.bar_chart);
        btnPieChart = view.findViewById(R.id.btn_pie_chart);
        btnBarChart = view.findViewById(R.id.btn_bar_chart);
    }

    private void getUserId() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, requireContext().MODE_PRIVATE);
        userId = prefs.getInt("ID_USER", 0);
        String username = prefs.getString("username", "User");

        // Set welcome message
        if (tvWelcome != null) {
            tvWelcome.setText("Hello, " + username + " 👋");
        }

        Log.d(TAG, "UserId: " + userId + ", Username: " + username);
    }

    private void initRepositories() {
        expressRepository = new ExpressRepository(requireContext());
        budgetRepository = new BudgetRepository(requireContext());
    }

    private void loadData() {
        try {
            if (userId <= 0) {
                Log.e(TAG, "Invalid userId: " + userId);
                return;
            }

            // Get current month and year
            Calendar calendar = Calendar.getInstance();
            int currentMonth = calendar.get(Calendar.MONTH) + 1;
            int currentYear = calendar.get(Calendar.YEAR);

            Log.d(TAG, "Loading data for month: " + currentMonth + "/" + currentYear);

            // Load budget
            Budget budget = budgetRepository.getBudgetByUserAndMonth(userId, currentMonth, currentYear);

            double totalBudget = 0;
            double spent = 0;

            if (budget != null) {
                totalBudget = budget.getMoney();
                spent = budget.getSpent();
                Log.d(TAG, "Budget found: " + totalBudget + ", Spent: " + spent);
            } else {
                // If no budget, calculate total spent this month
                spent = calculateMonthlySpent();
                Log.d(TAG, "No budget found. Calculated spent: " + spent);
            }

            double balance = totalBudget - spent;

            // Update UI
            if (tvTotalBudget != null) {
                tvTotalBudget.setText(formatCurrency(totalBudget));
            }
            if (tvSpent != null) {
                tvSpent.setText(formatCurrency(spent));
            }
            if (tvBalance != null) {
                tvBalance.setText(formatCurrency(balance));

                // Change balance color based on value
                if (balance < 0) {
                    tvBalance.setTextColor(ContextCompat.getColor(requireContext(), R.color.error));
                } else {
                    tvBalance.setTextColor(ContextCompat.getColor(requireContext(), R.color.success));
                }
            }

            Log.d(TAG, "UI updated - Budget: " + totalBudget + ", Spent: " + spent + ", Balance: " + balance);
        } catch (Exception e) {
            Log.e(TAG, "Error loading data: " + e.getMessage());
            e.printStackTrace();
            if (getContext() != null) {
                Toast.makeText(requireContext(), "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private double calculateMonthlySpent() {
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentYear = calendar.get(Calendar.YEAR);

        // Get start and end date of current month
        String startDate = String.format(Locale.getDefault(), "%d-%02d-01 00:00:00", currentYear, currentMonth);

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        String endDate = String.format(Locale.getDefault(), "%d-%02d-%02d 23:59:59",
                currentYear, currentMonth, calendar.get(Calendar.DAY_OF_MONTH));

        return expressRepository.getTotalExpenseByPeriod(userId, startDate, endDate);
    }

    // Trong setupRecyclerView(), thêm:

    private void setupRecyclerView() {
        try {
            List<Express> allExpenses = expressRepository.getAllExpressByUser(userId);
            List<Express> recentExpenses = new ArrayList<>();

            if (allExpenses != null && !allExpenses.isEmpty()) {
                int limit = Math.min(5, allExpenses.size());
                for (int i = 0; i < limit; i++) {
                    recentExpenses.add(allExpenses.get(i));
                }
            }

            adapter = new ExpressAdapter(requireContext(), recentExpenses);

            // ✨ NEW: Set listener
            adapter.setOnExpenseChangeListener(() -> {
                // Reload home data
                loadData();
                setupRecyclerView();
            });

            if (rvRecentExpenses != null) {
                rvRecentExpenses.setLayoutManager(new LinearLayoutManager(requireContext()));
                rvRecentExpenses.setAdapter(adapter);
                rvRecentExpenses.setNestedScrollingEnabled(false);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up RecyclerView: " + e.getMessage());
        }
    }

    private void setupFab() {
        if (fabAddExpense != null) {
            fabAddExpense.setOnClickListener(v -> {
                Intent intent = new Intent(requireContext(), AddExpressActivity.class);
                startActivity(intent);
            });
        }
    }

    private String formatCurrency(double amount) {
        return String.format(Locale.getDefault(), "%.0f ₫", amount);
    }

    private void setupCharts() {
        // Setup chart toggle buttons
        if (btnPieChart != null && btnBarChart != null) {
            btnPieChart.setOnClickListener(v -> {
                pieChart.setVisibility(View.VISIBLE);
                barChart.setVisibility(View.GONE);
            });

            btnBarChart.setOnClickListener(v -> {
                pieChart.setVisibility(View.GONE);
                barChart.setVisibility(View.VISIBLE);
            });
        }

        // Load chart data
        loadChartData();
    }

    private void loadChartData() {
        try {
            if (userId <= 0) {
                Log.e(TAG, "Invalid userId for chart data: " + userId);
                showEmptyChart();
                return;
            }

            Calendar calendar = Calendar.getInstance();
            int currentMonth = calendar.get(Calendar.MONTH) + 1;
            int currentYear = calendar.get(Calendar.YEAR);

            String startDate = String.format(Locale.getDefault(), "%d-%02d-01 00:00:00", currentYear, currentMonth);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            String endDate = String.format(Locale.getDefault(), "%d-%02d-%02d 23:59:59",
                    currentYear, currentMonth, calendar.get(Calendar.DAY_OF_MONTH));

            Log.d(TAG, "Loading chart data from " + startDate + " to " + endDate);
            
            List<ExpenseReport> reports = expressRepository.getExpenseReportByCategory(userId, startDate, endDate);

            Log.d(TAG, "Chart data loaded: " + (reports != null ? reports.size() : 0) + " categories");
            
            if (reports != null && !reports.isEmpty()) {
                // Log each report for debugging
                for (ExpenseReport report : reports) {
                    Log.d(TAG, "Category: " + report.getCategoryName() + ", Amount: " + report.getTotalAmount());
                }
                setupPieChart(reports);
                setupBarChart(reports);
            } else {
                Log.w(TAG, "No chart data found for current month, showing empty chart");
                showEmptyChart();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading chart data: " + e.getMessage());
            e.printStackTrace();
            showEmptyChart();
        }
    }

    private void setupPieChart(List<ExpenseReport> reports) {
        if (pieChart == null) {
            Log.e(TAG, "PieChart view is null!");
            return;
        }

        try {
            ArrayList<PieEntry> entries = new ArrayList<>();
            ArrayList<Integer> colors = new ArrayList<>();

            // Predefined colors
            int[] categoryColors = {
                    Color.rgb(211, 174, 121), // #D3AE79 - Primary
                    Color.rgb(255, 152, 0),   // #FF9800 - Orange
                    Color.rgb(76, 175, 80),   // #4CAF50 - Green
                    Color.rgb(33, 150, 243),  // #2196F3 - Blue
                    Color.rgb(156, 39, 176),  // #9C27B0 - Purple
                    Color.rgb(255, 193, 7),   // #FFC107 - Amber
                    Color.rgb(0, 150, 136),   // #009688 - Teal
                    Color.rgb(244, 67, 54),   // #F44336 - Red
                    Color.rgb(96, 125, 139)   // #607D8B - Blue Grey
            };

            double total = 0;
            for (ExpenseReport report : reports) {
                total += report.getTotalAmount();
            }

            for (int i = 0; i < reports.size(); i++) {
                ExpenseReport report = reports.get(i);
                if (report.getTotalAmount() > 0) {
                    entries.add(new PieEntry((float) report.getTotalAmount(), report.getCategoryName()));
                    colors.add(categoryColors[i % categoryColors.length]);
                }
            }

            if (entries.isEmpty()) {
                Log.w(TAG, "No valid entries for pie chart");
                showEmptyChart();
                return;
            }

            PieDataSet dataSet = new PieDataSet(entries, "");
            dataSet.setColors(colors);
            dataSet.setValueTextSize(12f);
            dataSet.setValueTextColor(Color.WHITE);
            dataSet.setSliceSpace(3f);
            dataSet.setValueLinePart1OffsetPercentage(80f);
            dataSet.setValueLinePart1Length(0.5f);
            dataSet.setValueLinePart2Length(0.5f);
            dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

            PieData data = new PieData(dataSet);
            data.setValueFormatter(new PercentFormatter(pieChart));
            data.setValueTextSize(11f);
            data.setValueTextColor(Color.BLACK);

            pieChart.setData(data);
            pieChart.setUsePercentValues(true);
            pieChart.getDescription().setEnabled(false);
            pieChart.setDrawHoleEnabled(true);
            pieChart.setHoleColor(Color.WHITE);
            pieChart.setHoleRadius(50f);
            pieChart.setTransparentCircleRadius(55f);
            pieChart.setDrawEntryLabels(false);
            pieChart.setRotationEnabled(true);
            pieChart.setHighlightPerTapEnabled(true);
            pieChart.setTouchEnabled(true);
            pieChart.setDragDecelerationFrictionCoef(0.95f);

            // Legend
            Legend legend = pieChart.getLegend();
            legend.setEnabled(true);
            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
            legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
            legend.setDrawInside(false);
            legend.setTextSize(11f);
            legend.setForm(Legend.LegendForm.CIRCLE);
            legend.setFormSize(10f);
            legend.setXEntrySpace(7f);
            legend.setYEntrySpace(5f);

            pieChart.animateY(1000);
            pieChart.invalidate();
            
            Log.d(TAG, "Pie chart setup completed with " + entries.size() + " entries");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up pie chart: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupBarChart(List<ExpenseReport> reports) {
        if (barChart == null) {
            Log.e(TAG, "BarChart view is null!");
            return;
        }

        try {
            ArrayList<BarEntry> entries = new ArrayList<>();
            ArrayList<String> labels = new ArrayList<>();

            for (int i = 0; i < reports.size(); i++) {
                ExpenseReport report = reports.get(i);
                if (report.getTotalAmount() > 0) {
                    entries.add(new BarEntry(i, (float) report.getTotalAmount()));
                    labels.add(report.getCategoryName());
                }
            }

            if (entries.isEmpty()) {
                Log.w(TAG, "No valid entries for bar chart");
                return;
            }

            BarDataSet dataSet = new BarDataSet(entries, "Chi tiêu theo danh mục");
            dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            dataSet.setValueTextSize(11f);
            dataSet.setValueTextColor(Color.BLACK);
            dataSet.setValueFormatter(new com.github.mikephil.charting.formatter.ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return String.format(Locale.getDefault(), "%.0f", value);
                }
            });

            BarData data = new BarData(dataSet);
            data.setBarWidth(0.7f);

            barChart.setData(data);
            barChart.getDescription().setEnabled(false);
            barChart.setFitBars(true);
            
            // Configure XAxis
            com.github.mikephil.charting.components.XAxis xAxis = barChart.getXAxis();
            xAxis.setPosition(com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM);
            xAxis.setGranularity(1f);
            xAxis.setLabelCount(labels.size());
            xAxis.setValueFormatter(new com.github.mikephil.charting.formatter.ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    int index = (int) value;
                    if (index >= 0 && index < labels.size()) {
                        return labels.get(index);
                    }
                    return "";
                }
            });
            xAxis.setLabelRotationAngle(-45f);
            xAxis.setTextSize(10f);

            // Configure YAxis
            barChart.getAxisLeft().setEnabled(true);
            barChart.getAxisRight().setEnabled(false);
            barChart.getAxisLeft().setTextSize(10f);
            
            // Configure Legend
            Legend legend = barChart.getLegend();
            legend.setEnabled(true);
            legend.setTextSize(11f);

            barChart.animateY(1000);
            barChart.invalidate();
            
            Log.d(TAG, "Bar chart setup completed with " + entries.size() + " entries");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up bar chart: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showEmptyChart() {
        if (pieChart != null) {
            pieChart.clear();
            pieChart.setNoDataText("Chưa có dữ liệu chi tiêu");
            pieChart.invalidate();
        }
        if (barChart != null) {
            barChart.clear();
            barChart.setNoDataText("Chưa có dữ liệu chi tiêu");
            barChart.invalidate();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh data when returning to this fragment
        if (userId > 0) {
            loadData();
            setupRecyclerView();
            loadChartData();
        }
    }
}