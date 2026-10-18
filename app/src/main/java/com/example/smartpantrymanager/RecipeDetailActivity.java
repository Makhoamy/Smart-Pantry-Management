package com.example.smartpantrymanager;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
public class RecipeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipe_detail);
        NavigationHelper.applyInsets(this);
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SmartPantryDatabase db = SmartPantryDatabase.getInstance(this);
        int recipeId = getIntent().getIntExtra("recipe_id", -1);
        Recipe recipe = db.recipeDao().getById(recipeId);
        if (recipe == null) {
            finish();
            return;
        }
        getSupportActionBar().setTitle(recipe.name);

        List<RecipeIngredient> ingredients = db.recipeDao().getIngredients(recipeId);
        StringBuilder sb = new StringBuilder();
        for (RecipeIngredient ri : ingredients) {
            sb.append("\u2022 ").append(ri.name).append(" - ")
                    .append(NavigationHelper.formatQuantity(ri.quantity)).append(" ").append(ri.unit).append("\n");
        }
        ((TextView) findViewById(R.id.tvIngredients)).setText(sb.toString().trim());
        ((TextView) findViewById(R.id.tvSteps)).setText(recipe.steps);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}


    }
}