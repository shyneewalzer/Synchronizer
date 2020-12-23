package com.example.mobileapplicationproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobileapplicationproject.DataController.ConnectionController;
import com.example.mobileapplicationproject.DataController.DataHolder;
import com.example.mobileapplicationproject.DataController.DataProcessor;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserDriverProfile extends AppCompatActivity {

    CircleImageView circleImageView;
    Button imagebutton, updateProfileButton;
    Uri imageuri,tempuri;
    TextView accttab;

    ConnectionController cc = new ConnectionController();
    DataHolder dh = new DataHolder();
    DataProcessor dp = new DataProcessor();

    Date birthdate;
    Calendar datenow = Calendar.getInstance();
    Calendar c = Calendar.getInstance();
    int age;

    LinearLayout profileviewer;
    ProgressBar pbar;

    InputStream imageStream;
    String encodedImage;
    byte[] decodedString;
    Bitmap decodedByte;

    BottomNavigationView bottomNavigationView;

    private static final String FILE_NAME = "example.txt";
    private TextInputEditText FirstName, Middle, LastName, Age, Contact, House, Barangay, City;
    APIInterface apiInterface;
    public static final int IMAGE_CODE = 1;
    String firstName, middle, lastName, contact, house, barangay, city;
    StringBuilder sb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_driver_profile);
        sb = new StringBuilder();
        apiInterface = APIClient.getClient().create(APIInterface.class);
        FirstName = findViewById(R.id.firstName);
        LastName = findViewById(R.id.lastName);
        Middle = findViewById(R.id.middle);
        Age = findViewById(R.id.age);
        Contact = findViewById(R.id.contact);
        House = findViewById(R.id.house);
        Barangay = findViewById(R.id.barangay);
        City = findViewById(R.id.city);
//        getInfo();
        circleImageView = findViewById(R.id.user_image);
        imagebutton = findViewById(R.id.user_image_button);
        updateProfileButton = findViewById(R.id.updateProfileButton);
        profileviewer = findViewById(R.id.profileview);
        pbar = findViewById(R.id.pbar);
        accttab = findViewById(R.id.account_user_tab);
        bottomNavigationView = findViewById(R.id.nav_menu_driver);
        bottomNavigationView.setSelectedItemId(R.id.profile_driver);

        Dbread dbread = new Dbread();
        dbread.execute();

        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(updateProfileButton.getText().toString().equals("EDIT"))
                {
                    FirstName.setEnabled(true);
                    LastName.setEnabled(true);
                    Middle.setEnabled(true);
                    Age.setEnabled(true);
                    Contact.setEnabled(true);
                    House.setEnabled(true);
                    Barangay.setEnabled(true);
                    City.setEnabled(true);

                    Age.setText(birthdate.toString());
                    updateProfileButton.setText("SAVE");
                }
                else if(updateProfileButton.getText().toString().equals("SAVE"))
                {
                    Dbupdate dbupdate = new Dbupdate();
                    dbupdate.execute();
                }
            }
        });
        imagebutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                openimageform();
            }
        });

        accttab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent myIntent = new Intent(UserDriverProfile.this, AccountsForm.class);
//                startActivity(myIntent);
//                finish();
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.home_driver:
                        startActivity(new Intent(getApplicationContext()
                                ,UserDriverDashboard.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.profile_driver:
                        return true;
                    case R.id.location_driver:
                        startActivity(new Intent(getApplicationContext()
                                , SetLocation.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.history_driver:
                        startActivity(new Intent(getApplicationContext()
                                ,TravelHistory.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.group_driver:
                        startActivity(new Intent(getApplicationContext()
                                ,SetLocationGroup.class));
                        overridePendingTransition(0,0);

                        return true;
                }
                return false;
            }
        });

    }


    private class Dbread extends AsyncTask<String, String, String>
    {

        String msger;

        @Override
        protected String doInBackground(String... strings) {

            try{
                Connection con=cc.CONN();
                if(con==null)
                {
                    msger="Please Check your Internet Connection";
                }
                else
                {

                    ResultSet rs=con.createStatement().executeQuery("select * from user_profile where profile_owner = '"+ dh.getUserid() +"' ");

                    while(rs.next()) {

                        firstName = rs.getString(2);
                        lastName = rs.getString(3);
                        middle = rs.getString(4);
                        birthdate = rs.getDate(5);

                        c.setTime(birthdate);
                        age = datenow.get(Calendar.YEAR) - c.get(Calendar.YEAR);
                        contact = rs.getString(6);
                        encodedImage = rs.getString(7);
                    }
                    rs.close();
                    con.close();
                }
            }
            catch (Exception ex){
                msger="Exception" + ex;
            }
            return msger;


        }

        @Override
        protected void onPreExecute() {
            profileviewer.setVisibility(View.GONE);
            pbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String a){

            Dbreadaddress dbreadaddress = new Dbreadaddress();
            dbreadaddress.execute();
        }
    }
    private class Dbreadaddress extends AsyncTask<String, String, String>
    {

        String msger;

        @Override
        protected String doInBackground(String... strings) {

            try{
                Connection con=cc.CONN();
                if(con==null)
                {
                    msger="Please Check your Internet Connection";
                }
                else
                {

                    ResultSet rsaddress=con.createStatement().executeQuery("select * from address_table where address_owner = '"+ dh.getUserid() +"' ");
                    while(rsaddress.next())
                    {
                        house = rsaddress.getString(2);
                        barangay = rsaddress.getString(3);
                        city = rsaddress.getString(4);
                    }

                    rsaddress.close();
                    con.close();
                }
            }
            catch (Exception ex){
                msger="Exception" + ex;
            }
            return msger;


        }

        @Override
        protected void onPostExecute(String a){

            FirstName.setText(firstName);
            LastName.setText(lastName);
            Middle.setText(middle);
            Age.setText(age+"");
            Contact.setText(contact);
            House.setText(house);
            Barangay.setText(barangay);
            City.setText(city);

            if(encodedImage!=null && !encodedImage.isEmpty())
            {
                dp.toasterlong(getApplicationContext(), "empty");
                decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
                decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                circleImageView.setImageBitmap(decodedByte);
            }


            profileviewer.setVisibility(View.VISIBLE);
            pbar.setVisibility(View.GONE);

        }
    }

    private class Dbupdate extends AsyncTask<String, String, String>
    {

        String msger;
        Boolean isSuccess=false;

        @Override
        protected String doInBackground(String... strings) {

            try{
                Connection con=cc.CONN();
                if(con==null)
                {
                    msger="Please Check your Internet Connection";
                }
                else
                {

                    con.createStatement().executeUpdate("UPDATE user_profile SET firstname = '" + FirstName.getText() + "', lastname = '" + LastName.getText() + "', middlename = '" + Middle.getText() + "', birthday = '" + Age.getText() + "', contactnumber = '" + Contact.getText() + "', image='"+ encodedImage +"' where profile_owner='" + dh.getUserid() + "' ");

                    con.createStatement().executeUpdate("UPDATE address_table SET house_lot_number = '" + House.getText() + "', barangay = '" + Barangay.getText() + "', city = '" + City.getText() + "'  where address_owner='" + dh.getUserid() + "' ");

                    isSuccess=true;
                    con.close();
                }
            }
            catch (Exception ex){
                msger="Exception" + ex;
            }
            return msger;


        }

        @SuppressLint("WrongThread")
        @Override
        protected void onPreExecute() {
            profileviewer.setVisibility(View.GONE);
            pbar.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(String a){

            if(isSuccess==true)
            {
                recreate();
                Toast.makeText(getApplicationContext(), "Profile Updated", Toast.LENGTH_LONG);
            }
            else
            {
                Toast.makeText(getApplicationContext(),msger,Toast.LENGTH_LONG);
            }
            profileviewer.setVisibility(View.VISIBLE);
            pbar.setVisibility(View.GONE);

        }
    }

    private void openimageform() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 100);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK &&
                data != null && data.getData() != null) {

            imageuri=data.getData();
            circleImageView.setImageURI(imageuri);

            try {
                imageStream = getContentResolver().openInputStream(imageuri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            encodedImage = encodeImage(selectedImage);

        }
    }

    private String encodeImage(Bitmap bm)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encImage;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int optid=item.getItemId();

        if(optid==R.id.logout)
        {
            Intent startIntent=new Intent(UserDriverProfile.this, Login.class);
            startActivity(startIntent);
            finish();
        }

        return true;

    }

}


