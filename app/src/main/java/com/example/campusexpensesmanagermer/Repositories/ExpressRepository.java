package com.example.campusexpensesmanagermer.Repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.campusexpensesmanagermer.Data.SQLiteDbHelper;
import com.example.campusexpensesmanagermer.Models.ExpenseReport;
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
     * ✅ FIX: Thêm chi tiêu mới vào database
     * Cải thiện: Lưu categoryName với trim(), tránh khoảng trắng
     */
    // Cập nhật method addExpress trong ExpressRepository.java

    public long addExpress(Express express) {
        if (express == null) {
            Log.e(TAG, "Express object is null");
            return -1;
        }

        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(SQLiteDbHelper.TITLE_EXPRESS, express.getTitle());
            values.put(SQLiteDbHelper.AMOUNT_EXPRESS, express.getAmount());
            values.put(SQLiteDbHelper.CATEGORY_ID_EXPRESS, express.getCategoryName().trim());
            values.put(SQLiteDbHelper.USER_ID_EXPRESS, express.getUserId());
            values.put(SQLiteDbHelper.CURRENCY_EXPRESS, "VND");

            // ✅ NEW: Use custom date if provided, otherwise use current datetime
            String dateTime;
            if (express.getDate() != null && !express.getDate().isEmpty()) {
                dateTime = express.getDate(); // Use provided date
            } else {
                dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        .format(new Date()); // Use current datetime
            }
            values.put(SQLiteDbHelper.DATE_EXPRESS, dateTime);
            values.put(SQLiteDbHelper.STATUS_EXPRESS, 1);

            long id = db.insert(SQLiteDbHelper.TABLE_EXPRESS, null, values);
            Log.d(TAG, "✓ Insert express success - ID: " + id + ", UserId: " + express.getUserId()
                    + ", Title: " + express.getTitle() + ", Amount: " + express.getAmount()
                    + ", Category: " + express.getCategoryName() + ", Date: " + dateTime);
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
                    SQLiteDbHelper.DATE_EXPRESS + " DESC, " + SQLiteDbHelper.ID_EXPRESS + " DESC"
            );

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    Express e = new Express();
                    e.setId(cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteDbHelper.ID_EXPRESS)));
                    e.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(SQLiteDbHelper.TITLE_EXPRESS)));
                    e.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow(SQLiteDbHelper.AMOUNT_EXPRESS)));
                    e.setCategoryName(cursor.getString(cursor.getColumnIndexOrThrow(SQLiteDbHelper.CATEGORY_ID_EXPRESS)));
                    e.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteDbHelper.USER_ID_EXPRESS)));
                    e.setDate(cursor.getString(cursor.getColumnIndexOrThrow(SQLiteDbHelper.DATE_EXPRESS)));
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
            values.put(SQLiteDbHelper.CATEGORY_ID_EXPRESS, express.getCategoryName().trim());

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

    // ==================== PHẦN BÁO CÁO ====================

    /**
     * Lấy tổng chi tiêu theo khoảng thời gian
     */
    public double getTotalExpenseByPeriod(int userId, String startDate, String endDate) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        double total = 0;

        try {
            db = dbHelper.getReadableDatabase();

            String query = "SELECT COALESCE(SUM(" + SQLiteDbHelper.AMOUNT_EXPRESS + "), 0) as total " +
                    "FROM " + SQLiteDbHelper.TABLE_EXPRESS +
                    " WHERE " + SQLiteDbHelper.USER_ID_EXPRESS + " = ? " +
                    "AND " + SQLiteDbHelper.DATE_EXPRESS + " >= ? " +
                    "AND " + SQLiteDbHelper.DATE_EXPRESS + " <= ?";

            cursor = db.rawQuery(query, new String[]{String.valueOf(userId), startDate, endDate});

            if (cursor != null && cursor.moveToFirst()) {
                total = cursor.getDouble(0);
            }
            Log.d(TAG, "Total for period: " + total);
        } catch (Exception e) {
            Log.e(TAG, "✗ Error getTotalExpenseByPeriod: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return total;
    }

    /**
     * ✅ FIX: Lấy báo cáo chi tiêu theo danh mục
     * Sử dụng TRIM() để so sánh chính xác
     */
    public List<ExpenseReport> getExpenseReportByCategory(int userId, String startDate, String endDate) {
        List<ExpenseReport> reportList = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();

            String query = "SELECT TRIM(" + SQLiteDbHelper.CATEGORY_ID_EXPRESS + ") as categoryName, " +
                    "SUM(" + SQLiteDbHelper.AMOUNT_EXPRESS + ") as totalAmount, " +
                    "COUNT(*) as transactionCount " +
                    "FROM " + SQLiteDbHelper.TABLE_EXPRESS +
                    " WHERE " + SQLiteDbHelper.USER_ID_EXPRESS + " = ? " +
                    "AND " + SQLiteDbHelper.DATE_EXPRESS + " >= ? " +
                    "AND " + SQLiteDbHelper.DATE_EXPRESS + " <= ? " +
                    "GROUP BY TRIM(" + SQLiteDbHelper.CATEGORY_ID_EXPRESS + ") " +
                    "ORDER BY totalAmount DESC";

            cursor = db.rawQuery(query, new String[]{String.valueOf(userId), startDate, endDate});

            double grandTotal = getTotalExpenseByPeriod(userId, startDate, endDate);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    ExpenseReport report = new ExpenseReport();
                    report.setCategoryName(cursor.getString(0));
                    report.setTotalAmount(cursor.getDouble(1));
                    report.setTransactionCount(cursor.getInt(2));

                    if (grandTotal > 0) {
                        report.setPercentage((report.getTotalAmount() / grandTotal) * 100);
                    }

                    reportList.add(report);
                }
                Log.d(TAG, "✓ Loaded " + reportList.size() + " category reports");
            }
        } catch (Exception e) {
            Log.e(TAG, "✗ Error getExpenseReportByCategory: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return reportList;
    }

    /**
     * Lấy top 5 chi tiêu lớn nhất
     */
    public List<Express> getTop5Expenses(int userId, String startDate, String endDate) {
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

            String selection = SQLiteDbHelper.USER_ID_EXPRESS + " = ? " +
                    "AND " + SQLiteDbHelper.DATE_EXPRESS + " >= ? " +
                    "AND " + SQLiteDbHelper.DATE_EXPRESS + " <= ?";

            String[] selectionArgs = {String.valueOf(userId), startDate, endDate};

            cursor = db.query(
                    SQLiteDbHelper.TABLE_EXPRESS,
                    columns,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    SQLiteDbHelper.AMOUNT_EXPRESS + " DESC",
                    "5"
            );

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    Express e = new Express();
                    e.setId(cursor.getInt(0));
                    e.setTitle(cursor.getString(1));
                    e.setAmount(cursor.getDouble(2));
                    e.setCategoryName(cursor.getString(3));
                    e.setUserId(cursor.getInt(4));
                    e.setDate(cursor.getString(5));
                    list.add(e);
                }
                Log.d(TAG, "✓ Loaded top " + list.size() + " expenses");
            }
        } catch (Exception e) {
            Log.e(TAG, "✗ Error getTop5Expenses: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return list;
    }
}