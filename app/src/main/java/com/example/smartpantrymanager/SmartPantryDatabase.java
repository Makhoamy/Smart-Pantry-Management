package com.example.smartpantrymanager;
import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import java.util.ArrayList;
import java.util.List;
@Database(entities = {PantryItem.class, Recipe.class, RecipeIngredient.class}, version = 1, exportSchema = false)
public abstract class SmartPantryDatabase extends RoomDatabase {
    public abstract PantryItemDao pantryDao();
    public abstract RecipeDataDao recipeDao();

    private static SmartPantryDatabase instance;
    @SuppressWarnings("deprecation")
    public static synchronized SmartPantryDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            SmartPantryDatabase.class, "smart_pantry.db")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

    // Add some sample recipes when the app is first used.
    public static void seedIfEmpty(SmartPantryDatabase db) {
        RecipeDataDao dao = db.recipeDao();
        if (dao.count() > 0) return;
        db.runInTransaction(()-> insertSampleRecipes(dao));
    }
    private static void insertSampleRecipes(RecipeDataDao dao){
        // I use lowercase ingredient names to make matching easier.
        addRecipe(dao, "Tomato omelette",
                "1. Whisk the eggs with salt.\n2. Chop the tomato.\n3. Cook everything in a pan for 3 minutes.",
                new String[][]{{"egg", "2", "pcs"}, {"tomato", "1", "pcs"}, {"salt", "0.5", "tsp"}});

        addRecipe(dao, "Egg fried rice",
                "1. Cook the rice.\n2. Scramble the eggs in a hot pan.\n3. Add the rice and stir for 2 minutes.",
                new String[][]{{"rice", "200", "g"}, {"egg", "2", "pcs"}, {"salt", "0.5", "tsp"}});

        addRecipe(dao, "Scrambled eggs",
                "1. Beat the eggs with salt.\n2. Melt the butter in a pan.\n3. Stir the eggs gently until just set.",
                new String[][]{{"egg", "3", "pcs"}, {"butter", "10", "g"}, {"salt", "0.5", "tsp"}});

        addRecipe(dao, "Cheese toast",
                "1. Place the cheese on the bread.\n2. Grill until the cheese melts and bubbles.",
                new String[][]{{"bread", "2", "pcs"}, {"cheese", "50", "g"}});

        addRecipe(dao, "Plain pancakes",
                "1. Whisk flour, egg, milk and sugar into a batter.\n2. Pour small rounds into a hot pan.\n3. Flip when bubbles appear and cook the other side.",
                new String[][]{{"flour", "150", "g"}, {"egg", "1", "pcs"}, {"milk", "200", "ml"}, {"sugar", "1", "tbsp"}});

        addRecipe(dao, "Tomato pasta",
                "1. Boil the pasta until tender.\n2. Fry chopped tomatoes in oil with salt for 8 minutes.\n3. Toss the pasta in the sauce.",
                new String[][]{{"pasta", "200", "g"}, {"tomato", "3", "pcs"}, {"oil", "1", "tbsp"}, {"salt", "0.5", "tsp"}});

        addRecipe(dao, "Garlic butter rice",
                "1. Cook the rice.\n2. Fry crushed garlic in butter for 1 minute.\n3. Stir the rice through the garlic butter.",
                new String[][]{{"rice", "200", "g"}, {"butter", "20", "g"}, {"garlic", "2", "pcs"}});

        addRecipe(dao, "Mashed potato",
                "1. Boil peeled potatoes until soft.\n2. Drain and mash with butter, milk and salt.",
                new String[][]{{"potato", "4", "pcs"}, {"butter", "30", "g"}, {"milk", "50", "ml"}, {"salt", "0.5", "tsp"}});

        addRecipe(dao, "Banana pancakes",
                "1. Mash the bananas.\n2. Mix in the eggs and flour.\n3. Fry spoonfuls until golden on both sides.",
                new String[][]{{"banana", "2", "pcs"}, {"egg", "2", "pcs"}, {"flour", "50", "g"}});

        addRecipe(dao, "Fried potatoes",
                "1. Slice the potatoes thinly.\n2. Fry in hot oil for 10 minutes, turning often.\n3. Season with salt.",
                new String[][]{{"potato", "3", "pcs"}, {"oil", "2", "tbsp"}, {"salt", "1", "tsp"}});

        addRecipe(dao, "Cheese omelette",
                "1. Beat the eggs with salt.\n2. Pour into a hot pan and add the cheese.\n3. Fold and cook for 2 minutes.",
                new String[][]{{"egg", "3", "pcs"}, {"cheese", "40", "g"}, {"salt", "0.5", "tsp"}});

        addRecipe(dao, "French toast",
                "1. Whisk eggs, milk and sugar.\n2. Soak the bread slices.\n3. Fry until golden on both sides.",
                new String[][]{{"bread", "4", "pcs"}, {"egg", "2", "pcs"}, {"milk", "100", "ml"}, {"sugar", "1", "tsp"}});

        addRecipe(dao, "Vegetable soup",
                "1. Chop all the vegetables.\n2. Simmer in water with salt for 20 minutes.\n3. Serve hot.",
                new String[][]{{"carrot", "2", "pcs"}, {"potato", "2", "pcs"}, {"onion", "1", "pcs"}, {"salt", "1", "tsp"}});

        addRecipe(dao, "Tuna sandwich",
                "1. Mix the tuna with mayonnaise.\n2. Spread onto a slice of bread and top with the second.",
                new String[][]{{"bread", "2", "pcs"}, {"tuna", "100", "g"}, {"mayonnaise", "1", "tbsp"}});

        addRecipe(dao, "Onion rice",
                "1. Fry chopped onion in oil until soft.\n2. Add cooked rice and salt.\n3. Stir for 3 minutes.",
                new String[][]{{"rice", "200", "g"}, {"onion", "1", "pcs"}, {"oil", "1", "tbsp"}, {"salt", "0.5", "tsp"}});

        addRecipe(dao, "Tomato soup",
                "1. Fry the onion in butter.\n2. Add chopped tomatoes and salt.\n3. Simmer for 15 minutes and blend.",
                new String[][]{{"tomato", "5", "pcs"}, {"onion", "1", "pcs"}, {"butter", "20", "g"}, {"salt", "0.5", "tsp"}});

        addRecipe(dao, "Peanut butter banana toast",
                "1. Toast the bread.\n2. Spread with peanut butter.\n3. Top with sliced banana.",
                new String[][]{{"bread", "2", "pcs"}, {"peanut butter", "2", "tbsp"}, {"banana", "1", "pcs"}});
    }

    private static void addRecipe(RecipeDataDao dao, String name, String steps, String[][] ingredients) {
        Recipe r = new Recipe();
        r.name = name;
        r.steps = steps;
        int recipeId = (int) dao.insertRecipe(r);

        List<RecipeIngredient> list = new ArrayList<>();
        for (String[] row : ingredients) {
            RecipeIngredient ri = new RecipeIngredient();
            ri.recipeId = recipeId;
            ri.name = row[0];
            ri.quantity = Double.parseDouble(row[1]);
            ri.unit = row[2];
            list.add(ri);
        }
        dao.insertIngredients(list);
    }


}
