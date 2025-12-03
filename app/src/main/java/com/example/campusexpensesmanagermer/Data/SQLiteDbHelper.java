package com.example.campusexpensesmanagermer.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SQLiteDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "campus_expense.db";
    private static final int DB_VERSION = 5;

    // 3 cot chung
    protected static final String CREATED_AT = "created_at";
    protected static final String UPDATED_AT = "updated_at";
    protected static final String DELETED_AT = "deleted_at";

    // ========== USERS ==========
    protected static final String TABLE_USER = "users";
    protected static final String ID_USER = "id";
    protected static final String USERNAME_USER = "username";
    protected static final String PASSWORD_USER = "password";
    protected static final String EMAIL_USER = "email";
    protected static final String PHONE_USER = "phone";
    protected static final String DISPLAY_NAME_USER = "display_name";
    protected static final String AVATAR_PATH_USER = "avatar_path";
    protected static final String ROLE_USER = "role";
    protected static final String STATUS_USER = "status";

    // ========== CATEGORIES ==========
    protected static final String TABLE_CATEGORIES = "categories";
    protected static final String ID_CATEGORY = "id";
    protected static final String USER_ID_CATEGORY = "user_id";
    protected static final String NAME_CATEGORY = "name";
    protected static final String ICON_CATEGORY = "icon";
    protected static final String COLOR_CATEGORY = "color";
    protected static final String IS_DEFAULT_CATEGORY = "is_default";

    // ========== EXPRESS ==========
    protected static final String TABLE_EXPRESS = "express";
    protected static final String ID_EXPRESS = "id";
    protected static final String USER_ID_EXPRESS = "user_id";
    protected static final String TITLE_EXPRESS = "title";
    protected static final String AMOUNT_EXPRESS = "amount";
    protected static final String CURRENCY_EXPRESS = "currency";
    protected static final String CATEGORY_ID_EXPRESS = "category_id";
    protected static final String DATE_EXPRESS = "date";
    protected static final String NOTE_EXPRESS = "note";
    protected static final String ATTACHMENT_ID_EXPRESS = "attachment_id";
    protected static final String IS_RECURRING_EXPRESS = "is_recurring";
    protected static final String STATUS_EXPRESS = "status";

    // ========== BUDGETS ==========
    protected static final String TABLE_BUDGETS = "budgets";
    protected static final String ID_BUDGET = "id";
    protected static final String USER_ID_BUDGET = "user_id";
    protected static final String YEAR_BUDGET = "year";
    protected static final String MONTH_BUDGET = "month";
    protected static final String TARGET_AMOUNT_BUDGET = "target_amount";
    protected static final String CURRENCY_BUDGET = "currency";
    protected static final String NOTE_BUDGET = "note";

    // ========== BUDGET ITEMS ==========
    protected static final String TABLE_BUDGET_ITEMS = "budget_items";
    protected static final String ID_BUDGET_ITEM = "id";
    protected static final String BUDGET_ID_ITEM = "budget_id";
    protected static final String CATEGORY_ID_ITEM = "category_id";
    protected static final String ALLOCATED_AMOUNT_ITEM = "allocated_amount";

    // ========== SETTINGS ==========
    protected static final String TABLE_SETTINGS = "settings";
    protected static final String ID_SETTINGS = "id";
    protected static final String USER_ID_SETTINGS = "user_id";
    protected static final String THEME_SETTINGS = "theme";
    protected static final String CURRENCY_SETTINGS = "currency";
    protected static final String NOTIFY_REMINDERS = "notify_reminders";
    protected static final String NOTIFY_BUDGET_LIMIT = "notify_budget_limit";
    protected static final String LANGUAGE_SETTINGS = "language";
    protected static final String BACKUP_AUTO = "backup_auto";
    protected static final String BACKUP_LAST = "backup_last";

    // ========== LINKED ACCOUNTS ==========
    protected static final String TABLE_LINKED_ACCOUNTS = "linked_accounts";
    protected static final String ID_LINKED_ACCOUNT = "id";
    protected static final String USER_ID_LINKED = "user_id";
    protected static final String PROVIDER = "provider";
    protected static final String PROVIDER_USER_ID = "provider_user_id";
    protected static final String EMAIL_LINKED = "email";

    // ========== BACKUPS ==========
    protected static final String TABLE_BACKUPS = "backups";
    protected static final String ID_BACKUP = "id";
    protected static final String USER_ID_BACKUP = "user_id";
    protected static final String FILE_PATH_BACKUP = "file_path";
    protected static final String SIZE_BYTES_BACKUP = "size_bytes";

    // ========== NOTIFICATIONS ==========
    protected static final String TABLE_NOTIFICATIONS = "notifications";
    protected static final String ID_NOTIFICATION = "id";
    protected static final String USER_ID_NOTIFICATION = "user_id";
    protected static final String TYPE_NOTIFICATION = "type";
    protected static final String ENABLED_NOTIFICATION = "enabled";
    protected static final String CONFIG_NOTIFICATION = "config";
    protected static final String LAST_SENT_NOTIFICATION = "last_sent";

    // ========== ATTACHMENTS ==========
    protected static final String TABLE_ATTACHMENTS = "attachments";
    protected static final String ID_ATTACHMENT = "id";
    protected static final String USER_ID_ATTACHMENT = "user_id";
    protected static final String FILE_PATH_ATTACHMENT = "file_path";
    protected static final String MIME_TYPE_ATTACHMENT = "mime_type";


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
                + CATEGORY_ID_EXPRESS + " INTEGER, "
                + DATE_EXPRESS + " DATETIME NOT NULL DEFAULT (datetime('now')), "
                + NOTE_EXPRESS + " TEXT, "
                + ATTACHMENT_ID_EXPRESS + " INTEGER, "
                + IS_RECURRING_EXPRESS + " INTEGER DEFAULT 0, "
                + STATUS_EXPRESS + " INTEGER DEFAULT 1, "
                + CREATED_AT + " DATETIME DEFAULT (datetime('now')), "
                + UPDATED_AT + " DATETIME, "
                + "FOREIGN KEY(" + USER_ID_EXPRESS + ") REFERENCES " + TABLE_USER + "(" + ID_USER + ") ON DELETE CASCADE, "
                + "FOREIGN KEY(" + CATEGORY_ID_EXPRESS + ") REFERENCES " + TABLE_CATEGORIES + "(" + ID_CATEGORY + ") ON DELETE SET NULL)";

        // TODO: CREATE TABLES BUDGETS, BUDGET_ITEMS, SETTINGS, LINKED_ACCOUNTS, BACKUPS, NOTIFICATIONS, ATTACHMENTS
        // Cách tạo giống với USERS, EXPRESS: dùng hằng số để định nghĩa tên cột

        db.execSQL(userTable);
        db.execSQL(categoriesTable);
        db.execSQL(expressTable);

        // Tạo index
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_express_user_date ON " + TABLE_EXPRESS + " (" + USER_ID_EXPRESS + ", " + DATE_EXPRESS + ")");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_express_category ON " + TABLE_EXPRESS + " (" + CATEGORY_ID_EXPRESS + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 5) {
            db.execSQL("ALTER TABLE expenses RENAME TO express");
        }
    }
}
