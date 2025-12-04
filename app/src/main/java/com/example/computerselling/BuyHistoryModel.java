package com.example.computerselling;

public class BuyHistoryModel {
    public String buyer;
    public String date;
    public String product;  // id sản phẩm
    public int quantity;

    public BuyHistoryModel() {} // Bắt buộc cho Firestore

    public BuyHistoryModel(String buyer, String date, String product, int quantity) {
        this.buyer = buyer;         // Gán userId
        this.date = date;
        this.product = product;   // Gán sản phẩm
        this.quantity = quantity;
    }
}
