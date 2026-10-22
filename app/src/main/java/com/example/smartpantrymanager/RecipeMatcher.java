package com.example.smartpantrymanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RecipeMatcher {

    // Return only recipes that can be made with the current pantry.
    public static List<Recipe> findStrictMatches(
            List<PantryItem> pantry,
            SmartPantryDatabase db) {

        Map<String, Double> available = new HashMap<>();

        // Add together quantities of the same ingredient.
        for (PantryItem item : pantry) {

            if (item.name == null || item.quantity <= 0
                    || Double.isNaN(item.quantity)
                    || Double.isInfinite(item.quantity)) {
                continue;
            }

            String name = normalizeName(item.name);
            String dimension = getDimension(item.unit);

            if (name.isEmpty() || dimension.equals("unknown")) {
                continue;
            }

            String key = name + "|" + dimension;
            double quantity = convertToBase(item.quantity, item.unit);

            available.put(key,
                    available.getOrDefault(key, 0.0) + quantity);
        }

        List<Recipe> matches = new ArrayList<>();
        List<Recipe> recipes = db.recipeDao().getAllRecipes();

        // Check every recipe.
        for (Recipe recipe : recipes) {

            List<RecipeIngredient> ingredients =
                    db.recipeDao().getIngredients(recipe.id);

            // Do not suggest a recipe without ingredients.
            if (ingredients == null || ingredients.isEmpty()) {
                continue;
            }

            boolean canMake = true;

            for (RecipeIngredient ingredient : ingredients) {

                if (ingredient.name == null
                        || ingredient.quantity <= 0
                        || Double.isNaN(ingredient.quantity)
                        || Double.isInfinite(ingredient.quantity)) {
                    canMake = false;
                    break;
                }

                String name = normalizeName(ingredient.name);
                String dimension = getDimension(ingredient.unit);

                // An unsupported unit cannot be safely compared.
                if (name.isEmpty() || dimension.equals("unknown")) {
                    canMake = false;
                    break;
                }

                String key = name + "|" + dimension;

                double required = convertToBase(
                        ingredient.quantity, ingredient.unit);

                double have = available.getOrDefault(key, 0.0);

                // Every ingredient must be available in full.
                if (have + 0.000001 < required) {
                    canMake = false;
                    break;
                }
            }

            // Add only complete matches.
            if (canMake) {
                matches.add(recipe);
            }
        }

        return matches;
    }

    // Make simple ingredient names consistent.
    public static String normalizeName(String name) {

        if (name == null) {
            return "";
        }

        String n = name.trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ");

        // Handle common irregular plurals.
        if (n.equals("potatoes")) return "potato";
        if (n.equals("tomatoes")) return "tomato";
        if (n.equals("leaves")) return "leaf";
        if (n.equals("loaves")) return "loaf";
        if (n.equals("knives")) return "knife";

        // General plural handling.
        if (n.endsWith("ies") && n.length() > 3) {
            return n.substring(0, n.length() - 3) + "y";
        }

        if (n.endsWith("oes") && n.length() > 3) {
            return n.substring(0, n.length() - 2);
        }

        if ((n.endsWith("ches") || n.endsWith("shes")
                || n.endsWith("xes") || n.endsWith("sses"))
                && n.length() > 4) {
            return n.substring(0, n.length() - 2);
        }

        if (n.endsWith("s") && !n.endsWith("ss")
                && !n.endsWith("us") && n.length() > 3) {
            return n.substring(0, n.length() - 1);
        }

        return n;
    }

    // Identify which units can be compared with each other.
    private static String getDimension(String unit) {

        String u = cleanUnit(unit);

        switch (u) {
            case "g":
            case "kg":
            case "oz":
            case "lb":
                return "weight";

            case "ml":
            case "l":
            case "tsp":
            case "tbsp":
            case "cup":
            case "fl oz":
                return "volume";

            case "pcs":
                return "count";

            default:
                return "unknown";
        }
    }

    // Convert common unit spellings to a standard form.
    private static String cleanUnit(String unit) {

        if (unit == null) {
            return "";
        }

        String u = unit.trim().toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ");

        switch (u) {
            case "gram":
            case "grams":
                return "g";

            case "kilogram":
            case "kilograms":
                return "kg";

            case "millilitre":
            case "millilitres":
            case "milliliter":
            case "milliliters":
                return "ml";

            case "litre":
            case "litres":
            case "liter":
            case "liters":
                return "l";

            case "piece":
            case "pieces":
            case "pc":
                return "pcs";

            case "teaspoon":
            case "teaspoons":
                return "tsp";

            case "tablespoon":
            case "tablespoons":
                return "tbsp";

            case "ounce":
            case "ounces":
                return "oz";

            case "pound":
            case "pounds":
            case "lbs":
                return "lb";

            case "fluid ounce":
            case "fluid ounces":
            case "fl. oz.":
            case "fl oz.":
                return "fl oz";

            case "cups":
                return "cup";

            default:
                return u;
        }
    }

    // Convert the quantity into grams, millilitres or pieces.
    private static double convertToBase(double quantity, String unit) {

        String u = cleanUnit(unit);

        switch (u) {

            // Weight is converted to grams.
            case "kg":
                return quantity * 1000;

            case "oz":
                return quantity * 28.3495;

            case "lb":
                return quantity * 453.59237;

            // Volume is converted to millilitres.
            case "l":
                return quantity * 1000;

            case "tsp":
                return quantity * 5;

            case "tbsp":
                return quantity * 15;

            case "cup":
                return quantity * 240;

            case "fl oz":
                return quantity * 29.5735;

            // g, ml and pcs are already in their base units.
            default:
                return quantity;
        }
    }
}
