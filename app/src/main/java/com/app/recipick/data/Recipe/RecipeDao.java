package com.app.recipick.data.Recipe;
import androidx.room.Dao;
import com.app.recipick.data.GeneralDao;

import java.util.List;

@Dao
public interface RecipeDao extends GeneralDao<Recipe>{


    @androidx.room.Query("UPDATE Recipe SET isFavorite = :favorite WHERE id = :recipeId")
    void updateFavorite(int recipeId, boolean favorite);
    @androidx.room.Query("SELECT * FROM Recipe WHERE isFavorite = 1")
    List<Recipe> getFavoriteRecipes();
}