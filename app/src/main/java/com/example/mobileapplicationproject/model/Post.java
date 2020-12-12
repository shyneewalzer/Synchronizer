package com.example.mobileapplicationproject.model;

public class Post {
    private float account_id;
    private String email;
    private String password;
    private String accountType;
    public Post(String email, String password, String accountType) {
        this.email = email;
        this.password = password;
        this.accountType = accountType;
    }

    private float isActive;
    private String created_at;
    private String updated_at;



    // Getter Methods

    public float getAccount_id() {
        return account_id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getAccountType() {
        return accountType;
    }

    public float getIsActive() {
        return isActive;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }
}
