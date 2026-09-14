package com.app.recipick.data.Ingredient;

import androidx.room.Dao;
import androidx.room.Query;
import com.app.recipick.data.GeneralDao;
import java.util.List;

@Dao
public interface IngredientDao extends GeneralDao<Ingredient>{

    @Query("UPDATE Ingredient SET selected = :isSelected WHERE id = :id")
    void updateSelection(int id, int isSelected);

    @Query("SELECT * FROM Ingredient WHERE selected = 1 ORDER BY name ASC")
    List<Ingredient> getSelectedIngredients();

    @Query("SELECT * FROM Ingredient WHERE selected = 0 ORDER BY name ASC")
    List<Ingredient> getUnselectedIngredients();

    @Query("SELECT * FROM Ingredient ORDER BY name ASC")
    List<Ingredient> getAllIngredients();
}