package com.app.recipick.data.Recipe_Ingredients;
import androidx.room.Entity;
import androidx.room.Index;
import com.app.recipick.data.Ingredient.Ingredient;
import com.app.recipick.data.Recipe.Recipe;

//κλάση που υλοποιεί τον πίνακα Recipe_Ingredients της βάσης δεδομένων,
//ο οποίος αποθηκέυει τα υλικά που έχει κάθε συνταγή
@Entity(tableName="Recipe_Ingredients", primaryKeys={"recipeId","ingredientId"}, indices={@Index("recipeId"),@Index("ingredientId")})
public class Recipe_Ingredients{
    public int recipeId, ingredientId;
}