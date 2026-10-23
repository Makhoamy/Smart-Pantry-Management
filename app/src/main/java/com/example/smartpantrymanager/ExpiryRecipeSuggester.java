package com.example.smartpantrymanager;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
public final class ExpiryRecipeSuggester {
    public static final int SOON_DAYS = 7;

    public static class Suggestion {
        public final Recipe recipe;
        public final long daysLeft;      // Long.MAX_VALUE when no used ingredient has an expiry date
        public final int expiringSoonCount;
        public final String reason;

        Suggestion(Recipe recipe, long daysLeft, int expiringSoonCount, String reason) {
            this.recipe = recipe;
            this.daysLeft = daysLeft;
            this.expiringSoonCount = expiringSoonCount;
            this.reason = reason;
        }
    }

    private ExpiryRecipeSuggester() {}

    public static List<Suggestion> suggest(List<Recipe> recipes,
                                           List<RecipeIngredient> allRequirements,
                                           List<PantryItem> pantry,
                                           LocalDate today) {
        // 1. Drop expired items.
        List<PantryItem> usable = new ArrayList<>();
        for (PantryItem item : pantry) {
            LocalDate expiry = parse(item.getExpiryDate());
            if (expiry == null || !expiry.isBefore(today)) usable.add(item);
        }

        // 2. Keep only recipes that can be made from what is left.
        List<Recipe> makeable = RecipeMatcher.findStrictMatches(recipes, allRequirements, usable);

        // 3. Work out how urgent each recipe is.
        List<Suggestion> result = new ArrayList<>();
        for (Recipe recipe : makeable) {
            long soonest = Long.MAX_VALUE;
            String soonestName = null;
            int soonCount = 0;

            for (RecipeIngredient requirement : allRequirements) {
                if (requirement.getRecipeId() != recipe.getId()) continue;

                long ingredientDays = Long.MAX_VALUE;
                for (PantryItem item : usable) {
                    if (!RecipeMatcher.usesItem(requirement, item)) continue;
                    LocalDate expiry = parse(item.getExpiryDate());
                    if (expiry == null) continue;
                    ingredientDays = Math.min(ingredientDays, ChronoUnit.DAYS.between(today, expiry));
                }

                if (ingredientDays == Long.MAX_VALUE) continue;
                if (ingredientDays <= SOON_DAYS) soonCount++;
                if (ingredientDays < soonest) {
                    soonest = ingredientDays;
                    soonestName = requirement.getIngredientName();
                }
            }
            result.add(new Suggestion(recipe, soonest, soonCount, describe(soonestName, soonest, soonCount)));
        }

        Collections.sort(result, new Comparator<Suggestion>() {
            @Override
            public int compare(Suggestion a, Suggestion b) {
                int byDays = Long.compare(a.daysLeft, b.daysLeft);
                if (byDays != 0) return byDays;
                int bySoon = Integer.compare(b.expiringSoonCount, a.expiringSoonCount);
                if (bySoon != 0) return bySoon;
                return a.recipe.getName().compareToIgnoreCase(b.recipe.getName());
            }
        });
        return result;
    }

    private static String describe(String name, long days, int soonCount) {
        if (name == null) return "No expiry dates set for the ingredients used";
        String label = name.isEmpty() ? name
                : name.substring(0, 1).toUpperCase(Locale.ROOT) + name.substring(1);
        String when;
        if (days == 0) when = "expires today";
        else if (days == 1) when = "expires tomorrow";
        else when = "expires in " + days + " days";

        String text = "Uses " + label + " - " + when;
        if (soonCount > 1) text += " (+" + (soonCount - 1) + " more expiring soon)";
        return text;
    }

    private static LocalDate parse(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return LocalDate.parse(value.trim());
        } catch (Exception e) {
            return null;
        }
    }

}
