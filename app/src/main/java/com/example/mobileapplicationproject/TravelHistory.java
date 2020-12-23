package com.example.mobileapplicationproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mobileapplicationproject.DataController.ConnectionController;
import com.example.mobileapplicationproject.DataController.DataHolder;
import com.example.mobileapplicationproject.DataController.DataProcessor;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;

public class TravelHistory extends AppCompatActivity {

    ConnectionController cc = new ConnectionController();
    DataHolder dh = new DataHolder();
    DataProcessor dp = new DataProcessor();

    ArrayList<String> destination;
    ArrayList<String> timee;
    ArrayList<String> datee;

    LinearLayout travelviewer;
    ProgressBar pbar;
    ListView listView;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.travel_history);

        travelviewer = findViewById(R.id.travelviewer);
        pbar = findViewById(R.id.pbar);
        listView = findViewById(R.id.listView);
        bottomNavigationView = findViewById(R.id.nav_menu);

        destination = new ArrayList<>();
        timee = new ArrayList<>();
        datee = new ArrayList<>();

        Dbread dbread = new Dbread();
        dbread.execute();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.location_driver:
                        return true;
                    case R.id.profile_driver:
                        startActivity(new Intent(getApplicationContext()
                                , UserDriverProfile.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.home_driver:
                        startActivity(new Intent(getApplicationContext()
                                , UserDriverDashboard.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.history_driver:
                        startActivity(new Intent(getApplicationContext()
                                , TravelHistory.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.group_driver:
                        startActivity(new Intent(getApplicationContext()
                                , SetLocationGroup.class));
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

                    ResultSet rs=con.createStatement().executeQuery("select * from travel_history where user_id = '"+ dh.getUserid() +"' ");

                    if(rs.isBeforeFirst())
                    {
                        isSuccess=true;
                        while (rs.next())
                        {
                            destination.add(rs.getString(2));
                            timee.add(rs.getString(5));
                            datee.add(rs.getString(6));
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
            travelviewer.setVisibility(View.GONE);
            pbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String a){

            travelviewer.setVisibility(View.VISIBLE);
            pbar.setVisibility(View.GONE);

            CustomAdapter customAdapter = new CustomAdapter();
            listView.setAdapter(customAdapter);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int optid=item.getItemId();

        if(optid==R.id.logout)
        {
            Intent startIntent=new Intent(TravelHistory.this, Login.class);
            startActivity(startIntent);
            finish();
        }

        return true;

    }
}