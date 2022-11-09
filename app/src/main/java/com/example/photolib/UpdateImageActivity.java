package com.example.photolib;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.photolib.databinding.ActivityUpdateImageBinding;
import com.example.photolib.utils.ViewSnackBar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

public class UpdateImageActivity extends AppCompatActivity {

    private ActivityUpdateImageBinding binding;
    private final String RED = "#ff0000";


    int id;
    String title, desc;
    byte[] image;

    private Bitmap selectedImage;
    private ViewSnackBar snackBar;

    ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateImageBinding.inflate(getLayoutInflater());
        Objects.requireNonNull(getSupportActionBar()).setTitle("Update Image");
        setContentView(binding.getRoot());

        snackBar = new ViewSnackBar(binding.getRoot());

        registerActivityForSelectingImage();

        id = getIntent().getIntExtra("id", -1);
        title = getIntent().getStringExtra("title");
        desc = getIntent().getStringExtra("desc");
        image = getIntent().getByteArrayExtra("image");

        binding.updateImageView.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
        binding.titleUpdateText.setText(title);
        binding.descUpdateText.setText(desc);

        binding.updateImageView.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(UpdateImageActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(UpdateImageActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intent);
            }
        });
        binding.button.setOnClickListener(v -> {
            updateData();
        });
    }

    private void updateData() {
        if (id == -1) {
            snackBar.show("There is a problem", Color.parseColor(RED));
            return;
        }
        Intent intent = new Intent();
        if (selectedImage == null) {
            intent.putExtra("image", image);
        } else {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Bitmap scaledImage = makeSmall(selectedImage, 300);
            scaledImage.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
            byte[] updateImage = outputStream.toByteArray();
            intent.putExtra("image", updateImage);

        }
        String updateTitle = binding.titleUpdateText.getText().toString().trim();
        String updateDesc = binding.descUpdateText.getText().toString().trim();
        intent.putExtra("title", updateTitle);
        intent.putExtra("desc", updateDesc);
        intent.putExtra("id", id);


        setResult(RESULT_OK, intent);
        finish();
    }

    private void registerActivityForSelectingImage() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            int resultCode = result.getResultCode();
            Intent data = result.getData();

            if (resultCode == RESULT_OK && data != null) {
                try {
                    selectedImage = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    binding.updateImageView.setImageBitmap(selectedImage);
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