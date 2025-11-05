package com.example.computerselling;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import java.util.Objects;

public class PC implements Parcelable {

    private String description;
    private int discountPercent;
    private String id;
    private String imageUrl;
    private String name;
    private long originalPrice;
    private long price;
    private List<String> subImgUrls; // Mảng URL ảnh phụ

    // ==========================================================
    // 1. CONSTRUCTORS
    // ==========================================================
    public PC() {
    }

    public PC(String description, int discountPercent, String id, String imageUrl, String name, long originalPrice, long price, List<String> subImgUrls) {
        this.description = description;
        this.discountPercent = discountPercent;
        this.id = id;
        this.imageUrl = imageUrl;
        this.name = name;
        this.originalPrice = originalPrice;
        this.price = price;
        this.subImgUrls = subImgUrls;
    }

    // ==========================================================
    // 2. GETTERS VÀ SETTERS
    // ==========================================================
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public long getPrice() { return price; }
    public void setPrice(long price) { this.price = price; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public long getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(long originalPrice) { this.originalPrice = originalPrice; }

    public int getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(int discountPercent) { this.discountPercent = discountPercent; }

    public List<String> getSubImgUrls() {
        return subImgUrls;
    }

    public void setSubImgUrls(List<String> subImgUrls) {
        this.subImgUrls = subImgUrls;
    }

    // ==========================================================
    // 3. EQUALS VÀ HASHCODE
    // ==========================================================
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PC pc = (PC) o;
        return Objects.equals(id, pc.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // ==========================================================
    // 4. PARCELABLE IMPLEMENTATION (ĐÃ CẬP NHẬT)
    // ==========================================================
    protected PC(Parcel in) {
        id = in.readString();
        name = in.readString();
        price = in.readLong();
        imageUrl = in.readString();
        description = in.readString();
        originalPrice = in.readLong();
        discountPercent = in.readInt();
        // Đã thêm: Đọc List<String>
        subImgUrls = in.createStringArrayList();
    }

    public static final Creator<PC> CREATOR = new Creator<PC>() {
        @Override
        public PC createFromParcel(Parcel in) {
            return new PC(in);
        }

        @Override
        public PC[] newArray(int size) {
            return new PC[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeLong(price);
        dest.writeString(imageUrl);
        dest.writeString(description);
        dest.writeLong(originalPrice);
        dest.writeInt(discountPercent);
        // Đã thêm: Ghi List<String>
        dest.writeStringList(subImgUrls);
    }
}