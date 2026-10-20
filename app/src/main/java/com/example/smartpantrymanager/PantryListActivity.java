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

import java.util.List;

public class PantryListActivity extends AppCompatActivity implements PantryAdapter.Listener {

    private SmartPantryDatabase db;
    private PantryAdapter adapter;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantry_list);
        NavigationHelper.applyInsets(this);
        setSupportActionBar(findViewById(R.id.toolbar));

        // get the database and add the starter recipes if needed
        db = SmartPantryDatabase.getInstance(this);
        SmartPantryDatabase.seedIfEmpty(db);

        tvEmpty = findViewById(R.id.tvEmpty);

        // set up the list
        RecyclerView recyclerView = findViewById(R.id.recyclerPantry);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PantryAdapter(this);
        recyclerView.setAdapter(adapter);

        // the + button opens the add screen
        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PantryListActivity.this, AddEditIngredientActivity.class);
                startActivity(intent);
            }
        });

        NavigationHelper.setupBottomNav(this, R.id.nav_pantry);
    }

    // reload the list every time we come back to this screen
    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    private void refreshList() {
        List<PantryItem> items = db.pantryDao().getAll();
        adapter.setItems(items);

        // show the "empty" text only when there are no items
        if (items.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.GONE);
        }
    }

    // tapping a row opens the edit screen for that item
    @Override
    public void onItemClick(PantryItem item) {
        Intent intent = new Intent(this, AddEditIngredientActivity.class);
        intent.putExtra("item_id", item.id);
        startActivity(intent);
    }

    // ask before deleting
    @Override
    public void onDeleteClick(final PantryItem item) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete ingredient")
                .setMessage("Remove " + item.name + " from your pantry?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.pantryDao().delete(item);
                        refreshList();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}