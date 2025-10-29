package com.example.computerselling;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Locale;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvTotalPrice;
    private Button btnCheckout;
    private CartAdapter cartAdapter;
    private CartManager cartManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Khởi tạo các thành phần
        cartManager = CartManager.getInstance();
        recyclerView = findViewById(R.id.recycler_view_cart);
        tvTotalPrice = findViewById(R.id.tv_total_price);
        btnCheckout = findViewById(R.id.btn_checkout);

        setupRecyclerView();

        // Thiết lập sự kiện cho nút THANH TOÁN
        btnCheckout.setOnClickListener(v -> {
            if (cartManager.getCartSize() > 0) {
                // ĐÃ SỬA: Chuyển sang màn hình Thanh toán/CheckoutActivity
                Intent checkoutIntent = new Intent(CartActivity.this, CheckoutActivity.class);
                startActivity(checkoutIntent);
            } else {
                Toast.makeText(this, "Giỏ hàng của bạn đang trống!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật giao diện mỗi khi Activity được hiển thị lại
        updateCartUI();
    }

    private void setupRecyclerView() {
        // Khởi tạo Adapter với danh sách hiện tại từ CartManager
        cartAdapter = new CartAdapter(this, cartManager.getCartItemList(), new CartAdapter.OnCartItemChangeListener() {

            // Xử lý khi số lượng sản phẩm thay đổi (+ hoặc -)
            @Override
            public void onQuantityChanged(PC pc, int newQuantity) {
                cartManager.updateQuantity(pc, newQuantity);
                updateCartUI();
            }

            // Xử lý khi sản phẩm bị xóa
            @Override
            public void onItemRemoved(PC pc) {
                cartManager.removeItem(pc);
                // Cập nhật lại danh sách và giao diện
                updateCartUI();
                Toast.makeText(CartActivity.this, pc.getName() + " đã bị xóa khỏi giỏ hàng.", Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(cartAdapter);
    }

    /**
     * Cập nhật giao diện người dùng: tổng tiền, nút thanh toán và danh sách sản phẩm.
     */
    public void updateCartUI() {
        long total = cartManager.calculateTotalPrice();

        // 1. Cập nhật Adapter (quan trọng để hiển thị thay đổi số lượng/xóa)
        cartAdapter.updateList(cartManager.getCartItemList());

        // 2. Cập nhật TextView Tổng tiền
        String formattedPrice = String.format(Locale.getDefault(), "Tổng tiền: %,d VND", total);
        tvTotalPrice.setText(formattedPrice);

        // 3. Quản lý trạng thái nút Thanh toán
        if (cartManager.getCartSize() == 0) {
            tvTotalPrice.setText("Giỏ hàng trống.");
            btnCheckout.setEnabled(false);
        } else {
            btnCheckout.setEnabled(true);
        }
    }
}