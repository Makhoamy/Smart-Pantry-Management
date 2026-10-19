package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SuggestedRecipesActivity extends AppCompatActivity implements RecipeAdapter.Listener {

    private SmartPantryDatabase db;
    private RecipeAdapter adapter;
    private TextView tvEmpty;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_suggested_recipes);
        NavigationHelper.applyInsets(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        db = SmartPantryDatabase.getInstance(this);
        tvEmpty = findViewById(R.id.tvEmpty);
        RecyclerView recycler = findViewById(R.id.recyclerRecipes);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecipeAdapter(this);
        recycler.setAdapter(adapter);

        NavigationHelper.setupBottomNav(this, R.id.nav_recipes);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecipes();
    }

    private void loadRecipes() {
        // Database work happens on a background thread
        executor.execute(() -> {
            List<PantryItem> pantry = db.pantryDao().getAll();
            List<Recipe> matches = RecipeMatcher.findStrictMatches(pantry, db);

            // UI updates must go back on the main thread
            runOnUiThread(() -> {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                adapter.setRecipes(matches);
                tvEmpty.setVisibility(matches.isEmpty() ? View.VISIBLE : View.GONE);
            });
        });
    }

    @Override
    public void onRecipeClick(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.id);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}