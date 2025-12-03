package com.example.campusexpensesmanagermer.Activities.Expenses;

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

import com.example.campusexpensesmanagermer.Models.Express;
import com.example.campusexpensesmanagermer.Repositories.ExpressRepository;
import com.example.campusexpensesmanagermer.R;

public class AddExpressActivity extends AppCompatActivity {

    private EditText edtExpenseName, edtAmount;
    private Spinner spinnerCategory;
    private Button btnAddExpense, btnCancel;
    private ExpressRepository expressRepository;
    private SharedPreferences prefs;
    private int userId;

    private static final String PREFS_NAME = "CampusExpensesPrefs";
    private static final String TAG = "AddExpressActivity";

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
        setContentView(R.layout.activity_add_express);

        try {
            // Init views
            edtExpenseName = findViewById(R.id.edtExpenseName);
            edtAmount = findViewById(R.id.edtAmount);
            spinnerCategory = findViewById(R.id.spinnerCategory);
            btnAddExpense = findViewById(R.id.btnAddExpense);
            btnCancel = findViewById(R.id.btnCancel);

            // Init repository & preferences
            expressRepository = new ExpressRepository(this);
            prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            userId = prefs.getInt("ID_USER", 0);

            Log.d(TAG, "onCreate - userId from prefs: " + userId);

            // Spinner setup
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this, android.R.layout.simple_spinner_item, categories);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCategory.setAdapter(adapter);

            // Toolbar title
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Thêm chi tiêu mới");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            btnAddExpense.setOnClickListener(v -> saveExpress());
            btnCancel.setOnClickListener(v -> finish());

            Log.d(TAG, "onCreate - Setup completed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void saveExpress() {
        String title = edtExpenseName.getText().toString().trim();
        String amountStr = edtAmount.getText().toString().trim();
        String categoryName = spinnerCategory.getSelectedItem().toString();

        Log.d(TAG, "saveExpress called - userId: " + userId);

        if (TextUtils.isEmpty(title)) {
            edtExpenseName.setError("Vui lòng nhập tên chi tiêu");
            edtExpenseName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(amountStr)) {
            edtAmount.setError("Vui lòng nhập số tiền");
            edtAmount.requestFocus();
            return;
        }

        if (userId == 0) {
            Toast.makeText(this, "❌ Lỗi: User không tồn tại. Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "userId is 0");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                edtAmount.setError("Số tiền phải lớn hơn 0");
                edtAmount.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            edtAmount.setError("Số tiền không hợp lệ");
            edtAmount.requestFocus();
            Log.e(TAG, "NumberFormatException: " + e.getMessage());
            return;
        }

        try {
            // Tạo Express object
            Express express = new Express(title, amount, categoryName, userId);
            Log.d(TAG, "Creating express: " + express.toString());

            long result = expressRepository.addExpress(express);
            Log.d(TAG, "addExpress result: " + result);

            if (result > 0) {
                Toast.makeText(this, "✅ Thêm chi tiêu thành công!", Toast.LENGTH_SHORT).show();
                edtExpenseName.setText("");
                edtAmount.setText("");
                spinnerCategory.setSelection(0);
                finish();
            } else {
                Toast.makeText(this, "❌ Lỗi: Thêm chi tiêu thất bại", Toast.LENGTH_SHORT).show();
            }
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