package com.example.mobileapplicationproject.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class Login {
    private String email;
    private String password;

    public Login(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    @SerializedName("account_type")
    @Expose
    private String accountType;
    @SerializedName("auth")
    @Expose
    private Boolean auth;
    @SerializedName("token")
    @Expose
    private String token;

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public Boolean getAuth() {
        return auth;
    }

    public void setAuth(Boolean auth) {
        this.auth = auth;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
