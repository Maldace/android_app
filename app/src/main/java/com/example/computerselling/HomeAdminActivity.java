package com.example.computerselling;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView; // <<< THÊM: Import CardView

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class HomeAdminActivity extends AppCompatActivity {

    private ArrayList<PC> aPC;
    private GridView gridView;
    private FirebaseFirestore db;
    private ListAdapter adapter;
    private String currentUsername;

    // KHAI BÁO BIẾN MỚI CHO THỐNG KÊ
    private CardView cardOpenStatistics; // <<< THÊM: Khai báo CardView

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_admin);

        db = FirebaseFirestore.getInstance();
        aPC = new ArrayList<>();

        // 1. LẤY USERNAME TỪ SharedPreferences VÀ HIỂN THỊ
        SharedPreferences sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        currentUsername = sharedPref.getString("current_username", "Khách");

        TextView welcomeTextView = findViewById(R.id.txtHi); // Giả sử ID là txtHi
        if (welcomeTextView != null) {
            // HIỂN THỊ USERNAME ĐÃ LƯU
            welcomeTextView.setText("Xin chào Admin: " + currentUsername); // Chỉnh sửa hiển thị cho Admin
        } else {
            Log.e("HomeActivity", "Không tìm thấy TextView có ID: txtHi");
        }

        // 2. Thiết lập GridView và Adapter
        gridView = findViewById(R.id.grid_view);

        adapter = new ListAdapter(this, aPC);
        gridView.setAdapter(adapter);

        // 3. Xử lý sự kiện click Item trên GridView
        gridView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            PC selectedPC = aPC.get(position);

            Intent intent = new Intent(HomeAdminActivity.this, ProductDetailActivity.class);
            intent.putExtra("selected_pc", selectedPC);
            startActivity(intent);
        });

        // 4. Xử lý sự kiện các nút điều hướng (Giả định rằng Admin vẫn có các nút này)
        ImageButton btnCart = findViewById(R.id.btnCart);
        if (btnCart != null) {
            btnCart.setOnClickListener(v -> {
                Intent intent = new Intent(HomeAdminActivity.this, CartActivity.class);
                startActivity(intent);
            });
        }

        ImageButton btnUser = findViewById(R.id.btnUser);
        if (btnUser != null) {
            btnUser.setOnClickListener(v -> {
                Intent intent = new Intent(HomeAdminActivity.this, ProfileActivity.class);
                // Truyền username sang Profile
                intent.putExtra("username", currentUsername);
                startActivity(intent);
            });
        }

        // ---------------------------------------------------------------------------------------
        // THÊM: XỬ LÝ SỰ KIỆN CLICK CHO TRANG THỐNG KÊ
        // ---------------------------------------------------------------------------------------

        // Ánh xạ CardView Thống Kê (ID từ home_admin.xml)
        cardOpenStatistics = findViewById(R.id.card_open_statistics); // <<< THÊM: Ánh xạ CardView

        if (cardOpenStatistics != null) {
            cardOpenStatistics.setOnClickListener(v -> {
                // Khởi tạo Intent để chuyển sang AdminStatisticActivity
                Intent intent = new Intent(HomeAdminActivity.this, AdminStatisticActivity.class);
                startActivity(intent);
            });
        } else {
            Log.e("HomeAdminActivity", "Không tìm thấy CardView có ID: card_open_statistics");
            Toast.makeText(this, "Lỗi giao diện: Không tìm thấy nút Thống Kê.", Toast.LENGTH_LONG).show();
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
        // GỌI loadProducts() Ở ĐÂY để tải lại dữ liệu
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
        Intent intent = new Intent(HomeAdminActivity.this, AddActivity.class);
        startActivity(intent);
    }

// ---------------------------------------------------------------------------------------

    /**
     * Phương thức xử lý đăng xuất cho tài khoản Admin.
     * Xóa dữ liệu người dùng khỏi SharedPreferences và chuyển về màn hình Login.
     */
    public void logout(View view) {

        // 1. Xóa tất cả dữ liệu người dùng khỏi SharedPreferences
        SharedPreferences sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        // Xóa tất cả các khóa đã lưu
        editor.clear();
        editor.apply();

        // 2. Chuyển người dùng về màn hình đăng nhập (MainActivity)
        Intent intent = new Intent(HomeAdminActivity.this, MainActivity.class);

        // Cờ quan trọng: Xóa tất cả các Activity trước đó trên stack
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);

        // 3. Kết thúc HomeAdminActivity hiện tại
        finish();

        Toast.makeText(this, "Đã đăng xuất tài khoản Admin!", Toast.LENGTH_SHORT).show();
    }
}