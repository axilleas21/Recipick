package com.app.recipick;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.recipick.adapters.TheAdapter;
import com.app.recipick.data.AppDatabase;
import com.app.recipick.data.Recipe.Recipe;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayout layoutEmptyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        recyclerView = findViewById(R.id.recyclerViewFavorites);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));


        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        bottomNav.setSelectedItemId(R.id.nav_favorites);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_ingredients) {
                android.content.Intent intent = new android.content.Intent(FavoritesActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
                return true;

            } else if (itemId == R.id.nav_recipes) {
                android.content.Intent intent = new android.content.Intent(FavoritesActivity.this, RecipeListActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
                return true;

            } else if (itemId == R.id.nav_favorites) {
                return true;
            }
            return false;
        });

        loadFavorites();
    }

    private void loadFavorites() {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            List<Recipe> favoriteList = db.recipeDao().getFavoriteRecipes();

            runOnUiThread(() -> {
                if (favoriteList.isEmpty()) {
                    // Αν δεν έχει αγαπημένα, δείξε το μήνυμα και κρύψε τη λίστα
                    layoutEmptyState.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    // Αν έχει, κρύψε το μήνυμα και γέμισε τη λίστα
                    layoutEmptyState.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                    TheAdapter adapter = new TheAdapter(favoriteList, recipe -> {
                        // Τι γίνεται όταν πατάμε μια αγαπημένη συνταγή: Ανοίγει τις λεπτομέρειες!
                        android.content.Intent intent = new android.content.Intent(FavoritesActivity.this, RecipeDetailsActivity.class);
                        intent.putExtra("RECIPE_ID", recipe.id);
                        intent.putExtra("RECIPE_NAME", recipe.name);
                        intent.putExtra("RECIPE_DESC", recipe.desc);
                        intent.putExtra("RECIPE_FAV", recipe.isFavorite);
                        intent.putExtra("RECIPE_INSTR", recipe.instr);
                        intent.putExtra("RECIPE_IMG", recipe.imgsrc);
                        startActivity(intent);
                    });

                    recyclerView.setAdapter(adapter);                    recyclerView.setAdapter(adapter);
                }
            });
        }).start();
    }

    // Όταν επιστρέφουμε σε αυτή την οθόνη από μια συνταγή,
    // ξαναφορτώνουμε τη λίστα σε περίπτωση που ο χρήστης αφαίρεσε την καρδούλα!
    @Override
    protected void onResume() {
        super.onResume();
        loadFavorites();
    }
}