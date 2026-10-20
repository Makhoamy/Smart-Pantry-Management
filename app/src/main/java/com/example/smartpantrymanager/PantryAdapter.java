package com.example.smartpantrymanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    // the activity implements this so it knows when a row is clicked
    public interface Listener {
        void onItemClick(PantryItem item);
        void onDeleteClick(PantryItem item);
    }

    private List<PantryItem> items = new ArrayList<>();
    private Listener listener;

    public PantryAdapter(Listener listener) {
        this.listener = listener;
    }


    public void setItems(List<PantryItem> newItems) {
        items = newItems;
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

        holder.tvName.setText(item.name);

        // quantity + unit, and the expiry date if there is one
        String details = NavigationHelper.formatQuantity(item.quantity) + " " + item.unit;
        if (item.expiryDate != null && !item.expiryDate.isEmpty()) {
            details = details + "  |  Expires: " + item.expiryDate;
        }
        holder.tvDetails.setText(details);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // holds the views for one row
    static class PantryViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvDetails;
        ImageButton btnDelete;

        PantryViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvDetails = itemView.findViewById(R.id.tvDetails);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}