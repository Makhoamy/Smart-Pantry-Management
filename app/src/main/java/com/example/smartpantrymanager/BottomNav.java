package com.example.smartpantrymanager;
import android.app.Activity;
import android.content.Intent;
import com.google.android.material.bottomnavigation.BottomNavigationView;
public class BottomNav {
    private BottomNav() {}

    public static void setup(Activity activity, int selectedId) {
        BottomNavigationView nav = activity.findViewById(R.id.bottomNav);
        if (nav == null) return;
        nav.setSelectedItemId(selectedId);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == selectedId) return false;

            Class<?> target;
            if (id == R.id.nav_pantry) target = PantryListActivity.class;
            else if (id == R.id.nav_recipes) target = RecipeActivity.class;
            else target = SettingsActivity.class;

            Intent intent = new Intent(activity, target);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            activity.startActivity(intent);
            return false; // keep the highlight on the screen we are leaving
        });
    }
}

