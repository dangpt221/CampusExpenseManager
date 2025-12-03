package com.example.campusexpensesmanagermer.Activities.Expenses;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.campusexpensesmanagermer.Data.SQLiteDbHelper;
import com.example.campusexpensesmanagermer.Models.Express;
import com.example.campusexpensesmanagermer.Repositories.ExpressRepository;
import com.example.campusexpensesmanagermer.R;

import java.util.Locale;

public class AddExpressActivity extends AppCompatActivity {

    private EditText edtExpenseName, edtAmount;
    private Spinner spinnerCategory;
    private Button btnAddExpense, btnCancel;
    private ExpressRepository expressRepository;
    private SharedPreferences prefs;
    private int userId;

    private static final String PREFS_NAME = "CampusExpensesPrefs";

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

        // Spinner
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
    }

    private void saveExpress() {
        String title = edtExpenseName.getText().toString().trim();
        String amountStr = edtAmount.getText().toString().trim();
        String categoryName = spinnerCategory.getSelectedItem().toString();

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
            return;
        }

        if (userId == 0) {
            Toast.makeText(this, "User không tồn tại, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo Express object
        Express e = new Express(title, amount, categoryName, userId);

        long result = expressRepository.addExpress(e);
        if (result > 0) {
            Toast.makeText(this, "Thêm chi tiêu thành công!", Toast.LENGTH_SHORT).show();
            edtExpenseName.setText("");
            edtAmount.setText("");
            spinnerCategory.setSelection(0);
            finish();
        } else {
            Toast.makeText(this, "Lỗi: Thêm chi tiêu thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
