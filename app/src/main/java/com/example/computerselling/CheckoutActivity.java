package com.example.computerselling;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText; // Cần import EditText
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class CheckoutActivity extends AppCompatActivity {

    private CartManager cartManager;

    // Khai báo các trường nhập liệu
    private EditText etName, etPhone, etAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        cartManager = CartManager.getInstance();
        long total = cartManager.calculateTotalPrice();

        // 1. Ánh xạ tất cả các View từ activity_checkout.xml
        TextView tvTotal = findViewById(R.id.tv_checkout_total);
        Button btnConfirmPayment = findViewById(R.id.btn_confirm_payment);

        // Ánh xạ 3 trường nhập liệu
        etName = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_phone);
        etAddress = findViewById(R.id.et_address);
        SharedPreferences sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);

        String username = sharedPref.getString("current_username", "Khách");
        String phone_num = sharedPref.getString("current_phone", "Khách");
        String addr = sharedPref.getString("current_address", "Khách");

        etName.setText(username);
        etPhone.setText(phone_num);
        etAddress.setText(addr);
        // Hiển thị tổng tiền
        String formattedTotal = String.format(Locale.getDefault(), "Tổng tiền cần thanh toán: %,d VND", total);
        tvTotal.setText(formattedTotal);

        // 2. Xử lý sự kiện khi người dùng xác nhận thanh toán
        btnConfirmPayment.setOnClickListener(v -> {

            // Lấy thông tin người dùng
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String address = etAddress.getText().toString().trim();

            // LOGIC KIỂM TRA THÔNG TIN GIAO HÀNG
            if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ Họ tên, SĐT và Địa chỉ giao hàng.", Toast.LENGTH_LONG).show();
                return;
            }

            // TODO: Ở đây bạn sẽ triển khai logic LƯU ĐƠN HÀNG vào Firestore/Database (Giữ lại chỗ trống này)
            // SaveOrderToFirebase(name, phone, address, total, cartManager.getCartItemList());

            // 1. Xóa giỏ hàng (Sau khi "thanh toán" thành công)
            cartManager.clearCart();

            // 2. Thông báo thành công và Cảm ơn Khách hàng
            Toast.makeText(this, "Đặt hàng thành công! Cảm ơn quý khách đã đặt hàng.", Toast.LENGTH_LONG).show();

            // 3. Quay về màn hình chính (HomeActivity)
            Intent intent = new Intent(CheckoutActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // Đóng Activity hiện tại
        });
    }
}