package com.app.recipick.data.Recipe;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName="Recipe")
public class Recipe implements Serializable{

    @Ignore
    public int missingIngredients;

    @PrimaryKey public int id;

    @NonNull
    @ColumnInfo(name="name")
    public String name;

    @NonNull
    @ColumnInfo(name="desc")
    public String desc;

    @NonNull
    @ColumnInfo(name="instr")
    public String instr;

    @ColumnInfo(name = "imgsrc")
    public String imgsrc;

    @ColumnInfo(name = "isFavorite")
    public boolean isFavorite = false;

    @Ignore
    public String getName(){return name;}
}
