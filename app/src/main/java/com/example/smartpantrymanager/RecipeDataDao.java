package com.example.smartpantrymanager;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface RecipeDataDao {
    @Insert long insertRecipe(Recipe recipe);
    @Insert void insertIngredients(List<RecipeIngredient> ingredients);
    @Query("SELECT * FROM Recipe ORDER BY name") List<Recipe> getAllRecipes();
    @Query("SELECT * FROM Recipe WHERE id = :id") Recipe getById(int id);
    @Query("SELECT * FROM RecipeIngredient WHERE recipeId = :recipeId") List<RecipeIngredient> getIngredients(int recipeId);
    @Query("SELECT COUNT(*) FROM Recipe") int count();
}
