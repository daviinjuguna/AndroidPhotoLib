package com.example.photolib;

import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_IDLE;
import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photolib.databinding.ActivityMainBinding;
import com.example.photolib.entity.MyImages;
import com.example.photolib.recyclerview.ImageAdapter;
import com.example.photolib.viewmodel.ImageViewModel;

public class MainActivity extends AppCompatActivity {
    private ImageViewModel viewModel;
    private ActivityMainBinding binding;
    private ImageAdapter adapter;

    ActivityResultLauncher<Intent> activityResultLauncherAddImage;
    ActivityResultLauncher<Intent> activityResultLauncherUpdateImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        registerResultLauncherAddImage();
        registerResultLauncherUpdateImage();

        adapter = new ImageAdapter();
        setRecyclerAdapter();

        viewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(ImageViewModel.class);

        viewModel.getAllImages().observe(this, myImages -> {
            adapter.setImagesList(myImages);
        });

        binding.fab.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddImageActivity.class);
            activityResultLauncherAddImage.launch(intent);
        });

        new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {

                return makeFlag(ACTION_STATE_IDLE, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) | makeFlag(ACTION_STATE_SWIPE, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                viewModel.delete(adapter.getPosition(viewHolder.getAdapterPosition()));
            }
        }).attachToRecyclerView(binding.rv);
    }


    private void setRecyclerAdapter() {
        binding.rv.setLayoutManager(new LinearLayoutManager(this));
        binding.rv.setAdapter(adapter);
        adapter.setListener(image -> {
            Intent intent = new Intent(MainActivity.this, UpdateImageActivity.class);
            intent.putExtra("id", image.getId());
            intent.putExtra("title", image.getTitle());
            intent.putExtra("desc", image.getDescription());
            intent.putExtra("image", image.getImage());

            activityResultLauncherUpdateImage.launch(intent);
        });
    }

    private void registerResultLauncherAddImage() {
        activityResultLauncherAddImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            int resultCode = result.getResultCode();
            Intent data = result.getData();
            if (resultCode == RESULT_OK && data != null) {
                String title = data.getStringExtra("title");
                String desc = data.getStringExtra("desc");
                byte[] image = data.getByteArrayExtra("image");
                MyImages myImages = new MyImages(title, desc, image);
                viewModel.insert(myImages);
            }

        });
    }

    private void registerResultLauncherUpdateImage() {
        activityResultLauncherUpdateImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            int resultCode = result.getResultCode();
            Intent data = result.getData();
            if (resultCode == RESULT_OK && data != null) {
                int image_id = data.getIntExtra("id", -1);
                String title = data.getStringExtra("title");
                String desc = data.getStringExtra("desc");
                byte[] image = data.getByteArrayExtra("image");
                MyImages myImages = new MyImages(title, desc, image);
                myImages.setId(image_id);
                viewModel.update(myImages);
            }
        });
    }
}