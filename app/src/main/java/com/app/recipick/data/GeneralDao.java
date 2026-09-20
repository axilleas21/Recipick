package com.app.recipick.data;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Update;
import java.util.List;

//βοηθητική διεπιφάνεια με μεθόδους για διαχείριση και των τριών πινάκων της βάσης δεδομένων
@Dao
public interface GeneralDao<T>{

    @Insert(onConflict=OnConflictStrategy.REPLACE)
    void insert(T entity);

    @Insert(onConflict=OnConflictStrategy.REPLACE)
    void insertList(List<T> entities);

    @Update
    void update(T entity);

    @Delete
    void delete(T entity);
}