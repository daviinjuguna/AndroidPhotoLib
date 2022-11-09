package com.example.photolib.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.photolib.entity.MyImages;
import com.example.photolib.repo.Repository;

import java.util.List;

public class ImageViewModel extends AndroidViewModel {

    private final Repository repository;
    private final LiveData<List<MyImages>> imageList;

    public ImageViewModel(@NonNull Application application) {
        super(application);

        repository = new Repository(application);
        imageList = repository.getAllImages();
    }

    public void insert(MyImages images) {
        repository.insert(images);
    }

    public void update(MyImages images) {
        repository.update(images);
    }

    public void delete(MyImages images) {
        repository.delete(images);
    }

    public LiveData<List<MyImages>> getAllImages() {
        return imageList;
    }
}
