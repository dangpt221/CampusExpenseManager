package com.example.campusexpensesmanagermer.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.example.campusexpensesmanagermer.Activities.LoginActivity;
import com.example.campusexpensesmanagermer.Data.SQLiteDbHelper;
import com.example.campusexpensesmanagermer.R;
import com.example.campusexpensesmanagermer.Utils.CurrencyUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class ProfileFragment extends Fragment {

    private ImageView ivAvatar;
    private TextView tvUsername, tvEmail, tvPhone, tvRole, tvStatus;
    private TextView tvYearlySpend, tvTransactionCount, tvTopCategory, tvAverageSpend;
    private TextView tvMonthlySpend, tvLastExpense;
    private Button btnEditProfile, btnChangePassword, btnLogout;

    private SharedPreferences prefs;
    private SQLiteDbHelper dbHelper;
    private int userId;
    private String avatarBase64;

    private static final String PREFS_NAME = "CampusExpensesPrefs";
    private static final String TAG = "ProfileFragment";

    // Launcher để chọn ảnh từ thư viện
    private ActivityResultLauncher<Intent> pickImageLauncher;

    public ProfileFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            prefs = requireActivity().getSharedPreferences(PREFS_NAME, 0);
            dbHelper = new SQLiteDbHelper(getContext());
            userId = prefs.getInt("ID_USER", 0);

            // Khởi tạo launcher để chọn ảnh
            pickImageLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            Uri imageUri = result.getData().getData();
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                                        requireActivity().getContentResolver(), imageUri);
                                // Resize ảnh để tránh quá lớn
                                Bitmap resized = resizeBitmap(bitmap, 300, 300);
                                ivAvatar.setImageBitmap(resized);
                                avatarBase64 = bitmapToBase64(resized);
                                saveAvatarToDatabase(avatarBase64);
                            } catch (IOException e) {
                                Log.e(TAG, "Error loading image: " + e.getMessage());
                                Toast.makeText(getContext(), "Error loading image", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );

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
            loadAvatarFromDatabase();
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
        // Click vào avatar để đổi ảnh
        ivAvatar.setOnClickListener(v -> showChangeAvatarDialog());

        // Chỉnh sửa profile
        btnEditProfile.setOnClickListener(v -> showEditProfileDialog());

        // Đổi mật khẩu
        btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());

        // Đăng xuất
        btnLogout.setOnClickListener(v -> showLogoutConfirmDialog());
    }

    // ==================== LOAD USER INFO ====================
    private void loadUserInfo() {
        try {
            String username = prefs.getString("username", "User");
            String email = prefs.getString("email", "N/A");
            int role = prefs.getInt("role", 0);

            // Lấy phone từ database
            String phone = getPhoneFromDatabase();

            tvUsername.setText(username);
            tvEmail.setText(email);
            tvPhone.setText(phone.isEmpty() ? "N/A" : phone);
            tvRole.setText(role == 0 ? "Regular user" : "Admin");
            tvStatus.setText("✅ Active");

            Log.d(TAG, "✓ User info loaded");
        } catch (Exception e) {
            Log.e(TAG, "✗ Error loading user info: " + e.getMessage());
        }
    }

    private String getPhoneFromDatabase() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
            String query = "SELECT " + SQLiteDbHelper.PHONE_USER + " FROM "
                    + SQLiteDbHelper.TABLE_USER + " WHERE " + SQLiteDbHelper.ID_USER + " = ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting phone: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
        return "";
    }

    // ==================== AVATAR ====================
    private void showChangeAvatarDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Change avatar")
                .setMessage("Select image from gallery?")
                .setPositiveButton("Choose image", (dialog, which) -> openGallery())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    private void loadAvatarFromDatabase() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
            String query = "SELECT " + SQLiteDbHelper.AVATAR_PATH_USER + " FROM "
                    + SQLiteDbHelper.TABLE_USER + " WHERE " + SQLiteDbHelper.ID_USER + " = ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

            if (cursor != null && cursor.moveToFirst()) {
                String base64 = cursor.getString(0);
                if (!TextUtils.isEmpty(base64)) {
                    Bitmap bitmap = base64ToBitmap(base64);
                    if (bitmap != null) {
                        ivAvatar.setImageBitmap(bitmap);
                        avatarBase64 = base64;
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading avatar: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
    }

    private void saveAvatarToDatabase(String base64) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            String sql = "UPDATE " + SQLiteDbHelper.TABLE_USER
                    + " SET " + SQLiteDbHelper.AVATAR_PATH_USER + " = ? "
                    + " WHERE " + SQLiteDbHelper.ID_USER + " = ?";
            db.execSQL(sql, new Object[]{base64, userId});
            Toast.makeText(getContext(), "✅ Avatar updated", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Avatar saved successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error saving avatar: " + e.getMessage());
            Toast.makeText(getContext(), "❌ Error saving image", Toast.LENGTH_SHORT).show();
        } finally {
            if (db != null) db.close();
        }
    }

    // ==================== EDIT PROFILE ====================
    private void showEditProfileDialog() {
        View dialogView = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_edit_profile, null);

        EditText edtUsername = dialogView.findViewById(R.id.edt_username);
        EditText edtEmail = dialogView.findViewById(R.id.edt_email);
        EditText edtPhone = dialogView.findViewById(R.id.edt_phone);

        // Fill current data
        edtUsername.setText(tvUsername.getText().toString());
        edtEmail.setText(tvEmail.getText().toString());
        String currentPhone = tvPhone.getText().toString();
        edtPhone.setText(currentPhone.equals("N/A") ? "" : currentPhone);

        new AlertDialog.Builder(getContext())
                .setTitle("✏️ Edit profile")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newUsername = edtUsername.getText().toString().trim();
                    String newEmail = edtEmail.getText().toString().trim();
                    String newPhone = edtPhone.getText().toString().trim();

                    if (TextUtils.isEmpty(newUsername)) {
                        Toast.makeText(getContext(), "Name cannot be empty",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    updateUserProfile(newUsername, newEmail, newPhone);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateUserProfile(String username, String email, String phone) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            String sql = "UPDATE " + SQLiteDbHelper.TABLE_USER
                    + " SET " + SQLiteDbHelper.USERNAME_USER + " = ?, "
                    + SQLiteDbHelper.EMAIL_USER + " = ?, "
                    + SQLiteDbHelper.PHONE_USER + " = ? "
                    + " WHERE " + SQLiteDbHelper.ID_USER + " = ?";

            db.execSQL(sql, new Object[]{username, email, phone, userId});

            // Cập nhật SharedPreferences
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("username", username);
            editor.putString("email", email);
            editor.apply();

            // Refresh UI
            loadUserInfo();

            Toast.makeText(getContext(), "✅ Update successful", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Profile updated successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error updating profile: " + e.getMessage());
            Toast.makeText(getContext(), "❌ Update failed", Toast.LENGTH_SHORT).show();
        } finally {
            if (db != null) db.close();
        }
    }

    // ==================== CHANGE PASSWORD ====================
    private void showChangePasswordDialog() {
        View dialogView = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_change_password, null);

        EditText edtOldPassword = dialogView.findViewById(R.id.edt_old_password);
        EditText edtNewPassword = dialogView.findViewById(R.id.edt_new_password);
        EditText edtConfirmPassword = dialogView.findViewById(R.id.edt_confirm_password);

        new AlertDialog.Builder(getContext())
                .setTitle("🔐 Change password")
                .setView(dialogView)
                .setPositiveButton("Change", (dialog, which) -> {
                    String oldPass = edtOldPassword.getText().toString().trim();
                    String newPass = edtNewPassword.getText().toString().trim();
                    String confirmPass = edtConfirmPassword.getText().toString().trim();

                    if (TextUtils.isEmpty(oldPass) || TextUtils.isEmpty(newPass)) {
                        Toast.makeText(getContext(), "Please fill in all fields",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!newPass.equals(confirmPass)) {
                        Toast.makeText(getContext(), "New password does not match",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (newPass.length() < 6) {
                        Toast.makeText(getContext(), "Password must be at least 6 characters",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    changePassword(oldPass, newPass);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void changePassword(String oldPassword, String newPassword) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();

            // Kiểm tra mật khẩu cũ
            String query = "SELECT " + SQLiteDbHelper.PASSWORD_USER + " FROM "
                    + SQLiteDbHelper.TABLE_USER + " WHERE " + SQLiteDbHelper.ID_USER + " = ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

            if (cursor != null && cursor.moveToFirst()) {
                String currentPassword = cursor.getString(0);

                if (!oldPassword.equals(currentPassword)) {
                    Toast.makeText(getContext(), "❌ Old password is incorrect",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // Cập nhật mật khẩu mới
                db = dbHelper.getWritableDatabase();
                String sql = "UPDATE " + SQLiteDbHelper.TABLE_USER
                        + " SET " + SQLiteDbHelper.PASSWORD_USER + " = ? "
                        + " WHERE " + SQLiteDbHelper.ID_USER + " = ?";

                db.execSQL(sql, new Object[]{newPassword, userId});

                Toast.makeText(getContext(), "✅ Password changed successfully",
                        Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Password changed successfully");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error changing password: " + e.getMessage());
            Toast.makeText(getContext(), "❌ Error changing password", Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
    }

    // ==================== STATISTICS ====================
    private void loadStatistics() {
        if (userId == 0) {
            Log.e(TAG, "Cannot load statistics - userId is 0");
            return;
        }

        try {
            double yearlySpend = getTotalSpendThisYear();
            tvYearlySpend.setText(CurrencyUtils.formatCurrency(requireContext(), yearlySpend));

            double monthlySpend = getTotalSpendThisMonth();
            tvMonthlySpend.setText(CurrencyUtils.formatCurrency(requireContext(), monthlySpend));

            int transactionCount = getTransactionCount();
            tvTransactionCount.setText(String.valueOf(transactionCount));

            double averageSpend = transactionCount > 0 ? yearlySpend / transactionCount : 0;
            tvAverageSpend.setText(CurrencyUtils.formatCurrency(requireContext(), averageSpend));

            String topCategory = getTopCategory();
            tvTopCategory.setText(topCategory);

            String lastExpense = getLastExpense();
            tvLastExpense.setText(lastExpense);

            Log.d(TAG, "✓ Statistics loaded");
        } catch (Exception e) {
            Log.e(TAG, "✗ Error loading statistics: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private double getTotalSpendThisYear() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);

            String query = "SELECT SUM(" + SQLiteDbHelper.AMOUNT_EXPRESS + ") as total FROM "
                    + SQLiteDbHelper.TABLE_EXPRESS
                    + " WHERE " + SQLiteDbHelper.USER_ID_EXPRESS + " = ? "
                    + "AND strftime('%Y', " + SQLiteDbHelper.DATE_EXPRESS + ") = ?";

            cursor = db.rawQuery(query, new String[]{String.valueOf(userId),
                    String.valueOf(currentYear)});

            if (cursor.moveToFirst()) {
                return cursor.getDouble(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
        return 0;
    }

    private double getTotalSpendThisMonth() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
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
                return cursor.getDouble(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
        return 0;
    }

    private int getTransactionCount() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
            String query = "SELECT COUNT(*) as count FROM " + SQLiteDbHelper.TABLE_EXPRESS
                    + " WHERE " + SQLiteDbHelper.USER_ID_EXPRESS + " = ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
        return 0;
    }

    private String getTopCategory() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
            String query = "SELECT " + SQLiteDbHelper.CATEGORY_ID_EXPRESS
                    + ", COUNT(*) as count FROM " + SQLiteDbHelper.TABLE_EXPRESS
                    + " WHERE " + SQLiteDbHelper.USER_ID_EXPRESS + " = ? "
                    + "GROUP BY " + SQLiteDbHelper.CATEGORY_ID_EXPRESS
                    + " ORDER BY count DESC LIMIT 1";

            cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

            if (cursor.moveToFirst()) {
                String category = cursor.getString(0);
                return category != null ? category : "No data";
            }
        } catch (Exception e) {
            Log.e(TAG, "Error: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
        return "No data";
    }

    private String getLastExpense() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
            String query = "SELECT " + SQLiteDbHelper.TITLE_EXPRESS + ", "
                    + SQLiteDbHelper.AMOUNT_EXPRESS
                    + " FROM " + SQLiteDbHelper.TABLE_EXPRESS
                    + " WHERE " + SQLiteDbHelper.USER_ID_EXPRESS + " = ? "
                    + "ORDER BY " + SQLiteDbHelper.DATE_EXPRESS + " DESC LIMIT 1";

            cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

            if (cursor.moveToFirst()) {
                String title = cursor.getString(0);
                double amount = cursor.getDouble(1);
                return title + " - " + CurrencyUtils.formatCurrency(requireContext(), amount);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
        return "No data";
    }

    // ==================== LOGOUT ====================
    private void showLogoutConfirmDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Confirm logout")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Log out", (dialog, which) -> logout())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void logout() {
        try {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

            Toast.makeText(getContext(), "✅ Logged out successfully", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        } catch (Exception e) {
            Log.e(TAG, "Error logging out: " + e.getMessage());
            Toast.makeText(getContext(), "❌ Error logging out", Toast.LENGTH_SHORT).show();
        }
    }

    // ==================== IMAGE UTILS ====================
    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private Bitmap base64ToBitmap(String base64) {
        try {
            byte[] decodedBytes = Base64.decode(base64, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (Exception e) {
            Log.e(TAG, "Error decoding base64: " + e.getMessage());
            return null;
        }
    }

    private Bitmap resizeBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float ratioBitmap = (float) width / (float) height;
        float ratioMax = (float) maxWidth / (float) maxHeight;

        int finalWidth = maxWidth;
        int finalHeight = maxHeight;

        if (ratioMax > ratioBitmap) {
            finalWidth = (int) ((float) maxHeight * ratioBitmap);
        } else {
            finalHeight = (int) ((float) maxWidth / ratioBitmap);
        }

        return Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, true);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadStatistics();
    }
}

