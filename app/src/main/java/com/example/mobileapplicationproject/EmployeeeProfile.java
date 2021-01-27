package com.example.mobileapplicationproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mobileapplicationproject.DataController.ConnectionController;
import com.example.mobileapplicationproject.DataController.DataHolder;
import com.example.mobileapplicationproject.DataController.DataProcessor;
import com.example.mobileapplicationproject.DataController.DebugMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class EmployeeeProfile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener{

    ConnectionController cc = new ConnectionController();
    DataHolder dh = new DataHolder();
    DataProcessor dp = new DataProcessor();
    DebugMode dm = new DebugMode();

    Uri imageuri;
    int age;

    Calendar datenow = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener dateSetListener;

    InputStream imageStream;

    ArrayAdapter<String> adapter;

    ////////////////UI ELEMENTS/////////////////

    Toolbar toolbar;

    LinearLayout lo_eprofileviewer;
    ProgressBar pbar;

    CircleImageView img_eprof;

    TextInputEditText edt_Fname, edt_Lname, edt_Mname, edt_Contact, edt_age, edt_house, edt_city;
    AutoCompleteTextView edt_brgy;
    Button btn_eUpload, btn_eUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.employeee_profile);

        lo_eprofileviewer = findViewById(R.id.lo_eprofileviewer);
        pbar = findViewById(R.id.pbar);
        img_eprof = findViewById(R.id.img_eprof);
        edt_Fname = findViewById(R.id.efirstname);
        edt_Lname = findViewById(R.id.elastname);
        edt_Mname = findViewById(R.id.emiddle);
        edt_Contact = findViewById(R.id.econtact);
        edt_age = findViewById(R.id.eage);
        edt_house = findViewById(R.id.ehouse);
        edt_brgy = findViewById(R.id.ebarangay);
        edt_city = findViewById(R.id.ecity);
        btn_eUpload = findViewById(R.id.btn_eUpload);
        btn_eUpdate = findViewById(R.id.btn_eUpdate);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_backarrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Employee Profile");
        img_eprof.setImageBitmap(dp.createImage(dh.getpImage()));
        if(dh.getpImage()==null)
        {
            img_eprof.setImageResource(R.drawable.ic_person);
        }

        btn_eUpdate.setOnClickListener(this);
        btn_eUpload.setOnClickListener(this);
        edt_age.setOnClickListener(this);

        adapter = new ArrayAdapter<String>(EmployeeeProfile.this, android.R.layout.simple_list_item_1, dh.getListBrgy());
        edt_brgy.setAdapter(adapter);

        edt_brgy.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                edt_brgy.setText(adapter.getItem(position));
            }
        });

        dataSet();

    }

    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.btn_eUpdate)
        {
            if(btn_eUpdate.getText().toString().equals("EDIT"))
            {
                edt_Fname.setEnabled(true);
                edt_Lname.setEnabled(true);
                edt_Mname.setEnabled(true);
                edt_age.setEnabled(true);
                edt_Contact.setEnabled(true);
                edt_house.setEnabled(true);
                edt_brgy.setEnabled(true);
                edt_city.setEnabled(true);

                edt_age.setText(dh.getpBday()+"");
                btn_eUpdate.setText("SAVE");
                btn_eUpload.setVisibility(View.VISIBLE);
            }
            else if(btn_eUpdate.getText().toString().equals("SAVE"))
            {
                Dbupdate dbupdate = new Dbupdate();
                dbupdate.execute();

                btn_eUpdate.setText("EDIT");
                btn_eUpload.setVisibility(View.INVISIBLE);
            }
        }
        else if(v.getId()==R.id.btn_eUpload)
        {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 100);
        }
        else if(v.getId()==R.id.eage)
        {
            int cal_yr = dh.getTempDate().get(Calendar.YEAR);
            int cal_mo = dh.getTempDate().get(Calendar.MONTH);
            int cal_dy = dh.getTempDate().get(Calendar.DAY_OF_MONTH);
            dm.displayMessage(getApplicationContext(),cal_yr + "-" + cal_mo + "-" + cal_dy);

            DatePickerDialog datepicker = new DatePickerDialog(EmployeeeProfile.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    month = month + 1;
                    edt_age.setText(year + "-" + month + "-" + dayOfMonth);
                }
            },cal_yr,cal_mo,cal_dy);
            datepicker.show();
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

                    con.createStatement().executeUpdate("UPDATE user_profile SET firstname = '" + edt_Fname.getText() + "', lastname = '" + edt_Lname.getText() + "', middlename = '" + edt_Mname.getText() + "', birthday = '" + edt_age.getText() + "', contactnumber = '" + edt_Contact.getText() + "', image='"+ dh.getpImage() +"' where user_id='" + dh.getUserid() + "' ");
//                    con.createStatement().executeUpdate("UPDATE address_table SET house_lot_number = '" + edt_house.getText() + "', barangay = '" + edt_brgy.getText() + "', city = '" + edt_city.getText() + "'  where user_id='" + dh.getUserid() + "' ");

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
            lo_eprofileviewer.setVisibility(View.GONE);
            pbar.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(String a){

            if(isSuccess==true)
            {
                dh.setProfile(edt_Fname.getText()+"", edt_Lname.getText()+"", edt_Mname.getText()+"", dp.stringToDate(edt_age.getText()+""), edt_Contact.getText()+"", dh.getpImage());
                dh.setAddress(edt_house.getText()+"", edt_brgy.getText()+"", edt_city.getText()+"");
                dataSet();
                Toast.makeText(getApplicationContext(), "Profile Updated", Toast.LENGTH_LONG);

                dm.displayMessage(getApplicationContext(), ""+dp.stringToDate(dp.stringToDate(edt_age+"")+""));
            }
            else
            {
                Toast.makeText(getApplicationContext(),msger,Toast.LENGTH_LONG);
                dm.displayMessage(getApplicationContext(), dh.getpBday()+"");
            }
            lo_eprofileviewer.setVisibility(View.VISIBLE);
            pbar.setVisibility(View.GONE);

        }
    }

    private void dataSet()
    {
        btn_eUpdate.setText("EDIT");
        edt_Fname.setEnabled(false);
        edt_Lname.setEnabled(false);
        edt_Mname.setEnabled(false);
        edt_age.setEnabled(false);
        edt_Contact.setEnabled(false);
        edt_house.setEnabled(false);
        edt_brgy.setEnabled(false);
        edt_city.setEnabled(false);

        edt_Fname.setText(dh.getpFName());
        edt_Lname.setText(dh.getpLName());
        edt_Mname.setText(dh.getpMName());

        age = datenow.get(Calendar.YEAR) - dh.getTempDate().get(Calendar.YEAR);
        edt_age.setText(age+"");

        edt_Contact.setText(dh.getpContact());
        edt_house.setText(dh.getHouse());
        edt_brgy.setText(dh.getBrgy());
        edt_city.setText(dh.getCity());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK &&
                data != null && data.getData() != null) {

            imageuri=data.getData();
            img_eprof.setImageURI(imageuri);

            try {
                imageStream = getContentResolver().openInputStream(imageuri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            dh.setpImage(dp.encodeImage(selectedImage));

        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int optid=item.getItemId();

        onBackPressed();

        return true;

    }

}