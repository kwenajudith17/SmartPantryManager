package com.example.smartpantry;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {
    public static class PantryItem {
        public final int id;
        public final String name;
        public final double quantity;
        public final String unit;
        public final String expiry;

        public PantryItem(int id, String name, double quantity, String unit, String expiry) {
            this.id = id;
            this.name = name;
            this.quantity = quantity;
            this.unit = unit;
            this.expiry = expiry;
        }
    }

    public interface OnPantryActionListener {
        void onEdit(PantryItem item);
        void onDelete(PantryItem item);
    }

    private final Context context;
    private final List<PantryItem> items;
    private final OnPantryActionListener listener;

    public PantryAdapter(Context context, List<PantryItem> items, OnPantryActionListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @Override
    public PantryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout card = new LinearLayout(context);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(24, 16, 24, 16);
        card.setBackgroundColor(0xFFFFFFFF);
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 8, 0, 8);
        card.setLayoutParams(params);
        return new PantryViewHolder(card);
    }

    @Override
    public void onBindViewHolder(PantryViewHolder holder, int position) {
        PantryItem item = items.get(position);
        holder.title.setText(item.name);
        holder.title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        String details = item.quantity + " " + item.unit;
        if (item.expiry != null && !item.expiry.trim().isEmpty()) details += "\nExpiry: " + item.expiry;
        holder.details.setText(details);
        holder.actions.removeAllViews();
        Button edit = new Button(context);
        edit.setText("Edit");
        edit.setAllCaps(false);
        edit.setOnClickListener(v -> listener.onEdit(item));
        Button delete = new Button(context);
        delete.setText("Delete");
        delete.setAllCaps(false);
        delete.setOnClickListener(v -> listener.onDelete(item));
        holder.actions.addView(edit);
        holder.actions.addView(delete);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class PantryViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView details;
        final LinearLayout actions;

        PantryViewHolder(View itemView) {
            super(itemView);
            LinearLayout card = (LinearLayout) itemView;
            title = new TextView(card.getContext());
            title.setTextSize(18);
            details = new TextView(card.getContext());
            actions = new LinearLayout(card.getContext());
            actions.setOrientation(LinearLayout.HORIZONTAL);
            card.addView(title);
            card.addView(details);
            card.addView(actions);
        }
    }
}
