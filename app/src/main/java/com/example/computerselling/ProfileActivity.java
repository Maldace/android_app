package com.example.computerselling;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        TextView txtName = findViewById(R.id.txtPName);
        TextView txtBirth = findViewById(R.id.txtPBirth);
        TextView txtPhone = findViewById(R.id.txtPPhone);
        TextView txtAddress = findViewById(R.id.txtPAddress);
        TextView txtMail = findViewById(R.id.txtPMail);
        TextView txtPass = findViewById(R.id.txtPPass);

        SharedPreferences sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String username = sharedPref.getString("current_username", "Khách");
        String birth = sharedPref.getString("current_birth", "Khách");
        String phone = sharedPref.getString("current_phone", "Khách");
        String address = sharedPref.getString("current_address", "Khách");
        String mail = sharedPref.getString("current_mail", "Khách");
        String pass = sharedPref.getString("current_pass", "Khách");

        txtName.setText(username);
        txtBirth.setText(birth);
        txtPhone.setText(phone);
        txtAddress.setText(address);
        txtMail.setText(mail);
        txtPass.setText(pass);
    }
    public void homePage(View view){
        Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
    public void buy_history(View view){

    }
}
