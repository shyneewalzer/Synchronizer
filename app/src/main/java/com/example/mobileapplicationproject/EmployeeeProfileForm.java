package com.example.mobileapplicationproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import de.hdodenhof.circleimageview.CircleImageView;

public class EmployeeeProfileForm extends AppCompatActivity {

    CircleImageView circleImageView;
    Button imagebutton;
    Uri imageuri;
    public static final int IMAGE_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employeee_profile_form);

        circleImageView = findViewById(R.id.emplo_image);
        imagebutton = findViewById(R.id.emplo_image_button);

        imagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openimageform();
            }
        });
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

    public void update_profile_emplo(View view) {
        Intent intent = new Intent(this,EmployeeeProfileViewForm.class);
        startActivity(intent);
    }
}