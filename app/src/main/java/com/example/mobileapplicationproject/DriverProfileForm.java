package com.example.mobileapplicationproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.service.autofill.RegexValidator;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.example.mobileapplicationproject.model.GetProfile;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.BreakIterator;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverProfileForm extends AppCompatActivity {

    CircleImageView circleImageView;
    Button imagebutton;
    Uri imageuri;
    private static final String FILE_NAME = "example.txt";
    private TextInputEditText FirstName, Middle, LastName, Age, Contact, House, Barangay, City;
    APIInterface apiInterface;
    public static final int IMAGE_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_profile_form);
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
        circleImageView = findViewById(R.id.driver_image);
        imagebutton = findViewById(R.id.driver_image_button);

        imagebutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                openimageform();
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

    private void openimageform() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_CODE && resultCode == RESULT_OK &&
                data != null && data.getData() != null) {

            imageuri=data.getData();
            circleImageView.setImageURI(imageuri);

        }
    }

    public void upddate_profile_driver(View view) {
        Intent intent = new Intent(this, DriverrProfileViewForm.class);
        startActivity(intent);
    }
}