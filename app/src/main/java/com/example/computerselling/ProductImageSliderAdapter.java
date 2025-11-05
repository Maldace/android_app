package com.example.computerselling;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ProductImageSliderAdapter extends RecyclerView.Adapter<ProductImageSliderAdapter.ImageViewHolder> {

    private final List<String> imageList;

    // Constructor nhận danh sách URL ảnh
    public ProductImageSliderAdapter(List<String> imageList) {
        this.imageList = imageList;
    }

    // ViewHolder để giữ các View của item
    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.slider_image_view);
        }
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image_slider, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imageUrl = imageList.get(position);

        // Sử dụng thư viện Glide để tải ảnh từ URL
        Glide.with(holder.imageView.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.ic_placeholder) // Cần có drawable placeholder (tùy chọn)
                .error(R.drawable.ic_error)         // Cần có drawable error (tùy chọn)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }
}
