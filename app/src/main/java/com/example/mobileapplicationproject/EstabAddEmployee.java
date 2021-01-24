package com.example.mobileapplicationproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.mobileapplicationproject.DataController.ConnectionController;
import com.example.mobileapplicationproject.DataController.DataHolder;
import com.example.mobileapplicationproject.DataController.DataProcessor;
import com.example.mobileapplicationproject.DataController.DebugMode;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class EstabAddEmployee extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener{

    ConnectionController cc = new ConnectionController();
    DataHolder dh = new DataHolder();
    DataProcessor dp = new DataProcessor();
    DebugMode dm = new DebugMode();

    Uri imageuri;
    InputStream imageStream;

    Calendar datenow = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener dateSetListener;

    /////////////////UI ELEMENTS//////////////////
    Toolbar toolbar;

    LinearLayout lo_addempviewer;
    ProgressBar pbar;

    CircleImageView img_addempprof;

    TextInputEditText edt_addempfname, edt_addemplname, edt_addempmname, edt_addempcontact, edt_addempage, edt_addemphouse, edt_addempbrgy, edt_addempcity;
    Button btn_addempupload, btn_addemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.estab_add_employee);

        lo_addempviewer = findViewById(R.id.lo_addempviewer);
        pbar = findViewById(R.id.pbar);
        img_addempprof = findViewById(R.id.img_addempprof);
        edt_addempfname = findViewById(R.id.edt_addempfname);
        edt_addemplname = findViewById(R.id.edt_addemplname);
        edt_addempmname = findViewById(R.id.edt_addempmname);
        edt_addempcontact = findViewById(R.id.edt_addempcontact);
        edt_addempage = findViewById(R.id.edt_addempage);
        edt_addemphouse = findViewById(R.id.edt_addemphouse);
        edt_addempbrgy = findViewById(R.id.edt_addempbrgy);
        edt_addempcity = findViewById(R.id.edt_addempcity);
        btn_addempupload = findViewById(R.id.btn_addempupload);
        btn_addemp = findViewById(R.id.btn_addemp);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_backarrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Add Employee");

        btn_addemp.setOnClickListener(this);
        btn_addempupload.setOnClickListener(this);
        edt_addempage.setOnClickListener(this);

    }

    private class Dbinsert extends AsyncTask<String, String, String>
    {

        int activecount=0;
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
                    ResultSet rs=con.createStatement().executeQuery("select * from user_profile where account_id = '"+ dh.getEstAcctID() +"' AND isActive = '1' ");

                    while(rs.next())
                    {
                        activecount = rs.getRow();
                    }
                    rs.close();

                    if(activecount<2)
                    {
                        con.createStatement().executeUpdate("INSERT into user_profile (firstname, lastname, middlename, birthday, contactnumber, image, isActive, account_id) VALUES('"+ edt_addempfname.getText() +"', '"+ edt_addemplname.getText() +"', '"+  edt_addempmname.getText() +"', '"+ edt_addempage.getText() +"', '"+ edt_addempcontact.getText() +"', '"+ dh.getpImage() +"', '1', '"+ dh.getEstAcctID() +"')");
                        isSuccess = true;
                    }
                    else
                    {
                        isSuccess = false;
                        msger = "Only (" + 2 + ") employees can be activated at a time";
                    }



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

            lo_addempviewer.setVisibility(View.GONE);
            pbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String a){
//            Intent myIntent = new Intent(SetLocation.this, UserDriverDashboard.class);
//            startActivity(myIntent);

            lo_addempviewer.setVisibility(View.VISIBLE);
            pbar.setVisibility(View.GONE);

            if(isSuccess==true)
            {
                dp.toastershort(getApplicationContext(), "Employee Successfully Added");
                finish();
            }
            else if(isSuccess==false)
            {
                dp.toastershort(getApplicationContext(), msger);
            }

        }
    }

    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.btn_addemp)
        {
            Dbinsert dbinsert = new Dbinsert();
            dbinsert.execute();
        }
        else if(v.getId()==R.id.btn_addempupload)
        {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 100);
        }
        else if(v.getId()==R.id.edt_addempage)
        {
            int cal_yr = dh.getTempDate().get(Calendar.YEAR);
            int cal_mo = dh.getTempDate().get(Calendar.MONTH);
            int cal_dy = dh.getTempDate().get(Calendar.DAY_OF_MONTH);
            dm.displayMessage(getApplicationContext(),cal_yr + "-" + cal_mo + "-" + cal_dy);

            DatePickerDialog datepicker = new DatePickerDialog(EstabAddEmployee.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    month = month + 1;
                    edt_addempage.setText(year + "-" + month + "-" + dayOfMonth);
                }
            },cal_yr,cal_mo,cal_dy);
            datepicker.show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK &&
                data != null && data.getData() != null) {

            imageuri=data.getData();
            img_addempprof.setImageURI(imageuri);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int optid=item.getItemId();

        onBackPressed();

        return true;

    }
}