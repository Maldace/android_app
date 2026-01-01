package com.example.computerselling;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity {

    String currentUser;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite);
        RecyclerView recyclerView = findViewById(R.id.favorite);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<FavoriteModel> list = new ArrayList<>(); // tạo danh sách rỗng
        FavoriteAdapter adapter = new FavoriteAdapter(list);
        recyclerView.setAdapter(adapter);
        SharedPreferences sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        currentUser = sharedPref.getString("current_username", "Khách");
        FirebaseFirestore db = FirebaseFirestore.getInstance(); // tạo Firestore

        db.collection("Favorite")  // lấy collection đúng tên bạn
                .whereEqualTo("user", currentUser) // chỉ lấy đơn hàng của user này
                .get()
                .addOnSuccessListener(query -> {  // khi lấy thành công
                    list.clear();                 // xóa danh sách cũ

                    for (DocumentSnapshot doc : query.getDocuments()) { // duyệt từng document
                        FavoriteModel item = doc.toObject(FavoriteModel.class); // biến thành object
                        list.add(item);                                           // thêm vào list
                    }

                    adapter.notifyDataSetChanged(); // cập nhật RecyclerView
                });
    }
}
