package com.app.recipick.data.Ingredient;

import androidx.room.Dao;
import androidx.room.Query;
import com.app.recipick.data.GeneralDao;
import java.util.List;

//βοηθητική διεπιφάνεια για τη διαχείριση του πίνακα Ingredients
@Dao
public interface IngredientDao extends GeneralDao<Ingredient>{

    @Query("UPDATE Ingredient SET selected = :isSelected WHERE id = :id")
    void updateSelection(int id, int isSelected);

    //όσα υλικά εχει επιλέξει ο χρήστης για την αρχική οθόνη
    @Query("SELECT * FROM Ingredient WHERE selected = 1 ORDER BY name ASC")
    List<Ingredient> getSelectedIngredients();

    //οσα υλικά δεν εχει επιλέξει ο χρήστης για την σελίδα προσθήκης υλικών
    @Query("SELECT * FROM Ingredient WHERE selected = 0 ORDER BY name ASC")
    List<Ingredient> getUnselectedIngredients();


    //επιστρέφει τα υλικά μίας συνταγής
    @Query("SELECT Ingredient.* FROM Ingredient INNER JOIN Recipe_Ingredients ON Ingredient.id = Recipe_Ingredients.ingredientId WHERE Recipe_Ingredients.recipeId = :recipeId")
    List<Ingredient> getIngredientsForRecipe(int recipeId);
}