package com.app.recipick;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

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
                return true;
            }
            return false;
        });

        AppDatabase db = AppDatabase.getInstance(this);
        recyclerRecipes = findViewById(R.id.recyclerRecipes);
        TextView txtNoRecipes = findViewById(R.id.txtNoRecipes);

        recyclerRecipes.setLayoutManager(new LinearLayoutManager(this));

        new Thread(() -> {
            try {
                SupportSQLiteDatabase sdb = db.getOpenHelper().getReadableDatabase();
                List<Recipe> matchingRecipes = new ArrayList<>();

                //Μετράει πόσα υλικά ζητάει η συνταγή και πόσα λειπουν
                String query = "SELECT r.id, r.name, r.desc, r.instr, " +
                        "COUNT(ri.ingredientId) AS total_ings, " +
                        "SUM(CASE WHEN i.selected = 1 THEN 1 ELSE 0 END) AS matched_ings, " +
                        "(COUNT(ri.ingredientId) - SUM(CASE WHEN i.selected = 1 THEN 1 ELSE 0 END)) AS missing_ings " +
                        "FROM Recipe r " +
                        "JOIN Recipe_Ingredients ri ON r.id = ri.recipeId " +
                        "JOIN Ingredient i ON ri.ingredientId = i.id " +
                        "GROUP BY r.id " +
                        "HAVING missing_ings <= 1 " +
                        "ORDER BY missing_ings ASC, matched_ings DESC";

                try (Cursor cursor = sdb.query(query, new Object[0])) {
                    while (cursor.moveToNext()) {
                        Recipe r = new Recipe();
                        r.id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                        r.name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                        r.instr = cursor.getString(cursor.getColumnIndexOrThrow("instr"));

                        int missing = cursor.getInt(cursor.getColumnIndexOrThrow("missing_ings"));
                        r.missingIngredients = missing;
                        r.desc = cursor.getString(cursor.getColumnIndexOrThrow("desc"));

                        matchingRecipes.add(r);
                    }
                }

                runOnUiThread(() -> {
                    if (matchingRecipes.isEmpty()) {
                        txtNoRecipes.setVisibility(View.VISIBLE);
                        recyclerRecipes.setVisibility(View.GONE);
                    } else {
                        txtNoRecipes.setVisibility(View.GONE);
                        recyclerRecipes.setVisibility(View.VISIBLE);
                        adapter = new TheAdapter(matchingRecipes, recipe -> {
                            Intent intent = new Intent(RecipeListActivity.this, RecipeDetailsActivity.class);
                            intent.putExtra("recipe", recipe);
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