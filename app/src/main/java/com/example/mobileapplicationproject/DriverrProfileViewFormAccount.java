package com.example.mobileapplicationproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class DriverrProfileViewFormAccount extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driverr_profile_view_form_account);
    }

    public void profile_text_driver(View view) {
        Intent intent = new Intent(this,DriverrProfileViewForm.class);
        startActivity(intent);
    }

    public void update_account_driver(View view) {
        Intent intent = new Intent(this,DriverProfileFormAccount.class);
        startActivity(intent);
    }
}