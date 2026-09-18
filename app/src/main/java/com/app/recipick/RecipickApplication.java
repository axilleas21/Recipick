package com.app.recipick;

import android.app.Application;
import com.app.recipick.data.AppDatabase;

public class RecipickApplication extends Application{
    private AppDatabase database;

    @Override
    public void onCreate(){
        super.onCreate();
        database=AppDatabase.getInstance(this);
    }
}
