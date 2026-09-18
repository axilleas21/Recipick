package com.app.recipick;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.app.recipick.adapters.IngredientAdapter;
import com.app.recipick.data.AppDatabase;
import com.app.recipick.data.Ingredient.Ingredient;
import java.util.List;


//οθονη προσθήκης νέων υλικών
public class AddIngredientActivity extends AppCompatActivity {

    private IngredientAdapter adapter;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ingredients_selection);
        setTitle("Add Ingredients");

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        //back button
        android.widget.ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> finish());

        db = AppDatabase.getInstance(this);
        RecyclerView rvAdd = findViewById(R.id.rvAddIngredients);
        EditText etSearch = findViewById(R.id.etSearchNew);
        Button btnSave = findViewById(R.id.btnSavePantry);

        rvAdd.setLayoutManager(new LinearLayoutManager(this));

        //is pantru mode=false άρα έχουμε το layout με τα checkboxes
        adapter = new IngredientAdapter(false);
        rvAdd.setAdapter(adapter);

        loadAllIngredients();

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnSave.setOnClickListener(v -> {
            List<Ingredient> selected = adapter.getSelectedIngredients();
            new Thread(() -> {
                for (Ingredient ingredient : selected) {
                    db.ingredientDao().updateSelection(ingredient.id, 1);
                }
                runOnUiThread(this::finish);
            }).start();
        });
    }

    //φορτώνει όλα τα υλικά που δεν έχει ο χρήστης ασύγχρονα
    private void loadAllIngredients() {
        new Thread(() -> {
            List<Ingredient> list = db.ingredientDao().getUnselectedIngredients();
            runOnUiThread(() -> adapter.setIngredients(list));
        }).start();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}