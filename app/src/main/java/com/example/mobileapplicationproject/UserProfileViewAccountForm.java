package com.example.mobileapplicationproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class UserProfileViewAccountForm extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_view_account_form);
    }

    public void profile_text(View view) {
        Intent intent = new Intent(this,UserProfileViewForm.class);
        startActivity(intent);
    }

    public void edit_user_account(View view) {
        Intent intent = new Intent(this,UserProfileFormAccount.class);
        startActivity(intent);
    }
}