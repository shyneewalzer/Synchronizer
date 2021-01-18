package com.example.mobileapplicationproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mobileapplicationproject.DataController.ConnectionController;
import com.example.mobileapplicationproject.DataController.DataHolder;
import com.example.mobileapplicationproject.DataController.DataProcessor;
import com.example.mobileapplicationproject.model.GetProfile;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmployeeeProfileForm extends AppCompatActivity {

    CircleImageView circleImageView;
    Button imagebutton;
    Uri imageuri;
    public static final int IMAGE_CODE = 1;

    private static final String FILE_NAME = "example.txt";
    private TextInputEditText FirstName, Middle, LastName, Age, Contact, House, Barangay, City, Establishment, Eposition;
    APIInterface apiInterface;

    ConnectionController cc = new ConnectionController();
    DataHolder dh = new DataHolder();
    DataProcessor dp = new DataProcessor();

    int userid;
    ProgressBar pbar;
    LinearLayout profileviewer;
    String firstName, middle, lastName, contact, house, barangay, city, establishment,position;
    int age,condition,est;
    Date birthdate;
    Calendar datenow = Calendar.getInstance();
    Calendar c = Calendar.getInstance();
    Button edit;

    InputStream imageStream;
    String encodedImage;
    byte[] decodedString;
    Bitmap decodedByte;
    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.employeee_profile_form);

        circleImageView = findViewById(R.id.emplo_image);
        imagebutton = findViewById(R.id.emplo_image_button);

        pbar = findViewById(R.id.pbar);
        profileviewer = findViewById(R.id.profileviewer);
        edit = findViewById(R.id.update_eprofile_button);
        FirstName = findViewById(R.id.efirstname);
        LastName = findViewById(R.id.elastname);
        Middle = findViewById(R.id.emiddle);
        Age = findViewById(R.id.eage);
        Contact = findViewById(R.id.econtact);
        House = findViewById(R.id.ehouse);
        Barangay = findViewById(R.id.ebarangay);
        City = findViewById(R.id.ecity);
        Establishment = findViewById(R.id.establishment);
        Eposition = findViewById(R.id.eposition);
        bottomNavigationView = findViewById(R.id.nav_menu_emplo);


        imagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openimageform();
            }
        });

        Dbread dbread = new Dbread();
        dbread.execute();

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(edit.getText().toString().equals("EDIT"))
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
                    edit.setText("SAVE");
                }
                else if(edit.getText().toString().equals("SAVE"))
                {
                    Dbupdate dbupdate = new Dbupdate();
                    dbupdate.execute();
                }
            }
        });


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
                    con.createStatement().executeUpdate("UPDATE employee_profile SET firstname = '"+ FirstName.getText() +"', lastname = '"+ LastName.getText() +"', middlename = '"+ Middle.getText() +"', birthday = '"+ Age.getText() +"', contactnumber = '"+ Contact.getText() +"', image = '"+ encodedImage +"' where profile_owner='"+ dh.getUserid() +"' ");
                    con.createStatement().executeUpdate("UPDATE employee_address SET house_lot_number = '"+ House.getText() +"', barangay = '"+ Barangay.getText() +"', city = '"+ City.getText() +"'  where address_id='"+ condition +"' ");
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



    private class Dbread extends AsyncTask<String, String, String>
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

                    ResultSet rs=con.createStatement().executeQuery("select * from employee_profile where profile_owner = '"+ dh.getUserid() +"' ");

                    while(rs.next())
                    {
                        condition = rs.getInt(1);
                        firstName = rs.getString(2);
                        lastName = rs.getString(3);
                        middle = rs.getString(4);
                        birthdate = rs.getDate(5);

                        c.setTime(birthdate);
                        age = datenow.get(Calendar.YEAR) - c.get(Calendar.YEAR);
                        contact = rs.getString(6);
                        position = rs.getString(7);
                        est = rs.getInt(10);
                        encodedImage = rs.getString(11);
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
        protected void onPostExecute(String a){

            Dbreadaddress dbreadaddress = new Dbreadaddress();
            dbreadaddress.execute();
        }
    }

    private class Dbreadaddress extends AsyncTask<String, String, String>
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
                    ResultSet rsaddress=con.createStatement().executeQuery("select * from employee_address where address_owner = '"+ condition +"' ");
                    while(rsaddress.next())
                    {

                        house = rsaddress.getString(2);
                        barangay = rsaddress.getString(3);
                        city = rsaddress.getString(4);

                    }
                    rsaddress.close();
                }
            }
            catch (Exception ex){
                msger="Exception" + ex;
            }
            return msger;


        }
        @Override
        protected void onPostExecute(String a){

            Dbreadest dbreadest = new Dbreadest();
            dbreadest.execute();

        }
    }

    private class Dbreadest extends AsyncTask<String, String, String>
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
                    ResultSet rsest=con.createStatement().executeQuery("select * from establishments where establishment_id = '"+ est +"' ");
                    while(rsest.next())
                    {
                        establishment = rsest.getString(2);

                    }
                    rsest.close();

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
            Establishment.setText(establishment);
            Eposition.setText(position);

            if(encodedImage!=null && !encodedImage.isEmpty())
            {
                dp.toasterlong(getApplicationContext(), "empty");
                decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
                decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                circleImageView.setImageBitmap(decodedByte);
            }
            profileviewer.setVisibility(View.VISIBLE);
            pbar.setVisibility(View.GONE);

            dp.toasterlong(getApplicationContext(), establishment+"");




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
            Intent startIntent=new Intent(EmployeeeProfileForm.this, Login.class);
            startActivity(startIntent);
            finish();
        }

        return true;

    }

}