package com.example.smartpantrymanager;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(
        entities = {
                PantryItem.class,
                Recipe.class,
                RecipeIngredient.class
        },
        version = 1,
        exportSchema = false
)
public abstract class PantryDatabase extends RoomDatabase {
    private static volatile PantryDatabase instance;

    public abstract PantryItemDao pantryItemDao();
    public abstract RecipeDao recipeDao();
    public abstract RecipeIngredientDao recipeIngredientDao();

    public static synchronized PantryDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            PantryDatabase.class,
                            "smart_pantry_database"
                    )
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}