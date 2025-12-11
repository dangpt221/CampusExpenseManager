package com.example.campusexpensesmanagermer;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.campusexpensesmanagermer.Utils.LanguageUtils;

public class CampusExpenseApplication extends Application {
    
    private static final String SETTINGS_PREFS = "AppSettings";
    
    @Override
    public void onCreate() {
        super.onCreate();
        // Áp dụng ngôn ngữ đã chọn
        LanguageUtils.applyAppLanguage(this);
        // Khởi tạo dark mode từ SharedPreferences khi app khởi động
        initializeDarkMode();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LanguageUtils.applyAppLanguage(base));
    }
    
    private void initializeDarkMode() {
        SharedPreferences settingsPrefs = getSharedPreferences(SETTINGS_PREFS, Context.MODE_PRIVATE);
        boolean isDarkMode = settingsPrefs.getBoolean("dark_mode", false);
        
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}



