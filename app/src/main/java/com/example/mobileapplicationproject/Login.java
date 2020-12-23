package com.example.mobileapplicationproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobileapplicationproject.DataController.ConnectionController;
import com.example.mobileapplicationproject.DataController.DataHolder;
import com.example.mobileapplicationproject.DataController.DataProcessor;
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


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailInput.getText()+"";
                password = passwordInput.getText()+"";
//                email = "kamusta@gmail.com";
//                password = "Jaudian29";
                if(email.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please Input Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if ( password.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please Input Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                Dblogin dblogin = new Dblogin();
                dblogin.execute();
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
                                dh.setAcct(rs.getInt(1), rs.getString(2), rs.getString(4));
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

            btns.setVisibility(View.GONE);
            pbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String a){

            if(isSuccess==true)
            {

                if(dh.getType().equals("User") || dh.getType().equals("Driver"))
                {
                    Intent myIntent = new Intent(Login.this, UserDriverDashboard.class);
                    startActivity(myIntent);
                }
                else if(dh.getType().equals("Employee"))
                {
                    Intent myIntent = new Intent(Login.this, MainMenuFormEmployee.class);
                    startActivity(myIntent);
                }

                finish();
                dp.toastershort(getApplicationContext(), "Logged In Successfully");
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