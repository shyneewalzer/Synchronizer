package com.example.mobileapplicationproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.StringTokenizer;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainMenuFormEmployee extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    ConnectionController cc = new ConnectionController();
    DataHolder dh = new DataHolder();
    DataProcessor dp = new DataProcessor();

    StringTokenizer tokenizer;
    ArrayList<String> gethoseid;
    ArrayList<String> userid;

    SimpleDateFormat timeformatter;
    SimpleDateFormat dateformatter;
    Timestamp timestamp;

    String qrscan;

    ////////////UI ELEMENTS////////////

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    NavigationView navigationView;
    ImageView nav_img_QR;
    TextView draw_name;
    CircleImageView nav_img_user;

    TextView txtFirstName, txtLastName, txtPosition;
    Button btn_escan;

    ProgressBar pbar;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu_form_employee);

        txtFirstName = findViewById(R.id.txt_firstname);
        txtLastName = findViewById(R.id.txt_lastname);
        txtPosition = findViewById(R.id.txt_position);
        btn_escan = findViewById(R.id.btn_escan);

        pbar = findViewById(R.id.pbar);

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

        gethoseid = new ArrayList<>();
        userid = new ArrayList<>();

        timeformatter = new SimpleDateFormat("HH:mm");
        dateformatter = new SimpleDateFormat("yyyy-MM-dd");

        txtFirstName.setText(dh.getpFName());
        txtLastName.setText(dh.getpLName());
        txtPosition.setText(dh.getpPosition());

    }


    private class Dbinsertest extends AsyncTask<String, String, String>
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
                    for(int x = 0; x<gethoseid.size();x++)
                    {
                        con.createStatement().executeUpdate("INSERT into employee_scanned (employee_id, companion_id, parent_id, time_created, date_created) VALUES('"+ dh.getUserid() +"', '"+ userid.get(x) +"', '"+ userid.get(0) +"', '"+ timeformatter.format(timestamp) +"', '"+ dateformatter.format(timestamp) +"')");
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

            pbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String a){
//            Intent myIntent = new Intent(SetLocation.this, UserDriverDashboard.class);
//            startActivity(myIntent);

            if(isSuccess==true)
            {
                dp.toasterlong(getApplicationContext(),userid+"");
            }
            else if(isSuccess==false)
            {
                dp.toasterlong(getApplicationContext(),userid+" fail");
            }
            pbar.setVisibility(View.GONE);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                sendData(Integer.parseInt(result.getContents()));
                qrscan = result.getContents()+"";

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
            Intent startIntent=new Intent(MainMenuFormEmployee.this, Login.class);
            startActivity(startIntent);
            finish();
        }
        return true;

    }

}
