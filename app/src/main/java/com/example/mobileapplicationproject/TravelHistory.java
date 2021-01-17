package com.example.mobileapplicationproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.mobileapplicationproject.DataController.ConnectionController;
import com.example.mobileapplicationproject.DataController.DataHolder;
import com.example.mobileapplicationproject.DataController.DataProcessor;
import com.example.mobileapplicationproject.DataController.DebugMode;
import com.google.android.material.navigation.NavigationView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class TravelHistory extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener{

    ConnectionController cc = new ConnectionController();
    DataHolder dh = new DataHolder();
    DataProcessor dp = new DataProcessor();
    DebugMode dm = new DebugMode();

    ArrayList<String> destination;
    ArrayList<String> timee;
    ArrayList<String> datee;
    ArrayList<String> searcher;

    CustomAdapter customAdapter;

    String sqlsearch="";

    DatePickerDialog.OnDateSetListener dateSetListener;

    //////////////UI ELEMENTS////////////////
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    NavigationView navigationView;
    TextView draw_name, draw_type;
    CircleImageView draw_img_user;

    LinearLayout travelviewer;
    ProgressBar pbar;
    ListView listView;

    Spinner spr_search;
    EditText edt_search;
    Button btn_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.travel_history);

        travelviewer = findViewById(R.id.travelviewer);
        pbar = findViewById(R.id.pbar);
        listView = findViewById(R.id.listView);

        spr_search = findViewById(R.id.spr_search);
        edt_search = findViewById(R.id.edt_search);
        btn_search = findViewById(R.id.btn_search);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Destination");

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

        btn_search.setOnClickListener(this);
        edt_search.setOnClickListener(this);

        destination = new ArrayList<>();
        timee = new ArrayList<>();
        datee = new ArrayList<>();
        searcher = new ArrayList<>();

        searcher.add("Search by");
        searcher.add("Destination");
        searcher.add("Date");

        customAdapter = new CustomAdapter();
        listView.setAdapter(customAdapter);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(TravelHistory.this, R.layout.spinner_format, searcher);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spr_search.setAdapter(dataAdapter);

        spr_search.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(parent.getItemAtPosition(position).equals("Destination"))
                {
                    sqlsearch="destination";
                    edt_search.setEnabled(true);
                    edt_search.setFocusableInTouchMode(true);
                }
                else if(parent.getItemAtPosition(position).equals("Date"))
                {
                    sqlsearch="date_boarded";
                    edt_search.setEnabled(true);
                    edt_search.setFocusableInTouchMode(false);
                }
                else
                {
                    dp.toasterlong(getApplicationContext(), "Please Select Search Criteria");
                    edt_search.setEnabled(false);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                dp.toasterlong(getApplicationContext(), "Please Select Search Criteria");

            }
        });

        Dbread dbread = new Dbread();
        dbread.execute();

    }

    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.btn_search)
        {
            if(spr_search.getSelectedItem().toString().equals("Search by"))
            {
                dp.toasterlong(getApplicationContext(), "Please Select Search Criteria");
            }
            else
            {
                Dbsearch dbsearch = new Dbsearch();
                dbsearch.execute();
            }
        }
        else if(v.getId()==R.id.edt_search)
        {
            Calendar cal = Calendar.getInstance();
            int cal_yr = cal.get(Calendar.YEAR);
            int cal_mo = cal.get(Calendar.MONTH);
            int cal_dy = cal.get(Calendar.DAY_OF_MONTH);
            dm.displayMessage(getApplicationContext(),cal_yr + "-" + cal_mo + "-" + cal_dy);
            @SuppressLint({"NewApi", "LocalSuppress"}) DatePickerDialog datepicker = new DatePickerDialog(TravelHistory.this, R.style.Theme_AppCompat_DayNight_Dialog_MinWidth, dateSetListener,cal_yr, cal_mo, cal_dy);
            datepicker.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            datepicker.show();

            dateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    month = month + 1;
                    edt_search.setText(year + "-" + month + "-" + dayOfMonth);

                    dm.displayMessage(getApplicationContext(),year + "-" + month + "-" + dayOfMonth );
                }
            };
        }
    }

    private class Dbread extends AsyncTask<String, String, String>
    {
        int tempp;
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

                    ResultSet rs=con.createStatement().executeQuery("SELECT * FROM travel_history WHERE account_id = '"+ dh.getUserid() +"' GROUP BY batch ORDER BY date_boarded ASC, time_boarded ASC");

                    if(rs.isBeforeFirst())
                    {
                        isSuccess=true;
                        while (rs.next())
                        {
                            tempp=rs.getRow();
                            destination.add(rs.getString("destination"));
                            timee.add(rs.getString("time_boarded"));
                            datee.add(rs.getString("date_boarded"));
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

            destination.clear();
            timee.clear();
            datee.clear();

            travelviewer.setVisibility(View.GONE);
            pbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String a){

            travelviewer.setVisibility(View.VISIBLE);
            pbar.setVisibility(View.GONE);

            customAdapter.notifyDataSetChanged();
            dm.displayMessage(getApplicationContext(), tempp+"");
        }
    }

    private class Dbsearch extends AsyncTask<String, String, String>
    {
        int tempp;
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
                    //TODO: Fix sql search
                    ResultSet rs=con.createStatement().executeQuery("SELECT * FROM travel_history WHERE "+ sqlsearch +" = '"+ edt_search.getText() +"' GROUP BY batch");

                    if(rs.isBeforeFirst())
                    {
                        isSuccess=true;
                        while (rs.next())
                        {
                            tempp=rs.getRow();
                            destination.add(rs.getString("destination"));
                            timee.add(rs.getString("time_boarded"));
                            datee.add(rs.getString("date_boarded"));
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

            destination.clear();
            timee.clear();
            datee.clear();

            dm.displayMessage(getApplicationContext(), sqlsearch+"");
            travelviewer.setVisibility(View.GONE);
            pbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String a){

            travelviewer.setVisibility(View.VISIBLE);
            pbar.setVisibility(View.GONE);

            customAdapter.notifyDataSetChanged();
            dm.displayMessage(getApplicationContext(), tempp+"");
        }
    }

    class CustomAdapter extends BaseAdapter {
        @Override
        public int getCount()
        {
            return destination.size();
        }

        @Override
        public Object getItem(int i)
        {
            return null;
        }

        @Override
        public long getItemId(int i)
        {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view= getLayoutInflater().inflate(R.layout.travelrow,null);

            final TextView tvdestination=(TextView)view.findViewById(R.id.tdestination);
            final TextView tvtime=(TextView)view.findViewById(R.id.ttime);
            final TextView tvdate=(TextView)view.findViewById(R.id.tdate);

            tvdestination.setText(destination.get(i));
            tvtime.setText(timee.get(i));
            tvdate.setText(datee.get(i));

            return view;
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.prof)
        {
            Intent startIntent=new Intent(TravelHistory.this, UserDriverProfile.class);
            startActivity(startIntent);
            finish();
        }
        else if(item.getItemId()==R.id.group)
        {
            Intent startIntent=new Intent(TravelHistory.this, AddCompanion.class);
            startActivity(startIntent);
            finish();
        }
        else if(item.getItemId()==R.id.nlogout)
        {
            Intent startIntent=new Intent(TravelHistory.this, Login.class);
            startActivity(startIntent);
            finish();
        }

        return false;
    }


}