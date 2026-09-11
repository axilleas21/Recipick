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

        AppDatabase db = AppDatabase.getInstance(this);
        recyclerRecipes = findViewById(R.id.recyclerRecipes);
        TextView txtNoRecipes = findViewById(R.id.txtNoRecipes);

        recyclerRecipes.setLayoutManager(new LinearLayoutManager(this));

        new Thread(() -> {
            try {
                SupportSQLiteDatabase sdb = db.getOpenHelper().getReadableDatabase();
                List<Recipe> matchingRecipes = new ArrayList<>();

                //Μετράει πόσα υλικά ζητάει η συνταγή και πόσα έχει επιλέξει ο χρήστης
                String query = "SELECT r.id, r.name, r.desc, r.instr, " +
                        "COUNT(ri.ingredientId) AS total_ings, " +
                        "SUM(CASE WHEN i.selected = 1 THEN 1 ELSE 0 END) AS matched_ings " +
                        "FROM Recipe r " +
                        "JOIN Recipe_Ingredients ri ON r.id = ri.recipeId " +
                        "JOIN Ingredient i ON ri.ingredientId = i.id " +
                        "GROUP BY r.id " +
                        "HAVING (CAST(matched_ings AS FLOAT) / total_ings) >= 0.5 " +
                        "ORDER BY CAST(matched_ings AS FLOAT) / total_ings DESC, matched_ings DESC";

                try (Cursor cursor = sdb.query(query, new Object[0])) {
                    while (cursor.moveToNext()) {
                        Recipe r = new Recipe();
                        r.id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                        r.name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                        r.instr = cursor.getString(cursor.getColumnIndexOrThrow("instr"));

                        int total = cursor.getInt(cursor.getColumnIndexOrThrow("total_ings"));
                        int matched = cursor.getInt(cursor.getColumnIndexOrThrow("matched_ings"));
                        r.matchPercentage = (int) (((float) matched / total) * 100); // Το αποθηκεύουμε στο αντικείμενο

                        String originalDesc = cursor.getString(cursor.getColumnIndexOrThrow("desc"));
                        r.desc = "Ταιριάζει: " + r.matchPercentage + "% (" + matched + "/" + total + " υλικά)\n" + originalDesc;

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