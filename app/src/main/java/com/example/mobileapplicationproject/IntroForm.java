package com.example.mobileapplicationproject;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mobileapplicationproject.DataController.ConnectionController;
import com.example.mobileapplicationproject.DataController.DataHolder;
import com.example.mobileapplicationproject.DataController.DataProcessor;
import com.example.mobileapplicationproject.DataController.DebugMode;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class IntroForm extends AppCompatActivity {

    ConnectionController cc = new ConnectionController();
    DataHolder dh = new DataHolder();
    DataProcessor dp = new DataProcessor();
    DebugMode dm = new DebugMode();

    ArrayList<String> listroutes;
    ArrayList<String> listdestination;
    ArrayList<String> listbrgy;

    Handler handler;
    Runnable runnable;

    ProgressBar pbar;
    TextView txt_loader;
    Button btn_retry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_form);

        pbar = findViewById(R.id.pbar);
        txt_loader = findViewById(R.id.txt_loader);
        btn_retry = findViewById(R.id.btn_retry);

        btn_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dbreadroute dbreadroute = new Dbreadroute();
                dbreadroute.execute();
            }
        });

        Dbreadroute dbreadroute = new Dbreadroute();
        dbreadroute.execute();

    }

    private class Dbreadroute extends AsyncTask<String, String, String>
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
                    ResultSet rs=con.createStatement().executeQuery("select * from vehicle_routes");

                    while (rs.next())
                    {
                        listroutes.add(rs.getString("vr_routes"));
                    }
                    isSuccess = true;
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

            pbar.setVisibility(View.VISIBLE);

            listroutes = new ArrayList<>();

            txt_loader.setText("Loading route references");

        }

        @Override
        protected void onPostExecute(String a){

            pbar.setVisibility(View.GONE);

            if(isSuccess==true)
            {
                Dbreaddestination dbreaddestination = new Dbreaddestination();
                dbreaddestination.execute();
            }
            else
            {
                txt_loader.setText(msger+"");
                btn_retry.setVisibility(View.VISIBLE);
            }

        }
    }

    private class Dbreaddestination extends AsyncTask<String, String, String>
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
                    ResultSet rs=con.createStatement().executeQuery("select * from locations");

                    while (rs.next())
                    {
                        String destinationchecker = rs.getString("destination");
                        if(destinationchecker!=null && !destinationchecker.isEmpty())
                        {
                            listdestination.add(destinationchecker);
                        }
                        String brgychecker = rs.getString("barangay");
                        if(brgychecker!=null && !brgychecker.isEmpty())
                        {
                            listbrgy.add(brgychecker);
                        }
                    }
                    isSuccess = true;
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

            btn_retry.setVisibility(View.GONE);

            listdestination = new ArrayList<>();
            listbrgy = new ArrayList<>();

            txt_loader.setText("Loading location references");
            pbar.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(String a){

            if(isSuccess==true)
            {
                txt_loader.setText("Lets Start!");
                Collections.sort(listroutes);
                Collections.sort(listdestination);
                Collections.sort(listbrgy);
                dh.setLocations(listroutes, listdestination, listbrgy);

                handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent dsp = new Intent(IntroForm.this, Login.class);
                        startActivity(dsp);
                        finish();
                    }
                }, 1000);//3000

            }
            else
            {
                txt_loader.setText(msger+"");
                btn_retry.setVisibility(View.VISIBLE);
            }
            pbar.setVisibility(View.GONE);

        }
    }
}