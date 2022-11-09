package com.example.photolib.utils;

import android.graphics.Color;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class ViewSnackBar {

    private final View view;


    public ViewSnackBar(View view) {
        this.view = view;

    }

    public void show(String message, int color) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.parseColor("#721b65"));
        snackbar.setBackgroundTint(color);
        snackbar.setTextColor(Color.WHITE);
        snackbar.show();
    }
}
