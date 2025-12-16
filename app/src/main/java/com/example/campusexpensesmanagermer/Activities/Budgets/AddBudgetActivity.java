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
            "Food", "Transport", "Shopping", "Entertainment",
            "Health", "Education", "Housing", "Utilities", "Other"
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
                getSupportActionBar().setTitle("Add new budget");
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
        String[] months = {"Month 1", "Month 2", "Month 3", "Month 4", "Month 5", "Month 6",
                "Month 7", "Month 8", "Month 9", "Month 10", "Month 11", "Month 12"};
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
            if (TextUtils.isEmpty(amountStr)) edtTargetAmount.setError("Please enter amount");
            if (userId == 0) Toast.makeText(this, "❌ Error: User not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                edtTargetAmount.setError("Amount must be greater than 0");
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
                // --- NEW: compute allocations using integer minor units (cents) to avoid rounding issues ---
                // Use 100 as scale to support two decimal places for currencies with cents. For VND (no cents)
                // this will still work as amounts will be whole numbers.
                long totalCents = Math.round(amount * 100.0);
                int n = categories.length;

                if (n > 0) {
                    long base = totalCents / n;
                    int rem = (int) (totalCents % n); // remainder to distribute

                    for (int i = 0; i < n; i++) {
                        long amountCents = base + (i < rem ? 1 : 0);
                        double amountPerCategory = amountCents / 100.0;
                        BudgetItem item = new BudgetItem((int) budgetId, categories[i], amountPerCategory);
                        budgetItemRepository.addBudgetItem(item);
                    }
                }

                // --- END new allocation logic ---

                Toast.makeText(this, "✅ Budget added successfully!", Toast.LENGTH_SHORT).show();

                Intent resultIntent = new Intent();
                resultIntent.putExtra(EXTRA_MONTH, month);
                resultIntent.putExtra(EXTRA_YEAR, year);
                resultIntent.putExtra(EXTRA_BUDGET_ID, (int) budgetId);
                setResult(RESULT_OK, resultIntent);
                finish();

            } else {
                Toast.makeText(this, "❌ Error: Failed to add budget", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "❌ Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Exception: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
