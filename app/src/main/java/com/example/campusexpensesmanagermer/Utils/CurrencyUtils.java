package com.example.campusexpensesmanagermer.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Helper to format amounts based on the selected currency in settings.
 */
public final class CurrencyUtils {

    private static final String SETTINGS_PREFS = "AppSettings";
    private static final String KEY_CURRENCY = "currency";

    private CurrencyUtils() {}

    public static String formatCurrency(Context context, double amount) {
        SharedPreferences prefs = context.getSharedPreferences(SETTINGS_PREFS, Context.MODE_PRIVATE);
        String currency = prefs.getString(KEY_CURRENCY, "VND");

        NumberFormat formatter;
        switch (currency) {
            case "USD":
                formatter = NumberFormat.getCurrencyInstance(Locale.US);
                break;
            case "EUR":
                formatter = NumberFormat.getCurrencyInstance(Locale.GERMANY);
                break;
            case "JPY":
                formatter = NumberFormat.getCurrencyInstance(Locale.JAPAN);
                break;
            default:
                // VND as default
                formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                currency = "VND";
                break;
        }

        try {
            return formatter.format(amount);
        } catch (Exception e) {
            // Fallback simple formatting
            return String.format(Locale.getDefault(), "%,.0f %s", amount, currency);
        }
    }
}

