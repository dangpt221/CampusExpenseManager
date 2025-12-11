package com.example.campusexpensesmanagermer.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;

import java.util.Locale;

/**
 * Helper to persist and apply app language across the app.
 */
public final class LanguageUtils {

    private static final String SETTINGS_PREFS = "AppSettings";
    private static final String KEY_LANGUAGE = "language_code";

    private LanguageUtils() {}

    public static Context applyAppLanguage(Context base) {
        SharedPreferences prefs = base.getSharedPreferences(SETTINGS_PREFS, Context.MODE_PRIVATE);
        String langCode = prefs.getString(KEY_LANGUAGE, "en");
        return updateResources(base, langCode);
    }

    public static void saveLanguage(Context context, String langCode) {
        context.getSharedPreferences(SETTINGS_PREFS, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_LANGUAGE, langCode)
                .apply();
    }

    public static String getLanguage(Context context) {
        return context.getSharedPreferences(SETTINGS_PREFS, Context.MODE_PRIVATE)
                .getString(KEY_LANGUAGE, "en");
    }

    public static String getDisplayName(Context context, String langCode) {
        return "English";
    }

    public static void recreateWithLanguage(Activity activity) {
        applyAppLanguage(activity);
        activity.recreate();
    }

    @SuppressLint("ObsoleteSdkInt")
    private static Context updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale);
            return context.createConfigurationContext(config);
        } else {
            config.locale = locale;
            DisplayMetrics dm = res.getDisplayMetrics();
            res.updateConfiguration(config, dm);
            return context;
        }
    }
}

