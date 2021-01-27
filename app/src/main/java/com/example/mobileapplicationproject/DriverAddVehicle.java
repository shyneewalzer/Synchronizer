package com.example.mobileapplicationproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.example.mobileapplicationproject.DataController.ConnectionController;
import com.example.mobileapplicationproject.DataController.DataHolder;
import com.example.mobileapplicationproject.DataController.DataProcessor;
import com.example.mobileapplicationproject.DataController.DebugMode;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class DriverAddVehicle extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener{

    ConnectionController cc = new ConnectionController();
    DataHolder dh = new DataHolder();
    DataProcessor dp = new DataProcessor();
    DebugMode dm = new DebugMode();

    ArrayAdapter<String> adapter;

    ////////////////UI ELEMENTS//////////////
    Toolbar toolbar;
    LinearLayout lo_addvehicleviewer;
    ProgressBar pbar;

    TextInputEditText edt_plate, edt_bodynum;
    AutoCompleteTextView edt_route;
    Button btn_addvehicle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_add_vehicle);

        edt_plate = findViewById(R.id.edt_plate);
        edt_bodynum = findViewById(R.id.edt_bodynum);
        edt_route = findViewById(R.id.edt_route);
        btn_addvehicle = findViewById(R.id.btn_addvehicle);
        lo_addvehicleviewer = findViewById(R.id.lo_addvehicleviewer);
        pbar = findViewById(R.id.pbar);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_backarrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Add Vehicle");

        btn_addvehicle.setOnClickListener(this);

        adapter = new ArrayAdapter<String>(DriverAddVehicle.this, android.R.layout.simple_list_item_1, dh.getListRoutes());
        edt_route.setAdapter(adapter);

        edt_route.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                edt_route.setText(adapter.getItem(position));
            }
        });


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
                    ResultSet rs=con.createStatement().executeQuery("select * from vehicles where plate_number = '"+ edt_plate.getText() +"' ");

                    if(rs.isBeforeFirst())
                    {
                        msger = "Plate Number Already Exist";
                        isSuccess = false;
                    }
                    else
                    {
                        con.createStatement().executeUpdate("INSERT into vehicles (plate_number, body_number, vehicle_route, isActive, account_id) VALUES('"+ edt_plate.getText() +"', '"+ edt_bodynum.getText() +"', '"+ edt_route.getText() +"', '0', '"+ dh.getUserid() +"')");
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

            lo_addvehicleviewer.setVisibility(View.GONE);
            pbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String a){
//            Intent myIntent = new Intent(SetLocation.this, UserDriverDashboard.class);
//            startActivity(myIntent);

            if(isSuccess==true)
            {
                dp.toastershort(getApplicationContext(), "Vehicle successfully added");
            }
            else if(isSuccess==false)
            {
                dp.toastershort(getApplicationContext(), msger);
            }
            lo_addvehicleviewer.setVisibility(View.VISIBLE);
            pbar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.btn_addvehicle)
        {
            Dbinsert dbinsert = new Dbinsert();
            dbinsert.execute();
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