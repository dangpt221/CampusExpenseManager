package com.example.campusexpensesmanagermer.Activities.Budgets;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.campusexpensesmanagermer.Models.Budget;
import com.example.campusexpensesmanagermer.Models.BudgetItem;
import com.example.campusexpensesmanagermer.R;
import com.example.campusexpensesmanagermer.Repositories.BudgetRepository;
import com.example.campusexpensesmanagermer.Repositories.BudgetItemRepository;

import java.util.Calendar;

public class AddBudgetActivity extends AppCompatActivity {

    public static final String EXTRA_MONTH = "EXTRA_MONTH";
    public static final String EXTRA_YEAR = "EXTRA_YEAR";
    public static final String EXTRA_BUDGET_ID = "EXTRA_BUDGET_ID";

    private EditText edtTargetAmount, edtNote;
    private Spinner spinnerMonth, spinnerYear;
    private Button btnAddBudget, btnCancel;
    private BudgetRepository budgetRepository;
    private BudgetItemRepository budgetItemRepository;
    private SharedPreferences prefs;
    private int userId;

    private static final String PREFS_NAME = "CampusExpensesPrefs";
    private static final String TAG = "AddBudgetActivity";

    private String[] categories = {
            "Ăn uống", "Giao thông", "Mua sắm", "Giải trí",
            "Y tế", "Giáo dục", "Nhà ở", "Utilities", "Khác"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_budget);

        try {
            initViews();

            budgetRepository = new BudgetRepository(this);
            budgetItemRepository = new BudgetItemRepository(this);
            prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            userId = prefs.getInt("ID_USER", 0);

            Log.d(TAG, "onCreate - userId: " + userId);

            setupSpinners();

            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Thêm ngân sách mới");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            btnAddBudget.setOnClickListener(v -> addBudget());
            btnCancel.setOnClickListener(v -> finish());

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
        }
    }

    private void initViews(){
        edtTargetAmount = findViewById(R.id.edtTargetAmount);
        edtNote = findViewById(R.id.edtNote);
        spinnerMonth = findViewById(R.id.spinnerMonth);
        spinnerYear = findViewById(R.id.spinnerYear);
        btnAddBudget = findViewById(R.id.btnAddBudget);
        btnCancel = findViewById(R.id.btnCancel);
    }

    private void setupSpinners() {
        String[] months = {"Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6",
                "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"};
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(monthAdapter);
        spinnerMonth.setSelection(Calendar.getInstance().get(Calendar.MONTH));

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        String[] years = new String[5];
        for (int i = 0; i < 5; i++) {
            years[i] = String.valueOf(currentYear - 2 + i);
        }
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearAdapter);
        spinnerYear.setSelection(2); // Select current year
    }

    private void addBudget() {
        String amountStr = edtTargetAmount.getText().toString().trim();
        String note = edtNote.getText().toString().trim();

        if (TextUtils.isEmpty(amountStr) || userId == 0) {
            if (TextUtils.isEmpty(amountStr)) edtTargetAmount.setError("Vui lòng nhập số tiền");
            if (userId == 0) Toast.makeText(this, "❌ Lỗi: User không tồn tại.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                edtTargetAmount.setError("Số tiền phải lớn hơn 0");
                return;
            }

            int month = spinnerMonth.getSelectedItemPosition() + 1;
            int year = Integer.parseInt((String) spinnerYear.getSelectedItem());

            Budget budget = new Budget();
            budget.setUserId(userId);
            budget.setMonth(month);
            budget.setYear(year);
            budget.setMoney(amount);
            budget.setDescription(note);

            long budgetId = budgetRepository.addBudget(budget);

            if (budgetId > 0) {
                double amountPerCategory = amount / categories.length;
                for (String category : categories) {
                    BudgetItem item = new BudgetItem((int) budgetId, category, amountPerCategory);
                    budgetItemRepository.addBudgetItem(item);
                }

                Toast.makeText(this, "✅ Thêm ngân sách thành công!", Toast.LENGTH_SHORT).show();

                Intent resultIntent = new Intent();
                resultIntent.putExtra(EXTRA_MONTH, month);
                resultIntent.putExtra(EXTRA_YEAR, year);
                resultIntent.putExtra(EXTRA_BUDGET_ID, (int) budgetId);
                setResult(RESULT_OK, resultIntent);
                finish();

            } else {
                Toast.makeText(this, "❌ Lỗi: Thêm ngân sách thất bại", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "❌ Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Exception: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
