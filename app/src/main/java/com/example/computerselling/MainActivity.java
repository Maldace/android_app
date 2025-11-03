package com.example.computerselling;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// Imports cho Firestore (Được giữ lại vì có thể bạn dùng ở nơi khác)
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    // ĐÃ XÓA: private Button btnAddItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. Khởi tạo Firestore (Được giữ lại)
        db = FirebaseFirestore.getInstance();

        // ĐÃ XÓA: Logic Gắn sự kiện Test Firestore (btn_add_item)
        /*
        btnAddItem = findViewById(R.id.btn_add_item);
        if (btnAddItem != null) {
            btnAddItem.setOnClickListener(v -> addSampleItemToFirestore());
        } else {
            Log.e("ButtonError", "Button btn_add_item not found! (Chức năng test Firestore sẽ không chạy)");
        }
        */
    }

// ---------------------------------------------------------------------------------------

    // Phương thức chuyển hướng đến màn hình đăng ký
    public void registerPageRedirect(View view){
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Phương thức xử lý logic đăng nhập và lưu username vào SharedPreferences.
     */
    public void logIn(View view){
        EditText etUsername = findViewById(R.id.txtName);
        EditText etPassword = findViewById(R.id.txtPass);
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        db.collection("Acc")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Chuyển document thành Object PC
                            User usr = document.toObject(User.class);

                            if(username.equals(usr.getName()) && password.equals(usr.getPass())){

                                // BƯỚC 1: LƯU USERNAME VÀO SharedPreferences (Đã sửa lỗi key)
                                SharedPreferences sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();

                                editor.putString("current_username", username);
                                editor.putString("current_birth", usr.getBirth());
                                editor.putString("current_phone", String.valueOf(usr.getPhone()));
                                editor.putString("current_mail", usr.getMail());
                                editor.putString("current_pass", usr.getPass());
                                editor.putString("current_address", usr.getAddress());
                                editor.apply();

                                // BƯỚC 2: Chuyển sang HomeActivity
                                if (usr.getRole().equals("admin")){
                                    Intent intent = new Intent(MainActivity.this, HomeAdminActivity.class);
                                    startActivity(intent);
                                }
                                else{
                                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                }

                                // Đóng LoginActivity
                                finish();

                            }
                        }
                        Toast.makeText(this, "Sai tên đăng nhập hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.w("Firestore", "Error getting documents.", task.getException());
                    }
                });

//        if (username.equals("ThanhPhuoc") && password.equals("1234")) {
//
//            // BƯỚC 1: LƯU USERNAME VÀO SharedPreferences (Đã sửa lỗi key)
//            SharedPreferences sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
//            SharedPreferences.Editor editor = sharedPref.edit();
//
//            editor.putString("current_username", username);
//            editor.apply();
//
//            // BƯỚC 2: Chuyển sang HomeActivity
//            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
//            startActivity(intent);
//
//            // Đóng LoginActivity
//            finish();
//
//        } else {
//            Toast.makeText(this, "Sai tên đăng nhập hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
//        }
    }

// ---------------------------------------------------------------------------------------

    // ĐÃ XÓA HOÀN TOÀN: Phương thức addSampleItemToFirestore()
}