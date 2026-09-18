package com.app.recipick.data;
import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.app.recipick.data.Ingredient.Ingredient;
import com.app.recipick.data.Ingredient.IngredientDao;
import com.app.recipick.data.Recipe.Recipe;
import com.app.recipick.data.Recipe.RecipeDao;
import com.app.recipick.data.Recipe_Ingredients.Recipe_Ingredients;

@Database(entities={Recipe.class,Ingredient.class, Recipe_Ingredients.class},version=12)
public abstract class AppDatabase extends RoomDatabase{
    public abstract IngredientDao ingredientDao();
    public abstract RecipeDao recipeDao();

    // εξασφαλίζει ότι όλες οι αλλαγές σε αυτή τη μεταβλητή είναι άμεσα ορατές σε όλα τα threads.
    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context){
        if(INSTANCE==null){
            synchronized (AppDatabase.class){
                if (INSTANCE==null){INSTANCE=Room.databaseBuilder(context.getApplicationContext(),AppDatabase.class,"recipickdb.db").createFromAsset("database/recipickdb.db").build();}
            }
        }
        return INSTANCE;
    }
}