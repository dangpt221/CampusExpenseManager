package com.example.campusexpensesmanagermer.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SQLiteDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "campus_expense.db";
    private static final int DB_VERSION = 6;  // Tăng version lên 6

    // 3 cột chung
    public static final String CREATED_AT = "created_at";
    public static final String UPDATED_AT = "updated_at";
    public static final String DELETED_AT = "deleted_at";

    // ========== USERS ==========
    public static final String TABLE_USER = "users";
    public static final String ID_USER = "id";
    public static final String USERNAME_USER = "username";
    public static final String PASSWORD_USER = "password";
    public static final String EMAIL_USER = "email";
    public static final String PHONE_USER = "phone";
    public static final String DISPLAY_NAME_USER = "display_name";
    public static final String AVATAR_PATH_USER = "avatar_path";
    public static final String ROLE_USER = "role";
    public static final String STATUS_USER = "status";

    // ========== CATEGORIES ==========
    public static final String TABLE_CATEGORIES = "categories";
    public static final String ID_CATEGORY = "id";
    public static final String USER_ID_CATEGORY = "user_id";
    public static final String NAME_CATEGORY = "name";
    public static final String ICON_CATEGORY = "icon";
    public static final String COLOR_CATEGORY = "color";
    public static final String IS_DEFAULT_CATEGORY = "is_default";

    // ========== EXPRESS ==========
    public static final String TABLE_EXPRESS = "express";
    public static final String ID_EXPRESS = "id";
    public static final String USER_ID_EXPRESS = "user_id";
    public static final String TITLE_EXPRESS = "title";
    public static final String AMOUNT_EXPRESS = "amount";
    public static final String CURRENCY_EXPRESS = "currency";
    public static final String CATEGORY_ID_EXPRESS = "category_id";
    public static final String DATE_EXPRESS = "date";
    public static final String NOTE_EXPRESS = "note";
    public static final String ATTACHMENT_ID_EXPRESS = "attachment_id";
    public static final String IS_RECURRING_EXPRESS = "is_recurring";
    public static final String STATUS_EXPRESS = "status";

    // ========== BUDGETS ==========
    public static final String TABLE_BUDGETS = "budgets";
    public static final String ID_BUDGET = "id";
    public static final String USER_ID_BUDGET = "user_id";
    public static final String YEAR_BUDGET = "year";
    public static final String MONTH_BUDGET = "month";
    public static final String TARGET_AMOUNT_BUDGET = "target_amount";
    public static final String CURRENCY_BUDGET = "currency";
    public static final String NOTE_BUDGET = "note";

    // ========== BUDGET ITEMS ==========
    public static final String TABLE_BUDGET_ITEMS = "budget_items";
    public static final String ID_BUDGET_ITEM = "id";
    public static final String BUDGET_ID_ITEM = "budget_id";
    public static final String CATEGORY_ID_ITEM = "category_id";
    public static final String ALLOCATED_AMOUNT_ITEM = "allocated_amount";

    // ========== SETTINGS ==========
    public static final String TABLE_SETTINGS = "settings";
    public static final String ID_SETTINGS = "id";
    public static final String USER_ID_SETTINGS = "user_id";
    public static final String THEME_SETTINGS = "theme";
    public static final String CURRENCY_SETTINGS = "currency";
    public static final String NOTIFY_REMINDERS = "notify_reminders";
    public static final String NOTIFY_BUDGET_LIMIT = "notify_budget_limit";
    public static final String LANGUAGE_SETTINGS = "language";
    public static final String BACKUP_AUTO = "backup_auto";
    public static final String BACKUP_LAST = "backup_last";

    // ========== LINKED ACCOUNTS ==========
    public static final String TABLE_LINKED_ACCOUNTS = "linked_accounts";
    public static final String ID_LINKED_ACCOUNT = "id";
    public static final String USER_ID_LINKED = "user_id";
    public static final String PROVIDER = "provider";
    public static final String PROVIDER_USER_ID = "provider_user_id";
    public static final String EMAIL_LINKED = "email";

    // ========== BACKUPS ==========
    public static final String TABLE_BACKUPS = "backups";
    public static final String ID_BACKUP = "id";
    public static final String USER_ID_BACKUP = "user_id";
    public static final String FILE_PATH_BACKUP = "file_path";
    public static final String SIZE_BYTES_BACKUP = "size_bytes";

    // ========== NOTIFICATIONS ==========
    public static final String TABLE_NOTIFICATIONS = "notifications";
    public static final String ID_NOTIFICATION = "id";
    public static final String USER_ID_NOTIFICATION = "user_id";
    public static final String TYPE_NOTIFICATION = "type";
    public static final String ENABLED_NOTIFICATION = "enabled";
    public static final String CONFIG_NOTIFICATION = "config";
    public static final String LAST_SENT_NOTIFICATION = "last_sent";

    // ========== ATTACHMENTS ==========
    public static final String TABLE_ATTACHMENTS = "attachments";
    public static final String ID_ATTACHMENT = "id";
    public static final String USER_ID_ATTACHMENT = "user_id";
    public static final String FILE_PATH_ATTACHMENT = "file_path";
    public static final String MIME_TYPE_ATTACHMENT = "mime_type";


    public SQLiteDbHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.execSQL("PRAGMA foreign_keys = ON;");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // CREATE TABLE USERS
        String userTable = "CREATE TABLE IF NOT EXISTS " + TABLE_USER + " ("
                + ID_USER + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + USERNAME_USER + " TEXT NOT NULL UNIQUE, "
                + EMAIL_USER + " TEXT NOT NULL UNIQUE, "
                + PASSWORD_USER + " TEXT, "
                + PHONE_USER + " TEXT, "
                + DISPLAY_NAME_USER + " TEXT, "
                + AVATAR_PATH_USER + " TEXT, "
                + ROLE_USER + " INTEGER DEFAULT 0, "
                + STATUS_USER + " INTEGER DEFAULT 1, "
                + CREATED_AT + " DATETIME DEFAULT (datetime('now')), "
                + UPDATED_AT + " DATETIME, "
                + DELETED_AT + " DATETIME)";

        // CREATE TABLE CATEGORIES
        String categoriesTable = "CREATE TABLE IF NOT EXISTS " + TABLE_CATEGORIES + " ("
                + ID_CATEGORY + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + USER_ID_CATEGORY + " INTEGER, "
                + NAME_CATEGORY + " TEXT NOT NULL, "
                + ICON_CATEGORY + " TEXT, "
                + COLOR_CATEGORY + " TEXT, "
                + IS_DEFAULT_CATEGORY + " INTEGER DEFAULT 0, "
                + CREATED_AT + " DATETIME DEFAULT (datetime('now')), "
                + UPDATED_AT + " DATETIME, "
                + "FOREIGN KEY(" + USER_ID_CATEGORY + ") REFERENCES " + TABLE_USER + "(" + ID_USER + ") ON DELETE CASCADE)";

        // CREATE TABLE EXPRESS
        String expressTable = "CREATE TABLE IF NOT EXISTS " + TABLE_EXPRESS + " ("
                + ID_EXPRESS + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + USER_ID_EXPRESS + " INTEGER NOT NULL, "
                + TITLE_EXPRESS + " TEXT NOT NULL, "
                + AMOUNT_EXPRESS + " REAL NOT NULL, "
                + CURRENCY_EXPRESS + " TEXT DEFAULT 'VND', "
                + CATEGORY_ID_EXPRESS + " TEXT, "
                + DATE_EXPRESS + " DATETIME NOT NULL DEFAULT (datetime('now')), "
                + NOTE_EXPRESS + " TEXT, "
                + ATTACHMENT_ID_EXPRESS + " INTEGER, "
                + IS_RECURRING_EXPRESS + " INTEGER DEFAULT 0, "
                + STATUS_EXPRESS + " INTEGER DEFAULT 1, "
                + CREATED_AT + " DATETIME DEFAULT (datetime('now')), "
                + UPDATED_AT + " DATETIME, "
                + "FOREIGN KEY(" + USER_ID_EXPRESS + ") REFERENCES " + TABLE_USER + "(" + ID_USER + ") ON DELETE CASCADE)";

        // CREATE TABLE BUDGETS
        String budgetsTable = "CREATE TABLE IF NOT EXISTS " + TABLE_BUDGETS + " ("
                + ID_BUDGET + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + USER_ID_BUDGET + " INTEGER NOT NULL, "
                + YEAR_BUDGET + " INTEGER, "
                + MONTH_BUDGET + " INTEGER, "
                + TARGET_AMOUNT_BUDGET + " REAL NOT NULL, "
                + CURRENCY_BUDGET + " TEXT DEFAULT 'VND', "
                + NOTE_BUDGET + " TEXT, "
                + CREATED_AT + " DATETIME DEFAULT (datetime('now')), "
                + UPDATED_AT + " DATETIME, "
                + "FOREIGN KEY(" + USER_ID_BUDGET + ") REFERENCES " + TABLE_USER + "(" + ID_USER + ") ON DELETE CASCADE)";

        // CREATE TABLE BUDGET_ITEMS
        String budgetItemsTable = "CREATE TABLE IF NOT EXISTS " + TABLE_BUDGET_ITEMS + " ("
                + ID_BUDGET_ITEM + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BUDGET_ID_ITEM + " INTEGER NOT NULL, "
                + CATEGORY_ID_ITEM + " TEXT, "
                + ALLOCATED_AMOUNT_ITEM + " REAL NOT NULL, "
                + CREATED_AT + " DATETIME DEFAULT (datetime('now')), "
                + "FOREIGN KEY(" + BUDGET_ID_ITEM + ") REFERENCES " + TABLE_BUDGETS + "(" + ID_BUDGET + ") ON DELETE CASCADE)";

        db.execSQL(userTable);
        db.execSQL(categoriesTable);
        db.execSQL(expressTable);
        db.execSQL(budgetsTable);
        db.execSQL(budgetItemsTable);

        // Tạo index
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_express_user_date ON " + TABLE_EXPRESS + " (" + USER_ID_EXPRESS + ", " + DATE_EXPRESS + ")");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_express_category ON " + TABLE_EXPRESS + " (" + CATEGORY_ID_EXPRESS + ")");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_budget_user_month ON " + TABLE_BUDGETS + " (" + USER_ID_BUDGET + ", " + YEAR_BUDGET + ", " + MONTH_BUDGET + ")");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_budget_items_budget ON " + TABLE_BUDGET_ITEMS + " (" + BUDGET_ID_ITEM + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Nâng cấp từ version cũ
        if (oldVersion < 5) {
            db.execSQL("ALTER TABLE expenses RENAME TO express");
        }

        // Thêm bảng budgets nếu chưa có (khi update app)
        if (oldVersion < 6) {
            String budgetsTable = "CREATE TABLE IF NOT EXISTS " + TABLE_BUDGETS + " ("
                    + ID_BUDGET + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + USER_ID_BUDGET + " INTEGER NOT NULL, "
                    + YEAR_BUDGET + " INTEGER, "
                    + MONTH_BUDGET + " INTEGER, "
                    + TARGET_AMOUNT_BUDGET + " REAL NOT NULL, "
                    + CURRENCY_BUDGET + " TEXT DEFAULT 'VND', "
                    + NOTE_BUDGET + " TEXT, "
                    + CREATED_AT + " DATETIME DEFAULT (datetime('now')), "
                    + UPDATED_AT + " DATETIME, "
                    + "FOREIGN KEY(" + USER_ID_BUDGET + ") REFERENCES " + TABLE_USER + "(" + ID_USER + ") ON DELETE CASCADE)";

            String budgetItemsTable = "CREATE TABLE IF NOT EXISTS " + TABLE_BUDGET_ITEMS + " ("
                    + ID_BUDGET_ITEM + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + BUDGET_ID_ITEM + " INTEGER NOT NULL, "
                    + CATEGORY_ID_ITEM + " TEXT, "
                    + ALLOCATED_AMOUNT_ITEM + " REAL NOT NULL, "
                    + CREATED_AT + " DATETIME DEFAULT (datetime('now')), "
                    + "FOREIGN KEY(" + BUDGET_ID_ITEM + ") REFERENCES " + TABLE_BUDGETS + "(" + ID_BUDGET + ") ON DELETE CASCADE)";

            db.execSQL(budgetsTable);
            db.execSQL(budgetItemsTable);
        }
    }
}