package com.app.recipick;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.app.recipick.adapters.IngredientAdapter;
import com.app.recipick.data.AppDatabase;
import com.app.recipick.data.Ingredient.Ingredient;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText etSearchIngredient;
    private RecyclerView rvIngredients;
    private Button btnSearch;
    private IngredientAdapter adapter;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);

        initializeViews();

        db = AppDatabase.getInstance(this);

        // Ρύθμιση του RecyclerView
        rvIngredients.setLayoutManager(new LinearLayoutManager(this));
        adapter = new IngredientAdapter();
        rvIngredients.setAdapter(adapter);

        // Φόρτωση όλων των υλικών από τη βάση
        loadIngredients();

        // Λειτουργία Αναζήτησης στο EditText
        etSearchIngredient.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Κουμπί Εύρεσης Συνταγών
        btnSearch.setOnClickListener(view -> {
            List<Ingredient> selectedIngredients = adapter.getSelectedIngredients();

            new Thread(() -> {
                SupportSQLiteDatabase sdb = db.getOpenHelper().getWritableDatabase();
                sdb.beginTransaction();
                try {
                    // Μηδενίσμος παλιων επιλογων
                    sdb.execSQL("UPDATE Ingredient SET selected=0");

                    // Αποθηκευση νεων επιλεγμένων υλικων
                    for (Ingredient ingredient : selectedIngredients) {
                        sdb.execSQL("UPDATE Ingredient SET selected=1 WHERE id=" + ingredient.id);
                    }
                    sdb.setTransactionSuccessful();
                } finally {
                    sdb.endTransaction();
                }

                // Πάμε στην επόμενη οθόνη
                runOnUiThread(() -> {
                    Intent intent = new Intent(MainActivity.this, RecipeListActivity.class);
                    startActivity(intent);
                });
            }).start();
        });
    }

    private void initializeViews() {
        etSearchIngredient = findViewById(R.id.etSearchIngredient);
        rvIngredients = findViewById(R.id.rvIngredients);
        btnSearch = findViewById(R.id.btnSearch);
    }

    private void loadIngredients() {
        new Thread(() -> {
            // Χρησιμοποιούμε SQL query για να πάρουμε όλα τα υλικά,
            SupportSQLiteDatabase sdb = db.getOpenHelper().getReadableDatabase();
            android.database.Cursor cursor = sdb.query("SELECT * FROM Ingredient ORDER BY name ASC");

            java.util.List<Ingredient> list = new java.util.ArrayList<>();
            while (cursor.moveToNext()) {
                Ingredient i = new Ingredient();
                i.id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                i.name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                i.selected = cursor.getInt(cursor.getColumnIndexOrThrow("selected")) == 1;
                i.imgsrc = cursor.getString(cursor.getColumnIndexOrThrow("imgsrc"));
                list.add(i);
            }
            cursor.close();

            runOnUiThread(() -> adapter.setIngredients(list));
        }).start();
    }
}