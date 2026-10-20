package com.example.smartpantrymanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
public class RecipeHelper {
    // returns the recipes where the pantry has all the ingredients
    public static List<Recipe> findStrictMatches(List<PantryItem> pantry, SmartPantryDatabase db) {

        // first add up how much of each ingredient is in the pantry
        HashMap<String, Double> have = new HashMap<>();
        for (PantryItem item : pantry) {
            String key = makeKey(item.name, item.unit);
            double amount = item.quantity * getFactor(item.unit);

            if (have.containsKey(key)) {
                have.put(key, have.get(key) + amount);
            } else {
                have.put(key, amount);
            }
        }

        // then check every recipe
        List<Recipe> result = new ArrayList<>();
        List<Recipe> allRecipes = db.recipeDao().getAllRecipes();

        for (Recipe recipe : allRecipes) {
            List<RecipeIngredient> ingredients = db.recipeDao().getIngredients(recipe.id);

            // skip recipes with no ingredients
            if (ingredients.size() == 0) {
                continue;
            }

            boolean canMake = true;
            for (RecipeIngredient ing : ingredients) {
                String key = makeKey(ing.name, ing.unit);
                double need = ing.quantity * getFactor(ing.unit);

                // not in pantry or not enough
                if (!have.containsKey(key) || have.get(key) < need - 0.0001) {
                    canMake = false;
                    break;
                }
            }

            if (canMake) {
                result.add(recipe);
            }
        }
        return result;
    }

    // name + unit type, so "Eggs" and "egg" count as the same thing
    private static String makeKey(String name, String unit) {
        return normalizeName(name) + "|" + getBaseUnit(unit);
    }

    // lowercase, remove extra spaces and turn plurals into singular
    static String normalizeName(String name) {
        if (name == null) {
            return "";
        }
        String n = name.trim().toLowerCase().replaceAll("\\s+", " ");

        if (n.endsWith("ies") && n.length() > 4) {
            return n.substring(0, n.length() - 3) + "y";
        }
        if (n.endsWith("oes") || n.endsWith("ches") || n.endsWith("shes")
                || n.endsWith("xes") || n.endsWith("sses")) {
            return n.substring(0, n.length() - 2);
        }
        if (n.endsWith("s") && !n.endsWith("ss") && !n.endsWith("us") && n.length() > 3) {
            return n.substring(0, n.length() - 1);
        }
        return n;
    }

    // makes different spellings of a unit the same (grams -> g)
    private static String cleanUnit(String unit) {
        if (unit == null) {
            return "pcs";
        }
        String u = unit.trim().toLowerCase();

        if (u.equals("gram") || u.equals("grams")) {
            return "g";
        } else if (u.equals("kilogram") || u.equals("kilograms")) {
            return "kg";
        } else if (u.equals("litre") || u.equals("liter") || u.equals("litres") || u.equals("liters")) {
            return "l";
        } else if (u.equals("piece") || u.equals("pieces") || u.equals("pc")) {
            return "pcs";
        }
        return u;
    }


    private static String getBaseUnit(String unit) {
        String u = cleanUnit(unit);

        if (u.equals("g") || u.equals("kg")) {
            return "g";
        } else if (u.equals("ml") || u.equals("l") || u.equals("tsp") || u.equals("tbsp")) {
            return "ml";
        }
        return "pcs";
    }

    // how much to multiply by to get the base unit
    private static double getFactor(String unit) {
        String u = cleanUnit(unit);

        if (u.equals("kg") || u.equals("l")) {
            return 1000;
        } else if (u.equals("tbsp")) {
            return 15;
        } else if (u.equals("tsp")) {
            return 5;
        }
        return 1;
    }
}
