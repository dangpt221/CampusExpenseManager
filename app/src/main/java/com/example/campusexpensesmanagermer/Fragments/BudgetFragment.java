package com.example.campusexpensesmanagermer.Fragments;

import android.app.Activity;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusexpensesmanagermer.Activities.Budgets.AddBudgetActivity;
import com.example.campusexpensesmanagermer.Adapters.BudgetAdapter;
import com.example.campusexpensesmanagermer.Models.Budget;
import com.example.campusexpensesmanagermer.Models.BudgetItem;
import com.example.campusexpensesmanagermer.R;
import com.example.campusexpensesmanagermer.Utils.CurrencyUtils;
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
    private static final int ADD_BUDGET_REQUEST_CODE = 1;

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

            Log.d(TAG, "✓ onCreate - userId: " + userId + ", Current Month: " + currentMonth + "/" + currentYear);
        } catch (Exception e) {
            Log.e(TAG, "✗ Error in onCreate: " + e.getMessage(), e);
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
        } catch (Exception e) {
            Log.e(TAG, "✗ Error in onCreateView: " + e.getMessage(), e);
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
    }

    private void setupListeners() {
        fabAddBudget.setOnClickListener(v -> openAddBudgetActivity());
        btnPrevMonth.setOnClickListener(v -> changeMonth(-1));
        btnNextMonth.setOnClickListener(v -> changeMonth(1));
    }

    /**
     * ✅ FIX: Load budget data với logic chính xác
     */
    private void loadBudgetData() {
        if (userId == 0) {
            Log.e(TAG, "userId is 0, cannot load budget");
            showEmptyState(true);
            return;
        }

        try {
            updateMonthDisplay();

            // ✅ Lấy budget từ database
            currentBudget = budgetRepository.getBudgetByUserAndMonth(userId, currentMonth, currentYear);

            if (currentBudget == null) {
                Log.d(TAG, "No budget found for " + currentMonth + "/" + currentYear);
                showEmptyState(true);
                return;
            }

            Log.d(TAG, "✓ Budget loaded: Total=" + currentBudget.getMoney() + ", Spent=" + currentBudget.getSpent());

            // Load budget items
            List<BudgetItem> items = budgetItemRepository.getBudgetItemsByBudget(currentBudget.getId());
            budgetItems.clear();
            budgetItems.addAll(items);
            budgetAdapter.notifyDataSetChanged();

            Log.d(TAG, "✓ Loaded " + items.size() + " budget items");

            // ✅ Tính tổng đã chi từ các mục (đảm bảo đồng bộ giữa tổng và danh mục)
            double totalSpentFromItems = 0;
            for (BudgetItem bi : items) {
                totalSpentFromItems += bi.getSpentAmount();
            }

            // Nếu repository trả về giá trị khác (ví dụ tính từ express), ưu tiên giá trị từ DB (tổng các mục)
            if (totalSpentFromItems > 0) {
                currentBudget.setSpent(totalSpentFromItems);
            } else {
                // Fallback: nếu không có mục hoặc tổng bằng 0 thì dùng giá trị tính sẵn
                currentBudget.setSpent(budgetRepository.getTotalSpentByBudget(currentBudget.getId()));
            }

            // Hiển thị tổng ngân sách và đã chi sau khi đồng bộ
            tvTotalBudget.setText(CurrencyUtils.formatCurrency(requireContext(), currentBudget.getMoney()));
            tvTotalSpent.setText(CurrencyUtils.formatCurrency(requireContext(), currentBudget.getSpent()));
            tvRemaining.setText(CurrencyUtils.formatCurrency(requireContext(), currentBudget.getRemaining()));

            // ✅ Thay đổi màu nếu vượt quá
            if (currentBudget.isExceeded()) {
                tvRemaining.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark));
                tvTotalSpent.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark));
            } else {
                tvRemaining.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark));
                tvTotalSpent.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_orange_dark));
            }

            // ✅ Progress bar cập nhật theo tổng mới
            budgetProgressBar.setProgress(currentBudget.getProgressPercentage());

            // Allow editing main budget total by tapping the total text
            tvTotalBudget.setOnClickListener(v -> showEditBudgetTotalDialog());


            if (items.isEmpty()) {
                rvBudgetItems.setVisibility(View.GONE);
                tvEmptyState.setVisibility(View.VISIBLE);
            } else {
                rvBudgetItems.setVisibility(View.VISIBLE);
                tvEmptyState.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Log.e(TAG, "✗ Error loading budget: " + e.getMessage(), e);
            showEmptyState(true);
        }
    }

    private void updateMonthDisplay() {
        String[] months = {"Month 1", "Month 2", "Month 3", "Month 4", "Month 5", "Month 6",
                "Month 7", "Month 8", "Month 9", "Month 10", "Month 11", "Month 12"};
        tvCurrentMonth.setText(String.format(Locale.getDefault(), "%s %d", months[currentMonth - 1], currentYear));
    }

    private void showEmptyState(boolean isTotalBudgetEmpty) {
        budgetItems.clear();
        budgetAdapter.notifyDataSetChanged();
        rvBudgetItems.setVisibility(View.GONE);
        tvEmptyState.setVisibility(View.VISIBLE);

        if (isTotalBudgetEmpty) {
            tvTotalBudget.setText(CurrencyUtils.formatCurrency(requireContext(), 0));
            tvTotalSpent.setText(CurrencyUtils.formatCurrency(requireContext(), 0));
            tvRemaining.setText(CurrencyUtils.formatCurrency(requireContext(), 0));
            budgetProgressBar.setProgress(0);
        }
    }

    private void changeMonth(int direction) {
        currentMonth += direction;
        if (currentMonth < 1) {
            currentMonth = 12;
            currentYear--;
        } else if (currentMonth > 12) {
            currentMonth = 1;
            currentYear++;
        }
        Log.d(TAG, "Month changed to: " + currentMonth + "/" + currentYear);
        loadBudgetData();
    }

    private void openAddBudgetActivity() {
        Intent intent = new Intent(getActivity(), AddBudgetActivity.class);
        startActivityForResult(intent, ADD_BUDGET_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_BUDGET_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            Log.d(TAG, "✓ Received result from AddBudgetActivity");

            int createdBudgetId = data.getIntExtra(AddBudgetActivity.EXTRA_BUDGET_ID, -1);
            if (createdBudgetId > 0) {
                Log.d(TAG, "Loading newly created budget ID: " + createdBudgetId);
                currentBudget = budgetRepository.getBudgetById(createdBudgetId);
                if (currentBudget != null) {
                    currentMonth = currentBudget.getMonth();
                    currentYear = currentBudget.getYear();

                    Log.d(TAG, "✓ Loaded new budget: " + currentMonth + "/" + currentYear);
                    loadBudgetData();
                    return;
                }
            }

            // ✅ Fallback: Dùng tháng/năm từ intent
            int newMonth = data.getIntExtra(AddBudgetActivity.EXTRA_MONTH, currentMonth);
            int newYear = data.getIntExtra(AddBudgetActivity.EXTRA_YEAR, currentYear);

            currentMonth = newMonth;
            currentYear = newYear;

            Log.d(TAG, "Jumping to month: " + currentMonth + "/" + currentYear);
            loadBudgetData();
        }
    }

    @Override
    public void onEdit(BudgetItem item) {
        showEditDialog(item);
    }


    private void showEditDialog(final BudgetItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Edit budget for " + item.getCategoryName());

        final EditText input = new EditText(getContext());
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        input.setText(String.valueOf((int) item.getAllocatedAmount()));
        input.setHint("Enter amount");

        builder.setView(input);
        builder.setPositiveButton("Save", (dialog, which) -> {
            String newAmount = input.getText().toString();
            if (!newAmount.isEmpty()) {
                try {
                    double amount = Double.parseDouble(newAmount);
                    item.setAllocatedAmount(amount);
                    if (budgetItemRepository.updateBudgetItem(item)) {
                        Toast.makeText(getContext(), "✅ Update successful", Toast.LENGTH_SHORT).show();
                        loadBudgetData();
                    } else {
                        Toast.makeText(getContext(), "❌ Update failed", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "❌ Invalid amount", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    // Show dialog to edit the main budget total and re-split items
    private void showEditBudgetTotalDialog() {
        if (currentBudget == null || currentBudget.getId() == 0) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Edit total budget");

        final EditText input = new EditText(getContext());
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setText(String.valueOf(currentBudget.getMoney()));
        input.setHint("Enter total amount");

        builder.setView(input);
        builder.setPositiveButton("Save", (dialog, which) -> {
            String val = input.getText().toString().trim();
            if (val.isEmpty()) return;
            try {
                double newAmount = Double.parseDouble(val);
                if (newAmount <= 0) {
                    Toast.makeText(getContext(), "Invalid amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean ok = budgetRepository.updateBudgetAndSplit(currentBudget.getId(), newAmount);
                if (ok) {
                    Toast.makeText(getContext(), "✅ Budget updated and split", Toast.LENGTH_SHORT).show();
                    loadBudgetData();
                } else {
                    Toast.makeText(getContext(), "❌ Update failed", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "❌ Invalid number", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "✓ onResume - Reloading budget data");
        loadBudgetData();  // ✅ Reload khi quay lại fragment
    }
}