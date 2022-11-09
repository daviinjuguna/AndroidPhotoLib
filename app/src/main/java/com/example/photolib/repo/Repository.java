package com.example.photolib.repo;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.photolib.dao.MyImagesDao;
import com.example.photolib.db.AppDatabase;
import com.example.photolib.entity.MyImages;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Repository {
    private final MyImagesDao dao;
    private final LiveData<List<MyImages>> imageList;

    //*Executor
    ExecutorService executorService = Executors.newSingleThreadExecutor();

    public Repository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        dao = database.dao();
        imageList = dao.getAllImages();
    }

    public void insert(MyImages images) {

        executorService.execute(() -> {
            dao.insert(images);
        });
    }

    public void delete(MyImages images) {
        executorService.execute(() -> {
            dao.delete(images);
        });
    }

    public void update(MyImages images) {
        executorService.execute(() -> {
            dao.update(images);
        });
    }

    public LiveData<List<MyImages>> getAllImages() {
        return imageList;
    }


}
