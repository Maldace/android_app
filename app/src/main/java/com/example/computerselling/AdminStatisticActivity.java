package com.example.computerselling; // Đảm bảo thay thế bằng package của bạn

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast; // Thêm import này
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.NumberFormat;
import java.util.Locale;

public class AdminStatisticActivity extends AppCompatActivity {

    private static final String TAG = "AdminStatisticActivity";
    private FirebaseFirestore db;
    private TextView tvTotalRevenue;
    private TextView tvCompletedOrders;
    private TextView tvLoadingStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_statistic);

        // Khởi tạo Firebase
        db = FirebaseFirestore.getInstance();

        // Ánh xạ Views
        tvTotalRevenue = findViewById(R.id.tv_total_revenue);
        tvCompletedOrders = findViewById(R.id.tv_completed_orders);
        tvLoadingStatus = findViewById(R.id.tv_loading_status);

        // Đặt tiêu đề cho thanh Action Bar (nếu có)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Bảng Thống Kê");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Thêm nút back
        }

        loadStatistics();
    }

    private void loadStatistics() {
        tvLoadingStatus.setVisibility(View.VISIBLE);
        tvLoadingStatus.setText("Đang tải dữ liệu...");

        // Truy vấn collection "Orders"
        db.collection("Orders")
                // Giả định trường status là "Hoàn thành"
                .whereEqualTo("status", "Hoàn thành")
                .get()
                .addOnCompleteListener(task -> {
                    tvLoadingStatus.setVisibility(View.GONE); // Ẩn loading khi hoàn tất
                    if (task.isSuccessful()) {
                        long totalRevenue = 0;
                        int completedOrderCount = 0;

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Lấy totalAmount
                            // **LƯU Ý QUAN TRỌNG: Đảm bảo trường totalAmount trong Firestore là Number (Long hoặc Double)**
                            Long amount = document.getLong("totalAmount");
                            if (amount != null) {
                                totalRevenue += amount;
                            } else {
                                // Xử lý trường hợp totalAmount không phải Long hoặc bị null
                                Log.w(TAG, "totalAmount is null or not Long for document: " + document.getId());
                            }
                            completedOrderCount++;
                        }

                        // HIỂN THỊ KẾT QUẢ
                        String formattedRevenue = formatCurrency(totalRevenue);
                        tvTotalRevenue.setText(formattedRevenue);
                        tvCompletedOrders.setText(String.valueOf(completedOrderCount));

                    } else {
                        // Xử lý lỗi
                        Log.e(TAG, "Error getting documents: ", task.getException());
                        tvTotalRevenue.setText("---");
                        tvCompletedOrders.setText("---");
                        tvLoadingStatus.setVisibility(View.VISIBLE);
                        tvLoadingStatus.setText("Lỗi tải dữ liệu: " + task.getException().getMessage());
                        Toast.makeText(this, "Lỗi tải dữ liệu thống kê.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Định dạng số tiền theo chuẩn Việt Nam (VND).
     */
    private String formatCurrency(long amount) {
        // Sử dụng Locale Việt Nam
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        // Thay thế ký hiệu tiền tệ mặc định (₫) bằng "VND"
        return format.format(amount).replace("₫", " VND");
    }
}