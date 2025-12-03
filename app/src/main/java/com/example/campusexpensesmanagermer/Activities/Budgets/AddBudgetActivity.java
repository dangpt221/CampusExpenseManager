package com.example.campusexpensesmanagermer.Activities.Budgets;

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
            "Ăn uống",
            "Giao thông",
            "Mua sắm",
            "Giải trí",
            "Y tế",
            "Giáo dục",
            "Nhà ở",
            "Utilities",
            "Khác"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_budget);

        try {
            // Init views
            edtTargetAmount = findViewById(R.id.edtTargetAmount);
            edtNote = findViewById(R.id.edtNote);
            spinnerMonth = findViewById(R.id.spinnerMonth);
            spinnerYear = findViewById(R.id.spinnerYear);
            btnAddBudget = findViewById(R.id.btnAddBudget);
            btnCancel = findViewById(R.id.btnCancel);

            // Init repository & preferences
            budgetRepository = new BudgetRepository(this);
            budgetItemRepository = new BudgetItemRepository(this);
            prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            userId = prefs.getInt("ID_USER", 0);

            Log.d(TAG, "onCreate - userId: " + userId);

            // Setup spinners
            setupSpinners();

            // Toolbar
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Thêm ngân sách mới");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            btnAddBudget.setOnClickListener(v -> addBudget());
            btnCancel.setOnClickListener(v -> finish());

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupSpinners() {
        // Setup month spinner
        String[] months = {"Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6",
                "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"};
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(monthAdapter);

        // Chọn tháng hiện tại
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
        spinnerMonth.setSelection(currentMonth);

        // Setup year spinner
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        String[] years = new String[5];
        for (int i = 0; i < 5; i++) {
            years[i] = String.valueOf(currentYear - 2 + i);
        }
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearAdapter);

        // Chọn năm hiện tại
        spinnerYear.setSelection(2);
    }

    // ========== VỊ TRÍ CHÍNH: Hàm addBudget() - THAY THẾ TOÀN BỘ ==========
    private void addBudget() {
        String amountStr = edtTargetAmount.getText().toString().trim();
        String note = edtNote.getText().toString().trim();

        // ✅ 1. Kiểm tra input
        if (TextUtils.isEmpty(amountStr)) {
            edtTargetAmount.setError("Vui lòng nhập số tiền");
            edtTargetAmount.requestFocus();
            return;
        }

        if (userId == 0) {
            Toast.makeText(this, "❌ Lỗi: User không tồn tại. Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "userId is 0");
            return;
        }

        try {
            // ✅ 2. Parse và validate số tiền
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                edtTargetAmount.setError("Số tiền phải lớn hơn 0");
                edtTargetAmount.requestFocus();
                return;
            }

            // ✅ 3. Lấy month, year từ spinner
            int month = spinnerMonth.getSelectedItemPosition() + 1;
            int year = Integer.parseInt((String) spinnerYear.getSelectedItem());

            Log.d(TAG, "Adding budget: amount=" + amount + ", month=" + month + ", year=" + year);

            // ✅ 4. Tạo Budget object
            Budget budget = new Budget();
            budget.setUserId(userId);
            budget.setMonth(month);
            budget.setYear(year);
            budget.setMoney(amount);
            budget.setDescription(note);
            budget.setStatus(1);

            // ✅ 5. Thêm Budget vào database
            long budgetId = budgetRepository.addBudget(budget);

            if (budgetId > 0) {
                Log.d(TAG, "✓ Budget created with ID: " + budgetId);

                // ✅ 6. Tính số tiền cho mỗi danh mục
                double amountPerCategory = amount / categories.length;
                int successCount = 0;
                int failCount = 0;

                Log.d(TAG, "Creating " + categories.length + " budget items with amount: " + amountPerCategory + " each");

                // ✅ 7. VÒNG LẶP: Thêm BudgetItem cho mỗi danh mục
                for (String category : categories) {
                    BudgetItem item = new BudgetItem();
                    item.setBudgetId((int) budgetId);
                    item.setCategoryName(category);
                    item.setAllocatedAmount(amountPerCategory);

                    // ✅ QUAN TRỌNG: Kiểm tra kết quả từng item
                    long itemId = budgetItemRepository.addBudgetItem(item);

                    if (itemId > 0) {
                        successCount++;
                        Log.d(TAG, "✓ Budget item created: " + category + " (ID: " + itemId + ")");
                    } else {
                        failCount++;
                        Log.e(TAG, "✗ Failed to create budget item: " + category);
                    }
                }

                // ✅ 8. Thông báo kết quả cho người dùng
                if (failCount == 0) {
                    // Tất cả thành công
                    Toast.makeText(this, "✅ Thêm ngân sách thành công! (" + successCount + " danh mục)",
                            Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "✓ All budget items created successfully");
                } else if (successCount > 0) {
                    // Một số thất bại
                    Toast.makeText(this, "⚠️ Thành công: " + successCount + ", Thất bại: " + failCount + "\nXem Logcat để chi tiết",
                            Toast.LENGTH_LONG).show();
                    Log.w(TAG, "⚠️ Partial success: " + successCount + " ok, " + failCount + " failed");
                } else {
                    // Tất cả thất bại
                    Toast.makeText(this, "❌ Lỗi: Không thể thêm danh mục. Xem Logcat để chi tiết",
                            Toast.LENGTH_LONG).show();
                    Log.e(TAG, "✗ All budget items failed to create");
                    return;
                }

                // ✅ 9. Kết thúc activity
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "❌ Lỗi: Thêm ngân sách thất bại", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "✗ Failed to add budget, returned ID: " + budgetId);
            }
        } catch (NumberFormatException e) {
            edtTargetAmount.setError("Số tiền không hợp lệ");
            edtTargetAmount.requestFocus();
            Log.e(TAG, "NumberFormatException: " + e.getMessage());
        } catch (Exception e) {
            Toast.makeText(this, "❌ Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}