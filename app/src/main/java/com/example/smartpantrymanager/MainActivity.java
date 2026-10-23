package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PantryDatabase database = PantryDatabase.getInstance(this);
        AppExecutors.DATABASE_EXECUTOR.execute(() -> RecipeSeeder.seedIfNeeded(database));
        startActivity(new Intent(this, PantryListActivity.class));
        finish();
    }
}