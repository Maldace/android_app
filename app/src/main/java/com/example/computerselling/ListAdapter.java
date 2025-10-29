package com.example.computerselling;

import android.content.Context;
import android.graphics.Paint; // Import cần thiết cho gạch ngang
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat; // Import để đổi màu (tùy chọn)
import com.bumptech.glide.Glide;

import java.util.ArrayList;


public class ListAdapter extends BaseAdapter {
    private final Context context;
    public ArrayList<PC> aPC;

    public ListAdapter(Context context, ArrayList<PC> aPC) {
        this.context = context;
        this.aPC = aPC;
    }

    @Override
    public int getCount() {
        return aPC.size();
    }

    @Override
    public Object getItem(int position) {
        return aPC.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
            holder = new ViewHolder();

            // Ánh xạ các View
            holder.imageView = convertView.findViewById(R.id.img);
            holder.pcName = convertView.findViewById(R.id.txt1);

            // ÁNH XẠ CÁC VIEW GIÁ
            holder.price = convertView.findViewById(R.id.txt2); // Giá đã giảm/Giá thường
            // CẦN THÊM ID NÀY VÀO list_item.xml
            holder.originalPrice = convertView.findViewById(R.id.txt_original_price); // Giá gốc

            holder.description = convertView.findViewById(R.id.txt_description);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        PC currentPC = aPC.get(position);

        // 1. Gán Tên và Mô tả
        holder.pcName.setText(currentPC.getName());
        holder.description.setText(currentPC.getDescription());

        // 2. LOGIC HIỂN THỊ GIÁ SALE (QUAN TRỌNG)
        if (currentPC.getDiscountPercent() > 0 && holder.originalPrice != null) {
            // TRƯỜNG HỢP CÓ GIẢM GIÁ

            // Hiển thị giá gốc (txt_original_price)
            holder.originalPrice.setVisibility(View.VISIBLE);
            String originalPriceText = String.format("%,d VNĐ", currentPC.getOriginalPrice());
            holder.originalPrice.setText(originalPriceText);
            // Thiết lập GẠCH NGANG
            holder.originalPrice.setPaintFlags(holder.originalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            // Hiển thị giá đã giảm (txt2)
            String finalPriceText = String.format("%,d VNĐ", currentPC.getPrice());
            holder.price.setText(finalPriceText);
            // (Tùy chọn) Đổi màu giá đã giảm (Cần định nghĩa color/red trong colors.xml)
            // holder.price.setTextColor(ContextCompat.getColor(context, R.color.red));

        } else {
            // TRƯỜNG HỢP KHÔNG GIẢM GIÁ (HOẶC CHƯA CÓ VIEW txt_original_price)

            // Ẩn giá gốc (nếu có)
            if (holder.originalPrice != null) {
                holder.originalPrice.setVisibility(View.GONE);
            }

            // Hiển thị giá thường (txt2)
            String priceText = String.format("%,d VNĐ", currentPC.getPrice());
            holder.price.setText(priceText);
            // (Tùy chọn) Đặt lại màu giá bán thành màu mặc định
            // holder.price.setTextColor(ContextCompat.getColor(context, R.color.black));
        }


        // 3. HÌNH ẢNH (DÙNG GLIDE)
        String imageUrl = currentPC.getImageUrl();

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context).load(imageUrl).into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.ic_launcher_background);
        }

        return convertView;
    }

    // Lớp ViewHolder (ĐÃ CẬP NHẬT)
    static class ViewHolder {
        ImageView imageView;
        TextView pcName;
        TextView price; // Giá đã giảm/Giá thường (ID: txt2)
        TextView originalPrice; // Giá gốc (ID: txt_original_price)
        TextView description;
    }
}