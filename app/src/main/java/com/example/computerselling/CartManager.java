package com.example.computerselling;

import com.example.computerselling.PC; // Đảm bảo import PC
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartManager {

    // 1. Instance Singleton
    private static CartManager instance;

    // 2. LƯU TRỮ CHUẨN: Map<PC, Integer> -> Sản phẩm và Số lượng
    private final Map<PC, Integer> cartItems = new HashMap<>();

//    ArrayList<PC> cartList = new ArrayList<>();  // danh sách các PC trong giỏ
//    CartAdapter adapter = new CartAdapter(this, cartList, this);


    // Constructor private
    private CartManager() {}

    // Phương thức tĩnh để lấy instance
    public static CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    // **********************************************
    // CHỨC NĂNG THÊM VÀO GIỎ HÀNG
    // **********************************************

    public void addToCart(PC pc) {
        // Lấy số lượng hiện tại, nếu chưa có thì là 0
        int currentQuantity = cartItems.getOrDefault(pc, 0);

        // Cập nhật số lượng (+1)
        cartItems.put(pc, currentQuantity + 1);
    }

    // **********************************************
    // QUẢN LÝ VÀ TÍNH TOÁN
    // **********************************************

    /**
     * Cập nhật số lượng mới cho một sản phẩm PC. Dùng cho CartActivity.
     */
    public void updateQuantity(PC pc, int newQuantity) {
        if (newQuantity <= 0) {
            // Xóa sản phẩm nếu số lượng <= 0
            cartItems.remove(pc);
        } else {
            // Cập nhật số lượng mới
            cartItems.put(pc, newQuantity);
        }
    }

    /**
     * Xóa một sản phẩm PC khỏi giỏ hàng.
     */
    public void removeItem(PC pc) {
        cartItems.remove(pc);
    }

    /**
     * LẤY DỮ LIỆU DƯỚI DẠNG MAP<PC, Integer> (Cần cho CheckoutActivity và logic đặt hàng)
     */
    public Map<PC, Integer> getCartItems() {
        return cartItems;
    }

    /**
     * Chuyển đổi Map thành List<CartItem> để đổ dữ liệu vào Adapter.
     * LƯU Ý: Yêu cầu lớp CartItem phải tồn tại.
     */
    public List<CartItem> getCartItemList() {
        List<CartItem> items = new ArrayList<>();

        for (Map.Entry<PC, Integer> entry : cartItems.entrySet()) {
            PC pc = entry.getKey();
            int quantity = entry.getValue();
            // LƯU Ý: Dòng này yêu cầu lớp CartItem phải tồn tại và có constructor phù hợp
             items.add(new CartItem(pc, quantity));
        }
        return items;
    }

    /**
     * Tính toán tổng giá trị của tất cả sản phẩm.
     */
    public long calculateTotalPrice() {
        long total = 0;
        for (Map.Entry<PC, Integer> entry : cartItems.entrySet()) {
            total += entry.getKey().getPrice() * entry.getValue();
        }
        return total;
    }

    /**
     * Xóa toàn bộ giỏ hàng.
     */
    public void clearCart() {
        cartItems.clear();
    }

    /**
     * Lấy số loại sản phẩm khác nhau có trong giỏ.
     */
    public int getCartSize() {
        return cartItems.size();
    }
}