package com.example.smartpantrymanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    private List<PantryItem> pantryItems = new ArrayList<>();
    private Listener listener;

    public interface Listener {
        void onItemClick(PantryItem item);
        void onDeleteClick(PantryItem item);
    }

    public PantryAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setItems(List<PantryItem> items) {
        if (items == null) {
            pantryItems = new ArrayList<>();
        } else {
            pantryItems = new ArrayList<>(items);
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);

        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull PantryViewHolder holder, int position) {

        PantryItem item = pantryItems.get(position);

        holder.tvIngredientName.setText(item.name);

        boolean metric = Prefs.isMetric(holder.itemView.getContext());
        holder.tvIngredientQuantity.setText(
                UnitDisplay.format(metric, item.quantity, item.unit));

        if (item.expiryDate == null || item.expiryDate.trim().isEmpty()) {
            holder.tvExpiryDate.setText("Expiry date: Not set");
        } else {
            holder.tvExpiryDate.setText("Expiry date: " + item.expiryDate);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pantryItems.size();
    }

    static class PantryViewHolder extends RecyclerView.ViewHolder {

        TextView tvIngredientName;
        TextView tvIngredientQuantity;
        TextView tvExpiryDate;
        ImageButton btnDelete;

        public PantryViewHolder(@NonNull View itemView) {
            super(itemView);

            tvIngredientName = itemView.findViewById(R.id.tvIngredientName);
            tvIngredientQuantity = itemView.findViewById(R.id.tvIngredientQuantity);
            tvExpiryDate = itemView.findViewById(R.id.tvExpiryDate);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
