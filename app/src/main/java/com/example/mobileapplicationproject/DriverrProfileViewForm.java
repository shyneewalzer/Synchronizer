package com.example.mobileapplicationproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mobileapplicationproject.model.GetProfile;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverrProfileViewForm extends AppCompatActivity {

    private static final String FILE_NAME = "example.txt";
    private TextInputEditText FirstName, Middle, LastName, Age, Contact, House, Barangay, City;
    APIInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driverr_profile_view_form);
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_menu_driver);
        bottomNavigationView.setSelectedItemId(R.id.profile_driver);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        FirstName = findViewById(R.id.firstName);
        LastName = findViewById(R.id.lastName);
        Middle = findViewById(R.id.middle);
        Age = findViewById(R.id.age);
        Contact = findViewById(R.id.contact);
        House = findViewById(R.id.house);
        Barangay = findViewById(R.id.barangay);
        City = findViewById(R.id.city);
        getInfo();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.profile_driver:
                        return true;
                    case R.id.history_driver:
                        startActivity(new Intent(getApplicationContext()
                                ,DriverrTravelForm.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.location_driver:
                        startActivity(new Intent(getApplicationContext()
                                ,DriverrLocationForm.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.home_driver:
                        startActivity(new Intent(getApplicationContext()
                                ,MainMenuFormDriverr.class));
                        overridePendingTransition(0,0);
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

    private void getInfo() {
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
                    Middle.setText(getProfile.getMiddlename());
                    Age.setText(getProfile.getAge());
                    Contact.setText(getProfile.getContactnumber());
                    House.setText(getProfile.getHouseLotNumber());
                    Barangay.setText(getProfile.getBarangay());
                    City.setText(getProfile.getCity());

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
    public void account_text_driver(View view) {
        Intent intent = new Intent(this,DriverrProfileViewFormAccount.class);
        startActivity(intent);
    }
    public void edit_profile_driver(View view) {
        Intent intent = new Intent(this, DriverProfileForm.class);
        startActivity(intent);
    }
}