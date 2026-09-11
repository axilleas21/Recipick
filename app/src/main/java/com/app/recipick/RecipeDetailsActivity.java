package com.app.recipick;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.app.recipick.data.AppDatabase;
import com.app.recipick.data.Recipe.Recipe;
import androidx.sqlite.db.SupportSQLiteDatabase;
import android.database.Cursor;
import java.util.ArrayList;
import java.util.List;

public class RecipeDetailsActivity extends AppCompatActivity{
    private TextView txtRecipeName;private TextView txtDescription;
    private TextView txtIngredients;private TextView txtInstructions;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);
        ImageView imgRecipe = findViewById(R.id.imgRecipe);


        initializeViews();
        Recipe recipe=(Recipe) getIntent().getSerializableExtra("recipe");
        if(recipe!=null){
            txtRecipeName.setText(recipe.getName());           // auta to vazoun sto ui
            txtDescription.setText(recipe.getDescription());
            txtInstructions.setText(recipe.getInstructions());

            AppDatabase db=AppDatabase.getInstance(this);
            new Thread(()->{
                List<String> ingredients=new ArrayList<>();
                SupportSQLiteDatabase sdb=db.getOpenHelper().getReadableDatabase();
                String query="SELECT Ingredient.name FROM Ingredient " +
                        "JOIN Recipe_Ingredients ON Ingredient.id=Recipe_Ingredients.ingredientId " +
                        "WHERE Recipe_Ingredients.recipeId=?";
                try (Cursor cursor=sdb.query(query,new Object[]{recipe.id})){while (cursor.moveToNext()){ingredients.add(cursor.getString(0));}}
                runOnUiThread(()->{
                    StringBuilder ingredientsText=new StringBuilder();
                    for(String ingredient:ingredients){ingredientsText.append("- ").append(ingredient).append("\n");}
                    txtIngredients.setText(ingredientsText.toString());
                });
            }).start();
        }
    }

    private void initializeViews(){
        txtRecipeName=findViewById(R.id.txtRecipeName);
        txtDescription=findViewById(R.id.txtDescription);
        txtIngredients=findViewById(R.id.txtIngredients);
        txtInstructions=findViewById(R.id.txtInstructions);
    }
}
