package com.example.computerselling;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.UserViewHolder> {
    private List<FavoriteModel> list; // danh sách lịch sử mua

    public FavoriteAdapter(List<FavoriteModel> list) { // constructor
        this.list = list;               // gán danh sách vào adapter
    }

    @NonNull
    @Override
    public FavoriteAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // tạo view cho từng dòng từ file XML
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favorite_row, parent, false); // đổi tên XML của bạn vào đây
        return new FavoriteAdapter.UserViewHolder(view); // trả về ViewHolder
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteAdapter.UserViewHolder holder, int position) {
        FavoriteModel item = list.get(position); // lấy 1 item tại vị trí đang hiển thị

        // gán data vào TextView (ví dụ)
        holder.tvProductName.setText(item.getProduct());      // tên sản phẩm
    }

    @Override
    public int getItemCount() {
        return list.size(); // trả về số lượng dòng
    }

    // ================================
    // VIEW HOLDER (BẮT BUỘC PHẢI CÓ)
    // ================================
    public static class UserViewHolder extends RecyclerView.ViewHolder {

        TextView tvProductName;  // tên sản phẩm

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            // ánh xạ các TextView từ layout
            tvProductName = itemView.findViewById(R.id.tvProductName);
        }
    }
}
