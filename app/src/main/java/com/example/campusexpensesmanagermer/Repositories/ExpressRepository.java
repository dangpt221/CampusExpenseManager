package com.example.campusexpensesmanagermer.Repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.campusexpensesmanagermer.Data.SQLiteDbHelper;
import com.example.campusexpensesmanagermer.Models.Express;

import java.util.ArrayList;
import java.util.List;

public class ExpressRepository {

    private SQLiteDbHelper dbHelper;

    public ExpressRepository(Context context) {
        dbHelper = new SQLiteDbHelper(context);
    }

    public long addExpress(Express express) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", express.getTitle());
        values.put("amount", express.getAmount());
        values.put("category", express.getCategoryName());
        values.put("user_id", express.getUserId());
        long id = db.insert("express", null, values);
        db.close();
        return id;
    }

    public List<Express> getAllExpressByUser(int userId) {
        List<Express> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                "express",
                new String[]{"id", "title", "amount", "category", "user_id"},
                "user_id = ?",
                new String[]{String.valueOf(userId)},
                null, null, "id DESC"
        );
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Express e = new Express();
                e.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                e.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
                e.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("amount")));
                e.setCategoryName(cursor.getString(cursor.getColumnIndexOrThrow("category")));
                e.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("user_id")));
                list.add(e);
            }
            cursor.close();
        }
        db.close();
        return list;
    }
}
