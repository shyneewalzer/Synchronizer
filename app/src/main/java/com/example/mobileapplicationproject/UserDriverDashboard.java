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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.mobileapplicationproject.DataController.ConnectionController;
import com.example.mobileapplicationproject.DataController.DataHolder;
import com.example.mobileapplicationproject.DataController.DataProcessor;
import com.example.mobileapplicationproject.DataController.DebugMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;

public class UserDriverDashboard extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener{

    private static final String FILE_NAME = "example.txt";
    private TextView txtfirstname, txtlastname, txtposition;
    APIInterface apiInterface;
    ImageView imageView;
    Button camera;
    LinearLayout dashboardviewer;
    ProgressBar pbar;

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    NavigationView navigationView;

    String qrcode, qrscan;
    ImageView personalqr;
    StringTokenizer tokenizer;
    ArrayList<String> travelinfo;

    ConnectionController cc = new ConnectionController();
    DataHolder dh = new DataHolder();
    DataProcessor dp = new DataProcessor();
    DebugMode dm = new DebugMode();

    SimpleDateFormat timeformatter;
    SimpleDateFormat dateformatter;
    Timestamp timestamp;

    String tester;

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

        dashboardviewer = findViewById(R.id.dashboard_viewer);
        pbar = findViewById(R.id.pbar);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("User Panel");

        drawerLayout = findViewById(R.id.drawer);
        drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open_drawer,R.string.close_drawer);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.setDrawerIndicatorEnabled(true);//hamburger icon
        drawerToggle.syncState();

        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
//        TextView draw_name = (TextView) headerView.findViewById(R.id.lbl_draw_name);
//        draw_name.setText("Dashboard");
        personalqr = headerView.findViewById(R.id.personal_qr);

        travelinfo = new ArrayList<>();

        Dbread dbread = new Dbread();
        dbread.execute();

        timeformatter = new SimpleDateFormat("HH:mm");
        dateformatter = new SimpleDateFormat("yyyy-MM-dd");

        if(dh.getType().equals("User"))
        {
            camera.setVisibility(View.INVISIBLE);
        }

        camera.setOnClickListener(this);


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

                    ResultSet rs=con.createStatement().executeQuery("select * from user_profile where account_id = '"+ dh.getUserid() +"' ");

                    while(rs.next()) {
                        dh.setProfile(rs.getString("firstname"), rs.getString("lastname"), rs.getString("middlename"), rs.getDate("birthday"),rs.getString("contactnumber"),rs.getString("image"),rs.getString("position"),rs.getInt("working_in"));
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
            dashboardviewer.setVisibility(View.GONE);
        }

        @Override
        protected void onPostExecute(String a){

            txtfirstname.setText(dh.getpFName());
            txtlastname.setText(dh.getpLName());
            txtposition.setText(dh.getType());

            imageView.setImageBitmap(dp.createQR(qrcode));
            personalqr.setImageBitmap(dp.createQR(dh.getpFName() + "," + dh.getpLName() + "," + dh.getpMName() + "," + dh.getpBday() + "," + dh.getpContact() + "," + dh.getpPosition() + "," + dh.getpEstab()));

            dm.displayMessage(getApplicationContext(), dh.getpFName() + "," + dh.getpLName() + "," + dh.getpMName() + "," + dh.getpBday() + "," + dh.getpContact() + "," + dh.getpPosition() + "," + dh.getpEstab());
            pbar.setVisibility(View.GONE);
            dashboardviewer.setVisibility(View.VISIBLE);

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
                    ResultSet rs=con.createStatement().executeQuery("select * from travel_history");
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

                    con.createStatement().executeUpdate("INSERT into travel_history (destination, driver_id, companion_id, plate_number, parent_id, time_boarded, date_boarded) VALUES('N/A', '"+ travelinfo.get(0) +"', 'companion', '"+ travelinfo.get(1) +"', '"+ dh.getUserid() +"', '"+ timeformatter.format(timestamp) +"', '"+ dateformatter.format(timestamp) +"')");

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
                travelinfo.add(tokenizer.nextToken());
            }
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.prof)
        {
//            Intent startIntent=new Intent(UserPanel.this, Profile.class);
//            startActivity(startIntent);
            finish();
        }

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

        if(optid==R.id.about)
        {
            dp.toasterlong(getApplicationContext(), "about");
        }
        else if(optid==R.id.logout)
        {
            Intent startIntent=new Intent(UserDriverDashboard.this, Login.class);
            startActivity(startIntent);
            finish();
        }
        return true;

    }
}
