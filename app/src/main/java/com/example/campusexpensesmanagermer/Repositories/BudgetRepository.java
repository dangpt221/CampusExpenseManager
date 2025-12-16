package com.example.campusexpensesmanagermer.Repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.campusexpensesmanagermer.Data.SQLiteDbHelper;
import com.example.campusexpensesmanagermer.Models.Budget;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BudgetRepository {

    private final SQLiteDbHelper dbHelper;
    private static final String TAG = "BudgetRepository";

    public BudgetRepository(Context context) {
        dbHelper = new SQLiteDbHelper(context);
    }

    /**
     * Thêm ngân sách mới
     */
    public long addBudget(Budget budget) {
        if (budget == null) {
            Log.e(TAG, "Budget object is null");
            return -1;
        }

        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(SQLiteDbHelper.USER_ID_BUDGET, budget.getUserId());
            values.put(SQLiteDbHelper.YEAR_BUDGET, budget.getYear() > 0 ? budget.getYear() : Calendar.getInstance().get(Calendar.YEAR));
            values.put(SQLiteDbHelper.MONTH_BUDGET, budget.getMonth() > 0 ? budget.getMonth() : Calendar.getInstance().get(Calendar.MONTH) + 1);
            values.put(SQLiteDbHelper.TARGET_AMOUNT_BUDGET, budget.getMoney());
            values.put(SQLiteDbHelper.CURRENCY_BUDGET, "VND");
            values.put(SQLiteDbHelper.NOTE_BUDGET, budget.getDescription());

            long id = db.insert(SQLiteDbHelper.TABLE_BUDGETS, null, values);
            Log.d(TAG, "✓ Insert budget success - ID: " + id + ", UserId: " + budget.getUserId()
                    + ", Month: " + budget.getMonth() + ", Year: " + budget.getYear()
                    + ", Amount: " + budget.getMoney());
            return id;
        } catch (Exception e) {
            Log.e(TAG, "✗ Error adding budget: " + e.getMessage(), e);
            return -1;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    /**
     * Lấy ngân sách theo ID
     */
    public Budget getBudgetById(int budgetId) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
            String query = "SELECT * FROM " + SQLiteDbHelper.TABLE_BUDGETS +
                    " WHERE " + SQLiteDbHelper.ID_BUDGET + " = ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(budgetId)});

            if (cursor != null && cursor.moveToFirst()) {
                Budget budget = new Budget();
                budget.setId(cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteDbHelper.ID_BUDGET)));
                budget.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteDbHelper.USER_ID_BUDGET)));
                budget.setYear(cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteDbHelper.YEAR_BUDGET)));
                budget.setMonth(cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteDbHelper.MONTH_BUDGET)));
                budget.setMoney(cursor.getDouble(cursor.getColumnIndexOrThrow(SQLiteDbHelper.TARGET_AMOUNT_BUDGET)));
                budget.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(SQLiteDbHelper.NOTE_BUDGET)));

                Log.d(TAG, "✓ Found budget ID: " + budgetId);
                return budget;
            }
        } catch (Exception e) {
            Log.e(TAG, "✗ Error getting budget: " + e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
        return null;
    }

    /**
     * ✅ FIX: Lấy ngân sách theo tháng/năm
     * Trả về budget mới nhất nếu có nhiều budget cho cùng tháng/năm
     */
    public Budget getBudgetByUserAndMonth(int userId, int month, int year) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
            String query = "SELECT * FROM " + SQLiteDbHelper.TABLE_BUDGETS +
                    " WHERE " + SQLiteDbHelper.USER_ID_BUDGET + " = ? " +
                    "AND " + SQLiteDbHelper.MONTH_BUDGET + " = ? " +
                    "AND " + SQLiteDbHelper.YEAR_BUDGET + " = ? " +
                    "ORDER BY " + SQLiteDbHelper.ID_BUDGET + " DESC LIMIT 1";
            cursor = db.rawQuery(query, new String[]{String.valueOf(userId), String.valueOf(month), String.valueOf(year)});

            if (cursor != null && cursor.moveToFirst()) {
                Budget budget = new Budget();
                budget.setId(cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteDbHelper.ID_BUDGET)));
                budget.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteDbHelper.USER_ID_BUDGET)));
                budget.setYear(cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteDbHelper.YEAR_BUDGET)));
                budget.setMonth(cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteDbHelper.MONTH_BUDGET)));
                budget.setMoney(cursor.getDouble(cursor.getColumnIndexOrThrow(SQLiteDbHelper.TARGET_AMOUNT_BUDGET)));
                budget.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(SQLiteDbHelper.NOTE_BUDGET)));

                // ✅ FIX: Tính tổng chi tiêu chính xác
                budget.setSpent(getTotalSpentByBudget(budget.getId()));

                Log.d(TAG, "✓ Found budget for " + month + "/" + year + " (userId=" + userId
                        + "), Spent: " + budget.getSpent());
                return budget;
            }
        } catch (Exception e) {
            Log.e(TAG, "✗ Error getting budget: " + e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
        return null;
    }

    /**
     * Lấy tất cả ngân sách của user
     */
    public List<Budget> getAllBudgetsByUser(int userId) {
        List<Budget> list = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            String query = "SELECT * FROM " + SQLiteDbHelper.TABLE_BUDGETS +
                    " WHERE " + SQLiteDbHelper.USER_ID_BUDGET + " = ? " +
                    "ORDER BY " + SQLiteDbHelper.YEAR_BUDGET + " DESC, " +
                    SQLiteDbHelper.MONTH_BUDGET + " DESC";
            cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    Budget budget = new Budget();
                    budget.setId(cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteDbHelper.ID_BUDGET)));
                    budget.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteDbHelper.USER_ID_BUDGET)));
                    budget.setYear(cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteDbHelper.YEAR_BUDGET)));
                    budget.setMonth(cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteDbHelper.MONTH_BUDGET)));
                    budget.setMoney(cursor.getDouble(cursor.getColumnIndexOrThrow(SQLiteDbHelper.TARGET_AMOUNT_BUDGET)));
                    budget.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(SQLiteDbHelper.NOTE_BUDGET)));

                    budget.setSpent(getTotalSpentByBudget(budget.getId()));
                    list.add(budget);
                }
                Log.d(TAG, "✓ Loaded " + list.size() + " budgets for userId: " + userId);
            }
        } catch (Exception e) {
            Log.e(TAG, "✗ Error getting budgets: " + e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return list;
    }

    /**
     * Cập nhật ngân sách
     */
    public boolean updateBudget(Budget budget) {
        if (budget == null || budget.getId() == 0) {
            Log.e(TAG, "Invalid budget object for update");
            return false;
        }

        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(SQLiteDbHelper.MONTH_BUDGET, budget.getMonth());
            values.put(SQLiteDbHelper.YEAR_BUDGET, budget.getYear());
            values.put(SQLiteDbHelper.TARGET_AMOUNT_BUDGET, budget.getMoney());
            values.put(SQLiteDbHelper.NOTE_BUDGET, budget.getDescription());
            values.put(SQLiteDbHelper.UPDATED_AT, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));

            int result = db.update(
                    SQLiteDbHelper.TABLE_BUDGETS,
                    values,
                    SQLiteDbHelper.ID_BUDGET + " = ?",
                    new String[]{String.valueOf(budget.getId())}
            );

            boolean success = result > 0;
            Log.d(TAG, success ? "✓ Update budget success" : "✗ Update budget failed");
            return success;
        } catch (Exception e) {
            Log.e(TAG, "✗ Error updating budget: " + e.getMessage(), e);
            return false;
        } finally {
            if (db != null) db.close();
        }
    }

    /**
     * Cập nhật ngân sách và chia đều số tiền cho các mục con của budget
     */
    public boolean updateBudgetAndSplit(int budgetId, double newAmount) {
        if (budgetId <= 0) {
            Log.e(TAG, "Invalid budgetId for updateBudgetAndSplit");
            return false;
        }

        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getWritableDatabase();
            db.beginTransaction();

            // 1) Cập nhật bản ghi budgets
            ContentValues values = new ContentValues();
            values.put(SQLiteDbHelper.TARGET_AMOUNT_BUDGET, newAmount);
            int updated = db.update(SQLiteDbHelper.TABLE_BUDGETS, values, SQLiteDbHelper.ID_BUDGET + " = ?",
                    new String[]{String.valueOf(budgetId)});

            if (updated <= 0) {
                Log.e(TAG, "✗ Update budget failed for id: " + budgetId);
                db.endTransaction();
                return false;
            }

            // 2) Lấy danh sách budget items của budget này theo id (để phân phối phần dư một cách xác định)
            String q = "SELECT " + SQLiteDbHelper.ID_BUDGET_ITEM + " FROM " + SQLiteDbHelper.TABLE_BUDGET_ITEMS +
                    " WHERE " + SQLiteDbHelper.BUDGET_ID_ITEM + " = ? ORDER BY " + SQLiteDbHelper.ID_BUDGET_ITEM + " ASC";
            cursor = db.rawQuery(q, new String[]{String.valueOf(budgetId)});

            List<Integer> itemIds = new java.util.ArrayList<>();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    itemIds.add(cursor.getInt(0));
                } while (cursor.moveToNext());
            }

            int n = itemIds.size();
            if (n > 0) {
                // Dùng đơn vị cents để tránh lỗi làm tròn
                long totalCents = Math.round(newAmount * 100.0);
                long base = totalCents / n;
                int rem = (int) (totalCents % n);

                for (int i = 0; i < n; i++) {
                    long amountCents = base + (i < rem ? 1 : 0);
                    double allocated = amountCents / 100.0;

                    ContentValues v2 = new ContentValues();
                    v2.put(SQLiteDbHelper.ALLOCATED_AMOUNT_ITEM, allocated);

                    int r = db.update(SQLiteDbHelper.TABLE_BUDGET_ITEMS, v2,
                            SQLiteDbHelper.ID_BUDGET_ITEM + " = ?", new String[]{String.valueOf(itemIds.get(i))});

                    if (r <= 0) {
                        Log.e(TAG, "✗ Failed to update budget item id: " + itemIds.get(i));
                        // continue trying to update others but mark failure
                    } else {
                        Log.d(TAG, "✓ Updated budget item id=" + itemIds.get(i) + " allocated=" + allocated);
                    }
                }
            } else {
                Log.d(TAG, "No budget items found for budgetId=" + budgetId + ", nothing to split.");
            }

            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "✗ Error in updateBudgetAndSplit: " + e.getMessage(), e);
            return false;
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) {
                try {
                    db.endTransaction();
                } catch (Exception ignored) {}
                db.close();
            }
        }
    }

    /**
     * Xóa ngân sách
     */
    public boolean deleteBudget(int budgetId) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();

            // Xóa budget items trước
            db.delete(SQLiteDbHelper.TABLE_BUDGET_ITEMS,
                    SQLiteDbHelper.BUDGET_ID_ITEM + " = ?",
                    new String[]{String.valueOf(budgetId)});

            // Xóa budget
            int result = db.delete(
                    SQLiteDbHelper.TABLE_BUDGETS,
                    SQLiteDbHelper.ID_BUDGET + " = ?",
                    new String[]{String.valueOf(budgetId)}
            );

            boolean success = result > 0;
            Log.d(TAG, success ? "✓ Delete budget success" : "✗ Delete budget failed");
            return success;
        } catch (Exception e) {
            Log.e(TAG, "✗ Error deleting budget: " + e.getMessage(), e);
            return false;
        } finally {
            if (db != null) db.close();
        }
    }

    /**
     * ✅ FIX: Tính tổng chi tiêu theo budget (sum của tất cả chi tiêu trong tháng/năm)
     * Sử dụng TRIM() để so sánh chính xác category
     */
    public double getTotalSpentByBudget(int budgetId) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();

            Budget budget = getBudgetById(budgetId);
            if (budget == null) {
                return 0;
            }

            // ✅ FIX: Tính tổng chi tiêu từ express trong tháng/năm
            String query = "SELECT COALESCE(SUM(e." + SQLiteDbHelper.AMOUNT_EXPRESS + "), 0) as total " +
                    "FROM " + SQLiteDbHelper.TABLE_EXPRESS + " e " +
                    "WHERE e." + SQLiteDbHelper.USER_ID_EXPRESS + " = ? " +
                    "AND strftime('%Y', e." + SQLiteDbHelper.DATE_EXPRESS + ") = ? " +
                    "AND strftime('%m', e." + SQLiteDbHelper.DATE_EXPRESS + ") = ?";

            cursor = db.rawQuery(query, new String[]{
                    String.valueOf(budget.getUserId()),
                    String.valueOf(budget.getYear()),
                    String.format(Locale.getDefault(), "%02d", budget.getMonth())
            });

            if (cursor != null && cursor.moveToFirst()) {
                double total = cursor.getDouble(0);
                Log.d(TAG, "Total spent for budget " + budgetId + " (" + budget.getMonth()
                        + "/" + budget.getYear() + "): " + total);
                return total;
            }
        } catch (Exception e) {
            Log.e(TAG, "✗ Error calculating total spent: " + e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return 0;
    }

    /**
     * ✅ FIX: Tính chi tiêu theo category
     * Sử dụng TRIM() để so sánh chính xác
     */
    public double getSpentByCategory(int budgetId, String categoryName) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
            Budget budget = getBudgetById(budgetId);
            if (budget == null) return 0;

            String query = "SELECT COALESCE(SUM(e." + SQLiteDbHelper.AMOUNT_EXPRESS + "), 0) as total " +
                    "FROM " + SQLiteDbHelper.TABLE_EXPRESS + " e " +
                    "WHERE e." + SQLiteDbHelper.USER_ID_EXPRESS + " = ? " +
                    "AND TRIM(e." + SQLiteDbHelper.CATEGORY_ID_EXPRESS + ") = TRIM(?) " +
                    "AND strftime('%Y', e." + SQLiteDbHelper.DATE_EXPRESS + ") = ? " +
                    "AND strftime('%m', e." + SQLiteDbHelper.DATE_EXPRESS + ") = ?";

            cursor = db.rawQuery(query, new String[]{
                    String.valueOf(budget.getUserId()),
                    categoryName.trim(),
                    String.valueOf(budget.getYear()),
                    String.format(Locale.getDefault(), "%02d", budget.getMonth())
            });

            if (cursor != null && cursor.moveToFirst()) {
                double result = cursor.getDouble(0);
                Log.d(TAG, "Spent for category '" + categoryName + "': " + result);
                return result;
            }
        } catch (Exception e) {
            Log.e(TAG, "✗ Error calculating spent by category: " + e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return 0;
    }
}