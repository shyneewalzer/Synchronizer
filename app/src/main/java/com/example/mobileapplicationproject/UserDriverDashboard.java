package com.example.mobileapplicationproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mobileapplicationproject.DataController.ConnectionController;
import com.example.mobileapplicationproject.DataController.DataHolder;
import com.example.mobileapplicationproject.DataController.DataProcessor;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class UserDriverDashboard extends AppCompatActivity implements View.OnClickListener {
    private static final String FILE_NAME = "example.txt";
    private TextView txtfirstname, txtlastname, txtposition;
    APIInterface apiInterface;
    androidx.constraintlayout.utils.widget.ImageFilterView imageView;
    Button camera;
    TableLayout userdriver;
    ProgressBar pbar;

    String firstname, lastname;
    String qrcode, qrscan;
    int position;

    ConnectionController cc = new ConnectionController();
    DataHolder dh = new DataHolder();
    DataProcessor dp = new DataProcessor();

    SimpleDateFormat timeformatter;
    SimpleDateFormat dateformatter;
    Timestamp timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        apiInterface = APIClient.getClient().create(APIInterface.class);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_driver_dashboard);
        txtfirstname = findViewById(R.id.firstName);
        txtlastname = findViewById(R.id.lastName);
        imageView = findViewById(R.id.imageview);
        camera = findViewById(R.id.camera_driver_menu);
        txtposition = findViewById(R.id.position_menu_driver);

        userdriver = findViewById(R.id.table_menu_driver);
        pbar = findViewById(R.id.pbar);

        timeformatter = new SimpleDateFormat("HH:mm");
        dateformatter = new SimpleDateFormat("yyyy-MM-dd");

        if(dh.getType().equals("User"))
        {
            camera.setVisibility(View.INVISIBLE);
        }

        camera.setOnClickListener(this);

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_menu_driver);
        bottomNavigationView.setSelectedItemId(R.id.home_driver);
//        getName();
        Dbread dbread = new Dbread();
        dbread.execute();


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.home_driver:
                        return true;
                    case R.id.profile_driver:
                        startActivity(new Intent(getApplicationContext()
                                ,UserDriverProfile.class));
                        overridePendingTransition(0,0);
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

                    ResultSet rs=con.createStatement().executeQuery("select * from user_profile where user_id = '"+ dh.getUserid() +"' ");

                    while(rs.next()) {

                        firstname = rs.getString(2);
                        lastname = rs.getString(3);
                        position = rs.getInt(9);

                    }
                    rs.close();

                    ResultSet rsqr=con.createStatement().executeQuery("select * from travel_history where user_id = '"+ dh.getUserid() +"' ");
                    while (rsqr.next())
                    {
                        qrcode = rsqr.getString(1);
                    }
                    rsqr.close();
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

            pbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String a){

            txtfirstname.setText(firstname);
            txtlastname.setText(lastname);

            if(position==1)
            {
                txtposition.setText("Driver");
            }
            else if(position==0)
            {
                txtposition.setText("User");
            }

            createQR(qrcode);


            pbar.setVisibility(View.INVISIBLE);
            userdriver.setVisibility(View.VISIBLE);

        }
    }

    private class Dbqr extends AsyncTask<String, String, String>
    {

        String msger;
        String passid,timee,datee;
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
                    ResultSet rs=con.createStatement().executeQuery("select * from travel_history where travel_id = '"+ qrscan +"' ");
                    if(rs.isBeforeFirst())
                    {
                        isSuccess=true;
                        while (rs.next())
                        {
                            passid = rs.getString(3);
                            timee = rs.getString(5);
                            datee = rs.getString(6);
                        }
                    }

                    rs.close();

                    con.createStatement().executeUpdate("INSERT into passengers_table (driver_id, travel_id, users_id, time_boarded, date_boarded) VALUES('"+ dh.getUserid() +"', '"+ qrscan +"', '"+ passid +"', '"+ timee +"', '"+ datee +"')");

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

            timestamp = new Timestamp(System.currentTimeMillis());
            pbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String a){
//            Intent myIntent = new Intent(SetLocation.this, UserDriverDashboard.class);
//            startActivity(myIntent);

            if(isSuccess==true)
            {
                dp.toasterlong(getApplicationContext(),"Success");
            }
            else if(isSuccess==false)
            {
                dp.toasterlong(getApplicationContext(),"Fail");
            }

            pbar.setVisibility(View.GONE);
        }
    }



    private void createQR(String modelName) {
//        Toast.makeText(getApplicationContext(),  "a" +modelName, Toast.LENGTH_LONG).show();

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

        try {
            BitMatrix bitMatrix = multiFormatWriter.encode( modelName, BarcodeFormat.QR_CODE,500,500);
            BarcodeEncoder barcodeEncoder =  new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            imageView.setImageBitmap(bitmap);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        scanCode();
    }

    private void scanCode(){

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanning Code");
        integrator.initiateScan();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null){
            if(result.getContents() != null){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                sendData(Integer.parseInt(result.getContents()));
                qrscan = result.getContents()+"";


                builder.setMessage("Scanned Successfully");
                builder.setTitle("Scanning Result");
                builder.setPositiveButton("Scan Again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        scanCode();
                    }
                }).setNegativeButton("Finish", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Dbqr dbqr = new Dbqr();
                        dbqr.execute();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else {
                Toast.makeText(this, "No Result", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

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
            Intent startIntent=new Intent(UserDriverDashboard.this, Login.class);
            startActivity(startIntent);
            finish();
        }

        return true;

    }
}
