package com.example.mobileapplicationproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.example.mobileapplicationproject.DataController.DebugMode;
import com.google.android.material.navigation.NavigationView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class EmployeeDashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    ConnectionController cc = new ConnectionController();
    DataHolder dh = new DataHolder();
    DataProcessor dp = new DataProcessor();
    DebugMode dm = new DebugMode();

    ArrayList<String>sImage;
    ArrayList<String>sID;
    ArrayList<String>sFullName;
    ArrayList<String>sStatus;


    ///////////////UI ELEMENTS////////////////
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    NavigationView navigationView;
    TextView draw_name, draw_type;
    CircleImageView draw_img_user;

    LinearLayout staffviewer;
    ProgressBar pbar;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.employee_dashboard);

        staffviewer = findViewById(R.id.travelviewer);
        pbar = findViewById(R.id.pbar);
        listView = findViewById(R.id.listView);

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

        draw_name.setText(dh.getEstName());
        draw_type.setText(dh.getType());
        draw_img_user.setImageBitmap(dp.createImage(dh.getEstImage()));
        if(dh.getEstImage()==null)
        {
            draw_img_user.setImageResource(R.drawable.ic_person);
        }


    }

//    private class Dbread extends AsyncTask<String, String, String>
//    {
//        int tempp;
//        String msger;
//        Boolean isSuccess=false;
//        @Override
//        protected String doInBackground(String... strings) {
//
//            try{
//                Connection con=cc.CONN();
//                if(con==null)
//                {
//                    msger="Please Check your Internet Connection";
//                }
//                else
//                {
//
//                    ResultSet rs=con.createStatement().executeQuery("SELECT * FROM user_profile WHERE account_id = '"+ dh.getUserid() +"' ");
//
//                    if(rs.isBeforeFirst())
//                    {
//                        isSuccess=true;
//                        while (rs.next())
//                        {
//                            tempp=rs.getRow();
//                            destination.add(rs.getString("destination"));
//                            timee.add(rs.getString("time_boarded"));
//                            datee.add(rs.getString("date_boarded"));
//                        }
//                    }
//                    rs.close();
//                    con.close();
//                }
//            }
//            catch (Exception ex){
//                msger="Exception" + ex;
//            }
//            return msger;
//
//
//        }
//
//        @Override
//        protected void onPreExecute() {
//
//            destination.clear();
//            timee.clear();
//            datee.clear();
//
//            travelviewer.setVisibility(View.GONE);
//            pbar.setVisibility(View.VISIBLE);
//        }
//
//        @Override
//        protected void onPostExecute(String a){
//
//            travelviewer.setVisibility(View.VISIBLE);
//            pbar.setVisibility(View.GONE);
//
//            customAdapter.notifyDataSetChanged();
//            dm.displayMessage(getApplicationContext(), tempp+"");
//        }
//    }
//
//    class CustomAdapter extends BaseAdapter {
//        @Override
//        public int getCount()
//        {
//            return destination.size();
//        }
//
//        @Override
//        public Object getItem(int i)
//        {
//            return null;
//        }
//
//        @Override
//        public long getItemId(int i)
//        {
//            return 0;
//        }
//
//        @Override
//        public View getView(int i, View view, ViewGroup viewGroup) {
//            view= getLayoutInflater().inflate(R.layout.travelrow,null);
//
//            final TextView tvdestination=(TextView)view.findViewById(R.id.tdestination);
//            final TextView tvtime=(TextView)view.findViewById(R.id.ttime);
//            final TextView tvdate=(TextView)view.findViewById(R.id.tdate);
//
//            tvdestination.setText(destination.get(i));
//            tvtime.setText(timee.get(i));
//            tvdate.setText(datee.get(i));
//
//            return view;
//        }
//    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.prof)
        {
            Intent startIntent=new Intent(EmployeeDashboard.this, EmployeeeProfileForm.class);
            startActivity(startIntent);
            finish();
        }


        return false;
    }
}