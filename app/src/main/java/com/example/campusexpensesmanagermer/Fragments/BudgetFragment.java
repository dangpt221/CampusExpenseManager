package com.example.campusexpensesmanagermer.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusexpensesmanagermer.Activities.Budgets.AddBudgetActivity;
import com.example.campusexpensesmanagermer.Adapters.BudgetAdapter;
import com.example.campusexpensesmanagermer.Models.Budget;
import com.example.campusexpensesmanagermer.Models.BudgetItem;
import com.example.campusexpensesmanagermer.R;
import com.example.campusexpensesmanagermer.Repositories.BudgetRepository;
import com.example.campusexpensesmanagermer.Repositories.BudgetItemRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BudgetFragment extends Fragment implements BudgetAdapter.OnBudgetItemClickListener {

    private BudgetRepository budgetRepository;
    private BudgetItemRepository budgetItemRepository;
    private SharedPreferences prefs;
    private int userId;

    private TextView tvTotalBudget, tvTotalSpent, tvRemaining, tvEmptyState;
    private ProgressBar budgetProgressBar;
    private RecyclerView rvBudgetItems;
    private BudgetAdapter budgetAdapter;
    private List<BudgetItem> budgetItems;

    private FloatingActionButton fabAddBudget;
    private Button btnPrevMonth, btnNextMonth;
    private TextView tvCurrentMonth;

    private int currentMonth;
    private int currentYear;
    private Budget currentBudget;

    private static final String PREFS_NAME = "CampusExpensesPrefs";
    private static final String TAG = "BudgetFragment";

    public BudgetFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            budgetRepository = new BudgetRepository(getContext());
            budgetItemRepository = new BudgetItemRepository(getContext());
            prefs = requireActivity().getSharedPreferences(PREFS_NAME, 0);
            userId = prefs.getInt("ID_USER", 0);

            currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
            currentYear = Calendar.getInstance().get(Calendar.YEAR);

            Log.d(TAG, "✓ onCreate - userId: " + userId);
        } catch (Exception e) {
            Log.e(TAG, "✗ Error in onCreate: " + e.getMessage());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_budget, container, false);

        try {
            initViews(view);
            setupRecyclerView();
            setupListeners();
            loadBudgetData();

            Log.d(TAG, "✓ onCreateView - Setup complete");
        } catch (Exception e) {
            Log.e(TAG, "✗ Error in onCreateView: " + e.getMessage());
            e.printStackTrace();
        }

        return view;
    }

    private void initViews(View view) {
        tvTotalBudget = view.findViewById(R.id.tv_total_budget);
        tvTotalSpent = view.findViewById(R.id.tv_total_spent);
        tvRemaining = view.findViewById(R.id.tv_remaining);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);
        budgetProgressBar = view.findViewById(R.id.budget_progress_bar);
        rvBudgetItems = view.findViewById(R.id.rv_budget_items);
        fabAddBudget = view.findViewById(R.id.fab_add_budget);
        btnPrevMonth = view.findViewById(R.id.btn_prev_month);
        btnNextMonth = view.findViewById(R.id.btn_next_month);
        tvCurrentMonth = view.findViewById(R.id.tv_current_month);
    }

    private void setupRecyclerView() {
        budgetItems = new ArrayList<>();
        budgetAdapter = new BudgetAdapter(getContext(), budgetItems);
        budgetAdapter.setListener(this);

        rvBudgetItems.setLayoutManager(new LinearLayoutManager(getContext()));
        rvBudgetItems.setAdapter(budgetAdapter);

        Log.d(TAG, "✓ RecyclerView setup");
    }

    private void setupListeners() {
        fabAddBudget.setOnClickListener(v -> openAddBudgetActivity());
        btnPrevMonth.setOnClickListener(v -> previousMonth());
        btnNextMonth.setOnClickListener(v -> nextMonth());
    }

    // ========== VỊ TRÍ 1: Hàm showEmptyState - THAY THẾ TOÀN BỘ ==========
    private void showEmptyState() {
        rvBudgetItems.setVisibility(View.GONE);
        tvEmptyState.setVisibility(View.VISIBLE);
        tvTotalBudget.setText("0 ₫");
        tvTotalSpent.setText("0 ₫");
        tvRemaining.setText("0 ₫");
        budgetProgressBar.setProgress(0);

        Log.d(TAG, "✓ Showing empty state");
    }

    // ========== VỊ TRÍ 2: Hàm loadBudgetData - THAY THẾ TOÀN BỘ ==========
    private void loadBudgetData() {
        if (userId == 0) {
            Log.e(TAG, "Cannot load budget - userId is 0");
            showEmptyState();
            return;
        }

        try {
            // Cập nhật tên tháng
            updateMonthDisplay();

            // Lấy budget của tháng/năm hiện tại
            currentBudget = budgetRepository.getBudgetByUserAndMonth(userId, currentMonth, currentYear);

            if (currentBudget == null) {
                // Không có budget cho tháng này
                Log.d(TAG, "No budget found for " + currentMonth + "/" + currentYear);
                showEmptyState();
                return;
            }

            // Hiển thị summary
            tvTotalBudget.setText(String.format(Locale.getDefault(), "%.0f ₫", currentBudget.getMoney()));
            tvTotalSpent.setText(String.format(Locale.getDefault(), "%.0f ₫", currentBudget.getSpent()));
            tvRemaining.setText(String.format(Locale.getDefault(), "%.0f ₫", currentBudget.getRemaining()));

            // Đổi màu nếu vượt quá
            if (currentBudget.isExceeded()) {
                tvRemaining.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                tvTotalSpent.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            } else {
                tvRemaining.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                tvTotalSpent.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
            }

            // Hiển thị progress bar
            int progress = currentBudget.getProgressPercentage();
            budgetProgressBar.setProgress(progress);

            // Load budget items
            List<BudgetItem> items = budgetItemRepository.getBudgetItemsByBudget(currentBudget.getId());

            if (items != null && !items.isEmpty()) {
                budgetItems.clear();
                budgetItems.addAll(items);
                budgetAdapter.notifyDataSetChanged();

                rvBudgetItems.setVisibility(View.VISIBLE);
                tvEmptyState.setVisibility(View.GONE);

                Log.d(TAG, "✓ Loaded " + items.size() + " budget items");
            } else {
                Log.d(TAG, "No budget items found");
                showEmptyState();
            }

        } catch (Exception e) {
            Log.e(TAG, "✗ Error loading budget: " + e.getMessage());
            e.printStackTrace();
            showEmptyState();
        }
    }

    private void updateMonthDisplay() {
        String[] months = {"Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6",
                "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"};
        tvCurrentMonth.setText(months[currentMonth - 1] + " năm " + currentYear);
    }

    private void previousMonth() {
        currentMonth--;
        if (currentMonth < 1) {
            currentMonth = 12;
            currentYear--;
        }
        loadBudgetData();
    }

    private void nextMonth() {
        currentMonth++;
        if (currentMonth > 12) {
            currentMonth = 1;
            currentYear++;
        }
        loadBudgetData();
    }

    private void openAddBudgetActivity() {
        Intent intent = new Intent(getActivity(), AddBudgetActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            loadBudgetData();
        }
    }

    @Override
    public void onEdit(BudgetItem item) {
        // Sửa budget item - hiển thị dialog
        showEditDialog(item);
    }

    @Override
    public void onDelete(BudgetItem item) {
        // Xóa budget item
        new AlertDialog.Builder(getContext())
                .setTitle("Xóa danh mục ngân sách")
                .setMessage("Bạn có chắc chắn muốn xóa danh mục \"" + item.getCategoryName() + "\" khỏi ngân sách?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    if (budgetItemRepository.deleteBudgetItem(item.getId())) {
                        Toast.makeText(getContext(), "✅ Xóa thành công", Toast.LENGTH_SHORT).show();
                        loadBudgetData();
                    } else {
                        Toast.makeText(getContext(), "❌ Xóa thất bại", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showEditDialog(BudgetItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Sửa ngân sách cho " + item.getCategoryName());

        EditText input = new EditText(getContext());
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setText(String.valueOf((int) item.getAllocatedAmount()));
        input.setHint("Nhập số tiền");

        builder.setView(input);
        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String newAmount = input.getText().toString();
            if (!newAmount.isEmpty()) {
                try {
                    double amount = Double.parseDouble(newAmount);
                    item.setAllocatedAmount(amount);
                    if (budgetItemRepository.updateBudgetItem(item)) {
                        Toast.makeText(getContext(), "✅ Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        loadBudgetData();
                    } else {
                        Toast.makeText(getContext(), "❌ Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "❌ Số tiền không hợp lệ", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadBudgetData();
    }
}