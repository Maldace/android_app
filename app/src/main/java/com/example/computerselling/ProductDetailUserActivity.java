package com.example.computerselling;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
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

    private boolean isFavorite = false;

    private SharedPreferences sharedPref;
    private String currentUserName = "Khách";

    private ViewPager2 viewPager;
    private TabLayout tabLayout;

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

        sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        currentUserName = sharedPref.getString("current_username", "Khách");

        // Ánh xạ View
        viewPager = findViewById(R.id.product_image_slider);
        tabLayout = findViewById(R.id.product_image_indicator);

        TextView txtName = findViewById(R.id.product_detail_name);
        TextView txtPrice = findViewById(R.id.product_detail_price);
        TextView txtOrPrice = findViewById(R.id.product_detail_or_price);
        TextView txtDescription = findViewById(R.id.product_detail_description);
        ImageView btnBack = findViewById(R.id.btnBack);
        Button btnAddToCart = findViewById(R.id.btn_add_to_cart);
        ImageButton btnFavorite = findViewById(R.id.btn_favorite);
        Button btnBuyNow = findViewById(R.id.btn_buy_now);

        etCommentInput = findViewById(R.id.et_comment_input);
        btnPostComment = findViewById(R.id.btn_post_comment);
        rvComments = findViewById(R.id.rv_comments);

        Intent intent = getIntent();
        currentProduct = intent.getParcelableExtra("selected_pc");

        if (currentProduct != null) {

            txtName.setText(currentProduct.getName());
            txtDescription.setText(currentProduct.getDescription());
            id = currentProduct.getId();

            // 🧩 --- BẮT ĐẦU: XỬ LÝ HIỂN THỊ GIÁ ---
            if (currentProduct.getOriginalPrice() > 0 && currentProduct.getOriginalPrice() > currentProduct.getPrice()) {
                // Format giá gốc
                String formattedOriginal = String.format(Locale.getDefault(), "%,d VNĐ", currentProduct.getOriginalPrice());
                txtOrPrice.setText(formattedOriginal);
                txtOrPrice.setTextColor(Color.GRAY); // Màu xám
                txtOrPrice.setTextSize(14); // Nhỏ hơn
                txtOrPrice.setPaintFlags(txtOrPrice.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG); // Gạch ngang
                txtOrPrice.setVisibility(View.VISIBLE);
            } else {
                txtOrPrice.setVisibility(View.GONE); // Ẩn nếu không có giảm giá
            }

            // Format giá giảm
            String formattedDiscount = String.format(Locale.getDefault(), "%,d VNĐ", currentProduct.getPrice());
            txtPrice.setText(formattedDiscount);
            txtPrice.setTextColor(Color.parseColor("#DC3545")); // Màu đỏ
            txtPrice.setTextSize(20); // To hơn

            // Tính và hiển thị phần trăm giảm giá
            if (currentProduct.getOriginalPrice() > 0 && currentProduct.getOriginalPrice() > currentProduct.getPrice()) {
                double percent = ((double) (currentProduct.getOriginalPrice() - currentProduct.getPrice()) / currentProduct.getOriginalPrice()) * 100;
                String discountText = String.format(Locale.getDefault(), "  (Giảm %.1f%%)", percent);
                txtPrice.append(discountText);
            }
            // 🧩 --- KẾT THÚC: XỬ LÝ HIỂN THỊ GIÁ ---

            // Load ảnh
            loadProductImages();

            // Yêu thích
            checkInitialFavoriteState(btnFavorite);

            // Bình luận
            commentsList = new ArrayList<>();
            commentAdapter = new CommentAdapter(commentsList);
            rvComments.setLayoutManager(new LinearLayoutManager(this));
            rvComments.setAdapter(commentAdapter);

            loadComments();

        } else {
            Toast.makeText(this, "Không tìm thấy dữ liệu sản phẩm!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        btnBack.setOnClickListener(v -> finish());
        btnPostComment.setOnClickListener(v -> postComment());
        btnFavorite.setOnClickListener(v -> toggleFavoriteState(btnFavorite));

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

    // =====================================================
    // PHẦN CÒN LẠI (GIỮ NGUYÊN)
    // =====================================================

    private void loadProductImages() {
        List<String> finalImageUrls = new ArrayList<>();
        String mainImageUrl = currentProduct.getImageUrl();
        if (mainImageUrl != null && !mainImageUrl.isEmpty()) {
            finalImageUrls.add(mainImageUrl);
        }
        List<String> secondaryUrls = currentProduct.getSubImgUrls();
        if (secondaryUrls != null && !secondaryUrls.isEmpty()) {
            for (String url : secondaryUrls) {
                if (url != null && !url.isEmpty() && !finalImageUrls.contains(url)) {
                    finalImageUrls.add(url);
                }
            }
        }
        if (finalImageUrls.isEmpty()) {
            finalImageUrls.add("https://via.placeholder.com/300?text=No+Image");
        }

        ProductImageSliderAdapter sliderAdapter = new ProductImageSliderAdapter(finalImageUrls);
        viewPager.setAdapter(sliderAdapter);

        if (finalImageUrls.size() > 1) {
            new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {}).attach();
        } else {
            tabLayout.setVisibility(View.GONE);
        }
    }

    private void loadComments() {
        if (currentProduct == null) return;

        db.collection("Comment")
                .whereEqualTo("productName", currentProduct.getName())
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
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

        String timestampString = new java.text.SimpleDateFormat("MMMM d, yyyy 'at' hh:mm:ss a z", Locale.ENGLISH).format(new java.util.Date());
        Comment newComment = new Comment(currentProduct.getName(), currentUserName, content, timestampString);

        db.collection("Comment")
                .add(newComment)
                .addOnSuccessListener(documentReference -> {
                    newComment.setId(documentReference.getId());
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

    private void toggleFavoriteState(ImageButton btnFavorite) {
        isFavorite = !isFavorite;                                     // Đảo trạng thái

        updateFavoriteUI(btnFavorite);                                // Cập nhật giao diện

        if (isFavorite) {
            addFavoriteToFirestore();                                 // Nếu bật → thêm vào Favorite
        } else {
            removeFavoriteFromFirestore();                            // Nếu tắt → xoá khỏi Favorite
        }
    }

    private void addFavoriteToFirestore() {

        // Tạo object lưu thông tin yêu thích
        FavoriteModel fav = new FavoriteModel(
                currentUserName,
                currentProduct.getName()
        );

        db.collection("Favorite")
                .add(fav)                                             // Thêm document mới
                .addOnSuccessListener(doc -> {
                    Toast.makeText(this, "Đã thêm vào yêu thích!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Log.e("Favorite", "Lỗi thêm yêu thích", e));
    }

    private void removeFavoriteFromFirestore() {

        db.collection("Favorite")
                .whereEqualTo("user", currentUserName)                 // Tìm theo người dùng
                .whereEqualTo("product", currentProduct.getName())     // Tìm theo sản phẩm
                .get()
                .addOnSuccessListener(query -> {
                    for (var doc : query.getDocuments()) {
                        doc.getReference().delete();                   // Xoá document
                    }
                    Toast.makeText(this, "Đã xóa khỏi yêu thích!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Log.e("Favorite", "Lỗi xóa yêu thích", e));
    }

    private void checkInitialFavoriteState(ImageButton btnFavorite) {

        db.collection("Favorite")
                .whereEqualTo("user", currentUserName)                  // Kiểm tra user
                .whereEqualTo("product", currentProduct.getName())      // Kiểm tra sản phẩm
                .get()
                .addOnSuccessListener(query -> {
                    isFavorite = !query.isEmpty();                      // Nếu có document → đã yêu thích
                    updateFavoriteUI(btnFavorite);                      // Cập nhật icon
                })
                .addOnFailureListener(e -> Log.e("Favorite", "Lỗi kiểm tra yêu thích", e));
    }



    private void updateFavoriteUI(ImageButton btnFavorite) {
        if (isFavorite) {
            btnFavorite.setImageResource(R.drawable.ic_favorite_24);
            btnFavorite.setBackgroundTintList(
                    ColorStateList.valueOf(Color.parseColor("#DC3545"))
            );
        } else {
            btnFavorite.setImageResource(R.drawable.ic_favorite_border_24);
            btnFavorite.setBackgroundTintList(
                    ColorStateList.valueOf(Color.parseColor("#CCCCCC"))
            );
        }
    }


    private void updateFavoriteInFirestore() {
        DocumentReference userRef = db.collection("Users").document(currentUserName);
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

    public static class ProductImageSliderAdapter extends RecyclerView.Adapter<ProductImageSliderAdapter.ImageViewHolder> {
        private final List<String> imageList;

        public ProductImageSliderAdapter(List<String> imageList) {
            this.imageList = imageList;
        }

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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_slider, parent, false);
            return new ImageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
            String imageUrl = imageList.get(position);
            Glide.with(holder.imageView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_error)
                    .into(holder.imageView);
        }

        @Override
        public int getItemCount() {
            return imageList.size();
        }
    }
}
