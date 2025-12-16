package com.example.campusexpensesmanagermer.Repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.campusexpensesmanagermer.Data.SQLiteDbHelper;
import com.example.campusexpensesmanagermer.Models.BudgetItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BudgetItemRepository {

    private SQLiteDbHelper dbHelper;
    private static final String TAG = "BudgetItemRepository";

    public BudgetItemRepository(Context context) {
        dbHelper = new SQLiteDbHelper(context);
    }

    /**
     * Thêm budget item
     */
    public long addBudgetItem(BudgetItem item) {
        if (item == null) {
            Log.e(TAG, "BudgetItem object is null");
            return -1;
        }

        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(SQLiteDbHelper.BUDGET_ID_ITEM, item.getBudgetId());
            values.put(SQLiteDbHelper.CATEGORY_ID_ITEM, item.getCategoryName());
            values.put(SQLiteDbHelper.ALLOCATED_AMOUNT_ITEM, item.getAllocatedAmount());

            long id = db.insert(SQLiteDbHelper.TABLE_BUDGET_ITEMS, null, values);
            Log.d(TAG, "✓ Insert budget item success - ID: " + id + ", Category: " + item.getCategoryName());
            return id;
        } catch (Exception e) {
            Log.e(TAG, "✗ Error adding budget item: " + e.getMessage());
            e.printStackTrace();
            return -1;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    /**
     * Lấy budget item theo ID
     */
    public BudgetItem getBudgetItemById(int itemId) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
            String query = "SELECT * FROM " + SQLiteDbHelper.TABLE_BUDGET_ITEMS +
                    " WHERE " + SQLiteDbHelper.ID_BUDGET_ITEM + " = ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(itemId)});

            if (cursor != null && cursor.moveToFirst()) {
                return cursorToBudgetItem(cursor);
            }
        } catch (Exception e) {
            Log.e(TAG, "✗ Error getting budget item: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
        return null;
    }

    /**
     * Lấy tất cả budget items của một budget
     */
    public List<BudgetItem> getBudgetItemsByBudget(int budgetId) {
        List<BudgetItem> list = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            String query = "SELECT * FROM " + SQLiteDbHelper.TABLE_BUDGET_ITEMS +
                    " WHERE " + SQLiteDbHelper.BUDGET_ID_ITEM + " = ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(budgetId)});

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    BudgetItem item = cursorToBudgetItem(cursor);
                    // ✅ FIX: Tính spent amount cho category này
                    item.setSpentAmount(getSpentAmountByCategory(budgetId, item.getCategoryName()));
                    list.add(item);
                }
                Log.d(TAG, "✓ Loaded " + list.size() + " budget items for budget: " + budgetId);
            }
        } catch (Exception e) {
            Log.e(TAG, "✗ Error getting budget items: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return list;
    }

    /**
     * ✅ FIX: Lấy spent amount cho một category trong budget
     * Sử dụng TRIM() để tránh khoảng trắng
     */
    private double getSpentAmountByCategory(int budgetId, String categoryName) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();

            // Lấy thông tin budget để biết month/year
            String budgetQuery = "SELECT * FROM " + SQLiteDbHelper.TABLE_BUDGETS +
                    " WHERE " + SQLiteDbHelper.ID_BUDGET + " = ?";
            Cursor budgetCursor = db.rawQuery(budgetQuery, new String[]{String.valueOf(budgetId)});

            if (budgetCursor == null || !budgetCursor.moveToFirst()) {
                if (budgetCursor != null) budgetCursor.close();
                return 0;
            }

            int year = budgetCursor.getInt(budgetCursor.getColumnIndexOrThrow(SQLiteDbHelper.YEAR_BUDGET));
            int month = budgetCursor.getInt(budgetCursor.getColumnIndexOrThrow(SQLiteDbHelper.MONTH_BUDGET));
            int userId = budgetCursor.getInt(budgetCursor.getColumnIndexOrThrow(SQLiteDbHelper.USER_ID_BUDGET));
            budgetCursor.close();

            // ✅ FIX: So sánh chính xác CATEGORY_ID_EXPRESS với TRIM()
            String query = "SELECT COALESCE(SUM(" + SQLiteDbHelper.AMOUNT_EXPRESS + "), 0) as total " +
                    "FROM " + SQLiteDbHelper.TABLE_EXPRESS +
                    " WHERE " + SQLiteDbHelper.USER_ID_EXPRESS + " = ? " +
                    "AND TRIM(" + SQLiteDbHelper.CATEGORY_ID_EXPRESS + ") = TRIM(?) " +
                    "AND strftime('%Y', " + SQLiteDbHelper.DATE_EXPRESS + ") = ? " +
                    "AND strftime('%m', " + SQLiteDbHelper.DATE_EXPRESS + ") = ?";

            cursor = db.rawQuery(query, new String[]{
                    String.valueOf(userId),
                    categoryName.trim(),  // ✅ Trim ở đây
                    String.valueOf(year),
                    String.format("%02d", month)
            });

            if (cursor != null && cursor.moveToFirst()) {
                double result = cursor.getDouble(0);
                Log.d(TAG, "Spent for category '" + categoryName + "' in " + month + "/" + year + ": " + result);
                return result;
            }
        } catch (Exception e) {
            Log.e(TAG, "✗ Error getting spent amount: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return 0;
    }

    /**
     * Cập nhật budget item
     */
    public boolean updateBudgetItem(BudgetItem item) {
        if (item == null || item.getId() == 0) {
            Log.e(TAG, "Invalid budget item for update");
            return false;
        }

        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(SQLiteDbHelper.ALLOCATED_AMOUNT_ITEM, item.getAllocatedAmount());

            int result = db.update(
                    SQLiteDbHelper.TABLE_BUDGET_ITEMS,
                    values,
                    SQLiteDbHelper.ID_BUDGET_ITEM + " = ?",
                    new String[]{String.valueOf(item.getId())}
            );

            boolean success = result > 0;
            Log.d(TAG, success ? "✓ Update budget item success" : "✗ Update budget item failed");
            return success;
        } catch (Exception e) {
            Log.e(TAG, "✗ Error updating budget item: " + e.getMessage());
            return false;
        } finally {
            if (db != null) db.close();
        }
    }

    /**
     * Xóa budget item
     */
    public boolean deleteBudgetItem(int itemId) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            int result = db.delete(
                    SQLiteDbHelper.TABLE_BUDGET_ITEMS,
                    SQLiteDbHelper.ID_BUDGET_ITEM + " = ?",
                    new String[]{String.valueOf(itemId)}
            );

            boolean success = result > 0;
            Log.d(TAG, success ? "✓ Delete budget item success" : "✗ Delete budget item failed");
            return success;
        } catch (Exception e) {
            Log.e(TAG, "✗ Error deleting budget item: " + e.getMessage());
            return false;
        } finally {
            if (db != null) db.close();
        }
    }

    /**
     * Xóa tất cả budget items của một budget
     */
    public boolean deleteBudgetItemsByBudget(int budgetId) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            int result = db.delete(
                    SQLiteDbHelper.TABLE_BUDGET_ITEMS,
                    SQLiteDbHelper.BUDGET_ID_ITEM + " = ?",
                    new String[]{String.valueOf(budgetId)}
            );

            boolean success = result > 0;
            Log.d(TAG, success ? "✓ Delete all budget items success" : "✗ Delete all budget items failed");
            return success;
        } catch (Exception e) {
            Log.e(TAG, "✗ Error deleting budget items: " + e.getMessage());
            return false;
        } finally {
            if (db != null) db.close();
        }
    }

    /**
     * Thêm nhiều budget items trong cùng một transaction
     */
    public boolean addBudgetItemsInTransaction(List<BudgetItem> items) {
        if (items == null || items.isEmpty()) {
            Log.e(TAG, "No items to insert");
            return false;
        }

        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            db.beginTransaction();

            for (BudgetItem item : items) {
                ContentValues values = new ContentValues();
                values.put(SQLiteDbHelper.BUDGET_ID_ITEM, item.getBudgetId());
                values.put(SQLiteDbHelper.CATEGORY_ID_ITEM, item.getCategoryName());
                values.put(SQLiteDbHelper.ALLOCATED_AMOUNT_ITEM, item.getAllocatedAmount());

                long id = db.insert(SQLiteDbHelper.TABLE_BUDGET_ITEMS, null, values);
                if (id <= 0) {
                    Log.e(TAG, "Failed to insert budget item: " + item.getCategoryName());
                    // continue to attempt others but mark as failure
                } else {
                    Log.d(TAG, "✓ Inserted budget item in tx - ID: " + id + ", Category: " + item.getCategoryName());
                }
            }

            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "✗ Error inserting budget items in transaction: " + e.getMessage(), e);
            return false;
        } finally {
            if (db != null) {
                try {
                    db.endTransaction();
                } catch (Exception ignored) {}
                db.close();
            }
        }
    }

    /**
     * Helper: Convert cursor to BudgetItem
     */
    private BudgetItem cursorToBudgetItem(Cursor cursor) {
        BudgetItem item = new BudgetItem();
        item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteDbHelper.ID_BUDGET_ITEM)));
        item.setBudgetId(cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteDbHelper.BUDGET_ID_ITEM)));
        item.setCategoryName(cursor.getString(cursor.getColumnIndexOrThrow(SQLiteDbHelper.CATEGORY_ID_ITEM)));
        item.setAllocatedAmount(cursor.getDouble(cursor.getColumnIndexOrThrow(SQLiteDbHelper.ALLOCATED_AMOUNT_ITEM)));
        item.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(SQLiteDbHelper.CREATED_AT)));
        return item;
    }
}