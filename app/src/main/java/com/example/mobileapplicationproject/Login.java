package com.example.mobileapplicationproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobileapplicationproject.DataController.ConnectionController;
import com.example.mobileapplicationproject.DataController.DataHolder;
import com.example.mobileapplicationproject.DataController.DataProcessor;
import com.example.mobileapplicationproject.DataController.DebugMode;
import com.google.android.material.textfield.TextInputEditText;

import java.sql.Connection;
import java.sql.ResultSet;

import at.favre.lib.crypto.bcrypt.BCrypt;


public class Login extends AppCompatActivity {
    private static final String FILE_NAME = "example.txt";
    APIInterface apiInterface;
    String email, password, msger;
    TextInputEditText emailInput, passwordInput;
    Button loginButton;
    DataHolder cd = new DataHolder();
    private TextView textViewResult;

    ConnectionController cc = new ConnectionController();
    DataHolder dh = new DataHolder();
    DataProcessor dp = new DataProcessor();
    DebugMode dm = new DebugMode();

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    ProgressBar pbar;
    LinearLayout btns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        emailInput = (TextInputEditText) findViewById(R.id.emailInput);
        passwordInput = (TextInputEditText) findViewById(R.id.passwordInput);
        loginButton = (Button) findViewById(R.id.loginMain);
        textViewResult = findViewById(R.id.text_view_result);

        pbar = findViewById(R.id.pbar);
        btns = findViewById(R.id.grpbuttons);

        sharedpreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String sessionemail= sharedpreferences.getString("Email",null);
        String sessionpass= sharedpreferences.getString("Password",null);

        if(sessionemail!=null || sessionpass!=null) {
            emailInput.setText(sessionemail);
            passwordInput.setText(sessionpass);
            email = sessionemail;
            password = sessionpass;
            Dblogin dblogin = new Dblogin();
            dblogin.execute();
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailInput.getText()+"";
                password = passwordInput.getText()+"";

                if(email.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please Input Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    emailInput.setError("Please enter a valid email address");
                }
                else if ( password.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please Input Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    Dblogin dblogin = new Dblogin();
                    dblogin.execute();
                }

            }
        });

    }

    private class Dblogin extends AsyncTask<String, String, String>
    {
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

                    ResultSet rs=con.createStatement().executeQuery("select * from accounts_table where email = '"+ email +"' ");


                    if(rs.isBeforeFirst())
                    {
                        while (rs.next())
                        {

                            brs = BCrypt.verifyer().verify(password.toCharArray(), rs.getString(3));

                            if(brs.verified==true)
                            {
                                dh.setAcct(rs.getInt("account_id"), rs.getString("email"), rs.getString("account_type"));
                                isSuccess = true;
                            }
                            else
                            {
                                isSuccess=false;
                                msger="Wrong Password";
                            }
                        }
                    }
                    else
                    {
                        isSuccess = false;
                        msger="Email not registered";
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
            Log.d("credit", email + " " + password);
            btns.setVisibility(View.GONE);
            pbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String a){

            if(isSuccess==true)
            {
                Dbreadprofile dbreadprofile = new Dbreadprofile();
                dbreadprofile.execute();
            }
            else
            {
                dp.toastershort(getApplicationContext(), msger);
                btns.setVisibility(View.VISIBLE);
                pbar.setVisibility(View.INVISIBLE);
            }

        }
    }

    private class Dbreadprofile extends AsyncTask<String, String, String>
    {

        boolean isSuccess;
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

                    if(dh.getType().equals("Establishment"))
                    {
                        isSuccess=true;
                        ResultSet rs=con.createStatement().executeQuery("select * from establishments where account_id = '"+ dh.getUserid() +"' ");
                        while (rs.next())
                        {
                            dh.setEstProfile(rs.getString("account_id"), rs.getString("name"), rs.getString("street"), rs.getString("telephone_number"), rs.getString("est_owner"), rs.getString("image"), rs.getString("est_id"));
                        }
                        rs.close();
                    }
                    else
                    {
                        isSuccess=true;
                        ResultSet rs=con.createStatement().executeQuery("select * from user_profile where account_id = '"+ dh.getUserid() +"' ");
                        while (rs.next())
                        {
                            dh.setProfile(rs.getString("firstname"), rs.getString("lastname"), rs.getString("middlename"), rs.getDate("birthday"),rs.getString("contactnumber"),rs.getString("image"));
                        }
                        rs.close();

                        ResultSet rsadr=con.createStatement().executeQuery("select * from address_table where account_id = '"+ dh.getUserid() +"' ");

                        while(rsadr.next())
                        {
                            dh.setAddress(rsadr.getString("house_lot_number"), rsadr.getString("barangay"), rsadr.getString("city"));
                        }
                        rsadr.close();
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

        }

        @Override
        protected void onPostExecute(String a){

            if(isSuccess==true)
            {

                if(dh.getType().equals("Individual"))
                {
                    dh.setVisitmode("travel");//refresh activity sequence
                    Intent myIntent = new Intent(Login.this, UserDashboard.class);
                    myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(myIntent);
                    dp.toastershort(getApplicationContext(), "Hi " + dh.getpFName() + " " + dh.getpLName());
                }
                else if(dh.getType().equals("Driver"))
                {
                    Intent myIntent = new Intent(Login.this, DriverDashboard.class);
                    myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(myIntent);
                    dp.toastershort(getApplicationContext(), "Hi " + dh.getpFName() + " " + dh.getpLName());
                }
                else if(dh.getType().equals("Establishment"))
                {
                    Intent myIntent = new Intent(Login.this, EstabDashboard.class);
                    myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(myIntent);
                    dp.toastershort(getApplicationContext(), "Hi " + dh.getEstOwner());
                }
                finishAffinity();

                editor = sharedpreferences.edit();
                editor.putString("Email", emailInput.getText().toString());
                editor.putString("Password", passwordInput.getText().toString());
                editor.apply();
            }
            else
            {
                dp.toastershort(getApplicationContext(), msger);
                btns.setVisibility(View.VISIBLE);
                pbar.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void signup_main_txt(View view) {
        Intent intent = new Intent(this,RegisterForm.class);
        startActivity(intent);
    }

}