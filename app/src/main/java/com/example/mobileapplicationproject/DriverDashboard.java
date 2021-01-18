package com.example.mobileapplicationproject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.mobileapplicationproject.DataController.ConnectionController;
import com.example.mobileapplicationproject.DataController.DataHolder;
import com.example.mobileapplicationproject.DataController.DataProcessor;
import com.example.mobileapplicationproject.DataController.DebugMode;
import com.google.android.material.navigation.NavigationView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.StringTokenizer;

import de.hdodenhof.circleimageview.CircleImageView;

public class DriverDashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    ArrayList<String>vehiclelist;
    ArrayList<String>routelist;

    ConnectionController cc = new ConnectionController();
    DataHolder dh = new DataHolder();
    DataProcessor dp = new DataProcessor();
    DebugMode dm = new DebugMode();

    SimpleDateFormat timeformatter;
    SimpleDateFormat dateformatter;

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
    TextView txt_route;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_dashboard);

        dashboardviewer = findViewById(R.id.dashboard_viewer);
        pbar = findViewById(R.id.pbar);

        spr_platenum = findViewById(R.id.spr_platenum);
        txt_route = findViewById(R.id.txt_route);

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

        vehiclelist = new ArrayList<>();
        routelist = new ArrayList<>();

        vehiclelist.add(0,"Select Plate Number");

        Dbread dbread = new Dbread();
        dbread.execute();

        timeformatter = new SimpleDateFormat("HH:mm");
        dateformatter = new SimpleDateFormat("yyyy-MM-dd");

        spr_platenum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(!parent.getItemAtPosition(position).equals("Select Plate Number"))
                {
                    txt_route.setText(routelist.get(position-1));
                }
                else
                {
                    dp.toasterlong(getApplicationContext(), "Please Select Vehicle");
                    txt_route.setText("");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                dp.toasterlong(getApplicationContext(), "Please Select Vehicle");
                txt_route.setText("");
            }
        });

    //TODO: Set up Employee Dashboard for QR......
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

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(DriverDashboard.this, R.layout.spinner_format, vehiclelist);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spr_platenum.setAdapter(dataAdapter);

            pbar.setVisibility(View.GONE);
            dashboardviewer.setVisibility(View.VISIBLE);

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
            Intent startIntent=new Intent(DriverDashboard.this, UserDriverProfile.class);
            startActivity(startIntent);
            finish();
        }
        else if(item.getItemId()==R.id.drivehistory)
        {
            Intent startIntent=new Intent(DriverDashboard.this, AddCompanion.class);
            startActivity(startIntent);
            finish();
        }
        else if(item.getItemId()==R.id.drivelogout)
        {
            Intent startIntent=new Intent(DriverDashboard.this, Login.class);
            startActivity(startIntent);
            finish();
        }

        return false;
    }


}
