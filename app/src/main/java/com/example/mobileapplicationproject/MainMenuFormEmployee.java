package com.example.mobileapplicationproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mobileapplicationproject.model.GetProfile;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainMenuFormEmployee extends AppCompatActivity {
    private static final String FILE_NAME = "example.txt";
    private TextView FirstName, LastName;
    APIInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        apiInterface = APIClient.getClient().create(APIInterface.class);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu_form_employee);
        FirstName = findViewById(R.id.firstName);
        LastName = findViewById(R.id.lastName);
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_menu_emplo);
        bottomNavigationView.setSelectedItemId(R.id.home_emplo);
        getName();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.home_emplo:
                        return true;
                    case R.id.profile_emplo:
                        startActivity(new Intent(getApplicationContext()
                                ,EmployeeeProfileViewForm.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.logout_emplo:
                        startActivity(new Intent(getApplicationContext()
                                ,MainForm.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }


    private void getName() {
        FileInputStream fis = null;
        try {
            fis = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;
            while ((text = br.readLine()) != null) {
                sb.append(text);
            }

            Call<GetProfile> getProfileCall =  apiInterface.getProfile(sb.toString());
            getProfileCall.enqueue(new Callback<GetProfile>() {
                @Override
                public void onResponse(Call<GetProfile> call, Response<GetProfile> response) {
                    if (!response.isSuccessful()) {

                        APIError apiError = ErrorUtils.parseError(response);
                        Toast.makeText(getApplicationContext(), "error "+ apiError.getMessage(), Toast.LENGTH_LONG).show();

                        return;
                    }
                    GetProfile getProfile = response.body();
                    FirstName.setText(getProfile.getFirstname());
                    LastName.setText(getProfile.getLastname());

                }

                @Override
                public void onFailure(Call<GetProfile> call, Throwable t) {
                    Toast.makeText(getApplicationContext(),  "a" +t.getMessage(), Toast.LENGTH_LONG).show();

                }
            });

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
