package com.example.campusexpensesmanagermer.Repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.campusexpensesmanagermer.Data.SQLiteDbHelper;
import com.example.campusexpensesmanagermer.Models.Express;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExpressRepository {

    private SQLiteDbHelper dbHelper;
    private static final String TAG = "ExpressRepository";

    public ExpressRepository(Context context) {
        dbHelper = new SQLiteDbHelper(context);
    }

    /**
     * Thêm chi tiêu mới vào database
     */
    public long addExpress(Express express) {
        if (express == null) {
            Log.e(TAG, "Express object is null");
            return -1;
        }

        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();

            // Sử dụng đúng tên cột từ SQLiteDbHelper
            values.put(SQLiteDbHelper.TITLE_EXPRESS, express.getTitle());
            values.put(SQLiteDbHelper.AMOUNT_EXPRESS, express.getAmount());
            values.put(SQLiteDbHelper.CATEGORY_ID_EXPRESS, express.getCategoryName());
            values.put(SQLiteDbHelper.USER_ID_EXPRESS, express.getUserId());
            values.put(SQLiteDbHelper.CURRENCY_EXPRESS, "VND");

            // Lưu ngày giờ hiện tại
            String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    .format(new Date());
            values.put(SQLiteDbHelper.DATE_EXPRESS, currentDateTime);
            values.put(SQLiteDbHelper.STATUS_EXPRESS, 1);

            long id = db.insert(SQLiteDbHelper.TABLE_EXPRESS, null, values);
            Log.d(TAG, "✓ Insert express success - ID: " + id + ", UserId: " + express.getUserId()
                    + ", Title: " + express.getTitle() + ", Amount: " + express.getAmount());
            return id;
        } catch (Exception e) {
            Log.e(TAG, "✗ Error adding express: " + e.getMessage());
            e.printStackTrace();
            return -1;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    /**
     * Lấy tất cả chi tiêu của user
     */
    public List<Express> getAllExpressByUser(int userId) {
        List<Express> list = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();

            String[] columns = {
                    SQLiteDbHelper.ID_EXPRESS,
                    SQLiteDbHelper.TITLE_EXPRESS,
                    SQLiteDbHelper.AMOUNT_EXPRESS,
                    SQLiteDbHelper.CATEGORY_ID_EXPRESS,
                    SQLiteDbHelper.USER_ID_EXPRESS,
                    SQLiteDbHelper.DATE_EXPRESS
            };

            String selection = SQLiteDbHelper.USER_ID_EXPRESS + " = ?";
            String[] selectionArgs = {String.valueOf(userId)};

            cursor = db.query(
                    SQLiteDbHelper.TABLE_EXPRESS,
                    columns,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    SQLiteDbHelper.ID_EXPRESS + " DESC"
            );

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    Express e = new Express();
                    e.setId(cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteDbHelper.ID_EXPRESS)));
                    e.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(SQLiteDbHelper.TITLE_EXPRESS)));
                    e.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow(SQLiteDbHelper.AMOUNT_EXPRESS)));
                    e.setCategoryName(cursor.getString(cursor.getColumnIndexOrThrow(SQLiteDbHelper.CATEGORY_ID_EXPRESS)));
                    e.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteDbHelper.USER_ID_EXPRESS)));
                    list.add(e);
                }
                Log.d(TAG, "✓ Loaded " + list.size() + " expenses for userId: " + userId);
            } else {
                Log.d(TAG, "No expenses found for userId: " + userId);
            }
        } catch (Exception e) {
            Log.e(TAG, "✗ Error getting expenses: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return list;
    }

    /**
     * Tính tổng chi tiêu của user
     */
    public double getTotalExpenseByUser(int userId) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        double total = 0;

        try {
            db = dbHelper.getReadableDatabase();
            String query = "SELECT SUM(" + SQLiteDbHelper.AMOUNT_EXPRESS + ") as total FROM "
                    + SQLiteDbHelper.TABLE_EXPRESS + " WHERE " + SQLiteDbHelper.USER_ID_EXPRESS + " = ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

            if (cursor != null && cursor.moveToFirst()) {
                total = cursor.getDouble(0);
            }
            Log.d(TAG, "Total expense for userId " + userId + ": " + total);
        } catch (Exception e) {
            Log.e(TAG, "✗ Error calculating total: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return total;
    }

    /**
     * Xóa chi tiêu
     */
    public boolean deleteExpress(int expressId) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            int result = db.delete(
                    SQLiteDbHelper.TABLE_EXPRESS,
                    SQLiteDbHelper.ID_EXPRESS + " = ?",
                    new String[]{String.valueOf(expressId)}
            );
            boolean success = result > 0;
            Log.d(TAG, success ? "✓ Delete express success" : "✗ Delete express failed");
            return success;
        } catch (Exception e) {
            Log.e(TAG, "✗ Error deleting express: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    /**
     * Cập nhật chi tiêu
     */
    public boolean updateExpress(Express express) {
        if (express == null || express.getId() == 0) {
            Log.e(TAG, "Invalid express object for update");
            return false;
        }

        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(SQLiteDbHelper.TITLE_EXPRESS, express.getTitle());
            values.put(SQLiteDbHelper.AMOUNT_EXPRESS, express.getAmount());
            values.put(SQLiteDbHelper.CATEGORY_ID_EXPRESS, express.getCategoryName());

            int result = db.update(
                    SQLiteDbHelper.TABLE_EXPRESS,
                    values,
                    SQLiteDbHelper.ID_EXPRESS + " = ?",
                    new String[]{String.valueOf(express.getId())}
            );
            boolean success = result > 0;
            Log.d(TAG, success ? "✓ Update express success" : "✗ Update express failed");
            return success;
        } catch (Exception e) {
            Log.e(TAG, "✗ Error updating express: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }
}