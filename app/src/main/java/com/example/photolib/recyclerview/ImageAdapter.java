package com.example.photolib.recyclerview;

import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photolib.R;
import com.example.photolib.entity.MyImages;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private List<MyImages> imagesList = new ArrayList<>();
    private OnImageClickListener listener;

    public void setListener(OnImageClickListener listener) {
        this.listener = listener;
    }

    public void setImagesList(List<MyImages> imagesList) {
        this.imagesList = imagesList;
        notifyDataSetChanged();
    }

    public MyImages getPosition(int position) {
        return imagesList.get(position);
    }

    public interface OnImageClickListener {
        void onImageClick(MyImages images);
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_card, parent, false);

        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {

        holder.bind(imagesList.get(position));
    }

    @Override
    public int getItemCount() {
        return imagesList.size();
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView titleTextView;
        private final TextView descTextView;


        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            titleTextView = itemView.findViewById(R.id.textViewTitle);
            descTextView = itemView.findViewById(R.id.textViewDesc);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onImageClick(imagesList.get(position));
                }
            });
        }

        public void bind(MyImages image) {
            titleTextView.setText(image.getTitle());
            descTextView.setText(image.getDescription());
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(image.getImage(), 0, image.getImage().length));
        }
    }
}
