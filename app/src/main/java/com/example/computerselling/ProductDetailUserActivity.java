package com.example.computerselling;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductDetailUserActivity extends AppCompatActivity {

    FirebaseFirestore db;
    String id;
    PC currentProduct;

    // Biến trạng thái Yêu thích và ID người dùng
    private boolean isFavorite = false;
    private String currentUserId = "test_user_id_12345"; // ID người dùng giả định

    // Khai báo SharedPreferences và UserName ở cấp Class
    private SharedPreferences sharedPref;
    private String currentUserName = "Khách"; // Giá trị mặc định ban đầu

    // ImageView cho ảnh sản phẩm
    private ImageView productImage;

    // Các View cho chức năng COMMENT
    private EditText etCommentInput;
    private Button btnPostComment;
    private RecyclerView rvComments;
    private CommentAdapter commentAdapter;
    private List<Comment> commentsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_detail_user);

        db = FirebaseFirestore.getInstance();

        // ************************************************************
        // KHỞI TẠO SHARED PREFERENCES VÀ LẤY TÊN NGƯỜI DÙNG (ĐÃ SỬA LỖI)
        // ************************************************************
        sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        // Lấy tên người dùng sau khi sharedPref đã được khởi tạo
        currentUserName = sharedPref.getString("current_username", "Khách");
        // currentUserId có thể được lấy tương tự từ SharedPreferences nếu cần

        // -------------------------------------------------------------------
        // ÁNH XẠ VIEW
        // -------------------------------------------------------------------

        productImage = findViewById(R.id.product_image);
        TextView txtName = findViewById(R.id.product_detail_name);
        TextView txtPrice = findViewById(R.id.product_detail_price);
        TextView txtDescription = findViewById(R.id.product_detail_description);
        ImageView btnBack = findViewById(R.id.btnBack);
        Button btnAddToCart = findViewById(R.id.btn_add_to_cart);
        ImageButton btnFavorite = findViewById(R.id.btn_favorite);
        Button btnBuyNow = findViewById(R.id.btn_buy_now);

        // ÁNH XẠ VIEW COMMENT
        etCommentInput = findViewById(R.id.et_comment_input);
        btnPostComment = findViewById(R.id.btn_post_comment);
        rvComments = findViewById(R.id.rv_comments);


        Intent intent = getIntent();
        currentProduct = intent.getParcelableExtra("selected_pc");

        if (currentProduct != null) {

            // -------------------------------------------------------------------
            // HIỂN THỊ THÔNG TIN SẢN PHẨM
            // -------------------------------------------------------------------
            txtName.setText(currentProduct.getName());
            String formattedPrice = String.format(Locale.getDefault(), "%,d VNĐ", currentProduct.getPrice());
            txtPrice.setText(formattedPrice);
            txtDescription.setText(currentProduct.getDescription());
            id = currentProduct.getId();

            // XỬ LÝ ẢNH BẰNG GLIDE
            String imageUrl = currentProduct.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(this).load(imageUrl).into(productImage);
            } else {
                Glide.with(this).load("https://via.placeholder.com/300?text=No+Image").into(productImage);
            }

            // 1. KIỂM TRA TRẠNG THÁI YÊU THÍCH BAN ĐẦU
            checkInitialFavoriteState(btnFavorite);

            // 2. KHỞI TẠO RECYCLERVIEW VÀ ADAPTER CHO BÌNH LUẬN
            commentsList = new ArrayList<>();
            commentAdapter = new CommentAdapter(commentsList);
            rvComments.setLayoutManager(new LinearLayoutManager(this));
            rvComments.setAdapter(commentAdapter);

            // 3. TẢI BÌNH LUẬN BAN ĐẦU
            loadComments();

        } else {
            Toast.makeText(this, "Không tìm thấy dữ liệu sản phẩm!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // --- Xử lý sự kiện Nút Back ---
        btnBack.setOnClickListener(v -> finish());

        // ************************************************************
        // LOGIC CHỨC NĂNG GỬI COMMENT
        // ************************************************************
        btnPostComment.setOnClickListener(v -> postComment());

        // ************************************************************
        // LOGIC CHỨC NĂNG YÊU THÍCH SẢN PHẨM
        // ************************************************************
        btnFavorite.setOnClickListener(v -> toggleFavoriteState(btnFavorite));

        // ************************************************************
        // LOGIC CHỨC NĂNG GIỎ HÀNG/MUA NGAY
        // ************************************************************
        btnAddToCart.setOnClickListener(v -> {
            CartManager.getInstance().addToCart(currentProduct);
            Toast.makeText(this, "Đã thêm " + currentProduct.getName() + " vào giỏ hàng!", Toast.LENGTH_SHORT).show();
            Intent cartIntent = new Intent(ProductDetailUserActivity.this, CartActivity.class);
            startActivity(cartIntent);
        });

        btnBuyNow.setOnClickListener(v -> {
            CartManager.getInstance().addToCart(currentProduct);
            Intent checkoutIntent = new Intent(ProductDetailUserActivity.this, CheckoutActivity.class);
            startActivity(checkoutIntent);
        });
    }

    // --- PHƯƠNG THỨC HỖ TRỢ CHỨC NĂNG COMMENT ---

    /**
     * Tải các bình luận hiện có của sản phẩm từ Firestore.
     */
    // ... trong ProductDetailUserActivity.java

    /**
     * Tải các bình luận hiện có của sản phẩm từ Firestore (Lọc theo productName).
     */
    private void loadComments() {
        if (currentProduct == null) return;

        db.collection("Comment")
                // ĐÃ CHỈNH SỬA: LỌC THEO productName, KHÔNG PHẢI productId
                .whereEqualTo("productName", currentProduct.getName())
                // CÓ THỂ BỊ LỖI SẮP XẾP NẾU TIMESTAMP LÀ STRING, NHƯNG TA VẪN GIỮ CÂU LỆNH NẾU BẠN MUỐN
                // Bạn cần tạo Index trong Firestore console nếu chưa có
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Chuyển đổi Firestore documents sang List<Comment>
                    List<Comment> loadedComments = queryDocumentSnapshots.toObjects(Comment.class);
                    commentAdapter.setComments(loadedComments);
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Lỗi tải bình luận", e);
                    Toast.makeText(this, "Không thể tải bình luận.", Toast.LENGTH_SHORT).show();
                });
    }


    private void postComment() {
        if (currentProduct == null) return;

        String content = etCommentInput.getText().toString().trim();

        if (content.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập nội dung bình luận.", Toast.LENGTH_SHORT).show();
            return;
        }

        // TẠO TIMESTAMP DƯỚI DẠNG CHUỖI NHƯ CẤU TRÚC FIRESTORE CỦA BẠN
        // Có thể dùng SimpleDateFormat để tạo chuỗi ngày giờ đầy đủ: "November 4, 2025 at 9:07:29 PM UTC+7"
        // HOẶC sử dụng giá trị đơn giản hơn nếu logic loadComments() không cần lọc theo ngày.

        // Giả định sử dụng SimpleDateFormat:
        String timestampString = new java.text.SimpleDateFormat("MMMM d, yyyy 'at' hh:mm:ss a z", Locale.ENGLISH).format(new java.util.Date());


        // ĐÃ CHỈNH SỬA: SỬ DỤNG productName, username, và timestamp là String
        Comment newComment = new Comment(
                currentProduct.getName(),  // <--- Gửi Tên sản phẩm
                currentUserName,           // <--- Gửi username
                content,                   // content
                timestampString            // <--- Gửi timestamp dưới dạng String
        );

        // Lưu vào Collection 'Comments'
        db.collection("Comment")
                .add(newComment)
                .addOnSuccessListener(documentReference -> {
                    newComment.setId(documentReference.getId());

                    // HIỂN THỊ TỨC THÌ TRÊN UI
                    commentAdapter.addComment(newComment);
                    rvComments.scrollToPosition(0);

                    etCommentInput.setText("");
                    Toast.makeText(this, "Bình luận đã được gửi!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi gửi bình luận.", Toast.LENGTH_SHORT).show();
                    Log.e("Comment", "Lỗi gửi bình luận", e);
                });
    }

// ...

// ...

    // --- CÁC PHƯƠNG THỨC HỖ TRỢ CHỨC NĂNG YÊU THÍCH (GIỮ NGUYÊN) ---

    private void checkInitialFavoriteState(ImageButton btnFavorite) {
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

    private void toggleFavoriteState(ImageButton btnFavorite) {
        isFavorite = !isFavorite;
        updateFavoriteUI(btnFavorite);
        updateFavoriteInFirestore();
    }

    private void updateFavoriteUI(ImageButton btnFavorite) {
        if (isFavorite) {
            btnFavorite.setImageResource(R.drawable.ic_favorite_24);
            btnFavorite.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#DC3545")));
        } else {
            btnFavorite.setImageResource(R.drawable.ic_favorite_border_24);
            btnFavorite.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#CCCCCC")));
        }
    }

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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}