package com.example.photolib.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.photolib.entity.MyImages;

import java.util.List;

@Dao
public interface MyImagesDao {

    @Insert
    void insert(MyImages myImages);

    @Delete
    void delete(MyImages myImages);

    @Update
    void update(MyImages myImages);

    @Query("SELECT * FROM my_images ORDER BY id ASC")
    LiveData<List<MyImages>>getAllImages();
}
