
package com.example.smartpantrymanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class PantryListActivity extends AppCompatActivity
        implements PantryAdapter.Listener {

    private SmartPantryDatabase db;
    private PantryAdapter adapter;
    private TextView tvEmpty;

    private boolean expiryAlertShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantry_list);

        NavigationHelper.applyInsets(this);
        setSupportActionBar(findViewById(R.id.toolbar));

        // Open the database and add starter recipes if needed.
        db = SmartPantryDatabase.getInstance(this);
        SmartPantryDatabase.seedIfEmpty(db);

        tvEmpty = findViewById(R.id.tvEmpty);

        // Set up the pantry list.
        RecyclerView recyclerView = findViewById(R.id.recyclerPantry);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PantryAdapter(this);
        recyclerView.setAdapter(adapter);

        // Open the add ingredient screen.
        FloatingActionButton fab = findViewById(R.id.fabAdd);

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(
                    PantryListActivity.this,
                    AddEditIngredientActivity.class
            );

            startActivity(intent);
        });

        NavigationHelper.setupBottomNav(this, R.id.nav_pantry);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Refresh the pantry whenever the user returns to this screen.
        if (db != null && adapter != null) {
            expiryAlertShown = false;
            refreshList();
        }
    }

    private void refreshList() {
        // Get the current pantry ingredients.
        List<PantryItem> items = db.pantryDao().getAll();

        if (items == null) {
            items = new ArrayList<>();
        }

        adapter.setItems(items);

        // Display a message if the pantry is empty.
        if (items.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            tvEmpty.setText("Your pantry is empty. Add an ingredient.");
        } else {
            tvEmpty.setVisibility(View.GONE);

            // Check for expired ingredients.
            checkExpiryDates(items);
        }
    }

    private void checkExpiryDates(List<PantryItem> items) {
        // Do not show reminders when the setting is disabled.
        if (!Prefs.alertsEnabled(this) || expiryAlertShown) {
            return;
        }

        List<String> expiredItems = new ArrayList<>();
        List<String> soonItems = new ArrayList<>();

        LocalDate today = LocalDate.now();
        LocalDate nextWeek = today.plusDays(7);

        for (PantryItem item : items) {
            if (item.expiryDate == null
                    || item.expiryDate.trim().isEmpty()) {
                continue;
            }

            try {
                LocalDate expiryDate = LocalDate.parse(item.expiryDate);

                if (expiryDate.isBefore(today)) {
                    expiredItems.add(item.name + " - " + item.expiryDate);
                } else if (!expiryDate.isAfter(nextWeek)) {
                    soonItems.add(item.name + " - " + item.expiryDate);
                }

            } catch (DateTimeParseException e) {
                // Ignore dates that are not in yyyy-MM-dd format.
            }
        }

        // Only display a reminder when there is something to report.
        if (expiredItems.isEmpty() && soonItems.isEmpty()) {
            return;
        }

        StringBuilder message = new StringBuilder();

        if (!expiredItems.isEmpty()) {
            message.append("Expired ingredients:\n");

            for (String name : expiredItems) {
                message.append("- ").append(name).append("\n");
            }
        }

        if (!soonItems.isEmpty()) {
            if (message.length() > 0) {
                message.append("\n");
            }

            message.append("Expiring within 7 days:\n");

            for (String name : soonItems) {
                message.append("- ").append(name).append("\n");
            }
        }

        expiryAlertShown = true;

        new MaterialAlertDialogBuilder(this)
                .setTitle("Pantry Expiry Reminder")
                .setMessage(message.toString())
                .setPositiveButton("OK", null)
                .show();
    }

    // Open the edit screen when an ingredient is selected.
    @Override
    public void onItemClick(PantryItem item) {
        Intent intent = new Intent(
                PantryListActivity.this,
                AddEditIngredientActivity.class
        );

        intent.putExtra("item_id", item.id);
        startActivity(intent);
    }

    // Ask the user before deleting an ingredient.
    @Override
    public void onDeleteClick(final PantryItem item) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete ingredient")
                .setMessage("Remove " + item.name + " from your pantry?")
                .setPositiveButton("Delete",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    DialogInterface dialog, int which) {

                                db.pantryDao().delete(item);
                                refreshList();
                            }
                        })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
