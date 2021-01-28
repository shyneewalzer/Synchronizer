package com.example.mobileapplicationproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import com.google.android.material.navigation.NavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DriverDashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, ConfirmDialog.ConfirmDialogListener{

    ArrayList<String>vehiclelist;
    ArrayList<String>routelist;
    ArrayList<String>bodynumlist;

    ConnectionController cc = new ConnectionController();
    DataHolder dh = new DataHolder();
    DataProcessor dp = new DataProcessor();
    DebugMode dm = new DebugMode();

    SimpleDateFormat timeformatter;
    SimpleDateFormat dateformatter;
    SimpleDateFormat batchformatter;

    String qrscan="", batch;
    Timestamp timestamp;
    ArrayList<String> iddest;
    List<List<String>>personlists;
    ArrayList<String>personinfo;

    ///////////UI ELEMENTS//////////

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    NavigationView navigationView;
    TextView draw_name, draw_type;
    CircleImageView draw_img_user;

    LinearLayout dashboardviewer;
    ProgressBar pbar;

    Spinner spr_platenum;
    TextView txt_route, txt_bodynum, txt_addvehicle;
    Button btn_driver_scan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_dashboard);

        dashboardviewer = findViewById(R.id.dashboard_viewer);
        pbar = findViewById(R.id.pbar);

        spr_platenum = findViewById(R.id.spr_platenum);
        txt_route = findViewById(R.id.txt_route);
        btn_driver_scan = findViewById(R.id.btn_driver_scan);
        txt_bodynum = findViewById(R.id.txt_bodynum);
        txt_addvehicle = findViewById(R.id.txt_addvehicle);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Vehicle Details");

        drawerLayout = findViewById(R.id.drawer);
        drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open_drawer,R.string.close_drawer);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.setDrawerIndicatorEnabled(true);//hamburger icon
        drawerToggle.syncState();

        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        draw_name = (TextView) headerView.findViewById(R.id.lbl_draw_name);
        draw_type = (TextView) headerView.findViewById(R.id.lbl_draw_type);
        draw_img_user = headerView.findViewById(R.id.cimg_user);

        draw_name.setText(dh.getpFName() + " " + dh.getpLName());
        draw_type.setText(dh.getType());
        draw_img_user.setImageBitmap(dp.createImage(dh.getpImage()));
        if(dh.getpImage()==null)
        {
            draw_img_user.setImageResource(R.drawable.ic_person);
        }

        btn_driver_scan.setOnClickListener(this);
        txt_addvehicle.setOnClickListener(this);

        timeformatter = new SimpleDateFormat("HH:mm");
        dateformatter = new SimpleDateFormat("yyyy-MM-dd");
        batchformatter = new SimpleDateFormat("yyyyMMddHHmmss");

        Dbread dbread = new Dbread();
        dbread.execute();

        spr_platenum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(!parent.getItemAtPosition(position).equals("Select Plate Number"))
                {
                    txt_route.setText(routelist.get(position-1));
                    txt_bodynum.setText(bodynumlist.get(position-1));
                }
                else
                {
                    dp.toasterlong(getApplicationContext(), "Please Select Vehicle");
                    txt_route.setText("");
                    txt_bodynum.setText("");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                dp.toasterlong(getApplicationContext(), "Please Select Vehicle");
                txt_route.setText("");
            }
        });


    }

    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.btn_driver_scan)
        {
            if(spr_platenum.getSelectedItem().equals("Select Plate Number"))
            {
                dp.toasterlong(getApplicationContext(), "Please Select Vehicle");
            }
            else
            {
                scanCode();
            }
        }
        else if(v.getId()==R.id.txt_addvehicle)
        {
            Intent startIntent = new Intent(DriverDashboard.this, DriverAddVehicle.class);
            startActivity(startIntent);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null){
            if(result.getContents() != null){
                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
//                sendData(Integer.parseInt(result.getContents()));
                qrscan = result.getContents()+"";
                dm.displayMessage(getApplicationContext(), qrscan+"");

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

                        Dbinsert dbinsert = new Dbinsert();
                        dbinsert.execute();
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
    public void getDialogResponse(boolean dialogResponse, String purpose) {

        if(purpose.equals("logout") && dialogResponse==true)
        {
            Intent startIntent=new Intent(DriverDashboard.this, Login.class);
            startActivity(startIntent);
            finish();
        }

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

                    ResultSet rsqr=con.createStatement().executeQuery("select * from vehicles where account_id = '"+ dh.getUserid() +"' ");
                    while (rsqr.next())
                    {
                        vehiclelist.add(rsqr.getString("plate_number"));
                        routelist.add(rsqr.getString("vehicle_route"));
                        bodynumlist.add(rsqr.getString("body_number"));
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

            vehiclelist = new ArrayList<>();
            routelist = new ArrayList<>();
            bodynumlist = new ArrayList<>();

            vehiclelist.add(0,"Select Plate Number");
        }

        @Override
        protected void onPostExecute(String a){

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(DriverDashboard.this, R.layout.spinner_format, vehiclelist);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spr_platenum.setAdapter(dataAdapter);

            pbar.setVisibility(View.GONE);
            dashboardviewer.setVisibility(View.VISIBLE);

        }
    }

    private class Dbinsert extends AsyncTask<String, String, String>
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
                    if(personlists.size()>0)
                    {
                        for(int x = 0; x<personlists.size();x++)
                        {
                            con.createStatement().executeUpdate("INSERT into travel_history (batch, firstname, lastname, contact_number, destination, driver_id, plate_number, account_id, time_boarded, date_boarded) " +
                                    "VALUES('"+ batch +"', '"+ personlists.get(x).get(0) +"', '"+ personlists.get(x).get(1) +"', '"+ personlists.get(x).get(2) +"', '"+ iddest.get(1) +"', '"+ dh.getUserid() +"', '"+ spr_platenum.getSelectedItem() +"', '"+ iddest.get(0) +"',  '"+ timeformatter.format(timestamp) +"', '"+ dateformatter.format(timestamp) +"')");
                            isSuccess = true;
                        }
                    }
                    else
                    {
                        con.createStatement().executeUpdate("INSERT into travel_history (batch, destination, driver_id, plate_number, account_id, time_boarded, date_boarded) VALUES('"+ batch +"', '"+ iddest.get(1) +"', '"+  dh.getUserid() +"', '"+ spr_platenum.getSelectedItem() +"', '"+ iddest.get(0) +"',  '"+ timeformatter.format(timestamp) +"', '"+ dateformatter.format(timestamp) +"')");
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

            dashboardviewer.setVisibility(View.GONE);
            pbar.setVisibility(View.VISIBLE);

            timestamp = new Timestamp(System.currentTimeMillis());
            String temp="";

            iddest = new ArrayList<>();
            personinfo = new ArrayList<>();
            personlists = new ArrayList<List<String>>();

            iddest = new ArrayList<>(dp.splitter(qrscan, "#"));
            batch = iddest.get(0) + dh.getUserid() + batchformatter.format(timestamp);

            if(iddest.size()>2)
            {
                personinfo = dp.splitter(iddest.get(2), ",");
                for (int x = 0; x < personinfo.size(); x++) {
                    personlists.add(new ArrayList<>(dp.splitter(personinfo.get(x), "_")));
                }

                for (int a = 0; a < personlists.size(); a++) {
                    for (int b = 0; b < personlists.get(a).size(); b++) {
                        temp = temp + personlists.get(a).get(b) + "+";
                    }
                    temp = temp + ".";
                }
            }

            dm.displayMessage(getApplicationContext(), "iddest content = " + iddest.get(2) + " " + temp+"");

        }

        @Override
        protected void onPostExecute(String a){

            if(isSuccess==true)
            {
                dp.toastershort(getApplicationContext(), "Data Successfully Sent");
            }
            else if(isSuccess==false)
            {
                dp.toastershort(getApplicationContext(), msger);
            }
            dashboardviewer.setVisibility(View.VISIBLE);
            pbar.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.drivehome)
        {
            dp.toastershort(getApplicationContext(), "Home");
        }
        else if(item.getItemId()==R.id.driveprof)
        {
            Intent startIntent=new Intent(DriverDashboard.this, ProfileTabbed.class);
            startActivity(startIntent);
            finish();
        }
        else if(item.getItemId()==R.id.drivehistory)
        {
            Intent startIntent=new Intent(DriverDashboard.this, DriverHistory.class);
            startActivity(startIntent);
            finish();
        }
        else if(item.getItemId()==R.id.about || item.getItemId()==R.id.driveabout || item.getItemId()==R.id.estabout)
        {
            Intent startIntent=new Intent(DriverDashboard.this, About.class);
            startActivity(startIntent);
            finish();
        }
        else if(item.getItemId()==R.id.drivelogout)
        {
            ConfirmDialog confirmDialog = new ConfirmDialog("Confirmation", "You are about to log out\nAre you sure?", "logout");
            confirmDialog.show(getSupportFragmentManager(), "confirm dialog");

        }

        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();


    }
}
