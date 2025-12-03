package com.example.campusexpensesmanagermer.Data;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;
public class SQLiteDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "campus_expense.db";
    private static final int DB_VERSION = 4; // tăng version để onUpgrade chạy

    // TABLE NAMES
    public static final String TABLE_USER = "users";
    public static final String TABLE_CATEGORIES = "categories";
    public static final String TABLE_EXPENSES = "expenses";
    public static final String TABLE_BUDGETS = "budgets";
    public static final String TABLE_BUDGET_ITEMS = "budget_items";
    public static final String TABLE_SETTINGS = "settings";
    public static final String TABLE_LINKED_ACCOUNTS = "linked_accounts";
    public static final String TABLE_BACKUPS = "backups";
    public static final String TABLE_NOTIFICATIONS = "notifications";
    public static final String TABLE_ATTACHMENTS = "attachments";

    public SQLiteDbHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // Bật foreign key support
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.execSQL("PRAGMA foreign_keys = ON;");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // users
        String userTable = "CREATE TABLE IF NOT EXISTS " + TABLE_USER + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "username TEXT NOT NULL UNIQUE, "
                + "email TEXT NOT NULL UNIQUE, "
                + "password TEXT, "
                + "phone TEXT, "
                + "display_name TEXT, "
                + "avatar_path TEXT, "
                + "role INTEGER DEFAULT 0, "
                + "status INTEGER DEFAULT 1, "
                + "created_at DATETIME DEFAULT (datetime('now')), "
                + "updated_at DATETIME, "
                + "deleted_at DATETIME)";

        // categories
        String categoriesTable = "CREATE TABLE IF NOT EXISTS " + TABLE_CATEGORIES + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "user_id INTEGER, "
                + "name TEXT NOT NULL, "
                + "icon TEXT, "
                + "color TEXT, "
                + "is_default INTEGER DEFAULT 0, "
                + "created_at DATETIME DEFAULT (datetime('now')), "
                + "updated_at DATETIME, "
                + "FOREIGN KEY(user_id) REFERENCES " + TABLE_USER + "(id) ON DELETE CASCADE)";

        // expenses
        String expensesTable = "CREATE TABLE IF NOT EXISTS " + TABLE_EXPENSES + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "user_id INTEGER NOT NULL, "
                + "title TEXT NOT NULL, "
                + "amount REAL NOT NULL, "
                + "currency TEXT DEFAULT 'VND', "
                + "category_id INTEGER, "
                + "date DATETIME NOT NULL DEFAULT (datetime('now')), "
                + "note TEXT, "
                + "attachment_id INTEGER, "
                + "is_recurring INTEGER DEFAULT 0, "
                + "status INTEGER DEFAULT 1, "
                + "created_at DATETIME DEFAULT (datetime('now')), "
                + "updated_at DATETIME, "
                + "FOREIGN KEY(user_id) REFERENCES " + TABLE_USER + "(id) ON DELETE CASCADE, "
                + "FOREIGN KEY(category_id) REFERENCES " + TABLE_CATEGORIES + "(id) ON DELETE SET NULL)";

        // budgets
        String budgetsTable = "CREATE TABLE IF NOT EXISTS " + TABLE_BUDGETS + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "user_id INTEGER NOT NULL, "
                + "year INTEGER NOT NULL, "
                + "month INTEGER NOT NULL, "
                + "target_amount REAL NOT NULL, "
                + "currency TEXT DEFAULT 'VND', "
                + "note TEXT, "
                + "created_at DATETIME DEFAULT (datetime('now')), "
                + "updated_at DATETIME, "
                + "UNIQUE(user_id, year, month), "
                + "FOREIGN KEY(user_id) REFERENCES " + TABLE_USER + "(id) ON DELETE CASCADE)";

        // budget_items
        String budgetItemsTable = "CREATE TABLE IF NOT EXISTS " + TABLE_BUDGET_ITEMS + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "budget_id INTEGER NOT NULL, "
                + "category_id INTEGER, "
                + "allocated_amount REAL NOT NULL, "
                + "FOREIGN KEY(budget_id) REFERENCES " + TABLE_BUDGETS + "(id) ON DELETE CASCADE, "
                + "FOREIGN KEY(category_id) REFERENCES " + TABLE_CATEGORIES + "(id) ON DELETE SET NULL)";

        // settings
        String settingsTable = "CREATE TABLE IF NOT EXISTS " + TABLE_SETTINGS + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "user_id INTEGER NOT NULL UNIQUE, "
                + "theme TEXT DEFAULT 'auto', "
                + "currency TEXT DEFAULT 'VND', "
                + "notify_reminders INTEGER DEFAULT 1, "
                + "notify_budget_limit INTEGER DEFAULT 1, "
                + "language TEXT DEFAULT 'vi', "
                + "backup_auto INTEGER DEFAULT 0, "
                + "backup_last DATETIME, "
                + "created_at DATETIME DEFAULT (datetime('now')), "
                + "updated_at DATETIME, "
                + "FOREIGN KEY(user_id) REFERENCES " + TABLE_USER + "(id) ON DELETE CASCADE)";

        // linked_accounts
        String linkedAccountsTable = "CREATE TABLE IF NOT EXISTS " + TABLE_LINKED_ACCOUNTS + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "user_id INTEGER NOT NULL, "
                + "provider TEXT NOT NULL, "
                + "provider_user_id TEXT, "
                + "email TEXT, "
                + "created_at DATETIME DEFAULT (datetime('now')), "
                + "FOREIGN KEY(user_id) REFERENCES " + TABLE_USER + "(id) ON DELETE CASCADE)";

        // backups
        String backupsTable = "CREATE TABLE IF NOT EXISTS " + TABLE_BACKUPS + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "user_id INTEGER NOT NULL, "
                + "file_path TEXT NOT NULL, "
                + "size_bytes INTEGER, "
                + "created_at DATETIME DEFAULT (datetime('now')), "
                + "FOREIGN KEY(user_id) REFERENCES " + TABLE_USER + "(id) ON DELETE CASCADE)";

        // notifications
        String notificationsTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NOTIFICATIONS + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "user_id INTEGER NOT NULL, "
                + "type TEXT, "
                + "enabled INTEGER DEFAULT 1, "
                + "config TEXT, "
                + "last_sent DATETIME, "
                + "FOREIGN KEY(user_id) REFERENCES " + TABLE_USER + "(id) ON DELETE CASCADE)";

        // attachments
        String attachmentsTable = "CREATE TABLE IF NOT EXISTS " + TABLE_ATTACHMENTS + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "user_id INTEGER NOT NULL, "
                + "file_path TEXT NOT NULL, "
                + "mime_type TEXT, "
                + "created_at DATETIME DEFAULT (datetime('now')), "
                + "FOREIGN KEY(user_id) REFERENCES " + TABLE_USER + "(id) ON DELETE CASCADE)";

        // exec create
        db.execSQL(userTable);
        db.execSQL(categoriesTable);
        db.execSQL(expensesTable);
        db.execSQL(budgetsTable);
        db.execSQL(budgetItemsTable);
        db.execSQL(settingsTable);
        db.execSQL(linkedAccountsTable);
        db.execSQL(backupsTable);
        db.execSQL(notificationsTable);
        db.execSQL(attachmentsTable);

        // indexes
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_expenses_user_date ON " + TABLE_EXPENSES + " (user_id, date)");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_expenses_category ON " + TABLE_EXPENSES + " (category_id)");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_budgets_user_year_month ON " + TABLE_BUDGETS + " (user_id, year, month)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Simple strategy: nếu thay đổi cấu trúc lớn -> backup rồi drop
        // Bạn có thể thay bằng migration preserve data nếu cần
        if (oldVersion != newVersion) {
            // Example: nếu upgrade từ very old version mà có bảng budgets cũ tên 'budgets' thì giữ migration
            // DROP and recreate:
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ATTACHMENTS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_BACKUPS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_LINKED_ACCOUNTS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUDGET_ITEMS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUDGETS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
            onCreate(db);
        }
    }
}