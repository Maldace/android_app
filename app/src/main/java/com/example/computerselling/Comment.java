package com.example.computerselling;

// KHÔNG CẦN NHẬP LẠI TOÀN BỘ CODE CỦA BẠN. CHỈ CẦN ĐẢM BẢO TÊN BIẾN SAU KHỚP.

public class Comment {

    // Tên biến trong Java phải khớp với tên trường Firestore
    private String productName; // <--- Thay đổi từ 'productId'
    private String username;    // <--- Thay đổi từ 'userName'
    private String content;
    private String timestamp;   // <--- Giữ là String nếu bạn lưu timestamp dưới dạng String
    private String id; // ID của Document

    // Constructor mặc định cần thiết cho Firestore
    public Comment() {
    }

    // Constructor để tạo đối tượng mới (Tạm thời dùng String cho timestamp)
    public Comment(String productName, String username, String content, String timestamp) {
        this.productName = productName;
        this.username = username;
        this.content = content;
        this.timestamp = timestamp;
    }

    // --- Getters và Setters (Bắt buộc phải khớp tên) ---

    public String getProductName() {
        return productName;
    }

    public String getUsername() {
        return username;
    }

    public String getContent() {
        return content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Lưu ý: Thêm các Setter khác nếu cần.
    // ...
}