package com.app.recipick;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.app.recipick.data.AppDatabase;
import com.app.recipick.data.Ingredient.Ingredient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class RecipeDetailsActivity extends AppCompatActivity {

    private int recipeId;
    private boolean isFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        TextView tvName = findViewById(R.id.tvDetailName);
        TextView tvDesc = findViewById(R.id.tvDetailDesc);
        TextView tvInstructions = findViewById(R.id.tvDetailInstructions);
        LinearLayout layoutIngredients = findViewById(R.id.layoutIngredientsList);
        FloatingActionButton fabFavorite = findViewById(R.id.fabDetailFavorite);
        ImageView ivImage = findViewById(R.id.ivDetailImage);

        recipeId = getIntent().getIntExtra("RECIPE_ID", -1);
        String name = getIntent().getStringExtra("RECIPE_NAME");
        String desc = getIntent().getStringExtra("RECIPE_DESC");
        isFavorite = getIntent().getBooleanExtra("RECIPE_FAV", false);
        String instructions = getIntent().getStringExtra("RECIPE_INSTR");

        tvName.setText(name);
        tvDesc.setText(desc);
        tvInstructions.setText(instructions);

        updateFavoriteUI(fabFavorite);
        fabFavorite.setOnClickListener(v -> {
            isFavorite = !isFavorite;
            updateFavoriteUI(fabFavorite);
            new Thread(() -> {
                AppDatabase db = AppDatabase.getInstance(this);
                db.recipeDao().updateFavorite(recipeId, isFavorite);
            }).start();
        });

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbarDetails);
        toolbar.setNavigationOnClickListener(v -> finish());

        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            List<Ingredient> recipeIngredients = db.ingredientDao().getIngredientsForRecipe(recipeId);

            runOnUiThread(() -> {
                layoutIngredients.removeAllViews();

                for (Ingredient ingredient : recipeIngredients) {
                    TextView tv = new TextView(this);

                    // Ελέγχουμε αν το υλικό υπάρχει στο ψυγείο μας (1 = το έχουμε)
                    boolean hasIngredient = ingredient.selected;

                    if (hasIngredient) {
                        tv.setText(ingredient.getName());
                        tv.setTextColor(Color.parseColor("#2E7D32"));
                    } else {
                        tv.setText("🛒  " + ingredient.getName());
                        tv.setTextColor(Color.parseColor("#E65100"));
                    }

                    tv.setTextSize(16f);
                    tv.setPadding(0, 8, 0, 16);
                    layoutIngredients.addView(tv);
                }
            });
        }).start();
    }

    private void updateFavoriteUI(FloatingActionButton fab) {
        if (isFavorite) {
            fab.setImageResource(R.drawable.baseline_favorite_24);
            fab.setColorFilter(Color.parseColor("#E91E63"));
        } else {
            fab.setImageResource(R.drawable.baseline_favorite_border_24);
            fab.setColorFilter(Color.parseColor("#757575"));
        }
    }
}