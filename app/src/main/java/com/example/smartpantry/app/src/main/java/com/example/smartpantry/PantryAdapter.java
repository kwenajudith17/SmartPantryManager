
package com.example.smartpantry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    private final Context context;
    private final List<PantryItem> pantryItems;

    public PantryAdapter(Context context, List<PantryItem> pantryItems) {
        this.context = context;
        this.pantryItems = pantryItems;
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextView itemView = new TextView(context);
        int padding = (int) (16 * context.getResources()
                .getDisplayMetrics().density);

        itemView.setPadding(padding, padding, padding, padding);
        itemView.setTextSize(16);

        return new PantryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        PantryItem item = pantryItems.get(position);

        holder.name.setText(
                item.name + "\nQuantity: " + item.quantity + " " + item.unit
        );
    }

    @Override
    public int getItemCount() {
        return pantryItems.size();
    }

    static class PantryViewHolder extends RecyclerView.ViewHolder {
        TextView name;

        PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView;
        }
    }

    public static class PantryItem {
        public String name;
        public double quantity;
        public String unit;

        public PantryItem(String name, double quantity, String unit) {
            this.name = name;
            this.quantity = quantity;
            this.unit = unit;
        }
    }
}
