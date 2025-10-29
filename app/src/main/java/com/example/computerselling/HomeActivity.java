package com.example.computerselling;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private ArrayList<PC> aPC;
    private GridView gridView;
    private FirebaseFirestore db;
    private ListAdapter adapter;
    private String currentUsername;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        db = FirebaseFirestore.getInstance();
        aPC = new ArrayList<>();

        // 1. LẤY USERNAME TỪ SharedPreferences VÀ HIỂN THỊ
        SharedPreferences sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        currentUsername = sharedPref.getString("current_username", "Khách");

        TextView welcomeTextView = findViewById(R.id.txtHi); // Giả sử ID là txtHi
        if (welcomeTextView != null) {
            // HIỂN THỊ USERNAME ĐÃ LƯU
            welcomeTextView.setText("Xin chào " + currentUsername);
        } else {
            Log.e("HomeActivity", "Không tìm thấy TextView có ID: txtHi");
        }

        // 2. Thiết lập GridView và Adapter
        gridView = findViewById(R.id.grid_view);

        adapter = new ListAdapter(this, aPC);
        gridView.setAdapter(adapter);

        // 3. Xử lý sự kiện click Item trên GridView
        gridView.setOnItemClickListener((AdapterView<?> parent, android.view.View view, int position, long id) -> {
            PC selectedPC = aPC.get(position);

            Intent intent = new Intent(HomeActivity.this, ProductDetailActivity.class);
            intent.putExtra("selected_pc", selectedPC);
            startActivity(intent);
        });

        // 4. Xử lý sự kiện các nút điều hướng
        ImageButton btnCart = findViewById(R.id.btnCart);
        if (btnCart != null) {
            btnCart.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(intent);
            });
        }

        ImageButton btnUser = findViewById(R.id.btnUser);
        if (btnUser != null) {
            btnUser.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                // Truyền username sang Profile
                intent.putExtra("username", currentUsername);
                startActivity(intent);
            });
        }
    }

// ---------------------------------------------------------------------------------------

    /**
     * Phương thức được gọi khi Activity hiển thị trở lại.
     * Đảm bảo dữ liệu mới nhất (bao gồm sản phẩm test) được tải lại.
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadProducts();
    }

// ---------------------------------------------------------------------------------------

    /**
     * Tải danh sách sản phẩm (PC) từ collection "Computer" của Firestore.
     */
    private void loadProducts() {
        db.collection("Computer")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    aPC.clear();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        try {
                            PC pc = doc.toObject(PC.class);
                            if (pc != null) {
                                pc.setId(doc.getId()); // Cập nhật ID từ document ID
                                aPC.add(pc);
                            }
                        } catch (Exception e) {
                            Log.e("Firestore", "Lỗi ánh xạ dữ liệu Document: " + doc.getId(), e);
                        }
                    }

                    adapter.notifyDataSetChanged();
                    setDynamicHeight(gridView);
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Lỗi khi lấy dữ liệu sản phẩm:", e);
                    Toast.makeText(this, "Lỗi tải dữ liệu sản phẩm.", Toast.LENGTH_LONG).show();
                });
    }

// ---------------------------------------------------------------------------------------

    /**
     * Hàm tính toán và thiết lập chiều cao động cho GridView.
     */
    public static void setDynamicHeight(GridView gridView) {
        ListAdapter listAdapter = (ListAdapter) gridView.getAdapter();
        if (listAdapter == null || listAdapter.getCount() == 0) {
            ViewGroup.LayoutParams params = gridView.getLayoutParams();
            params.height = 0;
            gridView.setLayoutParams(params);
            return;
        }

        int items = listAdapter.getCount();
        final int numColumns = 2;
        int rows = (items + numColumns - 1) / numColumns;

        if (gridView.getWidth() == 0) {
            gridView.post(() -> setDynamicHeight(gridView));
            return;
        }

        int columnWidth = (gridView.getWidth() - gridView.getPaddingLeft() - gridView.getPaddingRight() - (gridView.getHorizontalSpacing() * (numColumns - 1))) / numColumns;
        View listItem = listAdapter.getView(0, null, gridView);

        listItem.measure(
                View.MeasureSpec.makeMeasureSpec(columnWidth, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );
        int singleItemHeight = listItem.getMeasuredHeight();

        int totalHeight = singleItemHeight * rows;
        totalHeight += gridView.getVerticalSpacing() * (rows - 1);

        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight;
        gridView.setLayoutParams(params);
        gridView.requestLayout();
    }

    public void add(View view){
        Intent intent = new Intent(HomeActivity.this, AddActivity.class);
        startActivity(intent);
    }

// ---------------------------------------------------------------------------------------

    /**
     * Phương thức xử lý đăng xuất.
     * Xóa dữ liệu người dùng khỏi SharedPreferences và chuyển về màn hình Login.
     */
    public void logout(View view) {

        // 1. Xóa tất cả dữ liệu người dùng khỏi SharedPreferences
        SharedPreferences sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        // Xóa tất cả các khóa đã lưu (username, pass, mail, phone, v.v.)
        editor.clear();
        editor.apply();

        // 2. Chuyển người dùng về màn hình đăng nhập (MainActivity)
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);

        // Cờ quan trọng: Xóa tất cả các Activity trước đó trên stack
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);

        // 3. Kết thúc HomeActivity hiện tại
        finish();

        Toast.makeText(this, "Đã đăng xuất thành công!", Toast.LENGTH_SHORT).show();
    }
}