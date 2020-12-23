package com.example.mobileapplicationproject;

import android.content.DialogInterface;
import android.content.Intent;
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
import com.example.mobileapplicationproject.model.GetProfile;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.StringTokenizer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainMenuFormEmployee extends AppCompatActivity {
    private static final String FILE_NAME = "example.txt";
    private TextView txtFirstName, txtLastName, txtPosition;
    APIInterface apiInterface;

    ConnectionController cc = new ConnectionController();
    DataHolder dh = new DataHolder();
    DataProcessor dp = new DataProcessor();

    String firstName, lastName, qrscan;
    int position;
    TableLayout emplo;
    ProgressBar pbar;
    String empid, work;

    Button btnscan;
    StringTokenizer tokenizer;
    ArrayList<String> gethoseid;
    ArrayList<String> userid;

    SimpleDateFormat timeformatter;
    SimpleDateFormat dateformatter;
    Timestamp timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        apiInterface = APIClient.getClient().create(APIInterface.class);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu_form_employee);
        txtFirstName = findViewById(R.id.firstName);
        txtLastName = findViewById(R.id.lastName);
        txtPosition = findViewById(R.id.position_menu_emplo);
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_menu_emplo);
        bottomNavigationView.setSelectedItemId(R.id.home_emplo);
        btnscan = findViewById(R.id.btn_escan);
        emplo = findViewById(R.id.table_menu_emplo);
        pbar = findViewById(R.id.pbar);
        gethoseid = new ArrayList<>();
        userid = new ArrayList<>();

        timeformatter = new SimpleDateFormat("HH:mm");
        dateformatter = new SimpleDateFormat("yyyy-MM-dd");

        Dbread dbread = new Dbread();
        dbread.execute();

        btnscan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                scanCode();

            }
        });


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.home_emplo:
                        return true;
                    case R.id.profile_emplo:
                        startActivity(new Intent(getApplicationContext()
                                ,EmployeeeProfileForm.class));
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

                    ResultSet rs=con.createStatement().executeQuery("select * from employee_profile where profile_owner = '"+ dh.getUserid() +"' ");

                    while(rs.next()) {

                        firstName = rs.getString(2);
                        lastName = rs.getString(3);
//                        position = rs.getInt(7);

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

            pbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String a){

            txtFirstName.setText(firstName);
            txtLastName.setText(lastName);
            txtPosition.setText("Employee");


            pbar.setVisibility(View.INVISIBLE);
            emplo.setVisibility(View.VISIBLE);

        }
    }

    private void scanCode(){

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanning Code");
        integrator.initiateScan();

    }

    private class Dbstart extends AsyncTask<String, String, String>
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
                    if(rs.isBeforeFirst())
                    {
                        isSuccess=true;
                        while (rs.next())
                        {
                            empid = rs.getString(1);
                            work = rs.getString(10);
                        }
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

            timestamp = new Timestamp(System.currentTimeMillis());
            tokenizer = new StringTokenizer(qrscan, ",");
            while (tokenizer.hasMoreTokens())
            {
                gethoseid.add(tokenizer.nextToken());
            }

            pbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String a){

            Dbscan dbscan = new Dbscan();
            dbscan.execute();

        }
    }

    private class Dbscan extends AsyncTask<String, String, String>
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
                    for(int x = 0; x<gethoseid.size();x++)
                    {
                        ResultSet rs=con.createStatement().executeQuery("select * from user_profile where profile_owner = '"+ gethoseid.get(x) +"' ");

                            isSuccess=true;
                            while (rs.next())
                            {
                                userid.add(rs.getString(1));
                            }

                        rs.close();

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

            pbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String a){

            Dbinsertscan dbinsertscan = new Dbinsertscan();
            dbinsertscan.execute();

        }
    }

    private class Dbinsertscan extends AsyncTask<String, String, String>
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
                    for(int x = 0; x<gethoseid.size();x++)
                    {
                        con.createStatement().executeUpdate("INSERT into employee_scanned (employee_id, est_id, users_id, time_entered, date_entered) VALUES('"+ empid +"', '"+ work +"', '"+ userid.get(x) +"', '"+ timeformatter.format(timestamp) +"', '"+ dateformatter.format(timestamp) +"')");

                        isSuccess = true;
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

            pbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String a){

            Dbinsertest dbinsertest = new Dbinsertest();
            dbinsertest.execute();
        }
    }

    private class Dbinsertest extends AsyncTask<String, String, String>
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
                    for(int x = 0; x<gethoseid.size();x++)
                    {
                        con.createStatement().executeUpdate("INSERT into est_companions (est_id, users_id, parent_id, time_created, date_created) VALUES('"+ work +"', '"+ userid.get(x) +"', '"+ userid.get(0) +"', '"+ timeformatter.format(timestamp) +"', '"+ dateformatter.format(timestamp) +"')");
                        isSuccess = true;
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

            pbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String a){
//            Intent myIntent = new Intent(SetLocation.this, UserDriverDashboard.class);
//            startActivity(myIntent);

            if(isSuccess==true)
            {
                dp.toasterlong(getApplicationContext(),userid+"");
            }
            else if(isSuccess==false)
            {
                dp.toasterlong(getApplicationContext(),userid+" fail");
            }
            pbar.setVisibility(View.GONE);
        }
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
                        Dbstart dbstart = new Dbstart();
                        dbstart.execute();
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
            Intent startIntent=new Intent(MainMenuFormEmployee.this, Login.class);
            startActivity(startIntent);
            finish();
        }

        return true;

    }

}
