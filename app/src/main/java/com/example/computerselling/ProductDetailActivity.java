package com.example.computerselling;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {

    FirebaseFirestore db;
    String id;
    PC currentProduct;

    // Biến trạng thái Yêu thích và ID người dùng
    private boolean isFavorite = false;
    private String currentUserId = "test_user_id_12345"; // <<< ID GIẢ ĐÃ SỬA LỖI ĐỂ CHỨC NĂNG CHẠY

    // ImageView cho ảnh sản phẩm
    private ImageView productImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        db = FirebaseFirestore.getInstance();

        // -------------------------------------------------------------------
        // ÁNH XẠ VIEW ĐÃ CẬP NHẬT
        // -------------------------------------------------------------------

        // Ánh xạ ImageView ảnh sản phẩm (Thay thế ViewPager/TabLayout)
        productImage = findViewById(R.id.product_image); // *** CẦN ĐẢM BẢO ID NÀY CÓ TRONG activity_product_detail.xml ***

        // Ánh xạ các TextView
        TextView txtName = findViewById(R.id.product_detail_name);
        TextView txtPrice = findViewById(R.id.product_detail_price);
        TextView txtDescription = findViewById(R.id.product_detail_description);

        // Nút Back (ImageView)
        ImageView btnBack = findViewById(R.id.btnBack);

        // Nút THÊM VÀO GIỎ HÀNG
        Button btnAddToCart = findViewById(R.id.btn_add_to_cart);

        // NÚT YÊU THÍCH
        ImageButton btnFavorite = findViewById(R.id.btn_favorite);

        // Nút MUA NGAY
        Button btnBuyNow = findViewById(R.id.btn_buy_now);

        // Thiết lập Action Bar (Nếu cần)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chi tiết sản phẩm");
        }


        Intent intent = getIntent();
        currentProduct = intent.getParcelableExtra("selected_pc");

        if (currentProduct != null) {
            txtName.setText(currentProduct.getName());
            String formattedPrice = String.format(Locale.getDefault(), "%,d VNĐ", currentProduct.getPrice());
            txtPrice.setText(formattedPrice);
            txtDescription.setText(currentProduct.getDescription());
            id = currentProduct.getId();

            // *** XỬ LÝ ẢNH BẰNG GLIDE VÀ ImageView ĐƠN GIẢN ***
            String imageUrl = currentProduct.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(this).load(imageUrl).into(productImage);
            } else {
                // Tải ảnh placeholder nếu URL không hợp lệ
                Glide.with(this).load("https://via.placeholder.com/300?text=No+Image").into(productImage);
            }

            // 1. KIỂM TRA TRẠNG THÁI YÊU THÍCH BAN ĐẦU
            checkInitialFavoriteState(btnFavorite);

        } else {
            Toast.makeText(this, "Không tìm thấy dữ liệu sản phẩm!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // --- Xử lý sự kiện Nút Back ---
        btnBack.setOnClickListener(v -> finish());

        // ************************************************************
        // LOGIC CHỨC NĂNG YÊU THÍCH SẢN PHẨM
        // ************************************************************
        btnFavorite.setOnClickListener(v -> toggleFavoriteState(btnFavorite));

        // ************************************************************
        // LOGIC CHỨC NĂNG GIỎ HÀNG/MUA NGAY
        // ************************************************************
        btnAddToCart.setOnClickListener(v -> {
            // Đảm bảo CartManager.getInstance() và CartActivity tồn tại
            CartManager.getInstance().addToCart(currentProduct);
            Toast.makeText(this, "Đã thêm " + currentProduct.getName() + " vào giỏ hàng!", Toast.LENGTH_SHORT).show();
            Intent cartIntent = new Intent(ProductDetailActivity.this, CartActivity.class);
            startActivity(cartIntent);
        });

        btnBuyNow.setOnClickListener(v -> {
            CartManager.getInstance().addToCart(currentProduct);
            Intent checkoutIntent = new Intent(ProductDetailActivity.this, CheckoutActivity.class); // Thay bằng Activity thanh toán của bạn
            startActivity(checkoutIntent);
        });
    }

    // --- CÁC PHƯƠNG THỨC HỖ TRỢ CHỨC NĂNG YÊU THÍCH ---

    /**
     * Kiểm tra trạng thái Yêu thích ban đầu của sản phẩm.
     */
    private void checkInitialFavoriteState(ImageButton btnFavorite) {
        if (currentUserId.equals("test_user_id_12345")) {
            // Với ID giả, ta vẫn gọi Firestore để thử nghiệm
        }

        db.collection("Users").document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        ArrayList<String> favorites = (ArrayList<String>) documentSnapshot.get("favorites");

                        if (favorites != null && favorites.contains(currentProduct.getId())) {
                            isFavorite = true;
                        }
                    }
                    updateFavoriteUI(btnFavorite);
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Lỗi khi kiểm tra trạng thái yêu thích", e);
                    updateFavoriteUI(btnFavorite);
                });
    }

    /**
     * Chuyển đổi trạng thái Yêu thích khi click.
     */
    private void toggleFavoriteState(ImageButton btnFavorite) {
        // Đã sửa lỗi: UID không còn là "USER_ID_PLACEHOLDER" nên logic chạy bình thường

        isFavorite = !isFavorite;
        updateFavoriteUI(btnFavorite);
        updateFavoriteInFirestore();
    }

    /**
     * Cập nhật giao diện nút Yêu thích (icon và màu).
     */
    private void updateFavoriteUI(ImageButton btnFavorite) {
        if (isFavorite) {
            // TRẠNG THÁI YÊU THÍCH: Trái tim đầy, Nền đỏ
            btnFavorite.setImageResource(R.drawable.ic_favorite_24);
            btnFavorite.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#DC3545")));
        } else {
            // TRẠNG THÁI KHÔNG YÊU THÍCH: Trái tim rỗng, Nền xám
            btnFavorite.setImageResource(R.drawable.ic_favorite_border_24);
            btnFavorite.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#CCCCCC")));
        }
    }

    /**
     * Thêm hoặc xóa ID sản phẩm khỏi mảng 'favorites' trong Firestore.
     */
    private void updateFavoriteInFirestore() {
        DocumentReference userRef = db.collection("Users").document(currentUserId);
        String productId = currentProduct.getId();

        if (isFavorite) {
            userRef.update("favorites", FieldValue.arrayUnion(productId))
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Đã thêm vào mục yêu thích!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Log.e("Firestore", "Lỗi thêm yêu thích", e));
        } else {
            userRef.update("favorites", FieldValue.arrayRemove(productId))
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Đã xóa khỏi mục yêu thích!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Log.e("Firestore", "Lỗi xóa yêu thích", e));
        }
    }

    // --- CÁC PHƯƠNG THỨC KHÁC GIỮ NGUYÊN ---

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void edit(View view){
        Intent intent = new Intent(ProductDetailActivity.this, EditProductActivity.class);
        Log.d("DEBUG", "Gửi: " + currentProduct.getName());
        intent.putExtra("selected_pc", currentProduct);
        startActivity(intent);
    }

    public void delete(View view){
        db.collection("Computer").document(id)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Xóa thành công!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "❌ Lỗi khi xóa document", e);
                });
    }
}