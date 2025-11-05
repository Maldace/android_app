package com.example.computerselling;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

public class EditProductActivity extends AppCompatActivity {
    PC currentProduct;
    String id;
    FirebaseFirestore db;
    TextView txtProduct;
    TextView txtDiscount;
    TextView txtDescription;
    TextView txtPrice;
    TextView txtImg;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_product);
        db = FirebaseFirestore.getInstance();

        txtProduct = findViewById(R.id.txtEProduct);
        txtDiscount= findViewById(R.id.txtEDiscount);
        txtDescription= findViewById(R.id.txtEDescription);
        txtPrice= findViewById(R.id.txtEPrice);
        txtImg= findViewById(R.id.txtEImg);

        currentProduct = getIntent().getParcelableExtra("selected_pc");
        Log.d("DEBUG", "Nhận: " + currentProduct);

        if (currentProduct != null) {
            // Hiển thị dữ liệu
            txtProduct.setText(currentProduct.getName());
            // Định dạng và hiển thị giá
            txtDiscount.setText(String.valueOf(currentProduct.getDiscountPercent()));
            txtDescription.setText(currentProduct.getDescription());
            txtPrice.setText(String.valueOf(currentProduct.getPrice()));
            txtImg.setText(currentProduct.getImageUrl());

        } else {
            Toast.makeText(this, "Không tìm thấy dữ liệu sản phẩm!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }

    public void editProduct(View view){
        id=currentProduct.getId();

        db.collection("Computer").document(id) // ID document cần xóa
                .delete()
                .addOnSuccessListener(aVoid -> {


                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "❌ Lỗi khi xóa document", e);
                });

        String product=txtProduct.getText().toString().trim();
        String description=txtDescription.getText().toString().trim();
        int discount=Integer.parseInt(txtDiscount.getText().toString().trim());
        long price=Long.parseLong(txtPrice.getText().toString().trim());
        String img =txtImg.getText().toString().trim();
        CollectionReference collectionRef = db.collection("Computer");
        collectionRef.get().addOnSuccessListener(querySnapshot -> {

            PC mypc = new PC(description, discount, currentProduct.getId(), img, product, price, price-(price*(100/discount)), null );
            collectionRef.add(mypc)
                    .addOnSuccessListener(documentReference -> {
//                        Intent intent = new Intent(AddActivity.this, HomeAdminActivity.class);
//                        startActivity(intent);
                        Toast.makeText(this, "Sửa thành công!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Lỗi khi thêm document", e);
                    });
        });
    }
}
