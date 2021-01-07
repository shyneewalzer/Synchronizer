package com.example.mobileapplicationproject.DataController;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionController {

    String clazz="com.mysql.jdbc.Driver";

//    String url="jdbc:mysql://148.66.138.153/capstone_db";
//    String dbusername="cliljdn";
//    String dbpassword="jaudian29";

    String url="jdbc:mysql://192.168.254.125/capstonedb";
    String dbusername = "nath";
    String dbpassword = "nath";



    @SuppressLint("NewApi")
    public Connection CONN()
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn=null;
        String ConnURL=null;
        try{
            Class.forName(clazz);
            conn= DriverManager.getConnection(url,dbusername,dbpassword);
            //conn=DriverManager.getConnection(ConnURL);
        }
        catch (SQLException se) {
            Log.e("ERRO", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("ERRO", e.getMessage());
        } catch (Exception e) {
            Log.e("ERRO", e.getMessage());
        }
        return conn;
    }
}
