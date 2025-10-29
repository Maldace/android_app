package com.example.computerselling;

import android.os.Parcelable;

public class User{

    private String birth;
    private String mail;
    private String name;
    private String pass;
    private String phone;
    private String role;
    public User() {
    }

    public User(String birth, String mail, String name, String pass,
                String phone, String role) {
        this.birth = birth;
        this.mail = mail;
        this.name = name;
        this.pass = pass;
        this.phone = phone;
        this.role = role;
    }

    public String getBirth() { return birth; }
    public void setBirth(String birth) { this.birth = birth; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getMail() { return mail; }
    public void setMail(String mail) { this.mail = mail; }

    public String getPass() { return pass; }
    public void setPass(String pass) { this.pass = pass; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }


}
