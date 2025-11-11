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
import androidx.appcompat.widget.SearchView;
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
    private ArrayList<PC> filteredList; // Danh sách sản phẩm ĐÃ LỌC để hiển thị trên GridView
    private SearchView searchView; // KHAI BÁO MỚI

    // KHAI BÁO BIẾN MỚI CHO THỐNG KÊ
    private CardView cardOpenStatistics; // <<< THÊM: Khai báo CardView

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_admin);

        db = FirebaseFirestore.getInstance();
        aPC = new ArrayList<>();
        filteredList = new ArrayList<>(); // KHỞI TẠO filteredList

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

//        adapter = new ListAdapter(this, aPC);
        adapter = new ListAdapter(this, filteredList);
        gridView.setAdapter(adapter);
        searchView = findViewById(R.id.searchView); // ID đã thêm trong XML
        setupSearchView(); // Gọi phương thức cài đặt tìm kiếm

        // 3. Xử lý sự kiện click Item trên GridView
        gridView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            PC selectedPC = filteredList.get(position);

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
// PHẦN MỚI: TRIỂN KHAI CHỨC NĂNG TÌM KIẾM
// ---------------------------------------------------------------------------------------

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Xử lý khi người dùng nhấn nút tìm kiếm
                filterProducts(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Xử lý khi văn bản thay đổi (Tìm kiếm theo thời gian thực)
                filterProducts(newText);
                return true;
            }
        });
    }

    /**
     * Hàm thực hiện lọc dữ liệu dựa trên văn bản tìm kiếm (query).
     * @param query Văn bản người dùng nhập vào.
     */
    private void filterProducts(String query) {
        // Xóa dữ liệu cũ trong danh sách hiển thị
        filteredList.clear();

        if (query == null || query.isEmpty()) {
            // Nếu query rỗng, hiển thị toàn bộ danh sách gốc
            filteredList.addAll(aPC);
        } else {
            String lowerCaseQuery = query.toLowerCase().trim();
            // Lặp qua danh sách gốc (aPC) và kiểm tra
            for (PC pc : aPC) {
                // Lọc theo tên sản phẩm. Giả sử class PC có phương thức getName()
                if (pc.getName() != null && pc.getName().toLowerCase().contains(lowerCaseQuery)) {
                    filteredList.add(pc);
                }
            }
        }

        // Thông báo cho adapter biết dữ liệu đã thay đổi để GridView cập nhật
        adapter.notifyDataSetChanged();
        // Cập nhật chiều cao sau khi lọc
        setDynamicHeight(gridView);

        if (filteredList.isEmpty() && !query.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy sản phẩm nào.", Toast.LENGTH_SHORT).show();
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
                    aPC.clear(); // Xóa list gốc

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        try {
                            PC pc = doc.toObject(PC.class);
                            if (pc != null) {
                                pc.setId(doc.getId());
                                aPC.add(pc);
                            }
                        } catch (Exception e) {
                            Log.e("Firestore", "Lỗi ánh xạ dữ liệu Document: " + doc.getId(), e);
                        }
                    }

                    // Sau khi tải xong dữ liệu gốc (aPC), cập nhật filteredList để hiển thị
                    // Nếu đã có văn bản tìm kiếm, giữ nguyên bộ lọc hiện tại.
                    if (searchView.getQuery().toString().isEmpty()) {
                        filteredList.clear();
                        filteredList.addAll(aPC);
                    } else {
                        // Nếu đang có tìm kiếm, chạy lại bộ lọc với query hiện tại
                        filterProducts(searchView.getQuery().toString());
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