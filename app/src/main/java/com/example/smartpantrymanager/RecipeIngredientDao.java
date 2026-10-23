package com.example.smartpantrymanager;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface RecipeIngredientDao {
    @Insert
    void insert(RecipeIngredient ingredient);

    @Insert
    void insertAll(List<RecipeIngredient> ingredients);

    @Query("SELECT * FROM recipe_ingredients WHERE recipeId = :recipeId")
    List<RecipeIngredient> getIngredientsForRecipe(int recipeId);

    @Query("SELECT * FROM recipe_ingredients")
    List<RecipeIngredient> getAllRecipeIngredients();
}
