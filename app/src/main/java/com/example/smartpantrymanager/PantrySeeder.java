package com.example.smartpantrymanager;

import android.util.Log;
import java.time.LocalDate;

public class PantrySeeder {
    // name, quantity, unit, days until expiry
    private static final Object[][] SAMPLE_ITEMS = {
            {"Egg",           6,    "piece", 3},
            {"Tomato",        400,  "g",     2},
            {"Cheese",        200,  "g",     10},
            {"Bread",         8,    "slice", 4},
            {"Pasta",         500,  "g",     180},
            {"Garlic",        4,    "clove", 20},
            {"Oil",           500,  "ml",    365},
            {"Milk",          1000, "ml",    1},
            {"Banana",        3,    "piece", 2},
            {"Oats",          500,  "g",     120},
            {"Potato",        1000, "g",     14},
            {"Butter",        250,  "g",     30},
            {"Apple",         4,    "piece", 7},
            {"Peanut butter", 300,  "g",     90},
            {"Spinach",       100,  "g",     -1}   // already expired
    };

    // Call off the main thread. Adds the sample items only if the table is empty.
    public static void seedIfEmpty(PantryDatabase db) {
        try {
            if (!db.pantryItemDao().getAllItems().isEmpty()) return;

            LocalDate today = LocalDate.now();
            for (Object[] row : SAMPLE_ITEMS) {
                String expiry = today.plusDays((int) row[3]).toString(); // yyyy-MM-dd
                db.pantryItemDao().insert(new PantryItem(
                        (String) row[0],
                        ((Number) row[1]).doubleValue(),
                        (String) row[2],
                        expiry));
            }
            Log.d("PantrySeeder", "Inserted " + SAMPLE_ITEMS.length + " sample items");
        } catch (Exception e) {
            Log.e("PantrySeeder", "Seeding failed", e);
        }
    }
}