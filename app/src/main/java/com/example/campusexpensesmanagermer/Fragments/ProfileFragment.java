package com.example.campusexpensesmanagermer.Fragments;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.campusexpensesmanagermer.Data.SQLiteDbHelper;
import com.example.campusexpensesmanagermer.R;

import java.util.Calendar;

public class ProfileFragment extends Fragment {

    private ImageView ivAvatar;
    private TextView tvUsername, tvEmail, tvPhone, tvRole, tvStatus;
    private TextView tvYearlySpend, tvTransactionCount, tvTopCategory, tvAverageSpend;
    private TextView tvMonthlySpend, tvLastExpense;
    private Button btnEditProfile, btnChangePassword, btnLogout;

    private SharedPreferences prefs;
    private int userId;

    private static final String PREFS_NAME = "CampusExpensesPrefs";
    private static final String TAG = "ProfileFragment";

    public ProfileFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            prefs = requireActivity().getSharedPreferences(PREFS_NAME, 0);
            userId = prefs.getInt("ID_USER", 0);
            Log.d(TAG, "✓ onCreate - userId: " + userId);
        } catch (Exception e) {
            Log.e(TAG, "✗ Error in onCreate: " + e.getMessage());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        try {
            initViews(view);
            loadUserInfo();
            loadStatistics();
            setupListeners();

            Log.d(TAG, "✓ onCreateView - Setup complete");
        } catch (Exception e) {
            Log.e(TAG, "✗ Error in onCreateView: " + e.getMessage());
            e.printStackTrace();
        }

        return view;
    }

    private void initViews(View view) {
        // User info
        ivAvatar = view.findViewById(R.id.iv_avatar);
        tvUsername = view.findViewById(R.id.tv_student_name);
        tvEmail = view.findViewById(R.id.tv_student_email);
        tvPhone = view.findViewById(R.id.tv_phone);
        tvRole = view.findViewById(R.id.tv_role);
        tvStatus = view.findViewById(R.id.tv_status);

        // Statistics
        tvYearlySpend = view.findViewById(R.id.tv_total_spend_year);
        tvTransactionCount = view.findViewById(R.id.tv_transaction_count);
        tvTopCategory = view.findViewById(R.id.tv_top_category);
        tvAverageSpend = view.findViewById(R.id.tv_average_spend);
        tvMonthlySpend = view.findViewById(R.id.tv_monthly_spend);
        tvLastExpense = view.findViewById(R.id.tv_last_expense);

        // Buttons
        btnEditProfile = view.findViewById(R.id.btn_edit_profile);
        btnChangePassword = view.findViewById(R.id.btn_change_password);
        btnLogout = view.findViewById(R.id.btn_logout);
    }

    private void setupListeners() {
        btnEditProfile.setOnClickListener(v -> {
            Toast.makeText(getContext(), "🔨 Chức năng đang được phát triển", Toast.LENGTH_SHORT).show();
        });

        btnChangePassword.setOnClickListener(v -> {
            Toast.makeText(getContext(), "🔨 Chức năng đang được phát triển", Toast.LENGTH_SHORT).show();
        });

        btnLogout.setOnClickListener(v -> {
            logout();
        });
    }

    private void loadUserInfo() {
        try {
            String username = prefs.getString("username", "Người dùng");
            String email = prefs.getString("email", "N/A");
            String phone = prefs.getString("phone", "N/A");
            int role = prefs.getInt("role", 0);

            tvUsername.setText(username);
            tvEmail.setText(email);
            tvPhone.setText(phone.isEmpty() ? "N/A" : phone);
            tvRole.setText(role == 0 ? "Người dùng thường" : "Admin");
            tvStatus.setText("✅ Hoạt động");

            Log.d(TAG, "✓ User info loaded");
        } catch (Exception e) {
            Log.e(TAG, "✗ Error loading user info: " + e.getMessage());
        }
    }

    private void loadStatistics() {
        if (userId == 0) {
            Log.e(TAG, "Cannot load statistics - userId is 0");
            return;
        }

        try {
            // Tổng chi trong năm
            double yearlySpend = getTotalSpendThisYear();
            tvYearlySpend.setText(String.format("%.0f ₫", yearlySpend));

            // Tổng chi trong tháng
            double monthlySpend = getTotalSpendThisMonth();
            tvMonthlySpend.setText(String.format("%.0f ₫", monthlySpend));

            // Số giao dịch
            int transactionCount = getTransactionCount();
            tvTransactionCount.setText(String.valueOf(transactionCount));

            // Trung bình chi tiêu
            double averageSpend = transactionCount > 0 ? yearlySpend / transactionCount : 0;
            tvAverageSpend.setText(String.format("%.0f ₫", averageSpend));

            // Loại chi phổ biến nhất
            String topCategory = getTopCategory();
            tvTopCategory.setText(topCategory);

            // Chi tiêu gần nhất
            String lastExpense = getLastExpense();
            tvLastExpense.setText(lastExpense);

            Log.d(TAG, "✓ Statistics loaded");
        } catch (Exception e) {
            Log.e(TAG, "✗ Error loading statistics: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Tổng chi tiêu trong năm hiện tại
     */
    private double getTotalSpendThisYear() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = new SQLiteDbHelper(getContext()).getReadableDatabase();
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);

            String query = "SELECT SUM(" + SQLiteDbHelper.AMOUNT_EXPRESS + ") as total FROM "
                    + SQLiteDbHelper.TABLE_EXPRESS
                    + " WHERE " + SQLiteDbHelper.USER_ID_EXPRESS + " = ? "
                    + "AND strftime('%Y', " + SQLiteDbHelper.DATE_EXPRESS + ") = ?";

            cursor = db.rawQuery(query, new String[]{String.valueOf(userId), String.valueOf(currentYear)});

            if (cursor.moveToFirst()) {
                double total = cursor.getDouble(0);
                Log.d(TAG, "Yearly spend: " + total);
                return total;
            }
        } catch (Exception e) {
            Log.e(TAG, "✗ Error: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
        return 0;
    }

    /**
     * Tổng chi tiêu trong tháng hiện tại
     */
    private double getTotalSpendThisMonth() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = new SQLiteDbHelper(getContext()).getReadableDatabase();
            Calendar now = Calendar.getInstance();
            int currentYear = now.get(Calendar.YEAR);
            int currentMonth = now.get(Calendar.MONTH) + 1;

            String query = "SELECT SUM(" + SQLiteDbHelper.AMOUNT_EXPRESS + ") as total FROM "
                    + SQLiteDbHelper.TABLE_EXPRESS
                    + " WHERE " + SQLiteDbHelper.USER_ID_EXPRESS + " = ? "
                    + "AND strftime('%Y', " + SQLiteDbHelper.DATE_EXPRESS + ") = ? "
                    + "AND strftime('%m', " + SQLiteDbHelper.DATE_EXPRESS + ") = ?";

            cursor = db.rawQuery(query, new String[]{
                    String.valueOf(userId),
                    String.valueOf(currentYear),
                    String.format("%02d", currentMonth)
            });

            if (cursor.moveToFirst()) {
                double total = cursor.getDouble(0);
                Log.d(TAG, "Monthly spend: " + total);
                return total;
            }
        } catch (Exception e) {
            Log.e(TAG, "✗ Error: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
        return 0;
    }

    /**
     * Số lượng giao dịch
     */
    private int getTransactionCount() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = new SQLiteDbHelper(getContext()).getReadableDatabase();

            String query = "SELECT COUNT(*) as count FROM " + SQLiteDbHelper.TABLE_EXPRESS
                    + " WHERE " + SQLiteDbHelper.USER_ID_EXPRESS + " = ?";

            cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

            if (cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                Log.d(TAG, "Transaction count: " + count);
                return count;
            }
        } catch (Exception e) {
            Log.e(TAG, "✗ Error: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
        return 0;
    }

    /**
     * Loại chi tiêu phổ biến nhất
     */
    private String getTopCategory() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = new SQLiteDbHelper(getContext()).getReadableDatabase();

            String query = "SELECT " + SQLiteDbHelper.CATEGORY_ID_EXPRESS + ", COUNT(*) as count FROM "
                    + SQLiteDbHelper.TABLE_EXPRESS
                    + " WHERE " + SQLiteDbHelper.USER_ID_EXPRESS + " = ? "
                    + "GROUP BY " + SQLiteDbHelper.CATEGORY_ID_EXPRESS
                    + " ORDER BY count DESC LIMIT 1";

            cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

            if (cursor.moveToFirst()) {
                String category = cursor.getString(0);
                Log.d(TAG, "Top category: " + category);
                return category != null ? category : "Chưa có dữ liệu";
            }
        } catch (Exception e) {
            Log.e(TAG, "✗ Error: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
        return "Chưa có dữ liệu";
    }

    /**
     * Chi tiêu gần nhất
     */
    private String getLastExpense() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = new SQLiteDbHelper(getContext()).getReadableDatabase();

            String query = "SELECT " + SQLiteDbHelper.TITLE_EXPRESS + ", " + SQLiteDbHelper.AMOUNT_EXPRESS
                    + " FROM " + SQLiteDbHelper.TABLE_EXPRESS
                    + " WHERE " + SQLiteDbHelper.USER_ID_EXPRESS + " = ? "
                    + "ORDER BY " + SQLiteDbHelper.DATE_EXPRESS + " DESC LIMIT 1";

            cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

            if (cursor.moveToFirst()) {
                String title = cursor.getString(0);
                double amount = cursor.getDouble(1);
                String result = title + " - " + String.format("%.0f ₫", amount);
                Log.d(TAG, "Last expense: " + result);
                return result;
            }
        } catch (Exception e) {
            Log.e(TAG, "✗ Error: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
        return "Chưa có dữ liệu";
    }

    /**
     * Đăng xuất
     */
    private void logout() {
        try {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

            Toast.makeText(getContext(), "✅ Đăng xuất thành công", Toast.LENGTH_SHORT).show();

            // Quay lại LoginActivity
            requireActivity().finish();
        } catch (Exception e) {
            Log.e(TAG, "✗ Error logging out: " + e.getMessage());
            Toast.makeText(getContext(), "❌ Lỗi đăng xuất", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadStatistics();
    }
}