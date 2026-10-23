package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

/** Shows every row stored in the pantry_items table. */
public class PantryListActivity extends AppCompatActivity {
    private PantryDatabase database;
    private PantryAdapter adapter;
    private TextView emptyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry_list);

        database = PantryDatabase.getInstance(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("My Pantry");
        emptyText = findViewById(R.id.tvEmpty);

        adapter = new PantryAdapter(new PantryAdapter.Listener() {
            @Override
            public void onEdit(PantryItem item) {
                Intent intent = new Intent(PantryListActivity.this, AddEditIngredientActivity.class);
                intent.putExtra("item_id", item.getId());
                startActivity(intent);
            }

            @Override
            public void onDelete(PantryItem item) {
                new AlertDialog.Builder(PantryListActivity.this)
                        .setTitle("Delete ingredient")
                        .setMessage("Delete " + item.getName() + " from your pantry?")
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Delete", (dialog, which) ->
                                AppExecutors.DATABASE_EXECUTOR.execute(() -> {
                                    database.pantryItemDao().delete(item);
                                    runOnUiThread(() -> {
                                        Toast.makeText(PantryListActivity.this,
                                                "Ingredient deleted", Toast.LENGTH_SHORT).show();
                                        loadPantry();
                                    });
                                }))
                        .show();
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerPantry);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(v ->
                startActivity(new Intent(this, AddEditIngredientActivity.class)));

        BottomNav.setup(this, R.id.nav_pantry);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPantry();
    }

    private void loadPantry() {
        AppExecutors.DATABASE_EXECUTOR.execute(() -> {
            PantrySeeder.seedIfEmpty(database);   // fills the table first if it's empty
            List<PantryItem> items = database.pantryItemDao().getAllItems();
            runOnUiThread(() -> {
                adapter.setItems(items);
                emptyText.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
            });
        });
    }
}