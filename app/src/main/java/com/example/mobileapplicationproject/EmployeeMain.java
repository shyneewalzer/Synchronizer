package com.example.mobileapplicationproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobileapplicationproject.DataController.ConnectionController;
import com.example.mobileapplicationproject.DataController.DataHolder;
import com.example.mobileapplicationproject.DataController.DataProcessor;
import com.example.mobileapplicationproject.DataController.DebugMode;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class EmployeeMain extends AppCompatActivity implements View.OnClickListener, PasswordDialog.PasswordDialogListener {

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

    String testPassword;

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

    private class Dbread extends AsyncTask<String, String, String>
    {
        BCrypt.Result brs;
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
                    ResultSet rs=con.createStatement().executeQuery("select * from accounts_table where account_id = '"+ dh.getEstAcctID() +"' ");

                    while (rs.next())
                    {
                        brs = BCrypt.verifyer().verify(testPassword.toCharArray(), rs.getString("password"));

                        if(brs.verified==true)
                        {
                            isSuccess=true;
                        }
                        else
                        {
                            isSuccess=false;
                            msger = "Invalid Password!";
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

        @SuppressLint("WrongThread")
        @Override
        protected void onPreExecute() {
            lo_employeeviewer.setVisibility(View.GONE);
            pbar.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(String a){

            if(isSuccess==true)
            {
                testPassword="";
                Intent startIntent = new Intent(EmployeeMain.this, EstabDashboard.class);
                startActivity(startIntent);
                finish();
            }
            else
            {
                dp.toasterlong(getApplicationContext(), "Invalid Password");
            }
            lo_employeeviewer.setVisibility(View.VISIBLE);
            pbar.setVisibility(View.GONE);

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
                            con.createStatement().executeUpdate("INSERT into employee_scanned (batch, firstname, lastname, contact_number, est_id, employee_id, account_id, time_entered, date_entered) " +
                                    "VALUES('"+ batch +"', '"+ personlists.get(x).get(0) +"', '"+ personlists.get(x).get(1) +"', '"+ personlists.get(x).get(2) +"', '"+ dh.getEstID() +"', '"+  dh.getUserid() +"', '"+ idholder +"', '"+ timeformatter.format(timestamp) +"', '"+ dateformatter.format(timestamp) +"')");
                            isSuccess = true;
                        }
                    }
                    else
                    {
                        con.createStatement().executeUpdate("INSERT into employee_scanned (batch, est_id, employee_id, account_id, time_entered, date_entered) VALUES('"+ batch +"', '"+ idholder +"', '"+ dh.getUserid() +"', '"+  idholder +"', '"+ timeformatter.format(timestamp) +"', '"+ dateformatter.format(timestamp) +"')");
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

    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.btn_escanner)
        {
            scanCode();
        }
        else if(v.getId()==R.id.btn_timeout)
        {
            PasswordDialog passwordDialog = new PasswordDialog();
            passwordDialog.show(getSupportFragmentManager(), "password dialog");

        }

    }

    @Override
    public void getpassword(String password) {
        dm.displayMessage(getApplicationContext(), password+"");
        testPassword = password;
        Dbread dbread = new Dbread();
        dbread.execute();
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