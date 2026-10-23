package com.example.smartpantrymanager;
import android.content.Context;
import android.content.SharedPreferences;
import java.text.DecimalFormat;
import java.util.Locale;
public final class UnitFormatter {
    public static final String PREFS_NAME = "smart_pantry_settings";
    public static final String KEY_EXPIRY_ALERTS = "expiry_alerts";
    public static final String KEY_UNITS = "measurement_units";
    public static final String METRIC = "metric";
    public static final String IMPERIAL = "imperial";

    private static final double G_PER_OZ = 28.3495;
    private static final double G_PER_LB = 453.592;
    private static final double ML_PER_CUP = 236.588;
    private static final double ML_PER_TBSP = 14.7868;
    private static final double ML_PER_TSP = 4.92892;
    private static final double ML_PER_FL_OZ = 29.5735;

    private UnitFormatter() {}

    public static boolean isImperial(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return IMPERIAL.equals(prefs.getString(KEY_UNITS, METRIC));
    }

    public static String format(Context context, double quantity, String unit) {
        return format(quantity, unit, isImperial(context));
    }

    public static String format(double quantity, String unit, boolean imperial) {
        String u = unit == null ? "" : unit.trim().toLowerCase(Locale.ROOT);

        double grams = toGrams(quantity, u);
        if (grams >= 0) return imperial ? imperialMass(grams) : metricMass(grams);

        double ml = toMillilitres(quantity, u);
        if (ml >= 0) return imperial ? imperialVolume(ml) : metricVolume(ml);

        // Countable units (piece, slice, clove...) are the same in both systems.
        return (number(quantity) + " " + (unit == null ? "" : unit)).trim();
    }

    private static double toGrams(double q, String u) {
        switch (u) {
            case "g": case "gram": case "grams": return q;
            case "kg": case "kilogram": case "kilograms": return q * 1000;
            case "oz": case "ounce": case "ounces": return q * G_PER_OZ;
            case "lb": case "lbs": case "pound": case "pounds": return q * G_PER_LB;
            default: return -1;
        }
    }

    private static double toMillilitres(double q, String u) {
        switch (u) {
            case "ml": case "millilitre": case "millilitres":
            case "milliliter": case "milliliters": return q;
            case "l": case "litre": case "litres": case "liter": case "liters": return q * 1000;
            case "fl oz": case "floz": return q * ML_PER_FL_OZ;
            case "cup": case "cups": return q * ML_PER_CUP;
            case "tbsp": return q * ML_PER_TBSP;
            case "tsp": return q * ML_PER_TSP;
            default: return -1;
        }
    }

    private static String metricMass(double grams) {
        return grams >= 1000 ? number(grams / 1000) + " kg" : number(grams) + " g";
    }

    private static String imperialMass(double grams) {
        double oz = grams / G_PER_OZ;
        return oz >= 16 ? number(oz / 16) + " lb" : number(oz) + " oz";
    }

    private static String metricVolume(double ml) {
        return ml >= 1000 ? number(ml / 1000) + " l" : number(ml) + " ml";
    }

    private static String imperialVolume(double ml) {
        double cups = ml / ML_PER_CUP;
        if (cups >= 0.25) return number(cups) + (Math.abs(cups - 1) < 0.005 ? " cup" : " cups");
        double tbsp = ml / ML_PER_TBSP;
        if (tbsp >= 1) return number(tbsp) + " tbsp";
        return number(ml / ML_PER_TSP) + " tsp";
    }

    private static String number(double value) {
        return new DecimalFormat("0.##").format(value);
    }


}
