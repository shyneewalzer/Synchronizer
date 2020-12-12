package com.example.mobileapplicationproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import de.hdodenhof.circleimageview.CircleImageView;

public class EmployeeProfileFormAccount extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_profile_form_account);
    }

    public void update_accout_emplo(View view) {
        Intent intent = new Intent(this, EmployeeProfileViewFormAccount.class);
        startActivity(intent);
    }
}