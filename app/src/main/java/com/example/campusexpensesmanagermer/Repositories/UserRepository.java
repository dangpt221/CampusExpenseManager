package com.example.campusexpensesmanagermer.Repositories;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.campusexpensesmanagermer.Data.SQLiteDbHelper;
import com.example.campusexpensesmanagermer.Models.Users;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class UserRepository extends SQLiteDbHelper {
    public UserRepository(@Nullable Context context) {
        super(context);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getCurrentDate(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ZonedDateTime zone = ZonedDateTime.now();
        return dtf.format(zone); // tra ve ngay thang hien tai khi insert(update, delete) du lieu
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public long saveUserAccount(String username, String password, String email, String phone){
        String currentDate = getCurrentDate();
        ContentValues values = new ContentValues();
        values.put(USERNAME_USER, username);
        values.put(PASSWORD_USER, password);
        values.put(EMAIL_USER, email);
        values.put(PHONE_USER, phone);
        values.put(ROLE_USER, 0);
        values.put(STATUS_USER, 1);
        values.put(CREATED_AT, currentDate);
        SQLiteDatabase db = this.getWritableDatabase();
        long insert = db.insert(TABLE_USER,null, values);
        db.close();
        return insert;
    }
    @SuppressLint("Range")
    public Users getInfoUser(String username, String password){
        Users info = new Users();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            // select [cols] from users where [username] = ? and [password] = ?
            String[] cols = {ID_USER, USERNAME_USER, EMAIL_USER, PHONE_USER, ROLE_USER, STATUS_USER};
            // mang chua cac cot du lieu trong bang users can lay thong tin
            // select id, username, email, phone, role, status
            // where [username] = ? and [password] = ?
            String condition = USERNAME_USER + " =? AND " + PASSWORD_USER + " =? ";
            String[] params = {username, password};
            Cursor data = db.query(TABLE_USER, cols, condition, params, null, null, null);
            if (data.getCount() > 0){
                data.moveToFirst();
                // do du lieu vao Model User
                info.setId(data.getInt(data.getColumnIndex(ID_USER)));
                info.setUsername(data.getString(data.getColumnIndex(USERNAME_USER)));
                info.setEmail(data.getString(data.getColumnIndex(EMAIL_USER)));
                info.setPhone(data.getString(data.getColumnIndex(PHONE_USER)));
                info.setRole(data.getInt(data.getColumnIndex(ROLE_USER)));
                info.setStatus(data.getInt(data.getColumnIndex(STATUS_USER)));
            }
            data.close(); // ngat truy van du lieu
            db.close(); // ngat ket noi toi database
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
        return info;
    }
    public boolean checkAccountExistsByUsername(String username){
        boolean checking = false;
        String[] cols = {ID_USER, USERNAME_USER};
        String cond = USERNAME_USER + " =? ";
        String[] params = {username};
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor data = db.query(TABLE_USER, cols, cond, params, null, null, null);
        if (data.getCount() > 0){
            checking = true;
        }
        data.close();
        db.close();
        return checking;
    }
}