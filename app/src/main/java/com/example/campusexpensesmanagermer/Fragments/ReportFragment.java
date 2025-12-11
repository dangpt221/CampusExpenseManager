package com.example.campusexpensesmanagermer.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusexpensesmanagermer.Adapters.ExpenseReportAdapter;
import com.example.campusexpensesmanagermer.Adapters.ExpressAdapter;
import com.example.campusexpensesmanagermer.Models.Express;
import com.example.campusexpensesmanagermer.Models.ExpenseReport;
import com.example.campusexpensesmanagermer.R;
import com.example.campusexpensesmanagermer.Repositories.ExpressRepository;
import com.example.campusexpensesmanagermer.Utils.CurrencyUtils;
import com.google.android.material.chip.ChipGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ReportFragment extends Fragment {

    private TextView tvThisMonthTotal, tvLastMonthTotal;
    private RecyclerView rvTopExpenses, rvCategoryReport;
    private ChipGroup chipGroupFilters;
    private Button btnExport;

    private ExpressRepository expressRepository;
    private ExpenseReportAdapter reportAdapter;
    private ExpressAdapter topExpensesAdapter;
    private SharedPreferences prefs;
    private int userId;

    private static final String PREFS_NAME = "CampusExpensesPrefs";
    private static final String TAG = "ReportFragment";
    private String currentFilter = "week";

    public ReportFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        expressRepository = new ExpressRepository(getContext());
        prefs = requireActivity().getSharedPreferences(PREFS_NAME, 0);
        userId = prefs.getInt("ID_USER", 0);
        Log.d(TAG, "✓ onCreate - userId: " + userId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);

        try {
            // Initialize views
            tvThisMonthTotal = view.findViewById(R.id.tv_this_month_total);
            tvLastMonthTotal = view.findViewById(R.id.tv_last_month_total);
            rvTopExpenses = view.findViewById(R.id.rv_top_expenses);
            rvCategoryReport = view.findViewById(R.id.rv_category_report);
            chipGroupFilters = view.findViewById(R.id.chip_group_filters);
            btnExport = view.findViewById(R.id.btn_export);

            setupRecyclerViews();
            setupFilters();
            loadReportData();

            btnExport.setOnClickListener(v -> exportReport());

            Log.d(TAG, "✓ onCreateView complete");
        } catch (Exception e) {
            Log.e(TAG, "✗ Error in onCreateView: " + e.getMessage());
            e.printStackTrace();
        }

        return view;
    }

    private void setupRecyclerViews() {
        // Category Report RecyclerView
        rvCategoryReport.setLayoutManager(new LinearLayoutManager(getContext()));
        reportAdapter = new ExpenseReportAdapter(getContext(), new ArrayList<>());
        rvCategoryReport.setAdapter(reportAdapter);

        // Top Expenses RecyclerView
        rvTopExpenses.setLayoutManager(new LinearLayoutManager(getContext()));
        topExpensesAdapter = new ExpressAdapter(getContext(), new ArrayList<>());
        rvTopExpenses.setAdapter(topExpensesAdapter);

        Log.d(TAG, "✓ RecyclerViews setup");
    }

    private void setupFilters() {
        chipGroupFilters.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                int checkedId = checkedIds.get(0);

                if (checkedId == R.id.chip_week) {
                    currentFilter = "week";
                } else if (checkedId == R.id.chip_month) {
                    currentFilter = "month";
                } else if (checkedId == R.id.chip_year) {
                    currentFilter = "year";
                }

                Log.d(TAG, "Filter changed to: " + currentFilter);
                loadReportData();
            }
        });
    }

    private void loadReportData() {
        if (userId == 0) {
            Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String[] dateRange = getDateRange(currentFilter);
            String startDate = dateRange[0];
            String endDate = dateRange[1];

            Log.d(TAG, "Loading report from " + startDate + " to " + endDate);

            // Load category report
            List<ExpenseReport> categoryReports = expressRepository.getExpenseReportByCategory(userId, startDate, endDate);
            reportAdapter.updateData(categoryReports);

            // Load top 5 expenses
            List<Express> topExpenses = expressRepository.getTop5Expenses(userId, startDate, endDate);
            topExpensesAdapter = new ExpressAdapter(getContext(), topExpenses);
            rvTopExpenses.setAdapter(topExpensesAdapter);

            // Load monthly comparison
            loadMonthlyComparison();

            Log.d(TAG, "✓ Report data loaded");
        } catch (Exception e) {
            Log.e(TAG, "✗ Error loading report: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(getContext(), "Lỗi tải dữ liệu báo cáo", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadMonthlyComparison() {
        try {
            Calendar calendar = Calendar.getInstance();

            // This month
            String thisMonthStart = String.format("%04d-%02d-01 00:00:00",
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1);
            String thisMonthEnd = getCurrentDate();

            double thisMonthTotal = expressRepository.getTotalExpenseByPeriod(userId, thisMonthStart, thisMonthEnd);
            tvThisMonthTotal.setText(CurrencyUtils.formatCurrency(requireContext(), thisMonthTotal));

            // Last month
            calendar.add(Calendar.MONTH, -1);
            String lastMonthStart = String.format("%04d-%02d-01 00:00:00",
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1);

            calendar.add(Calendar.MONTH, 1);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            String lastMonthEnd = String.format("%04d-%02d-%02d 23:59:59",
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH));

            double lastMonthTotal = expressRepository.getTotalExpenseByPeriod(userId, lastMonthStart, lastMonthEnd);
            tvLastMonthTotal.setText(CurrencyUtils.formatCurrency(requireContext(), lastMonthTotal));

            Log.d(TAG, "Monthly comparison: This=" + thisMonthTotal + ", Last=" + lastMonthTotal);
        } catch (Exception e) {
            Log.e(TAG, "✗ Error loading monthly comparison: " + e.getMessage());
        }
    }

    private String[] getDateRange(String filter) {
        Calendar calendar = Calendar.getInstance();
        String endDate = getCurrentDate();
        String startDate;

        switch (filter) {
            case "week":
                calendar.add(Calendar.DAY_OF_YEAR, -7);
                startDate = formatDate(calendar) + " 00:00:00";
                break;
            case "month":
                calendar.add(Calendar.MONTH, -1);
                startDate = formatDate(calendar) + " 00:00:00";
                break;
            case "year":
                calendar.add(Calendar.YEAR, -1);
                startDate = formatDate(calendar) + " 00:00:00";
                break;
            default:
                calendar.add(Calendar.DAY_OF_YEAR, -7);
                startDate = formatDate(calendar) + " 00:00:00";
        }

        return new String[]{startDate, endDate};
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(Calendar.getInstance().getTime());
    }

    private String formatDate(Calendar calendar) {
        return String.format("%04d-%02d-%02d",
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH));
    }

    private void exportReport() {
        Toast.makeText(getContext(), "Tính năng xuất báo cáo đang phát triển", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadReportData();
    }
}