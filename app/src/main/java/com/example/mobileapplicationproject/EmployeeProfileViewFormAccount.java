package com.example.mobileapplicationproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class EmployeeProfileViewFormAccount extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_profile_view_form_account);
    }


    public void profile_text_emplo(View view) {
        Intent intent = new Intent(this,EmployeeeProfileViewForm.class);
        startActivity(intent);
    }

    public void edit_account_emplo(View view) {
        Intent intent = new Intent(this,EmployeeProfileFormAccount.class);
        startActivity(intent);
    }
}