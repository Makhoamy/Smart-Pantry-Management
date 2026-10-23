package com.example.smartpantrymanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import com.google.android.material.button.MaterialButton;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {
    public interface Listener {
        void onEdit(PantryItem item);
        void onDelete(PantryItem item);
    }

    private final List<PantryItem> items = new ArrayList<>();
    private final Listener listener;

    public PantryAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setItems(List<PantryItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);
        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        PantryItem item = items.get(position);
        holder.name.setText(item.getName());
        holder.quantity.setText(UnitFormatter.format(
                holder.itemView.getContext(), item.getQuantity(), item.getUnit()));
        holder.expiry.setText(item.getExpiryDate().isEmpty()
                ? "Expiry date not set" : "Expires: " + item.getExpiryDate());
        holder.edit.setOnClickListener(v -> listener.onEdit(item));
        holder.delete.setOnClickListener(v -> listener.onDelete(item));
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class PantryViewHolder extends RecyclerView.ViewHolder {
        TextView name, quantity, expiry;
        MaterialButton edit, delete;

        PantryViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.pantryItemName);
            quantity = itemView.findViewById(R.id.pantryItemQuantity);
            expiry = itemView.findViewById(R.id.pantryItemExpiry);
            edit = itemView.findViewById(R.id.editPantryItemButton);
            delete = itemView.findViewById(R.id.deletePantryItemButton);
        }
    }
}
