package com.example.smartpantrymanager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecipeMatcher {

    public static List<Recipe> findStrictMatches(
            List<Recipe> recipes,
            List<RecipeIngredient> allRequirements,
            List<PantryItem> pantryItems) {

        List<Recipe> matches = new ArrayList<>();

        for (Recipe recipe : recipes) {
            List<RecipeIngredient> required = new ArrayList<>();
            for (RecipeIngredient ingredient : allRequirements) {
                if (ingredient.getRecipeId() == recipe.getId()) {
                    required.add(ingredient);
                }
            }

            if (required.isEmpty()) continue;

            boolean canMake = true;
            for (RecipeIngredient requirement : required) {
                double available = getAvailableQuantity(
                        pantryItems, requirement.getIngredientName(), requirement.getUnit());

                if (available < requirement.getRequiredQuantity()) {
                    canMake = false;
                    break;
                }
            }

            if (canMake) matches.add(recipe);
        }

        return matches;
    }

    /** True if this pantry item can supply the given recipe requirement (same name, compatible unit). */
    public static boolean usesItem(RecipeIngredient requirement, PantryItem item) {
        return sameIngredient(item.getName(), requirement.getIngredientName())
                && compatibleUnit(item.getUnit(), requirement.getUnit());
    }

    private static double getAvailableQuantity(List<PantryItem> pantry,
                                               String requiredName, String requiredUnit) {
        double total = 0;

        for (PantryItem item : pantry) {
            if (sameIngredient(item.getName(), requiredName)
                    && compatibleUnit(item.getUnit(), requiredUnit)) {
                total += convertQuantity(item.getQuantity(), item.getUnit(), requiredUnit);
            }
        }
        return total;
    }

    private static boolean sameIngredient(String first, String second) {
        return normalize(first).equals(normalize(second));
    }

    private static String normalize(String name) {
        if (name == null) return "";
        String value = name.trim().toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9 ]", "")
                .replaceAll("\\s+", " ");

        // Handle common plural forms without a full language-processing library.
        if (value.endsWith("ies") && value.length() > 4) {
            value = value.substring(0, value.length() - 3) + "y";
        } else if (value.endsWith("oes") && value.length() > 4) {
            value = value.substring(0, value.length() - 2);
        } else if (value.endsWith("s") && !value.endsWith("ss") && value.length() > 3) {
            value = value.substring(0, value.length() - 1);
        }
        return value;
    }

    private static boolean compatibleUnit(String pantryUnit, String recipeUnit) {
        String a = normalizeUnit(pantryUnit);
        String b = normalizeUnit(recipeUnit);
        if (a.equals(b)) return true;

        // Convert common mass and volume units to grams and millilitres.
        return (isMass(a) && isMass(b)) || (isVolume(a) && isVolume(b));
    }

    private static double convertQuantity(double quantity, String from, String to) {
        String a = normalizeUnit(from);
        String b = normalizeUnit(to);
        if (a.equals(b)) return quantity;

        if (isMass(a) && isMass(b)) {
            double grams = a.equals("kg") ? quantity * 1000 : quantity;
            return b.equals("kg") ? grams / 1000 : grams;
        }
        if (isVolume(a) && isVolume(b)) {
            double ml = a.equals("l") ? quantity * 1000 : quantity;
            return b.equals("l") ? ml / 1000 : ml;
        }
        return 0;
    }

    private static String normalizeUnit(String unit) {
        if (unit == null) return "";
        String value = unit.trim().toLowerCase(Locale.ROOT);
        if (value.equals("grams") || value.equals("gram")) return "g";
        if (value.equals("kilograms") || value.equals("kilogram")) return "kg";
        if (value.equals("millilitres") || value.equals("milliliters")
                || value.equals("millilitre") || value.equals("milliliter")) return "ml";
        if (value.equals("litres") || value.equals("liters")
                || value.equals("litre") || value.equals("liter")) return "l";
        if (value.equals("slices")) return "slice";
        if (value.equals("pieces")) return "piece";
        if (value.equals("cloves")) return "clove";
        return value;
    }

    private static boolean isMass(String unit) {
        return unit.equals("g") || unit.equals("kg");
    }

    private static boolean isVolume(String unit) {
        return unit.equals("ml") || unit.equals("l");
    }
}
