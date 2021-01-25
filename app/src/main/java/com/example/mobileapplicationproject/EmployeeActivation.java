package com.example.mobileapplicationproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class EmployeeActivation extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ConfirmDialog.ConfirmDialogListener{

    ConnectionController cc = new ConnectionController();
    DataHolder dh = new DataHolder();
    DataProcessor dp = new DataProcessor();
    DebugMode dm = new DebugMode();

    ArrayList<String> sImage;
    ArrayList<String>sID;
    ArrayList<String>sFullName;
    ArrayList<String>sStatus;

    CustomAdapter customAdapter;

    String selectedEmp, procedure;

    //////////////UI ELEMENTS//////////////

    Toolbar toolbar;

    LinearLayout lo_astaffviewer;
    ProgressBar pbar;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.employee_activation);

        lo_astaffviewer = findViewById(R.id.lo_astaffviewer);
        pbar = findViewById(R.id.pbar);
        listView = findViewById(R.id.listView);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_backarrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Activation/Deactivation");

        Dbread dbread = new Dbread();
        dbread.execute();
    }

    @Override
    public void getDialogResponse(boolean dialogResponse, String purpose) {
        if(purpose.equals("activation") && dialogResponse==true)
        {
            Dbupdate dbupdate = new Dbupdate();
            dbupdate.execute();
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

                    ResultSet rs=con.createStatement().executeQuery("SELECT * FROM user_profile WHERE account_id = '"+ dh.getEstAcctID() +"' ");

                    if(rs.isBeforeFirst())
                    {
                        isSuccess=true;
                        while (rs.next())
                        {
                            tempppp = rs.getRow();
                            sImage.add(rs.getString("image"));
                            sID.add(rs.getString("user_id"));
                            sFullName.add(rs.getString("firstname") + " " + rs.getString("lastname"));
                            sStatus.add(rs.getString("isActive"));
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
            sStatus = new ArrayList<>();

            lo_astaffviewer.setVisibility(View.GONE);
            pbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String a){

            lo_astaffviewer.setVisibility(View.VISIBLE);
            pbar.setVisibility(View.GONE);

            customAdapter = new CustomAdapter();
            listView.setDivider(null);
            listView.setAdapter(customAdapter);
            dm.displayMessage(getApplicationContext(), dh.getEstAcctID()+"");

        }
    }

    private class Dbupdate extends AsyncTask<String, String, String>
    {
        int activecount;
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
                        while (rs.next())
                        {
                            activecount = rs.getRow();
                        }
                    }
                    rs.close();

                    if(procedure.equals("Activate"))
                    {
                        if(activecount<2)
                        {
                            msger = "Activated";
                            con.createStatement().executeUpdate("UPDATE user_profile SET isActive = '" + '1' + "'  where user_id='" + selectedEmp + "' ");
                            isSuccess=true;
                        }
                        else
                        {
                            msger = "Only (" + 2 + ") employees can be activated at a time";
                        }
                    }
                    else if(procedure.equals("Deactivate"))
                    {
                        msger = "Deactivated";
                        con.createStatement().executeUpdate("UPDATE user_profile SET isActive = '" + '0' + "'  where user_id='" + selectedEmp + "' ");
                        isSuccess=true;
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

            lo_astaffviewer.setVisibility(View.GONE);
            pbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String a){

            lo_astaffviewer.setVisibility(View.VISIBLE);
            pbar.setVisibility(View.GONE);

            if(isSuccess==true)
            {
                dp.toasterlong(getApplicationContext(), "Employee Successfully " + msger);
                Dbread dbread = new Dbread();
                dbread.execute();
            }
            else
            {
                dp.toasterlong(getApplicationContext(), msger);
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
            TextView tvStatus = view.findViewById(R.id.list_txt_status);

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
            if(sStatus.get(i).equals("0"))
            {
                tvStatus.setText("Deactivated");
                tvStatus.setTextColor(getResources().getColor(R.color.gray));
            }
            else if(sStatus.get(i).equals("1"))
            {
                tvStatus.setText("Active");
                tvStatus.setTextColor(getResources().getColor(R.color.light_green));
            }

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    selectedEmp = sID.get(position)+"";

                    if(sStatus.get(position).equals("0"))
                    {
                        procedure = "Activate";
                        ConfirmDialog confirmDialog = new ConfirmDialog("Confirmation", "You are about to activate employee\nAre you sure?", "activation");
                        confirmDialog.show(getSupportFragmentManager(), "confirm dialog");
                    }
                    else if(sStatus.get(position).equals("1"))
                    {
                        procedure = "Deactivate";
                        ConfirmDialog confirmDialog = new ConfirmDialog("Confirmation", "You are about to deactivate employee\nAre you sure?", "activation");
                        confirmDialog.show(getSupportFragmentManager(), "confirm dialog");
                    }

                    dm.displayMessage(getApplicationContext(), selectedEmp);
                }
            });

            return view;
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