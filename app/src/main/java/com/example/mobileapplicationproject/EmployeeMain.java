package com.example.mobileapplicationproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobileapplicationproject.DataController.ConnectionController;
import com.example.mobileapplicationproject.DataController.DataHolder;
import com.example.mobileapplicationproject.DataController.DataProcessor;
import com.example.mobileapplicationproject.DataController.DebugMode;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EmployeeMain extends AppCompatActivity implements View.OnClickListener{

    ConnectionController cc = new ConnectionController();
    DataHolder dh = new DataHolder();
    DataProcessor dp = new DataProcessor();
    DebugMode dm = new DebugMode();

    String qrscan, idholder, batch;
    ArrayList<String> qrholder;
    ArrayList<String>tempholder;
    ArrayList<String> personinfo;
    List<List<String>>personlists;

    SimpleDateFormat timeformatter;
    SimpleDateFormat dateformatter;
    SimpleDateFormat batchformatter;
    Timestamp timestamp;

    //////////////UI ELEMENTS////////////////
    Toolbar toolbar;

    LinearLayout lo_employeeviewer;
    ProgressBar pbar;

    TextView txt_fullname, txt_estab;
    ImageView img_scanbox;
    Button btn_timeout, btn_escanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.employee_main);

        lo_employeeviewer = findViewById(R.id.lo_employeeviewer);
        pbar = findViewById(R.id.pbar);
        txt_fullname = findViewById(R.id.txt_fullname);
        txt_estab = findViewById(R.id.txt_estab);
        img_scanbox = findViewById(R.id.img_scanbox);
        btn_timeout = findViewById(R.id.btn_timeout);
        btn_escanner = findViewById(R.id.btn_escanner);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        btn_timeout.setOnClickListener(this);
        btn_escanner.setOnClickListener(this);

        timeformatter = new SimpleDateFormat("HH:mm");
        dateformatter = new SimpleDateFormat("yyyy-MM-dd");
        batchformatter = new SimpleDateFormat("yyyyMMddHHmmss");

        txt_fullname.setText(dh.getpFName() + " " + dh.getpLName());
        txt_estab.setText(dh.getEstName());
        img_scanbox.setImageBitmap(dp.createQR(dh.getUserid() + "," + dh.getEstID()));

    }


    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.btn_escanner)
        {
            scanCode();
        }

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
                    if(personlists.size()>0)
                    {
                        for(int x = 0; x<personlists.size();x++)
                        {
                            con.createStatement().executeUpdate("INSERT into employee_scanned (batch, firstname, middlename, lastname, contact_number, address, employee_id, account_id, time_entered, date_entered) " +
                                    "VALUES('"+ batch +"', '"+ personlists.get(x).get(0) +"', '"+ personlists.get(x).get(1) +"', '"+ personlists.get(x).get(2) +"', '"+ personlists.get(x).get(3) +"', '"+ personlists.get(x).get(4) +"', '"+ dh.getUserid() +"', '"+  idholder +"', '"+ timeformatter.format(timestamp) +"', '"+ dateformatter.format(timestamp) +"')");
                            isSuccess = true;
                        }
                    }
                    else
                    {
                        con.createStatement().executeUpdate("INSERT into employee_scanned (batch, employee_id, account_id, time_entered, date_entered) VALUES('"+ batch +"', '"+ dh.getUserid() +"', '"+  idholder +"', '"+ timeformatter.format(timestamp) +"', '"+ dateformatter.format(timestamp) +"')");
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

            timestamp = new Timestamp(System.currentTimeMillis());
            String temp="";

            tempholder = new ArrayList<>();
            personinfo = new ArrayList<>();
            personlists = new ArrayList<List<String>>();

            tempholder = new ArrayList<>(dp.splitter(qrscan, "#"));
            idholder = tempholder.get(0);
            if(tempholder.size()>1)
            {
                qrholder = dp.splitter(tempholder.get(1), ",");
                for (int x = 0; x < qrholder.size(); x++) {
                    personlists.add(new ArrayList<>(dp.splitter(qrholder.get(x), "_")));
                }

                for (int a = 0; a < personlists.size(); a++) {
                    for (int b = 0; b < personlists.get(a).size(); b++) {
                        temp = temp + personlists.get(a).get(b) + "+";
                    }
                    temp = temp + ".";
                }
            }
            batch = idholder + batchformatter.format(timestamp);
            dm.displayMessage(getApplicationContext(), "id=" + idholder + " " + temp+"");

            pbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String a){
//            Intent myIntent = new Intent(SetLocation.this, UserDriverDashboard.class);
//            startActivity(myIntent);

            if(isSuccess==true)
            {
                dp.toastershort(getApplicationContext(), "Data Successfully Sent");
            }
            else if(isSuccess==false)
            {
                dp.toastershort(getApplicationContext(), msger);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int optid=item.getItemId();

        if(optid==R.id.editemp)
        {
            Intent startIntent=new Intent(EmployeeMain.this, EmployeeeProfile.class);
            startActivity(startIntent);
        }

        return true;

    }


}