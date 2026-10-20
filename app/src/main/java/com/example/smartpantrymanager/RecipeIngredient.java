package com.example.smartpantrymanager;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class RecipeIngredient {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int recipeId;
    public String name;
    public double quantity;
    public String unit;
}
