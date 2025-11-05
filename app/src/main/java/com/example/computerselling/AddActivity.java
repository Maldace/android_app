package com.example.computerselling;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddActivity extends AppCompatActivity {
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add);
        db = FirebaseFirestore.getInstance();
    }

    public void addProduct(View view){

        EditText etProduct = findViewById(R.id.txtAProduct);
        EditText etDescription = findViewById(R.id.txtADescription);
        EditText etDiscount = findViewById(R.id.txtADiscount);
        EditText etPrice = findViewById(R.id.txtAPrice);
        EditText etImg = findViewById(R.id.txtAImg);

        String product=etProduct.getText().toString().trim();
        String description=etDescription.getText().toString().trim();
        int discount=Integer.parseInt(etDiscount.getText().toString().trim());
        long price=Long.parseLong(etPrice.getText().toString().trim());
        String img =etImg.getText().toString().trim();
        CollectionReference collectionRef = db.collection("Computer");
        collectionRef.get().addOnSuccessListener(querySnapshot -> {

            // Lấy số lượng document hiện có
            int count = querySnapshot.size();

            // Tạo id mới = số lượng + 1
            int id = count + 1;

            PC mypc = new PC(description, discount, String.valueOf(id), img, product, price, price-(price*(100/discount)), null );
            collectionRef.add(mypc)
                    .addOnSuccessListener(documentReference -> {
                        Intent intent = new Intent(AddActivity.this, HomeAdminActivity.class);
                        startActivity(intent);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Lỗi khi thêm document", e);
                    });
        });
    }

}
