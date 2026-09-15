package com.app.recipick;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.app.recipick.adapters.IngredientAdapter;
import com.app.recipick.data.AppDatabase;
import com.app.recipick.data.Ingredient.Ingredient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvIngredients;
    private Button btnSearch;
    private BottomNavigationView bottomNav;
    private IngredientAdapter adapter;
    private AppDatabase db;

    private android.widget.LinearLayout layoutEmptyState;
    private androidx.appcompat.widget.SearchView searchViewPantry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);

        rvIngredients = findViewById(R.id.rvIngredients);
        btnSearch = findViewById(R.id.btnSearch);
        bottomNav = findViewById(R.id.bottom_navigation);
        db = AppDatabase.getInstance(this);

        rvIngredients.setLayoutManager(new LinearLayoutManager(this));
        adapter = new IngredientAdapter(true);
        rvIngredients.setAdapter(adapter);

        // Swipe to Delete
        androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback(0, androidx.recyclerview.widget.ItemTouchHelper.LEFT | androidx.recyclerview.widget.ItemTouchHelper.RIGHT) {

                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                        int position = viewHolder.getAdapterPosition();
                        viewHolder.itemView.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);

                        Ingredient deletedIngredient = adapter.getIngredientAt(position);

                        new Thread(() -> {
                            db.ingredientDao().updateSelection(deletedIngredient.id, 0);
                        }).start();

                        adapter.removeIngredient(position);
                        checkEmptyState();
                    }
                };

        new androidx.recyclerview.widget.ItemTouchHelper(simpleItemTouchCallback).attachToRecyclerView(rvIngredients);

        btnSearch.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, RecipeListActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.fabAddIngredient).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AddIngredientActivity.class));
        });

        bottomNav.setSelectedItemId(R.id.nav_ingredients);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_ingredients) {
                return true;

            } else if (itemId == R.id.nav_recipes) {
                Intent intent = new Intent(MainActivity.this, RecipeListActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
                return true;

            } else if (itemId == R.id.nav_favorites) {
                Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
                return true;
            }

            return false;
        });

        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        searchViewPantry = findViewById(R.id.searchViewPantry);

        searchViewPantry.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadIngredients();
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_ingredients);
        }
    }

    private void loadIngredients() {
        new Thread(() -> {
            List<Ingredient> list = db.ingredientDao().getSelectedIngredients();

            runOnUiThread(() -> {
                adapter.setIngredients(list);
                checkEmptyState();
            });
        }).start();
    }

    private void checkEmptyState() {
        if (adapter.getItemCount() == 0) {
            rvIngredients.setVisibility(android.view.View.GONE);
            layoutEmptyState.setVisibility(android.view.View.VISIBLE);
        } else {
            rvIngredients.setVisibility(android.view.View.VISIBLE);
            layoutEmptyState.setVisibility(android.view.View.GONE);
        }
    }
}