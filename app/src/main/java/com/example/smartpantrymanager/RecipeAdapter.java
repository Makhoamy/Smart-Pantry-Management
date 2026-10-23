package com.example.smartpantrymanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    public interface Listener {
        void onRecipeClick(Recipe recipe);
    }

    private final List<Recipe> recipes = new ArrayList<>();
    private final List<String> summaries = new ArrayList<>();
    private final Listener listener;

    public RecipeAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setRecipes(List<Recipe> newRecipes) {
        setRecipes(newRecipes, null);
    }

    /** summaries is parallel to newRecipes (may be null); each is the second line of the card. */
    public void setRecipes(List<Recipe> newRecipes, List<String> newSummaries) {
        recipes.clear();
        summaries.clear();
        recipes.addAll(newRecipes);
        if (newSummaries != null) summaries.addAll(newSummaries);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.name.setText(recipe.getName());
        String summary = position < summaries.size() ? summaries.get(position) : null;
        holder.instructions.setText(summary == null || summary.isEmpty()
                ? "Tap to see ingredients and preparation steps" : summary);
        holder.itemView.setOnClickListener(v -> listener.onRecipeClick(recipe));
    }

    @Override
    public int getItemCount() { return recipes.size(); }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView name, instructions;
        RecipeViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.recipeName);
            instructions = itemView.findViewById(R.id.recipeSummary);
        }
    }
}
