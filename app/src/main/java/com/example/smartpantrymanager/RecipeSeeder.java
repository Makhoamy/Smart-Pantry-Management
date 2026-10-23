package com.example.smartpantrymanager;
import java.util.ArrayList;
import java.util.List;
public class RecipeSeeder {
    private static class SeedRecipe {
        String name, instructions;
        String[][] ingredients;
        SeedRecipe(String name, String instructions, String[][] ingredients) {
            this.name = name; this.instructions = instructions; this.ingredients = ingredients;
        }
    }
    public static void seedIfNeeded(PantryDatabase db) {
        if (db.recipeDao().getRecipeCount() > 0) return;

        SeedRecipe[] recipes = new SeedRecipe[] {
                new SeedRecipe("Tomato and Egg Scramble", "1. Chop the tomatoes.\n2. Beat the eggs.\n3. Cook tomatoes in a pan, add eggs and stir until cooked.", new String[][]{{"egg","2","piece"},{"tomato","100","g"}}),
                new SeedRecipe("Cheese and Tomato Sandwich", "1. Slice the tomato.\n2. Put cheese and tomato on the bread.\n3. Close the sandwich and serve.", new String[][]{{"bread","2","slice"},{"cheese","30","g"},{"tomato","50","g"}}),
                new SeedRecipe("Garlic Pasta", "1. Boil the pasta.\n2. Cook garlic gently in oil.\n3. Mix pasta with garlic and oil.", new String[][]{{"pasta","100","g"},{"garlic","1","clove"},{"oil","10","ml"}}),
                new SeedRecipe("Simple Tomato Pasta", "1. Boil the pasta.\n2. Chop and cook tomatoes.\n3. Mix the sauce with pasta.", new String[][]{{"pasta","100","g"},{"tomato","150","g"}}),
                new SeedRecipe("Banana Oat Bowl", "1. Cook oats with milk.\n2. Slice the banana.\n3. Serve banana over the oats.", new String[][]{{"oats","50","g"},{"milk","200","ml"},{"banana","1","piece"}}),
                new SeedRecipe("Cheese Omelette", "1. Beat the eggs.\n2. Pour into a warm pan.\n3. Add cheese, fold and cook through.", new String[][]{{"egg","2","piece"},{"cheese","25","g"}}),
                new SeedRecipe("Potato Mash", "1. Peel and chop potatoes.\n2. Boil until soft.\n3. Mash with milk and butter.", new String[][]{{"potato","250","g"},{"milk","30","ml"},{"butter","15","g"}}),
                new SeedRecipe("Carrot Salad", "1. Grate the carrots.\n2. Add lemon juice and oil.\n3. Toss and serve.", new String[][]{{"carrot","100","g"},{"lemon","10","ml"},{"oil","5","ml"}}),
                new SeedRecipe("Peanut Banana Toast", "1. Toast the bread.\n2. Spread peanut butter.\n3. Add sliced banana.", new String[][]{{"bread","2","slice"},{"peanut butter","20","g"},{"banana","1","piece"}}),
                new SeedRecipe("Apple Oat Porridge", "1. Cook oats with milk.\n2. Chop the apple.\n3. Stir apple into the porridge.", new String[][]{{"oats","50","g"},{"milk","200","ml"},{"apple","1","piece"}}),
                new SeedRecipe("Fried Rice and Egg", "1. Cook the rice.\n2. Scramble the egg in a pan.\n3. Add rice and peas and stir-fry.", new String[][]{{"rice","150","g"},{"egg","1","piece"},{"peas","50","g"},{"oil","10","ml"}}),
                new SeedRecipe("Spinach Cheese Toast", "1. Cook the spinach until wilted.\n2. Place on bread with cheese.\n3. Grill until the cheese melts.", new String[][]{{"spinach","50","g"},{"bread","2","slice"},{"cheese","30","g"}}),
                new SeedRecipe("Lemon Carrot Soup", "1. Boil carrots in water until soft.\n2. Blend or mash.\n3. Add lemon juice and serve.", new String[][]{{"carrot","150","g"},{"lemon","10","ml"}}),
                new SeedRecipe("Garlic Butter Potatoes", "1. Boil potato pieces until nearly soft.\n2. Melt butter with garlic.\n3. Toss potatoes in the mixture and cook briefly.", new String[][]{{"potato","250","g"},{"butter","20","g"},{"garlic","1","clove"}}),
                new SeedRecipe("Tomato Cheese Salad", "1. Chop tomatoes.\n2. Add cheese.\n3. Toss gently and serve.", new String[][]{{"tomato","150","g"},{"cheese","40","g"}}),
                new SeedRecipe("Milk Pancakes", "1. Mix flour, egg and milk into a batter.\n2. Pour small amounts into a warm pan.\n3. Cook both sides.", new String[][]{{"flour","100","g"},{"egg","1","piece"},{"milk","150","ml"}}),
                new SeedRecipe("Buttered Peas", "1. Cook peas until tender.\n2. Drain.\n3. Stir through butter and serve.", new String[][]{{"peas","100","g"},{"butter","10","g"}}),
                new SeedRecipe("Apple Peanut Snack", "1. Wash and slice the apple.\n2. Serve with peanut butter for dipping.", new String[][]{{"apple","1","piece"},{"peanut butter","20","g"}}),
                new SeedRecipe("Egg Fried Bread", "1. Beat the egg.\n2. Dip bread in egg.\n3. Fry in a lightly oiled pan until golden on both sides.", new String[][]{{"bread","2","slice"},{"egg","1","piece"},{"oil","10","ml"}}),
                new SeedRecipe("Banana Milk Smoothie", "1. Peel the banana.\n2. Blend banana with milk until smooth.\n3. Serve immediately.", new String[][]{{"banana","1","piece"},{"milk","200","ml"}})
        };

        for (SeedRecipe seed : recipes) {
            long id = db.recipeDao().insert(new Recipe(seed.name, seed.instructions));
            List<RecipeIngredient> ingredients = new ArrayList<>();
            for (String[] item : seed.ingredients) {
                ingredients.add(new RecipeIngredient((int) id, item[0],
                        Double.parseDouble(item[1]), item[2]));
            }
            db.recipeIngredientDao().insertAll(ingredients);
        }
    }
}
