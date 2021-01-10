package com.example.mobileapplicationproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.mobileapplicationproject.DataController.ConnectionController;
import com.example.mobileapplicationproject.DataController.DataHolder;
import com.example.mobileapplicationproject.DataController.DataProcessor;
import com.example.mobileapplicationproject.DataController.DebugMode;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class SetLocation extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener{

    APIInterface apiInterface;
    ArrayList<String> itemList;
    ArrayAdapter<String> adapter;
    ArrayList<Integer> persons;
    ArrayList<String>travelinfo;

    int getthoseid;
    StringTokenizer tokenizer;
    String qrscan;

    SimpleDateFormat timeformatter;
    SimpleDateFormat dateformatter;
    Timestamp timestamp;

    ConnectionController cc = new ConnectionController();
    DataHolder dh = new DataHolder();
    DataProcessor dp = new DataProcessor();
    DebugMode dm = new DebugMode();

    ///////////UI ELEMENTS//////////
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    NavigationView navigationView;
    ImageView personalQR;

    LinearLayout lo_locationviewer;
    LinearLayout lo_location, lo_companion;

    ListView lv;

    ProgressBar pbar;
    SwipeRefreshLayout refresher;

    Button btn_scan, btn_destination, btn_addCompanion;

    TextInputEditText edt_companion, edt_destination;
    TextView tv_location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        setContentView(R.layout.set_location);

        lv = (ListView) findViewById(R.id.listview);
        refresher = findViewById(R.id.lo_refresher);
        pbar = findViewById(R.id.pbar);

        lo_locationviewer = findViewById(R.id.locationviewer);
        lo_location = findViewById(R.id.lo_location);
        lo_companion = findViewById(R.id.lo_companion);

        btn_scan = findViewById(R.id.btn_scanner);
        btn_destination = findViewById(R.id.btn_destination);
        btn_addCompanion = findViewById(R.id.btn_addCompanion);

        tv_location = findViewById(R.id.tv_location);

        edt_destination = findViewById(R.id.edt_destination);
        edt_companion = findViewById(R.id.edt_companion);

        itemList = new ArrayList<>();
        persons = new ArrayList<>();
        travelinfo = new ArrayList<>();
        adapter = new ArrayAdapter<String>(SetLocation.this , android.R.layout.simple_list_item_multiple_choice,itemList);
        timeformatter = new SimpleDateFormat("HH:mm");
        dateformatter = new SimpleDateFormat("yyyy-MM-dd");

        btn_destination.setOnClickListener(this);
        btn_addCompanion.setOnClickListener(this);
        btn_scan.setOnClickListener(this);

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
        TextView draw_name = (TextView) headerView.findViewById(R.id.lbl_draw_name);
        draw_name.setText(dh.getpFName() + " " + dh.getpLName());
        personalQR = headerView.findViewById(R.id.personal_qr);
        personalQR.setImageBitmap(dp.createQR(dh.getpFName() + "," + dh.getpLName() + "," + dh.getpMName() + "," + dh.getpBday() + "," + dh.getpContact() + "," + dh.getpPosition() + "," + dh.getpEstab()));


        refresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recreate();
            }
        });


        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                SparseBooleanArray positionchecker = lv.getCheckedItemPositions();

                int count = lv.getCount();
                for(int item = count-1; item>=0; item--){

                    if(positionchecker.get(item)){
                        adapter.remove(itemList.get(item));
                        persons.remove(item);
                        Toast.makeText(SetLocation.this,"Item Delete Successfully",Toast.LENGTH_LONG).show();
                    }
                }

                positionchecker.clear();
                adapter.notifyDataSetChanged();
                return false;
            }
        });

        lv.setAdapter(adapter);

    }

    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.btn_destination)
        {
            YoYo.with(Techniques.ZoomOut).duration(1000).repeat(0).playOn(lo_location);
            lo_location.setVisibility(View.GONE);
            YoYo.with(Techniques.ZoomIn).duration(1000).repeat(0).playOn(lo_companion);
            lo_companion.setVisibility(View.VISIBLE);
            dm.displayMessage(getApplicationContext(), edt_destination.getText()+"");
        }
        else if(v.getId()==R.id.btn_addCompanion)
        {
            Dbread dbread = new Dbread();
            dbread.execute();
        }
        else if(v.getId()==R.id.btn_scanner)
        {
            scanCode();
        }

    }

    private class Dbinsert extends AsyncTask<String, String, String>
    {
        boolean isSuccess=false;
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
                    con.createStatement().executeUpdate("INSERT into travel_history (destination, driver_id, companion_id, plate_number, parent_id, time_boarded, date_boarded) VALUES('"+ edt_destination.getText().toString() +"', '"+ travelinfo.get(0) +"', '"+ dh.getUserid() +"', '"+ travelinfo.get(1) +"', '"+ dh.getUserid() +"', '"+ timeformatter.format(timestamp) +"', '"+ dateformatter.format(timestamp) +"')");

                    if(!persons.isEmpty())
                    {
                        for(int x=0;x<persons.size();x++)
                        {
                            con.createStatement().executeUpdate("INSERT into travel_history (destination, driver_id, companion_id, plate_number, parent_id, time_boarded, date_boarded) VALUES('"+ edt_destination.getText().toString() +"', '"+ travelinfo.get(0) +"', '"+ persons.get(x) +"', '"+ travelinfo.get(1) +"', '"+ dh.getUserid() +"', '"+ timeformatter.format(timestamp) +"', '"+ dateformatter.format(timestamp) +"')");
                        }
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
            timestamp = new Timestamp(System.currentTimeMillis());

            tokenizer = new StringTokenizer(qrscan, ",");
            while (tokenizer.hasMoreTokens())
            {
                travelinfo.add(tokenizer.nextToken());
            }

            lo_locationviewer.setVisibility(View.GONE);
            pbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String a){

            if(isSuccess==true)
            {
                recreate();
            }
            else
            {
                dp.toasterlong(getApplicationContext(), msger+"");
                lo_locationviewer.setVisibility(View.VISIBLE);
                pbar.setVisibility(View.GONE);
            }
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

                    ResultSet rs=con.createStatement().executeQuery("select * from user_profile where concat(firstname,' ',lastname) like '"+ edt_companion.getText().toString() +"' ");

                    if(rs.isBeforeFirst())
                    {
                        isSuccess=true;
                        while (rs.next())
                        {
                            getthoseid = rs.getInt("account_id");
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
            lo_locationviewer.setVisibility(View.GONE);
            pbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String a){

            if (isSuccess==true)
            {
                persons.add(getthoseid);
                itemList.add(edt_companion.getText()+"");
                edt_companion.setText("");
                adapter.notifyDataSetChanged();
            }
            else
            {
                dp.toasterlong(getApplicationContext(), "Name not found");
            }

            dm.displayMessage(getApplicationContext(), edt_companion.getText()+"");
            lo_locationviewer.setVisibility(View.VISIBLE);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
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
                        Dbinsert dbinsert = new Dbinsert();
                        dbinsert.execute();
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
            Intent startIntent=new Intent(SetLocation.this, UserDriverProfile.class);
            startActivity(startIntent);
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
            Intent startIntent=new Intent(SetLocation.this, Login.class);
            startActivity(startIntent);
            finish();
        }
        return true;

    }
}