package com.example.mobileapplicationproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class UserProfileFormAccount extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_form_account);
    }

    public void profile_text(View view) {
        Intent intent = new Intent(this,UserProfileForm.class);
        startActivity(intent);
    }

    public void user_profile_update(View view) {
        Intent intent = new Intent(this,UserProfileViewAccountForm.class);
        startActivity(intent);
    }
}