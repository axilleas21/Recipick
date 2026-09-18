package com.app.recipick;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

/*
       η αρχική οθόνη της εφαρμογής.
       δείχνει τα υλικά που έχει ο χρήστης και επιτρέπει αναζήτηση,
       διαγραφή με swipe και με πολλαπλή επιλογή
 */
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


        //αρχικοποίηση UI στοιχείων και βάσης δεδομένων
        rvIngredients = findViewById(R.id.rvIngredients);
        btnSearch = findViewById(R.id.btnSearch);
        bottomNav = findViewById(R.id.bottom_navigation);
        db = AppDatabase.getInstance(this);

        rvIngredients.setLayoutManager(new LinearLayoutManager(this));

        //to true δείχνει οτι είμαστε στη σελίδα με τα επιλεγμένα υλικά (χωρίς checkboxes)
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

                        //ακυρωση αν ειναι ηδη σε mode διαγραφης
                        if (adapter.isMultiSelectMode()) {
                            adapter.notifyItemChanged(position);
                            return;
                        }

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

        // Κουμπί αναζήτησης συνταγών, πάει στη λίστα με τις συνταγες
        btnSearch.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, RecipeListActivity.class);
            startActivity(intent);
        });

        // Κουμπί Floating Action για προσθήκη νέων υλικών
        findViewById(R.id.fabAddIngredient).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AddIngredientActivity.class));
        });

        //bottom navigation bar
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

        //real time φ   ιλτράρισμα λίστας
        searchViewPantry.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return true;
            }
        });

        //λογική πολλαπλής επιλογής και διαγραφής
        android.widget.LinearLayout layoutMultiSelectActions = findViewById(R.id.layoutMultiSelectActions);
        Button btnSelectAll = findViewById(R.id.btnSelectAll);
        Button btnDeleteSelected = findViewById(R.id.btnDeleteSelected);
        View fabAddIngredient = findViewById(R.id.fabAddIngredient);

        // Όταν μπαίνουμε σε Λειτουργία Διαγραφής, εμφανίζουμε την μπάρα με τα κουμπιά Select All & Delete
        adapter.setOnMultiSelectListener((isMultiSelect, count) -> {
            layoutMultiSelectActions.setVisibility(isMultiSelect ? android.view.View.VISIBLE : android.view.View.GONE);
            fabAddIngredient.setVisibility(isMultiSelect ? android.view.View.GONE : android.view.View.VISIBLE);

            if (isMultiSelect) {
                btnSearch.setVisibility(android.view.View.GONE);  //κρύβουμε το κουμπί
                btnDeleteSelected.setText("Delete (" + count + ")"); //ανανέωση αριθμού διαγραφόμενων υλικών
            } else {
                checkEmptyState();
            }
        });

       // Λειτουργία Select All για επιλογ΄΄η όλων των υλικών για διαγραφή
        btnSelectAll.setOnClickListener(v -> {
            adapter.selectAll();
        });

        btnDeleteSelected.setOnClickListener(v -> {
            List<Ingredient> toDelete = adapter.getSelectedForDeletion();
            new Thread(() -> {
                for (Ingredient ing : toDelete) {
                    db.ingredientDao().updateSelection(ing.id, 0); // Διαγραφή
                }
                runOnUiThread(() -> {
                    adapter.clearSelection();
                    loadIngredients();
                    android.widget.Toast.makeText(MainActivity.this, "Deleted successfully!", android.widget.Toast.LENGTH_SHORT).show();
                });
            }).start();
        });


    }


    //αν πατηθεί το κουμπί πίσω του κινητού τότε αν ειναι σε λειτουργία πολλαπλης διαγραφής απλώς κλείνει αυτη η λειτουργία
    @Override
    public void onBackPressed() {
        if (adapter != null && adapter.isMultiSelectMode()) {
            adapter.clearSelection();
        } else {
            super.onBackPressed();
        }
    }

    //ανανεώνει τη λίστα των επιλεγμπένων κάθε φορά που επιστρέφουμε στην οθονη
    @Override
    protected void onResume() {
        super.onResume();
        loadIngredients();
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_ingredients);
        }
    }

    //φέρνει τα επιλεγμένα υλικά του χρήστη
    private void loadIngredients() {
        new Thread(() -> {
            List<Ingredient> list = db.ingredientDao().getSelectedIngredients();

            runOnUiThread(() -> {
                adapter.setIngredients(list);
                checkEmptyState();
            });
        }).start();
    }

    //ελέγχει αν ο χρήστης έχει υλικά, αν δεν έχει κρύβει το κουμπί αναζήτησης και δείχνει την empty state οθόνη
    private void checkEmptyState() {
        if (adapter.getItemCount() == 0) {
            rvIngredients.setVisibility(android.view.View.GONE);
            layoutEmptyState.setVisibility(android.view.View.VISIBLE);
            btnSearch.setVisibility(android.view.View.GONE); //Κρύβει το κουμπί
        } else {
            rvIngredients.setVisibility(android.view.View.VISIBLE);
            layoutEmptyState.setVisibility(android.view.View.GONE);

            if (!adapter.isMultiSelectMode()) {
                btnSearch.setVisibility(android.view.View.VISIBLE);
            }
        }
    }
}