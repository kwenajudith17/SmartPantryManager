import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
package com.example.smartpantry;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private PantryDb db;
    private LinearLayout content;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new PantryDb(this);
        showScreen("Pantry");
    }

    private int dp(int value) { return (int)(value * getResources().getDisplayMetrics().density); }

    private TextView label(String value, int size, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value); t.setTextSize(size); t.setTextColor(0xFF24352C);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        t.setPadding(0, dp(5), 0, dp(5));
        return t;
    }

    private Button actionButton(String title, Runnable action) {
        Button b = new Button(this); b.setText(title); b.setAllCaps(false);
        b.setOnClickListener(v -> action.run()); return b;
    }

    private LinearLayout card() {
        LinearLayout box = new LinearLayout(this); box.setOrientation(LinearLayout.VERTICAL);
        box.setBackgroundColor(0xFFFFFFFF); box.setPadding(dp(12), dp(8), dp(12), dp(8));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(0, dp(6), 0, dp(6)); box.setLayoutParams(p); return box;
    }

    private void showScreen(String screen) {
        LinearLayout root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(12), dp(16), dp(8)); root.setBackgroundColor(0xFFF5F7F2);
        TextView heading = label("Smart Pantry Manager", 24, true); heading.setTextColor(0xFF24543D);
        root.addView(heading); root.addView(label("Use what you have. Waste less.", 14, false));
        HorizontalScrollView navScroll = new HorizontalScrollView(this);
        LinearLayout nav = new LinearLayout(this); nav.setOrientation(LinearLayout.HORIZONTAL);
        for (String tab : new String[]{"Pantry", "Suggested Recipes", "All Recipes", "Settings"})
            nav.addView(actionButton(tab, () -> showScreen(tab)));
        navScroll.addView(nav); root.addView(navScroll);
        ScrollView scroll = new ScrollView(this);
        content = new LinearLayout(this); content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(0, dp(10), 0, dp(10)); scroll.addView(content);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1)); setContentView(root);
        if (screen.equals("Pantry")) showPantry();
        else if (screen.equals("Suggested Recipes")) showSuggestions();
        else if (screen.equals("All Recipes")) showRecipes();
        else showSettings();
    }
    
private void showPantry() {
    content.addView(label("My Pantry", 21, true));
    content.addView(label(
        "Add ingredients you have at home. Quantities are checked against recipe requirements.",
        14, false
    ));

    content.addView(actionButton("+ Add pantry item",
        () -> itemDialog(null, "", "", "pcs", "")
    ));

    List<PantryAdapter.PantryItem> items = new ArrayList<>();

    Cursor c = db.getReadableDatabase().rawQuery(
        "SELECT id, name, quantity, unit, expiry FROM pantry ORDER BY name",
        null
    );

    while (c.moveToNext()) {
        items.add(new PantryAdapter.PantryItem(
            c.getInt(0),
            c.getString(1),
            c.getDouble(2),
            c.getString(3),
            c.getString(4)
        ));
    }
    c.close();

    if (items.isEmpty()) {
        content.addView(label(
            "Your pantry is empty. Add ingredients to start getting recipe suggestions.",
            16, false
        ));
    }

    RecyclerView recycler = new RecyclerView(this);
    recycler.setLayoutManager(new LinearLayoutManager(this));
    recycler.setNestedScrollingEnabled(false);

    PantryAdapter adapter = new PantryAdapter(
        this,
        items,
        new PantryAdapter.OnPantryActionListener() {
            @Override
            public void onEdit(PantryAdapter.PantryItem item) {
                itemDialog(
                    item.id,
                    item.name,
                    fmt(item.quantity),
                    item.unit,
                    item.expiry == null ? "" : item.expiry
                );
            }

            @Override
            public void onDelete(PantryAdapter.PantryItem item) {
                new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Delete " + item.name + "?")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Delete", (dialog, which) -> {
                        db.getWritableDatabase().delete(
                            "pantry",
                            "id=?",
                            new String[]{String.valueOf(item.id)}
                        );
                        showScreen("Pantry");
                    })
                    .show();
            }
        }
    );

    recycler.setAdapter(adapter);
    content.addView(recycler,
        new LinearLayout.LayoutParams(-1, -2));
}


   private EditText field(String hint, String value, boolean number) {
        EditText e = new EditText(this); e.setSingleLine(true); e.setHint(hint);
        if (value != null) e.setText(value); if (number) e.setInputType(8194); return e;
    }

    private void itemDialog(Integer id, String oldName, String oldQty, String oldUnit, String oldExpiry) {
        LinearLayout form = new LinearLayout(this); form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(dp(20), dp(4), dp(20), dp(4));
        EditText name = field("Ingredient name", oldName, false), qty = field("Quantity", oldQty, true);
        EditText unit = field("Unit (pcs, g, ml, slices, etc.)", oldUnit, false);
        EditText expiry = field("Expiry date (optional: YYYY-MM-DD)", oldExpiry, false);
        form.addView(name); form.addView(qty); form.addView(unit); form.addView(expiry);
        new AlertDialog.Builder(this).setTitle(id == null ? "Add pantry item" : "Edit pantry item").setView(form)
            .setNegativeButton("Cancel", null).setPositiveButton("Save", (dialog, which) -> {
                String n = name.getText().toString().trim(), q = qty.getText().toString().trim(), u = unit.getText().toString().trim();
                if (n.isEmpty() || q.isEmpty() || u.isEmpty()) { Toast.makeText(this, "Enter a name, quantity and unit.", Toast.LENGTH_LONG).show(); return; }
                try {
                    double amount = Double.parseDouble(q); if (amount <= 0) throw new NumberFormatException();
                    ContentValues values = new ContentValues(); values.put("name", n); values.put("quantity", amount);
                    values.put("unit", u); values.put("expiry", expiry.getText().toString().trim());
                    if (id == null) db.getWritableDatabase().insert("pantry", null, values);
                    else db.getWritableDatabase().update("pantry", values, "id=?", new String[]{String.valueOf(id)});
                    showScreen("Pantry");
                } catch (NumberFormatException ex) { Toast.makeText(this, "Quantity must be a number greater than zero.", Toast.LENGTH_LONG).show(); }
            }).show();
    }

    private void showSuggestions() {
        content.addView(label("Recipes you can make now", 21, true));
        content.addView(label("Only recipes with every required ingredient in sufficient quantity appear here.", 14, false));
        List<Recipe> matches = new ArrayList<>();
        for (Recipe r : db.getRecipes()) if (canMake(r)) matches.add(r);
        if (matches.isEmpty()) { content.addView(label("No recipes match your pantry yet — add ingredients or increase their quantities.", 16, false)); return; }
        content.addView(label(matches.size() + " recipe(s) match your current pantry.", 14, true));
        for (Recipe r : matches) addRecipeCard(r);
    }

    private boolean canMake(Recipe recipe) {
        SQLiteDatabase sql = db.getReadableDatabase();
        for (Map.Entry<String, Double> need : recipe.ingredients.entrySet()) {
            Cursor c = sql.rawQuery("SELECT name,quantity,unit FROM pantry", null); double available = 0;
            while (c.moveToNext()) {
                if (normalise(c.getString(0)).equals(normalise(need.getKey())) && c.getString(2).equalsIgnoreCase(recipe.units.get(need.getKey())))
                    available += c.getDouble(1);
            }
            c.close(); if (available < need.getValue()) return false;
        }
        return true;
    }

    private String normalise(String s) {
        String n = s.toLowerCase(Locale.ROOT).trim();
        if (n.endsWith("ies") && n.length() > 4) return n.substring(0, n.length()-3) + "y";
        if (n.endsWith("oes") && n.length() > 4) return n.substring(0, n.length()-2);
        if (n.endsWith("s") && !n.endsWith("ss") && n.length() > 3) return n.substring(0, n.length()-1);
        return n;
    }

    private void showRecipes() {
        content.addView(label("Recipe Collection", 21, true));
        content.addView(label("Browse all saved recipes and view their ingredients and method.", 14, false));
        for (Recipe r : db.getRecipes()) addRecipeCard(r);
    }

    private void addRecipeCard(Recipe r) {
        LinearLayout box = card(); box.addView(label(r.name, 17, true));
        box.addView(label(r.ingredients.size() + " ingredients", 13, false));
        box.addView(actionButton("View recipe", () -> showRecipeDetail(r))); content.addView(box);
    }

    private void showRecipeDetail(Recipe r) {
        LinearLayout detail = new LinearLayout(this); detail.setOrientation(LinearLayout.VERTICAL);
        detail.setPadding(dp(20), dp(6), dp(20), dp(6)); detail.addView(label("Ingredients required", 18, true));
        for (Map.Entry<String, Double> e : r.ingredients.entrySet())
            detail.addView(label("• " + fmt(e.getValue()) + " " + r.units.get(e.getKey()) + " " + e.getKey(), 15, false));
        detail.addView(label("Preparation method", 18, true)); detail.addView(label(r.method, 15, false));
        new AlertDialog.Builder(this).setTitle(r.name).setView(detail).setPositiveButton("Close", null).show();
    }

    private void showSettings() {
        content.addView(label("Settings", 21, true)); content.addView(label("Preferences for your pantry.", 14, false));
        CheckBox alerts = new CheckBox(this); alerts.setText("Show expiring-soon reminders");
        alerts.setChecked(getPreferences(MODE_PRIVATE).getBoolean("expiry_alerts", true));
        alerts.setOnCheckedChangeListener((button, checked) -> getPreferences(MODE_PRIVATE).edit().putBoolean("expiry_alerts", checked).apply());
        content.addView(alerts); content.addView(label("About", 18, true));
        content.addView(label("Smart Pantry Manager helps you use ingredients you already own and reduce food waste. Suggested recipes follow strict ingredient and quantity matching.", 15, false));
        content.addView(label("Pantry data is stored locally on this device using SQLite.", 13, false));
    }

    private String fmt(double q) { return q == Math.rint(q) ? String.valueOf((long)q) : String.valueOf(q); }

    static class Recipe {
        String name, method; LinkedHashMap<String, Double> ingredients = new LinkedHashMap<>(); HashMap<String, String> units = new HashMap<>();
        Recipe(String n, String m) { name = n; method = m; }
        Recipe add(String ingredient, double quantity, String unit) { ingredients.put(ingredient, quantity); units.put(ingredient, unit); return this; }
    }

    static class PantryDb extends SQLiteOpenHelper {
        PantryDb(Context context) { super(context, "smart_pantry.db", null, 1); }
        @Override public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE pantry(id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT NOT NULL,quantity REAL NOT NULL,unit TEXT NOT NULL,expiry TEXT)");
            db.execSQL("CREATE TABLE recipes(id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT NOT NULL,ingredients TEXT NOT NULL,method TEXT NOT NULL)"); seed(db);
        }
        @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
        private void seed(SQLiteDatabase db) {
            String[][] recipes = {
                {"Tomato Omelette","eggs:2:pcs|tomato:1:pcs|salt:1:pinch","Beat eggs with chopped tomato and salt; cook in a lightly oiled pan until set."},
                {"Garlic Tomato Pasta","pasta:200:g|tomato:2:pcs|garlic:1:clove|salt:1:pinch","Boil pasta. Cook chopped tomato and garlic, season, then combine."},
                {"Banana Oat Porridge","oats:50:g|milk:200:ml|banana:1:pcs","Simmer oats in milk until soft. Slice banana over the top."},
                {"Cheese Toast","bread:2:slices|cheese:2:slices","Place cheese between bread and toast in a pan until golden."},
                {"Simple Pancakes","flour:100:g|milk:150:ml|eggs:1:pcs","Whisk flour, milk and egg into a smooth batter; cook small pancakes on both sides."},
                {"Potato Hash","potato:2:pcs|onion:1:pcs|salt:1:pinch","Dice and pan-cook potato and onion until tender; season."},
                {"Peanut Banana Toast","bread:2:slices|peanut butter:2:tbsp|banana:1:pcs","Spread peanut butter on toast and add sliced banana."},
                {"Rice and Beans","rice:150:g|beans:150:g|salt:1:pinch","Cook rice. Warm cooked beans and season, then serve together."},
                {"Apple Oat Bowl","oats:40:g|apple:1:pcs|milk:100:ml","Combine oats and milk; top with chopped apple."},
                {"Tuna Sandwich","bread:2:slices|tuna:1:can|mayonnaise:1:tbsp","Mix tuna with mayonnaise and place between slices of bread."},
                {"Garlic Fried Rice","rice:200:g|garlic:1:clove|egg:1:pcs","Fry garlic, add cooked rice and stir in egg until cooked through."},
                {"Tomato Cheese Toast","bread:2:slices|tomato:1:pcs|cheese:2:slices","Top bread with tomato and cheese; grill until cheese melts."},
                {"Spinach Omelette","eggs:2:pcs|spinach:50:g|salt:1:pinch","Beat eggs, fold in chopped spinach and cook until firm."},
                {"Carrot Soup","carrot:2:pcs|onion:1:pcs|water:300:ml|salt:1:pinch","Simmer chopped carrot and onion in water until soft; mash and season."},
                {"Yoghurt Fruit Bowl","yoghurt:150:g|banana:1:pcs|apple:1:pcs","Slice fruit and serve mixed into yoghurt."},
                {"Lemon Water","lemon:1:pcs|water:250:ml","Squeeze lemon into water and stir."},
                {"Avocado Toast","bread:2:slices|avocado:1:pcs|salt:1:pinch","Mash avocado with salt and spread on toast."},
                {"Simple Salad","tomato:1:pcs|cucumber:1:pcs|lettuce:30:g","Chop all vegetables and toss together."}
            };
            for (String[] r : recipes) { ContentValues v = new ContentValues(); v.put("name",r[0]); v.put("ingredients",r[1]); v.put("method",r[2]); db.insert("recipes",null,v); }
        }
        List<Recipe> getRecipes() {
            List<Recipe> list = new ArrayList<>(); Cursor c = getReadableDatabase().rawQuery("SELECT name,ingredients,method FROM recipes ORDER BY name",null);
            while (c.moveToNext()) { Recipe r = new Recipe(c.getString(0),c.getString(2));
                for (String item : c.getString(1).split("\\|")) { String[] p = item.split(":"); if (p.length == 3) try { r.add(p[0],Double.parseDouble(p[1]),p[2]); } catch (NumberFormatException ignored) { } }
                list.add(r);
            } c.close(); return list;
        }
    }
}
