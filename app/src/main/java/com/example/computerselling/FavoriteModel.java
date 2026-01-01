package com.example.computerselling;

public class FavoriteModel {
    public String product;  // id sản phẩm

    public FavoriteModel() {} // Bắt buộc cho Firestore

    public FavoriteModel(String userId, String productId) {
        this.product = productId;   // Gán sản phẩm
    }

    public String getProduct() {
        return product;
    }
    public void setProduct(String product) {
        this.product = product;
    }
}

