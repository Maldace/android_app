package com.example.computerselling;

public class FavoriteModel {
    public String user;     // id người dùng
    public String product;  // id sản phẩm

    public FavoriteModel() {} // Bắt buộc cho Firestore

    public FavoriteModel(String userId, String productId) {
        this.user = userId;         // Gán userId
        this.product = productId;   // Gán sản phẩm
    }
}

