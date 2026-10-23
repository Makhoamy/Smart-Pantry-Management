package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

/** Shows every row stored in the recipes table. */
public class RecipeActivity extends AppCompatActivity {
    private PantryDatabase database;
    private RecipeAdapter adapter;
    private TextView emptyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        database = PantryDatabase.getInstance(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Recipes");
        emptyText = findViewById(R.id.tvNoRecipes);

        adapter = new RecipeAdapter(recipe -> {
            Intent intent = new Intent(this, RecipeDetailActivity.class);
            intent.putExtra("recipe_id", recipe.getId());
            startActivity(intent);
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerRecipes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        MaterialButton suggested = findViewById(R.id.btnSuggestedRecipes);
        suggested.setOnClickListener(v ->
                startActivity(new Intent(this, SuggestedRecipesActivity.class)));

        BottomNav.setup(this, R.id.nav_recipes);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecipes();
    }

    private void loadRecipes() {
        AppExecutors.DATABASE_EXECUTOR.execute(() -> {
            RecipeSeeder.seedIfNeeded(database); // no-op once the table has rows
            List<Recipe> recipes = database.recipeDao().getAllRecipes();

            List<String> previews = new ArrayList<>();
            for (Recipe recipe : recipes) {
                String text = recipe.getInstructions() == null ? "" : recipe.getInstructions();
                previews.add(text.replace("\r", "").replace("\n", " ").trim());
            }

            runOnUiThread(() -> {
                adapter.setRecipes(recipes, previews);
                emptyText.setVisibility(recipes.isEmpty() ? View.VISIBLE : View.GONE);
            });
        });
    }
}
