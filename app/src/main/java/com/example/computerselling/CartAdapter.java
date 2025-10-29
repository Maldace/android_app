package com.example.computerselling;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Locale;
import com.bumptech.glide.Glide; // Đã import Glide

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final Context context;
    private List<CartItem> cartItemList;
    private final OnCartItemChangeListener listener;

    /**
     * Interface để giao tiếp với CartActivity.
     */
    public interface OnCartItemChangeListener {
        void onQuantityChanged(PC pc, int newQuantity);
        void onItemRemoved(PC pc);
    }

    public CartAdapter(Context context, List<CartItem> cartItemList, OnCartItemChangeListener listener) {
        this.context = context;
        this.cartItemList = cartItemList;
        this.listener = listener;
    }

    // Phương thức để cập nhật danh sách và làm mới RecyclerView
    public void updateList(List<CartItem> newList) {
        this.cartItemList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Ánh xạ layout item_cart.xml
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem currentItem = cartItemList.get(position);
        PC pc = currentItem.getPc();

        // 1. Hiển thị thông tin
        holder.tvName.setText(pc.getName());
        holder.tvQuantity.setText(String.valueOf(currentItem.getQuantity()));

        // Tính và định dạng tổng giá của mục này (Price * Quantity)
        long subtotal = pc.getPrice() * currentItem.getQuantity();
        String priceText = String.format(Locale.getDefault(), "%,d VND", subtotal);
        holder.tvSubtotal.setText(priceText);

        // ************************************************
        // CODE TẢI VÀ HIỂN THỊ HÌNH ẢNH BẰNG GLIDE
        // ************************************************
        String imageUrl = pc.getImageUrl();

        if (imageUrl != null && !imageUrl.isEmpty()) {
            // Tải ảnh từ URL vào ImageView imgPC
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_background) // Ảnh tạm thời khi đang tải
                    .error(R.drawable.ic_launcher_background)      // Ảnh khi tải thất bại
                    .into(holder.imgPC);

        } else {
            // Nếu không có URL, hiển thị ảnh mặc định
            holder.imgPC.setImageResource(R.drawable.ic_launcher_background);
        }
        // ************************************************

        // 2. Xử lý nút TĂNG (+)
        holder.btnIncrease.setOnClickListener(v -> {
            int newQuantity = currentItem.getQuantity() + 1;
            currentItem.setQuantity(newQuantity);
            // Gửi sự kiện về CartActivity
            listener.onQuantityChanged(pc, newQuantity);
        });

        // 3. Xử lý nút GIẢM (-)
        holder.btnDecrease.setOnClickListener(v -> {
            int currentQuantity = currentItem.getQuantity();
            if (currentQuantity > 1) {
                int newQuantity = currentQuantity - 1;
                currentItem.setQuantity(newQuantity);
                // Gửi sự kiện về CartActivity
                listener.onQuantityChanged(pc, newQuantity);
            } else {
                // Nếu số lượng là 1, nhấn Giảm, ta hỏi hoặc trực tiếp gọi xóa
                Toast.makeText(context, "Giỏ hàng sẽ xóa mục này. Nhấn Xóa để xác nhận.", Toast.LENGTH_SHORT).show();
            }
        });

        // 4. Xử lý nút XÓA (Delete/Remove)
        holder.btnRemove.setOnClickListener(v -> {
            // Gửi sự kiện về CartActivity để xóa khỏi CartManager
            listener.onItemRemoved(pc);
        });
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    // **********************************************
    // Class ViewHolder
    // **********************************************
    public static class CartViewHolder extends RecyclerView.ViewHolder {

        // CÁC THÀNH PHẦN GIAO DIỆN CỦA MỖI MỤC
        ImageView imgPC;

        TextView tvName, tvSubtotal, tvQuantity;
        Button btnIncrease, btnDecrease;
        ImageView btnRemove; // Sử dụng ImageView nếu nút xóa là icon thùng rác

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            // Các ID này được lấy từ item_cart.xml của bạn
            imgPC = itemView.findViewById(R.id.img_cart_pc);
            tvName = itemView.findViewById(R.id.tv_cart_item_name);
            tvSubtotal = itemView.findViewById(R.id.tv_cart_item_subtotal);
            tvQuantity = itemView.findViewById(R.id.tv_item_quantity);
            btnIncrease = itemView.findViewById(R.id.btn_increase);
            btnDecrease = itemView.findViewById(R.id.btn_decrease);
            btnRemove = itemView.findViewById(R.id.btn_remove);
        }
    }
}