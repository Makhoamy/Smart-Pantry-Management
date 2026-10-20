package com.example.smartpantrymanager;
import android.content.Context;
import android.content.SharedPreferences;
public class Prefs {
    private static final String PREFS_NAME = "smart_pantry_settings";
    public static final String KEY_ALERTS = "alerts_enabled";
    public static final String KEY_UNITS = "measurement_units";
    public static final String UNITS_METRIC = "metric";
    public static final String UNITS_IMPERIAL = "imperial";

    private Prefs() { }

    public static SharedPreferences get(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static boolean alertsEnabled(Context context) {
        return get(context).getBoolean(KEY_ALERTS, true);
    }

    public static boolean isMetric(Context context) {
        return get(context).getString(KEY_UNITS, UNITS_METRIC).equals(UNITS_METRIC);
    }
}
