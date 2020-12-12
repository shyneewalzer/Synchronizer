package com.example.mobileapplicationproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class UserTravelForm extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_travel_form);

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_menu);
        bottomNavigationView.setSelectedItemId(R.id.history);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.history:
                        return true;
                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext()
                                ,UserProfileViewForm.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.location:
                        startActivity(new Intent(getApplicationContext()
                                ,UserLocationForm.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext()
                                ,MainMenuForm.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.logout:
                        startActivity(new Intent(getApplicationContext()
                                ,MainForm.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }
}