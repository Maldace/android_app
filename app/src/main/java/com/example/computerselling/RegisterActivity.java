package com.example.computerselling;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        db = FirebaseFirestore.getInstance();
    }

    public void loginPageRedirect(View view){
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void register(View view){
        EditText etName = findViewById(R.id.txtRUserName);
        EditText etPass = findViewById(R.id.txtRPass);
        EditText etBirth = findViewById(R.id.txtRBirth);
        EditText etPhone = findViewById(R.id.txtRPhone);
        EditText etMail = findViewById(R.id.txtRMail);

        String name=etName.getText().toString().trim();
        String pass=etPass.getText().toString().trim();
        String birth=etBirth.getText().toString().trim();
        String phone=etPhone.getText().toString().trim();
        String mail=etMail.getText().toString().trim();

        User usr = new User(birth, mail, name, pass, phone, "user");
        db.collection("Acc")
                .add(usr) // firestore tạo id random
                .addOnSuccessListener(documentReference -> {
//                Log.d("Firestore", "Item added with ID: " + docId);
//                // Nếu bạn muốn lưu id vào document (optional):
//                documentReference.update("id", docId);
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error adding item", e);
                });


    }}

