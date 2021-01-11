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

    APIInterface apiInterface;
    String qrcode;
    StringTokenizer tokenizer;
    ArrayList<String> travelinfo;
    ArrayList<String>vehiclelist;

    ConnectionController cc = new ConnectionController();
    DataHolder dh = new DataHolder();
    DataProcessor dp = new DataProcessor();
    DebugMode dm = new DebugMode();

    SimpleDateFormat timeformatter;
    SimpleDateFormat dateformatter;

    ///////////UI ELEMENTS/////////

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    NavigationView navigationView;
    ImageView nav_img_QR;
    TextView draw_name;
    CircleImageView nav_img_user;

    ImageView driverQR;

    LinearLayout dashboardviewer;
    ProgressBar pbar;

    TextView txtfirstname, txtlastname, txtposition, txt_qrsign;
    Spinner spr_platenum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        apiInterface = APIClient.getClient().create(APIInterface.class);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_dashboard);

        txtfirstname = findViewById(R.id.txt_firstname);
        txtlastname = findViewById(R.id.txt_lastname);
        txtposition = findViewById(R.id.txt_position);
        txt_qrsign = findViewById(R.id.txt_qrsign);

        driverQR = findViewById(R.id.imageview);

        dashboardviewer = findViewById(R.id.dashboard_viewer);
        pbar = findViewById(R.id.pbar);

        spr_platenum = findViewById(R.id.spr_platenum);

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

        Menu drawer_menu = navigationView.getMenu();
        drawer_menu.findItem(R.id.destination).setVisible(false);

        View headerView = navigationView.getHeaderView(0);
        draw_name = (TextView) headerView.findViewById(R.id.lbl_draw_name);
        nav_img_QR = headerView.findViewById(R.id.personal_qr);
        nav_img_user = headerView.findViewById(R.id.cimg_user);

        draw_name.setText(dh.getpFName() + " " + dh.getpLName());
        nav_img_QR.setImageBitmap(dp.createQR(dh.getpFName() + "," + dh.getpLName() + "," + dh.getpMName() + "," + dh.getpBday() + "," + dh.getpContact() + "," + dh.getpPosition() + "," + dh.getpEstab()));
        nav_img_user.setImageBitmap(dp.createImage(dh.getpImage()));

        travelinfo = new ArrayList<>();
        vehiclelist = new ArrayList<>();

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
                    driverQR.setImageBitmap(dp.createQR(dh.getUserid() + "," + parent.getItemAtPosition(position)));
                    txt_qrsign.setVisibility(View.GONE);
                }
                else
                {
                    dp.toasterlong(getApplicationContext(), "Please Select Vehicle");
                    txt_qrsign.setVisibility(View.VISIBLE);
                    driverQR.setImageBitmap(null);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                dp.toasterlong(getApplicationContext(), "Please Select Vehicle");

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

                    ResultSet rsqr=con.createStatement().executeQuery("select * from vehicles where account_id = '"+ dh.getUserid() +"' ");
                    while (rsqr.next())
                    {
                        vehiclelist.add(rsqr.getString("plate_number"));
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

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(DriverDashboard.this, R.layout.spinner_format, vehiclelist);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spr_platenum.setAdapter(dataAdapter);

            driverQR.setImageBitmap(dp.createQR(qrcode));
            pbar.setVisibility(View.GONE);
            dashboardviewer.setVisibility(View.VISIBLE);

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
            Intent startIntent=new Intent(DriverDashboard.this, Login.class);
            startActivity(startIntent);
            finish();
        }
        return true;

    }

}
