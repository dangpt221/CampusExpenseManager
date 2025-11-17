package com.example.campusexpensesmanagermer.Data;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;
public class SQLiteDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "campus_expense"; // ten csdl
    private static final int DB_VERSION = 1;

    // dinh nghia cac thong tin bang users luu tru tai khoan
    protected static final String TABLE_USER = "users";
    // dinh nghia ten cac cot nam trong bang
    protected  static final String ID_USER = "id";
    protected  static final String USERNAME_USER = "username";
    protected static final String PASSWORD_USER = "password";
    protected  static final String EMAIL_USER = "email";
    protected  static final String PHONE_USER = "phone";
    protected static final String ROLE_USER = "role";
    protected static final String STATUS_USER = "status";

    // 3 cot dung chung cho cac bang
    protected static final String CREATED_AT = "created_at";
    protected static final String UPDATED_AT = "updated_at";
    protected static final String DELETED_AT = "deleted_at";

    public SQLiteDbHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String userTable = " CREATE TABLE " + TABLE_USER + " ( "
                + ID_USER + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + USERNAME_USER + " VARCHAR(30) NOT NULL, "
                + PASSWORD_USER + " VARCHAR(250) NOT NULL, "
                + EMAIL_USER + " VARCHAR(60) NOT NULL, "
                + PHONE_USER + " VARCHAR(20), "
                + ROLE_USER + " TINYINT DEFAULT(0), "
                + STATUS_USER + " TINYINT DEFAULT(1), "
                + CREATED_AT + " DATETIME, "
                + UPDATED_AT + " DATETIME, "
                + DELETED_AT + " DATETIME ) ";
        db.execSQL(userTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion){
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER); // xoa du lieu
            onCreate(db); // tao lai du lieu
        }
    }
}
