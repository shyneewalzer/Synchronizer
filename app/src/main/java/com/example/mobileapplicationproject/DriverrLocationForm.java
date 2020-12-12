package com.example.mobileapplicationproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DriverrLocationForm extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driverr_location_form);

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_menu_driver);
        bottomNavigationView.setSelectedItemId(R.id.location_driver);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.location_driver:
                        return true;
                    case R.id.profile_driver:
                        startActivity(new Intent(getApplicationContext()
                                , DriverrProfileViewForm.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.home_driver:
                        startActivity(new Intent(getApplicationContext()
                                , MainMenuFormDriverr.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.history_driver:
                        startActivity(new Intent(getApplicationContext()
                                , DriverrTravelForm.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.logout_driver:
                        startActivity(new Intent(getApplicationContext()
                                ,MainForm.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }

    public void Set(View view) {
        Intent intent = new Intent(this, MainMenuForm.class);
        startActivity(intent);
    }
}