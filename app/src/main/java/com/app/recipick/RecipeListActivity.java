package com.app.recipick;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.app.recipick.adapters.TheAdapter;
import com.app.recipick.data.AppDatabase;
import com.app.recipick.data.Recipe.Recipe;
import java.util.ArrayList;
import java.util.List;

public class RecipeListActivity extends AppCompatActivity {

    private RecyclerView recyclerRecipes;
    private TheAdapter adapter;
    private AppDatabase db;
    private View layoutEmptyRecipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        db = AppDatabase.getInstance(this);

        // BOTTOM NAVIGATION
        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_recipes);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_ingredients) {
                startActivity(new Intent(RecipeListActivity.this, MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_recipes) {
                return true;
            } else if (itemId == R.id.nav_favorites) {
                startActivity(new Intent(RecipeListActivity.this, FavoritesActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });

        recyclerRecipes = findViewById(R.id.recyclerRecipes);
        layoutEmptyRecipes = findViewById(R.id.layoutEmptyRecipes);
        recyclerRecipes.setLayoutManager(new LinearLayoutManager(this));

        //  ΣΥΡΟΜΕΝΗ ΜΠΑΡΑ
        SeekBar seekBarFilter = findViewById(R.id.seekBarFilter);
        TextView tvSliderLabel = findViewById(R.id.tvSliderLabel);

        seekBarFilter.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 4) {
                    tvSliderLabel.setText("Missing ingredients: Any");
                } else if (progress == 0) {
                    tvSliderLabel.setText("Missing ingredients: Exact Match");
                } else {
                    tvSliderLabel.setText("Missing ingredients: Up to " + progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                int maxMissing = (progress == 4) ? 999 : progress;
                loadRecipes(maxMissing);
            }
        });

        loadRecipes(1);
    }

    private void loadRecipes(int maxMissing) {
        new Thread(() -> {
            try {
                SupportSQLiteDatabase sdb = db.getOpenHelper().getReadableDatabase();
                List<Recipe> matchingRecipes = new ArrayList<>();

                String query = "SELECT r.id, r.name, r.desc, r.instr, r.imgsrc, r.isFavorite, " +
                        "COUNT(ri.ingredientId) AS total_ings, " +
                        "SUM(CASE WHEN i.selected = 1 THEN 1 ELSE 0 END) AS matched_ings, " +
                        "(COUNT(ri.ingredientId) - SUM(CASE WHEN i.selected = 1 THEN 1 ELSE 0 END)) AS missing_ings " +
                        "FROM Recipe r " +
                        "JOIN Recipe_Ingredients ri ON r.id = ri.recipeId " +
                        "JOIN Ingredient i ON ri.ingredientId = i.id " +
                        "GROUP BY r.id " +
                        "HAVING missing_ings <= " + maxMissing + " " +
                        "ORDER BY missing_ings ASC, matched_ings DESC";

                try (Cursor cursor = sdb.query(query, new Object[0])) {
                    while (cursor.moveToNext()) {
                        Recipe r = new Recipe();
                        r.id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                        r.name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                        r.instr = cursor.getString(cursor.getColumnIndexOrThrow("instr"));
                        r.imgsrc = cursor.getString(cursor.getColumnIndexOrThrow("imgsrc"));
                        r.isFavorite = cursor.getInt(cursor.getColumnIndexOrThrow("isFavorite")) == 1;
                        r.missingIngredients = cursor.getInt(cursor.getColumnIndexOrThrow("missing_ings"));
                        r.desc = cursor.getString(cursor.getColumnIndexOrThrow("desc"));

                        matchingRecipes.add(r);
                    }
                }

                runOnUiThread(() -> {
                    if (matchingRecipes.isEmpty()) {
                        layoutEmptyRecipes.setVisibility(View.VISIBLE);
                        recyclerRecipes.setVisibility(View.GONE);
                    } else {
                        layoutEmptyRecipes.setVisibility(View.GONE);
                        recyclerRecipes.setVisibility(View.VISIBLE);

                        adapter = new TheAdapter(matchingRecipes, recipe -> {
                            Intent intent = new Intent(RecipeListActivity.this, RecipeDetailsActivity.class);
                            intent.putExtra("RECIPE_ID", recipe.id);
                            intent.putExtra("RECIPE_NAME", recipe.name);
                            intent.putExtra("RECIPE_DESC", recipe.desc);
                            intent.putExtra("RECIPE_FAV", recipe.isFavorite);
                            intent.putExtra("RECIPE_INSTR", recipe.instr);
                            intent.putExtra("RECIPE_IMG", recipe.imgsrc);
                            startActivity(intent);
                        });
                        recyclerRecipes.setAdapter(adapter);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}