package com.example.mobileapplicationproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
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
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class DriverHistory extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, ConfirmDialog.ConfirmDialogListener{

    ConnectionController cc = new ConnectionController();
    DataHolder dh = new DataHolder();
    DataProcessor dp = new DataProcessor();
    DebugMode dm = new DebugMode();

    ArrayList<String> parentid;
    ArrayList<String> parentdest;
    ArrayList<String> plate;
    ArrayList<String> timee;
    ArrayList<String> datee;
    ArrayList<String> route;
    ArrayList<String> searcher;

    DatePickerDialog.OnDateSetListener dateSetListener;

    ArrayList<String>listGroup;
    ArrayList<String>listPerson;
    HashMap<String,ArrayList<String>> listChild = new HashMap<>();
    AdapterDriverHistory expandAdapter;

    ///////////////UI ELEMENTS/////////////////

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    NavigationView navigationView;
    TextView draw_name, draw_type;
    CircleImageView draw_img_user;

    ExpandableListView expandableListView;

    LinearLayout lo_routeviewer;
    ProgressBar pbar;
    ListView listView;

    Spinner spr_search;
    EditText edt_search;
    Button btn_search;

    SwipeRefreshLayout lo_driverhistoryrefresher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_history);

        lo_routeviewer = findViewById(R.id.lo_routeviewer);
        pbar = findViewById(R.id.pbar);
        listView = findViewById(R.id.listView);

        spr_search = findViewById(R.id.spr_search);
        edt_search = findViewById(R.id.edt_search);
        btn_search = findViewById(R.id.btn_dsearch);
        lo_driverhistoryrefresher = findViewById(R.id.lo_driverhistoryrefresher);

        expandableListView = findViewById(R.id.expandableListView);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Reports");

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

        searcher = new ArrayList<>();

        searcher.add("Search by");
        searcher.add("Destination");
        searcher.add("Plate Number");
        searcher.add("Date");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(DriverHistory.this, R.layout.spinner_format, searcher);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spr_search.setAdapter(dataAdapter);

        spr_search.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(parent.getItemAtPosition(position).equals("Search by"))
                {
                    dp.toasterlong(getApplicationContext(), "Please Select Search Criteria");
                    edt_search.setEnabled(false);
                }
                else if(parent.getItemAtPosition(position).equals("Date"))
                {
                    edt_search.setEnabled(true);
                    edt_search.setFocusableInTouchMode(false);
                }
                else
                {
                    edt_search.setEnabled(true);
                    edt_search.setFocusableInTouchMode(true);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        lo_driverhistoryrefresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                lo_driverhistoryrefresher.setRefreshing(false);
                Dbread dbread = new Dbread();
                dbread.execute();
            }
        });

        expandableListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (expandableListView.getChildAt(0) != null) {
                    lo_driverhistoryrefresher.setEnabled(expandableListView.getFirstVisiblePosition() == 0 && expandableListView.getChildAt(0).getTop() == 0);
                }
            }
        });

        Dbread dbread = new Dbread();
        dbread.execute();
    }

    @Override
    public void getDialogResponse(boolean dialogResponse, String purpose) {

        if(purpose.equals("logout") && dialogResponse==true)
        {
            Intent startIntent=new Intent(DriverHistory.this, Login.class);
            startActivity(startIntent);
            finish();
        }
    }

    private class Dbread extends AsyncTask<String, String, String>
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

                    ResultSet rs=con.createStatement().executeQuery("SELECT * FROM travel_history WHERE driver_id = '"+ dh.getUserid() +"' GROUP BY batch ORDER BY date_boarded ASC, time_boarded ASC");

                    if(rs.isBeforeFirst())
                    {
                        isSuccess=true;
                        while (rs.next())
                        {
                            parentid.add(rs.getString("account_id"));
                            parentdest.add(rs.getString("destination"));

                            plate.add(rs.getString("plate_number"));
                            timee.add(rs.getString("time_boarded"));
                            datee.add(rs.getString("date_boarded"));

                            listGroup.add(rs.getString("batch"));

                        }
                    }
                    rs.close();

                    if(isSuccess==true)
                    {
                        for (int x=0;x<listGroup.size();x++)
                        {
                            ResultSet rsparent=con.createStatement().executeQuery("SELECT * FROM user_profile WHERE account_id = '"+ parentid.get(x) +"' ");

                            while (rsparent.next())
                            {
                                parentid.set(x, rsparent.getString("firstname") + " " + rsparent.getString("lastname") + "_" + rsparent.getString("contactnumber") + "_" + parentdest.get(x));
                            }
                            rsparent.close();
                        }
                    }

                    ////////////////////////////////////////////////////////////////////////////////////////////////////

                    for(int x=0;x<plate.size();x++)
                    {
                        listPerson = new ArrayList<>();
                        ResultSet rsv=con.createStatement().executeQuery("SELECT * FROM vehicles WHERE plate_number = '"+ plate.get(x) +"' ");

                        isSuccess=true;
                        while (rsv.next())
                        {
                            route.add(rsv.getString("vehicle_route"));
                        }

                        rsv.close();
                    }
////////////////////////////////////////////////////////////////////////////////////////////////////

                    for(int x=0;x<=listGroup.size();x++)
                    {
                        listPerson = new ArrayList<>();
                        listPerson.add(parentid.get(x));
                        ResultSet rst=con.createStatement().executeQuery("SELECT * FROM travel_history WHERE batch = '"+ listGroup.get(x) +"' ORDER BY date_boarded ASC, time_boarded ASC");

                        isSuccess=true;
                        while (rst.next())
                        {
                            String fnamechecker = rst.getString("firstname");
                            String lnamechecker = rst.getString("lastname");
                            if((fnamechecker!=null && !fnamechecker.isEmpty()) && (lnamechecker!=null && !lnamechecker.isEmpty()))
                            {
                                listPerson.add(rst.getString("firstname") + " " + rst.getString("lastname") + "_" + rst.getString("contact_number") + "_" + rst.getString("destination"));
                            }
                        }

                        listChild.put(listGroup.get(x), listPerson);

                        rst.close();
                    }
////////////////////////////////////////////////////////////////////////////////////////////////////

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

            listGroup = new ArrayList<>();
            parentid = new ArrayList<>();
            parentdest = new ArrayList<>();
            plate = new ArrayList<>();
            timee = new ArrayList<>();
            datee = new ArrayList<>();
            route = new ArrayList<>();
            listChild.clear();

            lo_driverhistoryrefresher.setVisibility(View.GONE);
            lo_routeviewer.setVisibility(View.GONE);
            pbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String a){

            if(isSuccess==true)
            {
                expandAdapter = new AdapterDriverHistory(listGroup, listChild, plate, timee, datee, route);
                expandableListView.setAdapter(expandAdapter);

                lo_driverhistoryrefresher.setVisibility(View.VISIBLE);
                lo_routeviewer.setVisibility(View.VISIBLE);
                pbar.setVisibility(View.GONE);
            }
            else
            {
                expandableListView.setAdapter((ExpandableListAdapter) null);
                dp.toasterlong(getApplicationContext(), "Nothing Found");

                lo_driverhistoryrefresher.setVisibility(View.VISIBLE);
                lo_routeviewer.setVisibility(View.VISIBLE);
                pbar.setVisibility(View.GONE);
            }
        }
    }

    private class Dbsearch extends AsyncTask<String, String, String>
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

                    if(spr_search.getSelectedItem().equals("Plate Number"))
                    {
                        ResultSet rs=con.createStatement().executeQuery("SELECT * FROM travel_history WHERE plate_number like '%"+ edt_search.getText() +"%' AND driver_id='"+ dh.getUserid() +"' GROUP BY batch ORDER BY date_boarded ASC, time_boarded ASC");

                        if(rs.isBeforeFirst())
                        {
                            isSuccess=true;
                            while (rs.next())
                            {
                                parentid.add(rs.getString("account_id"));
                                parentdest.add(rs.getString("destination"));

                                plate.add(rs.getString("plate_number"));
                                timee.add(rs.getString("time_boarded"));
                                datee.add(rs.getString("date_boarded"));

                                listGroup.add(rs.getString("batch"));

                            }
                        }
                        rs.close();

                        if(isSuccess==true)
                        {
                            for (int x=0;x<listGroup.size();x++)
                            {
                                ResultSet rsparent=con.createStatement().executeQuery("SELECT * FROM user_profile WHERE account_id = '"+ parentid.get(x) +"' ");

                                while (rsparent.next())
                                {
                                    parentid.set(x, rsparent.getString("firstname") + " " + rsparent.getString("lastname") + "_" + rsparent.getString("contactnumber") + "_" + parentdest.get(x));
                                }
                                rsparent.close();
                            }
                        }

                    }
                    else if(spr_search.getSelectedItem().equals("Destination"))
                    {
                        ResultSet rs=con.createStatement().executeQuery("SELECT * FROM travel_history WHERE destination like '%"+ edt_search.getText() +"%' AND driver_id='"+ dh.getUserid() +"' GROUP BY batch ORDER BY date_boarded ASC, time_boarded ASC ");

                        if(rs.isBeforeFirst())
                        {
                            isSuccess=true;
                            while (rs.next())
                            {
                                parentid.add(rs.getString("account_id"));
                                parentdest.add(rs.getString("destination"));
                                plate.add(rs.getString("plate_number"));
                                timee.add(rs.getString("time_boarded"));
                                datee.add(rs.getString("date_boarded"));

                                listGroup.add(rs.getString("batch"));

                            }
                        }
                        rs.close();

                        if(isSuccess==true)
                        {
                            for (int x=0;x<listGroup.size();x++)
                            {
                                ResultSet rsparent=con.createStatement().executeQuery("SELECT * FROM user_profile WHERE account_id = '"+ parentid.get(x) +"' ");

                                while (rsparent.next())
                                {
                                    parentid.set(x, rsparent.getString("firstname") + " " + rsparent.getString("lastname") + "_" + rsparent.getString("contactnumber") + "_" + parentdest.get(x));
                                }
                                rsparent.close();
                            }
                        }
                    }
                    else if(spr_search.getSelectedItem().equals("Date"))
                    {
                        ResultSet rs=con.createStatement().executeQuery("SELECT * FROM travel_history WHERE date_boarded = '"+ edt_search.getText() +"' AND driver_id = '"+ dh.getUserid() +"' GROUP BY batch");

                        if(rs.isBeforeFirst())
                        {
                            isSuccess=true;
                            while (rs.next())
                            {
                                parentid.add(rs.getString("account_id"));
                                parentdest.add(rs.getString("destination"));
                                plate.add(rs.getString("plate_number"));
                                timee.add(rs.getString("time_boarded"));
                                datee.add(rs.getString("date_boarded"));

                                listGroup.add(rs.getString("batch"));

                            }
                        }
                        rs.close();

                        if(isSuccess==true)
                        {
                            for (int x=0;x<listGroup.size();x++)
                            {
                                ResultSet rsparent=con.createStatement().executeQuery("SELECT * FROM user_profile WHERE account_id = '"+ parentid.get(x) +"' ");

                                while (rsparent.next())
                                {
                                    parentid.set(x, rsparent.getString("firstname") + " " + rsparent.getString("lastname") + "_" + rsparent.getString("contactnumber") + "_" + parentdest.get(x));
                                }
                                rsparent.close();
                            }
                        }

                    }

////////////////////////////////////////////////////////////////////////////////////////////////////

                    for(int x=0;x<plate.size();x++)
                    {
                        listPerson = new ArrayList<>();
                        ResultSet rsv=con.createStatement().executeQuery("SELECT * FROM vehicles WHERE plate_number = '"+ plate.get(x) +"' ");

                        isSuccess=true;
                        while (rsv.next())
                        {
                            route.add(rsv.getString("vehicle_route"));
                        }

                        rsv.close();
                    }
////////////////////////////////////////////////////////////////////////////////////////////////////

                    for(int x=0;x<=listGroup.size();x++)
                    {
                        listPerson = new ArrayList<>();
                        listPerson.add(parentid.get(x));
                        ResultSet rst=con.createStatement().executeQuery("SELECT * FROM travel_history WHERE batch = '"+ listGroup.get(x) +"' ORDER BY date_boarded ASC, time_boarded ASC");

                        isSuccess=true;
                        while (rst.next())
                        {
                            String fnamechecker = rst.getString("firstname");
                            String lnamechecker = rst.getString("lastname");
                            if((fnamechecker!=null && !fnamechecker.isEmpty()) && (lnamechecker!=null && !lnamechecker.isEmpty()))
                            {
                                listPerson.add(rst.getString("firstname") + " " + rst.getString("lastname") + "_" + rst.getString("contact_number") + "_" + rst.getString("destination"));
                            }
                        }

                        listChild.put(listGroup.get(x), listPerson);

                        rst.close();
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

            listGroup = new ArrayList<>();
            parentid = new ArrayList<>();
            parentdest = new ArrayList<>();
            plate = new ArrayList<>();
            timee = new ArrayList<>();
            datee = new ArrayList<>();
            route = new ArrayList<>();
            listChild.clear();

            dm.displayMessage(getApplicationContext(), parentid+"");
            lo_driverhistoryrefresher.setVisibility(View.GONE);
            lo_routeviewer.setVisibility(View.GONE);
            pbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String a){

            if(isSuccess==true)
            {
                expandAdapter = new AdapterDriverHistory(listGroup, listChild, plate, timee, datee, route);
                expandableListView.setAdapter(expandAdapter);

                lo_driverhistoryrefresher.setVisibility(View.VISIBLE);
                lo_routeviewer.setVisibility(View.VISIBLE);
                pbar.setVisibility(View.GONE);
            }
            else
            {
                expandableListView.setAdapter((ExpandableListAdapter) null);
                dp.toasterlong(getApplicationContext(), "Nothing Found");

                lo_driverhistoryrefresher.setVisibility(View.VISIBLE);
                lo_routeviewer.setVisibility(View.VISIBLE);
                pbar.setVisibility(View.GONE);
            }

        }
    }

    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.btn_dsearch)
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
            if(spr_search.getSelectedItem().equals("Date"))
            {
                Calendar cal = Calendar.getInstance();
                int cal_yr = cal.get(Calendar.YEAR);
                int cal_mo = cal.get(Calendar.MONTH);
                int cal_dy = cal.get(Calendar.DAY_OF_MONTH);
                dm.displayMessage(getApplicationContext(),cal_yr + "-" + cal_mo + "-" + cal_dy);

                DatePickerDialog datepicker = new DatePickerDialog(DriverHistory.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        edt_search.setText(year + "-" + month + "-" + dayOfMonth);
                    }
                },cal_yr,cal_mo,cal_dy);
                datepicker.show();
            }

        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.drivehome)
        {
            Intent startIntent=new Intent(DriverHistory.this, DriverDashboard.class);
            startActivity(startIntent);
            finish();
        }
        else if(item.getItemId()==R.id.driveprof)
        {
            Intent startIntent=new Intent(DriverHistory.this, ProfileTabbed.class);
            startActivity(startIntent);
            finish();
        }
        else if(item.getItemId()==R.id.about || item.getItemId()==R.id.driveabout || item.getItemId()==R.id.estabout)
        {
            Intent startIntent=new Intent(DriverHistory.this, About.class);
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
}