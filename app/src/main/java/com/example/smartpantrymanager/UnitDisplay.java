package com.example.smartpantrymanager;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class UnitDisplay {
    private UnitDisplay() { }

    public static String format(boolean metric, double quantity, String unit) {
        String u = RecipeMatcher.cleanUnit(unit);
        double base = RecipeMatcher.convertToBase(quantity, unit); // g, ml or pcs

        if (metric) {
            // Imperial units entered earlier are shown in metric.
            if (u.equals("oz") || u.equals("lb")) {
                return number(base) + " g";
            }
            if (u.equals("fl oz") || u.equals("cup")) {
                return number(base) + " ml";
            }
        } else {
            // Metric weights become ounces / pounds.
            if (u.equals("g") || u.equals("kg")) {
                return base >= 453.59237
                        ? number(base / 453.59237) + " lb"
                        : number(base / 28.3495) + " oz";
            }
            // Metric volumes become fluid ounces / cups.
            if (u.equals("ml") || u.equals("l")) {
                return base >= 240
                        ? number(base / 240) + " cup"
                        : number(base / 29.5735) + " fl oz";
            }
        }

        // Counts (pcs) and units that need no conversion stay as entered.
        return number(quantity) + (unit == null || unit.trim().isEmpty() ? "" : " " + unit);
    }

    private static String number(double value) {
        return new DecimalFormat("#.##",
                DecimalFormatSymbols.getInstance(Locale.getDefault())).format(value);
    }
}
