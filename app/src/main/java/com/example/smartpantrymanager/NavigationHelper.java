package com.example.smartpantrymanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.view.View;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class NavigationHelper {

    private NavigationHelper() {
        // Utility class, no instances needed
    }


    public static void applyInsets(Activity activity) {
        View root = activity.findViewById(R.id.main);
        if (root == null) {
            return;
        }

        ViewCompat.setOnApplyWindowInsetsListener(root, (view, windowInsets) -> {
            Insets systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return windowInsets;
        });
    }

    public static void setupBottomNav(Activity activity, int selectedId) {
        BottomNavigationView bottomNav = activity.findViewById(R.id.bottomNav);
        if (bottomNav == null) {
            return;
        }


        bottomNav.setSelectedItemId(selectedId);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == selectedId) {
                return true;
            }

            Class<?> targetActivity = getTargetActivity(itemId);
            if (targetActivity == null) {
                return false;
            }

            activity.startActivity(new Intent(activity, targetActivity));
            disableTransition(activity);
            activity.finish();
            return true;
        });
    }

    private static Class<?> getTargetActivity(int menuItemId) {
        if (menuItemId == R.id.nav_pantry) {
            return PantryListActivity.class;
        }
        if (menuItemId == R.id.nav_recipes) {
            return SuggestedRecipesActivity.class;
        }
        if (menuItemId == R.id.nav_settings) {
            return SettingsActivity.class;
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    private static void disableTransition(Activity activity) {
        if (Build.VERSION.SDK_INT >= 34) {
            activity.overrideActivityTransition(Activity.OVERRIDE_TRANSITION_OPEN, 0, 0);
            activity.overrideActivityTransition(Activity.OVERRIDE_TRANSITION_CLOSE, 0, 0);
        } else {
            activity.overridePendingTransition(0, 0);
        }
    }


    public static String formatQuantity(double quantity) {
        DecimalFormat format = new DecimalFormat("#.##", DecimalFormatSymbols.getInstance(Locale.getDefault()));
        return format.format(quantity);
    }
}