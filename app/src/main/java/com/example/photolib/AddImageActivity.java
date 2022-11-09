package com.example.photolib;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.photolib.databinding.ActivityAddImageBinding;
import com.example.photolib.utils.ViewSnackBar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

public class AddImageActivity extends AppCompatActivity {
    private ActivityAddImageBinding binding;
    private final String RED = "#ff0000";

    ActivityResultLauncher<Intent> activityResultLauncher;

    private Bitmap selectedImage;
    private Bitmap scaledImage;
    private ViewSnackBar snackBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddImageBinding.inflate(getLayoutInflater());
        Objects.requireNonNull(getSupportActionBar()).setTitle("Add Image");
        setContentView(binding.getRoot());

        snackBar = new ViewSnackBar(binding.getRoot());

        registerActivityForSelectingImage();

        binding.addImageView.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(AddImageActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(AddImageActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intent);
            }
        });

        binding.button.setOnClickListener(v -> {
            if (selectedImage == null) {
                snackBar.show("Please select an image", Color.parseColor(RED));
                return;
            }
            String title = binding.titleEditText.getText().toString().trim();
            String desc = binding.descEditText.getText().toString().trim();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            scaledImage = makeSmall(selectedImage, 300);
            scaledImage.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
            byte[] image = outputStream.toByteArray();

            Intent intent = new Intent();
            intent.putExtra("title", title);
            intent.putExtra("desc", desc);
            intent.putExtra("image", image);
            setResult(RESULT_OK, intent);
            finish();
        });
    }

    private void registerActivityForSelectingImage() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            int resultCode = result.getResultCode();
            Intent data = result.getData();

            if (resultCode == RESULT_OK && data != null) {
                try {
                    selectedImage = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    binding.addImageView.setImageBitmap(selectedImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            // Ignore all other requests.
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intent);
        } else {

            snackBar.show("Cannot pick photo without permission", Color.parseColor(RED));
        }
    }

    private Bitmap makeSmall(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();
        float ratio = (float) width / (float) height;
        if (ratio > 1) {
            width = maxSize;
            height = (int) (width / ratio);
        } else {
            height = maxSize;
            width = (int) (height * ratio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }
}