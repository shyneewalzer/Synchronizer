package com.example.mobileapplicationproject;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mobileapplicationproject.DataController.ConnectionController;
import com.example.mobileapplicationproject.DataController.DataHolder;
import com.example.mobileapplicationproject.DataController.DataProcessor;
import com.google.android.material.textfield.TextInputEditText;

import java.sql.Connection;
import java.sql.ResultSet;

import at.favre.lib.crypto.bcrypt.BCrypt;


public class AccountsForm extends AppCompatActivity {

    private TextInputEditText txtemail, txtold, txtnew, txtconfirm;
    String email, oldpass, newpass, confirmpass;

    Button changepass;
    LinearLayout profileviewer;
    ProgressBar pbar;

    ConnectionController cc = new ConnectionController();
    DataHolder dh = new DataHolder();
    DataProcessor dp = new DataProcessor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accounts_form);

        txtemail = findViewById(R.id.txt_email);
        txtold = findViewById(R.id.txt_old);
        txtnew = findViewById(R.id.txt_new);
        txtconfirm = findViewById(R.id.txt_confirm);
        changepass = findViewById(R.id.btn_changepass);

        profileviewer = findViewById(R.id.acctviewer);
        pbar = findViewById(R.id.pbar);

        txtemail.setText(dh.getEmail());

        changepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(changepass.getText().equals("CHANGE PASSWORD"))
                {
                    txtold.setEnabled(true);
                    txtnew.setEnabled(true);
                    txtconfirm.setEnabled(true);

                    changepass.setText("SAVE");
                }
                else if(changepass.getText().equals("SAVE"))
                {

                    if(txtnew.getText().toString().equals(txtconfirm.getText().toString()))
                    {
                        Dbupdate dbupdate = new Dbupdate();
                        dbupdate.execute();
                    }
                    else
                    {
                        dp.toasterlong(getApplicationContext(), "Password does not match!");
                    }
                }
            }
        });

    }

    private class Dbupdate extends AsyncTask<String, String, String>
    {

        String msger;
        Boolean isSuccess=false;
        BCrypt.Result brs;

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
                    ResultSet rs=con.createStatement().executeQuery("select * from acounts_table where email = '"+ dh.getEmail() +"' ");

                    while(rs.next())
                    {
                        brs = BCrypt.verifyer().verify(oldpass.toCharArray(), rs.getString(3));
                        if(brs.verified==true)
                        {
                            con.createStatement().executeUpdate("UPDATE accounts_table SET password = '"+ txtnew.getText() +"' where email='"+ dh.getEmail() +"' ");
                            isSuccess=true;
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

            oldpass = txtold.getText()+"";
            newpass = txtnew.getText()+"";
            confirmpass = txtconfirm.getText()+"";

            profileviewer.setVisibility(View.GONE);
            pbar.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(String a){

            if(isSuccess==true)
            {
                recreate();
                Toast.makeText(getApplicationContext(), "Password Updated", Toast.LENGTH_LONG);
            }
            else
            {

                profileviewer.setVisibility(View.VISIBLE);
                pbar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(),"Wrong Password!",Toast.LENGTH_LONG);
            }



        }
    }



}