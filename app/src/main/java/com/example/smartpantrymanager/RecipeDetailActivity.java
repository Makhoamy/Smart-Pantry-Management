package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import java.util.List;

public class RecipeDetailActivity extends AppCompatActivity {
    private PantryDatabase database;
    private int recipeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        database = PantryDatabase.getInstance(this);
        recipeId = getIntent().getIntExtra("recipe_id", -1);

        MaterialButton back = findViewById(R.id.recipeDetailBackButton);
        back.setOnClickListener(v -> finish());
        loadRecipe();
    }

    private void loadRecipe() {
        AppExecutors.DATABASE_EXECUTOR.execute(() -> {
            Recipe recipe = database.recipeDao().getRecipeById(recipeId);
            List<RecipeIngredient> ingredients =
                    database.recipeIngredientDao().getIngredientsForRecipe(recipeId);

            runOnUiThread(() -> {
                if (recipe == null) {
                    finish();
                    return;
                }

                ((TextView) findViewById(R.id.recipeDetailName)).setText(recipe.getName());
                ((TextView) findViewById(R.id.recipeDetailInstructions)).setText(recipe.getInstructions());

                StringBuilder text = new StringBuilder();
                for (RecipeIngredient ingredient : ingredients) {
                    text.append("• ")
                            .append(ingredient.getIngredientName())
                            .append(" — ")
                            .append(UnitFormatter.format(this,
                                    ingredient.getRequiredQuantity(), ingredient.getUnit()))
                            .append("\n");
                }
                ((TextView) findViewById(R.id.recipeDetailIngredients)).setText(text.toString().trim());
            });
        });
    }
}
