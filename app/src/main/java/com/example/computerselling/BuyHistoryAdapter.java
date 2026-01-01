package com.example.computerselling;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
public class BuyHistoryAdapter extends RecyclerView.Adapter<BuyHistoryAdapter.UserViewHolder> {

    private List<BuyHistoryModel> list; // danh sách lịch sử mua

    public BuyHistoryAdapter(List<BuyHistoryModel> list) { // constructor
        this.list = list;               // gán danh sách vào adapter
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // tạo view cho từng dòng từ file XML
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.buy_history_row, parent, false); // đổi tên XML của bạn vào đây
        return new UserViewHolder(view); // trả về ViewHolder
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        BuyHistoryModel item = list.get(position); // lấy 1 item tại vị trí đang hiển thị

        // gán data vào TextView (ví dụ)
        holder.tvProductName.setText(item.getProduct());      // tên sản phẩm
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));  // giá
        holder.tvDate.setText(item.getDate());                    // ngày mua
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
        TextView tvQuantity;        // giá
        TextView tvDate;         // ngày mua

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            // ánh xạ các TextView từ layout
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}
