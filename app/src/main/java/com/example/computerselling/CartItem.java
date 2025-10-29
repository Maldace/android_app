package com.example.computerselling;

public class CartItem {
    private PC pc;
    private int quantity;

    public CartItem(PC pc, int quantity) {
        this.pc = pc;
        this.quantity = quantity;
    }

    // Getters
    public PC getPc() { return pc; }
    public int getQuantity() { return quantity; }

    // Setters (quan trọng cho việc thay đổi số lượng)
    public void setQuantity(int quantity) { this.quantity = quantity; }

    // Phương thức tiện ích để tính tổng giá trị của mục này
    public long getTotalPrice() {
        return pc.getPrice() * quantity;
    }
}
