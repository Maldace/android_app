package com.example.computerselling;

public class FavoriteModel {
    public String product;  // id sản phẩm
    public String user;

    public FavoriteModel() {} // Bắt buộc cho Firestore

    public FavoriteModel(String user, String productId) {
        this.product = productId;   // Gán sản phẩm
        this.user=user;
    }

    public String getProduct() {
        return product;
    }
    public void setProduct(String product) {
        this.product = product;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}

