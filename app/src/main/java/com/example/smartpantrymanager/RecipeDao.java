package com.example.smartpantrymanager;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface RecipeDao {
    @Insert
    long insert(Recipe recipe);

    @Insert
    void insertAll(List<Recipe> recipes);

    @Query("SELECT * FROM recipes ORDER BY name ASC")
    List<Recipe> getAllRecipes();

    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    Recipe getRecipeById(int recipeId);

    @Query("SELECT COUNT(*) FROM recipes")
    int getRecipeCount();
}
