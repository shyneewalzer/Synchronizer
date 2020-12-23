package com.example.mobileapplicationproject;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mobileapplicationproject.DataController.ConnectionController;
import com.example.mobileapplicationproject.DataController.DataHolder;
import com.example.mobileapplicationproject.DataController.DataProcessor;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class SetLocation extends AppCompatActivity {
    private static final String FILE_NAME = "example.txt";
    private TextInputEditText itemText, location;
    APIInterface apiInterface;
    ArrayList<String> itemList;
    ArrayAdapter<String> adapter;
    Button addEmail, setProfile;
    ListView lv;
    String temp;

    SimpleDateFormat timeformatter;
    SimpleDateFormat dateformatter;
    Timestamp timestamp;

    ConnectionController cc = new ConnectionController();
    DataHolder dh = new DataHolder();
    DataProcessor dp = new DataProcessor();
    ArrayList<Integer> persons;
    String emailchecker;
    int getthoseid;

    LinearLayout locationviewer;
    ProgressBar pbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        setContentView(R.layout.set_location);
        lv = (ListView) findViewById(R.id.listview);
        itemText = (TextInputEditText) findViewById(R.id.emailRegister);
        location = (TextInputEditText) findViewById(R.id.location);
        addEmail = (Button) findViewById(R.id.addEmail);
        setProfile = (Button) findViewById(R.id.setProfile);
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_menu);
        bottomNavigationView.setSelectedItemId(R.id.location);
        itemList = new ArrayList<>();
        persons = new ArrayList<>();
        adapter = new ArrayAdapter<String>(SetLocation.this , android.R.layout.simple_list_item_multiple_choice,itemList);

        timeformatter = new SimpleDateFormat("HH:mm");
        dateformatter = new SimpleDateFormat("yyyy-MM-dd");

        locationviewer = findViewById(R.id.locationviewer);
        pbar = findViewById(R.id.pbar);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.location_driver:
                        return true;
                    case R.id.profile_driver:
                        startActivity(new Intent(getApplicationContext()
                                , UserDriverProfile.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.home_driver:
                        startActivity(new Intent(getApplicationContext()
                                , UserDriverDashboard.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.history_driver:
                        startActivity(new Intent(getApplicationContext()
                                , TravelHistory.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.group_driver:
                        startActivity(new Intent(getApplicationContext()
                                , SetLocationGroup.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        View.OnClickListener addlistner = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Dbread dbread = new Dbread();
                dbread.execute();

            }
        };

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


        addEmail.setOnClickListener(addlistner);
        lv.setAdapter(adapter);

        setProfile.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
//                addTravel();

                Dbinsert dbinsert = new Dbinsert();
                dbinsert.execute();

            }
        });
    }

    private class Dbinsert extends AsyncTask<String, String, String>
    {

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
                    con.createStatement().executeUpdate("INSERT into travel_history (destination, user_id, isCompanion, time_created, date_created) VALUES('"+ location.getText() +"', '"+ dh.getUserid() +"', '0', '"+ timeformatter.format(timestamp) +"', '"+ dateformatter.format(timestamp) +"')");

                    if(!persons.isEmpty())
                    {
                        for(int x=0;x<persons.size();x++)
                        {
                            con.createStatement().executeUpdate("INSERT into travel_history (destination, user_id, isCompanion, time_created, date_created) VALUES('"+ location.getText() +"', '"+ persons.get(x) +"', '0', '"+ timeformatter.format(timestamp) +"', '"+ dateformatter.format(timestamp) +"')");
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
            locationviewer.setVisibility(View.GONE);
            pbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String a){
            Intent myIntent = new Intent(SetLocation.this, UserDriverDashboard.class);
            startActivity(myIntent);

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

                    ResultSet rs=con.createStatement().executeQuery("select * from accounts_table where email = '"+ emailchecker +"' ");

                    if(rs.isBeforeFirst())
                    {
                        isSuccess=true;
                        while (rs.next())
                        {
                            getthoseid = rs.getInt(1);
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
            emailchecker=itemText.getText().toString();
            locationviewer.setVisibility(View.GONE);
            pbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String a){

            if (isSuccess==true)
            {
                persons.add(getthoseid);
                itemList.add(itemText.getText().toString());
                itemText.setText("");
                adapter.notifyDataSetChanged();
            }
            else
            {
                dp.toasterlong(getApplicationContext(), "Email not yet registered!");
            }

            dp.toasterlong(getApplicationContext(), emailchecker+"");
            locationviewer.setVisibility(View.VISIBLE);
            pbar.setVisibility(View.GONE);
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

        if(optid==R.id.logout)
        {
            Intent startIntent=new Intent(SetLocation.this, Login.class);
            startActivity(startIntent);
            finish();
        }

        return true;

    }
}