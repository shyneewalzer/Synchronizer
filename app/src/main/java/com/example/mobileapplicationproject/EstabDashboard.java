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
import android.widget.AdapterView;
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
import com.google.android.material.textfield.TextInputEditText;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class EstabDashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, ConfirmDialog.ConfirmDialogListener{

    ConnectionController cc = new ConnectionController();
    DataHolder dh = new DataHolder();
    DataProcessor dp = new DataProcessor();
    DebugMode dm = new DebugMode();

    ArrayList<String>sImage;
    ArrayList<String>sID;
    ArrayList<String>sFullName;
    ArrayList<String>sStatus;

    CustomAdapter customAdapter;

    String selectedEmp;

    ///////////////UI ELEMENTS////////////////
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    NavigationView navigationView;
    TextView draw_name, draw_type;
    CircleImageView draw_img_user;

    LinearLayout lo_staffviewer;
    ProgressBar pbar;
    ListView listView;

    TextView txt_addstaff, txt_deactivate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.estab_dashboard);

        lo_staffviewer = findViewById(R.id.lo_staffviewer);
        pbar = findViewById(R.id.pbar);
        listView = findViewById(R.id.listView);

        txt_addstaff = findViewById(R.id.txt_addstaff);
        txt_deactivate = findViewById(R.id.txt_deactivate);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Employee List");

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

        txt_addstaff.setOnClickListener(this);
        txt_deactivate.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.txt_addstaff)
        {
            Dbcheck dbcheck = new Dbcheck();
            dbcheck.execute();
        }
        else if(v.getId()==R.id.txt_deactivate)
        {
            Intent startIntent = new Intent(EstabDashboard.this, EmployeeActivation.class);
            startActivity(startIntent);
        }
    }

    @Override
    public void getDialogResponse(boolean dialogResponse, String purpose) {

        if(purpose.equals("logout") && dialogResponse==true)
        {
            Intent startIntent=new Intent(EstabDashboard.this, Login.class);
            startActivity(startIntent);
            finish();
        }
    }

    private class Dbread extends AsyncTask<String, String, String>
    {

        int tempppp;
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

                    ResultSet rs=con.createStatement().executeQuery("SELECT * FROM user_profile WHERE account_id = '"+ dh.getEstAcctID() +"' AND isActive = '1' ");

                    if(rs.isBeforeFirst())
                    {
                        isSuccess=true;
                        while (rs.next())
                        {
                            tempppp = rs.getRow();
                            sImage.add(rs.getString("image"));
                            sID.add(rs.getString("user_id"));
                            sFullName.add(rs.getString("firstname") + " " + rs.getString("lastname"));
                            sStatus.add(rs.getString("contactnumber"));
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

            sID = new ArrayList<>();
            sFullName = new ArrayList<>();
            sStatus = new ArrayList<>();
            sImage = new ArrayList<>();

            lo_staffviewer.setVisibility(View.GONE);
            pbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String a){

            lo_staffviewer.setVisibility(View.VISIBLE);
            pbar.setVisibility(View.GONE);

            customAdapter = new CustomAdapter();
            listView.setAdapter(customAdapter);
            dm.displayMessage(getApplicationContext(), dh.getEstAcctID()+"");

        }
    }

    private class Dbreadprofile extends AsyncTask<String, String, String>
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

                    ResultSet rs=con.createStatement().executeQuery("SELECT * FROM user_profile WHERE user_id = '"+ selectedEmp +"' ");

                        isSuccess=true;
                        while (rs.next())
                        {
                            dh.setProfile(rs.getString("firstname"), rs.getString("lastname"), rs.getString("middlename"), rs.getDate("birthday"),rs.getString("contactnumber"),rs.getString("image"));
                            dh.setUserid(rs.getInt("user_id"));
                        }
                    rs.close();


                    ResultSet rsadr=con.createStatement().executeQuery("select * from address_table where user_id = '"+ dh.getEstAcctID() +"' ");

                    while(rsadr.next())
                    {
                        dh.setAddress(rsadr.getString("house_lot_number"), rsadr.getString("barangay"), rsadr.getString("city"));
                    }
                    rsadr.close();

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

            lo_staffviewer.setVisibility(View.GONE);
            pbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String a){

            Intent startIntent=new Intent(EstabDashboard.this, EmployeeMain.class);
            startActivity(startIntent);
            finish();

            lo_staffviewer.setVisibility(View.VISIBLE);
            pbar.setVisibility(View.GONE);

        }
    }

    private class Dbcheck extends AsyncTask<String, String, String>
    {

        int activecount=0;
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

                    ResultSet rs=con.createStatement().executeQuery("SELECT * FROM user_profile WHERE account_id = '"+ dh.getEstAcctID() +"' AND isActive = '1' ");
                    isSuccess=true;
                    while (rs.next())
                    {
                        activecount = rs.getRow();
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

            lo_staffviewer.setVisibility(View.GONE);
            pbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String a){

            lo_staffviewer.setVisibility(View.VISIBLE);
            pbar.setVisibility(View.GONE);

            if(isSuccess==true)
            {
                if(activecount<2)
                {
                    Intent startIntent = new Intent(EstabDashboard.this, EstabAddEmployee.class);
                    startActivity(startIntent);
                }
                else
                {
                    dp.toasterlong(getApplicationContext(), msger + "Only (" + 2 + ") employees can be activated at a time");
                }
            }
            else
            {
                dp.toasterlong(getApplicationContext(), msger+"");
            }

        }
    }

    class CustomAdapter extends BaseAdapter {
        @Override
        public int getCount()
        {
            return sID.size();
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
            view= getLayoutInflater().inflate(R.layout.row_employee,null);

            TextView tvID = view.findViewById(R.id.list_txt_id);
            CircleImageView tvImg = view.findViewById(R.id.list_img_prof);
            TextView tvFullName = view.findViewById(R.id.list_txt_fullname);
            TextView tvstatus = view.findViewById(R.id.list_txt_status);

            tvID.setText(sID.get(i));
            if(sImage.get(i)==null)
            {
                tvImg.setImageResource(R.drawable.ic_person);
            }
            else
            {
                tvImg.setImageBitmap(dp.createImage(sImage.get(i)));
            }
            tvFullName.setText(sFullName.get(i));
            tvstatus.setText(sStatus.get(i));

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    selectedEmp = tvID.getText()+"";

                    Dbreadprofile dbreadprofile = new Dbreadprofile();
                    dbreadprofile.execute();
                    dm.displayMessage(getApplicationContext(), selectedEmp);
                }
            });

            return view;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.estprof)
        {
            Intent startIntent=new Intent(EstabDashboard.this, ProfileTabbed.class);
            startActivity(startIntent);
            finish();
        }
        else if(item.getItemId()==R.id.esthistory)
        {
            Intent startIntent=new Intent(EstabDashboard.this, UserHistory.class);
            startActivity(startIntent);
            finish();
        }
        else if(item.getItemId()==R.id.estlogout)
        {
            ConfirmDialog confirmDialog = new ConfirmDialog("Confirmation", "You are about to log out\nAre you sure?", "logout");
            confirmDialog.show(getSupportFragmentManager(), "confirm dialog");
        }


        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        Dbread dbread = new Dbread();
        dbread.execute();
    }
}