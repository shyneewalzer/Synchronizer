package com.example.mobileapplicationproject;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mobileapplicationproject.DataController.ConnectionController;
import com.example.mobileapplicationproject.DataController.DataHolder;
import com.example.mobileapplicationproject.DataController.DataProcessor;
import com.example.mobileapplicationproject.DataController.DebugMode;
import com.google.android.material.textfield.TextInputEditText;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Calendar;

import at.favre.lib.crypto.bcrypt.BCrypt;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class FragmentPasswordUDE extends Fragment implements View.OnClickListener{

    View fragpass;
    ConnectionController cc = new ConnectionController();
    DataHolder dh = new DataHolder();
    DataProcessor dp = new DataProcessor();
    DebugMode dm = new DebugMode();


    ///////////////////UI ELEMENTS////////////////

    TextInputEditText edt_oldpassword, edt_newpassword, edt_confirmpassword;
    Button btn_changepassword;

    ProgressBar pbar;
    LinearLayout lo_profileviewer;


    public FragmentPasswordUDE() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        fragpass = inflater.inflate(R.layout.fragment_password_ud, container, false);

        edt_oldpassword = fragpass.findViewById(R.id.edt_oldpassword);
        edt_newpassword = fragpass.findViewById(R.id.edt_newpassword);
        edt_confirmpassword = fragpass.findViewById(R.id.edt_confirmpassword);
        btn_changepassword = fragpass.findViewById(R.id.btn_changepassword);
        lo_profileviewer = fragpass.findViewById(R.id.lo_passwordviewer);
        pbar = fragpass.findViewById(R.id.pbar);

        btn_changepassword.setOnClickListener(this);

        return fragpass;
    }

    private class Dbupdate extends AsyncTask<String, String, String>
    {
        BCrypt.Result brs;
        String msger;
        Boolean isSuccess=false;
        String sqler="";

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
                        sqler = dh.getEstAcctID();
                    }
                    else
                    {
                        sqler = dh.getUserid()+"";
                    }

                    ResultSet rs=con.createStatement().executeQuery("select * from accounts_table where account_id = '"+ sqler +"' ");

                    while (rs.next())
                    {
                        brs = BCrypt.verifyer().verify(edt_oldpassword.getText().toString().toCharArray(), rs.getString("password"));

                        if(brs.verified==true)
                        {
                            con.createStatement().executeUpdate("UPDATE accounts_table SET password = '" + BCrypt.withDefaults().hashToString(10, edt_newpassword.getText().toString().toCharArray()) + "' where account_id = '"+ dh.getUserid() +"' ");
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
            lo_profileviewer.setVisibility(View.GONE);
            pbar.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(String a){

            if(isSuccess==true)
            {
                dp.toasterlong(getContext(), "Account Password Updated");
                edt_oldpassword.setText("");
                edt_newpassword.setText("");
                edt_confirmpassword.setText("");
            }
            else
            {
                dp.toasterlong(getContext(), "Invalid Password");
            }
            lo_profileviewer.setVisibility(View.VISIBLE);
            pbar.setVisibility(View.GONE);

        }
    }

    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.btn_changepassword)
        {
            if(edt_oldpassword.getText().toString().trim().isEmpty() || edt_newpassword.getText().toString().trim().isEmpty() || edt_confirmpassword.getText().toString().trim().isEmpty())
            {
                dp.toasterlong(getContext(), "Please enter password");
            }
            else if(!edt_newpassword.getText().toString().equals(edt_confirmpassword.getText().toString()))
            {
                dp.toasterlong(getContext(), "Password do not match!");
            }
            else
            {
                Dbupdate dbupdate = new Dbupdate();
                dbupdate.execute();
            }
        }

    }

}
