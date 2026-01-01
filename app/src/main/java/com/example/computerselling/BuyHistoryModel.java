package com.example.computerselling;

public class BuyHistoryModel {

    public String buyer;
    public String date;
    public String product;
    public int quantity;


    public BuyHistoryModel() {} // Bắt buộc cho Firestore

    public BuyHistoryModel(String buyer, String product, int quantity, String date) {
        this.buyer = buyer;         // Gán userId
        this.date = date;
        this.product = product;   // Gán sản phẩm
        this.quantity = quantity;
    }

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
