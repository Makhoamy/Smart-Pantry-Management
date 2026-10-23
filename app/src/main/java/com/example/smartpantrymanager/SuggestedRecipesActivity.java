package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Recipes you can cook right now, with the ones using soon-to-expire items first. */
public class SuggestedRecipesActivity extends AppCompatActivity {
    private PantryDatabase database;
    private RecipeAdapter adapter;
    private TextView emptyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        database = PantryDatabase.getInstance(this);
        emptyText = findViewById(R.id.noRecipesText);
        RecyclerView recyclerView = findViewById(R.id.recipesRecyclerView);

        adapter = new RecipeAdapter(recipe -> {
            Intent intent = new Intent(this, RecipeDetailActivity.class);
            intent.putExtra("recipe_id", recipe.getId());
            startActivity(intent);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        MaterialButton back = findViewById(R.id.backToPantryButton);
        back.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSuggestions();
    }

    private void loadSuggestions() {
        AppExecutors.DATABASE_EXECUTOR.execute(() -> {
            RecipeSeeder.seedIfNeeded(database);
            List<Recipe> recipes = database.recipeDao().getAllRecipes();
            List<RecipeIngredient> requirements = database.recipeIngredientDao().getAllRecipeIngredients();
            List<PantryItem> pantry = database.pantryItemDao().getAllItems();

            List<ExpiryRecipeSuggester.Suggestion> suggestions =
                    ExpiryRecipeSuggester.suggest(recipes, requirements, pantry, LocalDate.now());

            List<Recipe> ordered = new ArrayList<>();
            List<String> reasons = new ArrayList<>();
            for (ExpiryRecipeSuggester.Suggestion s : suggestions) {
                ordered.add(s.recipe);
                reasons.add(s.reason);
            }

            runOnUiThread(() -> {
                adapter.setRecipes(ordered, reasons);
                emptyText.setVisibility(ordered.isEmpty() ? View.VISIBLE : View.GONE);
            });
        });
    }
}
