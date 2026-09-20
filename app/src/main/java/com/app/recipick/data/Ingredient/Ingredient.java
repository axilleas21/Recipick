package com.app.recipick.data.Ingredient;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

//κλάση που υλοποιεί τον πίνακα Ingredient της βάσης δεδομένων
@Entity
public class Ingredient{
    @PrimaryKey public int id;
    @NonNull public String name;
    public boolean selected=false;
    public String imgsrc;

    @Ignore
    public String getName(){return name;}
    @Ignore
    public boolean isSelected(){return selected;}
}
