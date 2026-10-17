
package com.example.smartpantry;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    public interface OnPantryActionListener {
        void onEdit(PantryItem item);
        void onDelete(PantryItem item);
    }

    private final Context context;
    private final List<PantryItem> pantryItems;
    private final OnPantryActionListener listener;

    public PantryAdapter(Context context, List<PantryItem> items,
                         OnPantryActionListener listener) {
        this.context = context;
        this.pantryItems = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                               int viewType) {
        LinearLayout card = new LinearLayout(context);
        card.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) (12 * context.getResources()
                .getDisplayMetrics().density);
        card.setPadding(padding, padding, padding, padding);

        TextView name = new TextView(context);
        name.setTextSize(18);
        name.setTypeface(null, Typeface.BOLD);
        card.addView(name);

        TextView details = new TextView(context);
        details.setTextSize(14);
        card.addView(details);

        LinearLayout buttons = new LinearLayout(context);
        Button edit = new Button(context);
        edit.setText("Edit");
        edit.setAllCaps(false);

        Button delete = new Button(context);
        delete.setText("Delete");
        delete.setAllCaps(false);

        buttons.addView(edit);
        buttons.addView(delete);
        card.addView(buttons);

        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, padding / 2, 0, padding / 2);
        card.setLayoutParams(params);

        return new PantryViewHolder(card, name, details, edit, delete);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder,
                                 int position) {
        PantryItem item = pantryItems.get(position);
        holder.name.setText(item.name);

        String text = "Quantity: " + item.quantity + " " + item.unit;
        if (item.expiry != null && !item.expiry.isEmpty()) {
            text += "\nExpiry: " + item.expiry;
        }
        holder.details.setText(text);

        holder.edit.setOnClickListener(v -> listener.onEdit(item));
        holder.delete.setOnClickListener(v -> listener.onDelete(item));
    }

    @Override
    public int getItemCount() {
        return pantryItems.size();
    }

    static class PantryViewHolder extends RecyclerView.ViewHolder {
        TextView name, details;
        Button edit, delete;

        PantryViewHolder(View view, TextView name, TextView details,
                         Button edit, Button delete) {
            super(view);
            this.name = name;
            this.details = details;
            this.edit = edit;
            this.delete = delete;
        }
    }

    public static class PantryItem {
        public int id;
        public String name;
        public double quantity;
        public String unit;
        public String expiry;

        public PantryItem(int id, String name, double quantity,
                          String unit, String expiry) {
            this.id = id;
            this.name = name;
            this.quantity = quantity;
            this.unit = unit;
            this.expiry = expiry;
        }
    }
}

