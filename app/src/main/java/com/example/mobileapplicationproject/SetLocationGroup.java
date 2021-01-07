package com.example.mobileapplicationproject;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobileapplicationproject.DataController.ConnectionController;
import com.example.mobileapplicationproject.DataController.DataHolder;
import com.example.mobileapplicationproject.DataController.DataProcessor;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class SetLocationGroup extends AppCompatActivity {
    private static final String FILE_NAME = "example.txt";
    private TextInputEditText itemText;
    ImageView qrbox;
    APIInterface apiInterface;
    ArrayList<String> itemList;
    ArrayAdapter<String> adapter;
    Button addEmail, setProfile;
    ListView lv;
    String temp="";

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
    TextView txtsign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        setContentView(R.layout.set_location_group);
        lv = (ListView) findViewById(R.id.listview);
        itemText = (TextInputEditText) findViewById(R.id.emailInsert);
        qrbox = findViewById(R.id.scanbox);
        txtsign = findViewById(R.id.txtsign);
        addEmail = (Button) findViewById(R.id.addEmail);
        setProfile = (Button) findViewById(R.id.setProfile);
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_menu);
        bottomNavigationView.setSelectedItemId(R.id.group_driver);
        itemList = new ArrayList<>();
        persons = new ArrayList<>();
        adapter = new ArrayAdapter<String>(SetLocationGroup.this , android.R.layout.simple_list_item_multiple_choice,itemList);

        timeformatter = new SimpleDateFormat("HH:mm");
        dateformatter = new SimpleDateFormat("yyyy-MM-dd");
        temp=dh.getUserid()+",";
        locationviewer = findViewById(R.id.locationviewer);
        pbar = findViewById(R.id.pbar);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.location_driver:
                        startActivity(new Intent(getApplicationContext()
                                , SetLocation.class));
                        overridePendingTransition(0,0);
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
                        Toast.makeText(SetLocationGroup.this,"Item Delete Successfully",Toast.LENGTH_LONG).show();
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

                for(int x = 0 ; x<persons.size();x++)
                {
                    temp = temp + persons.get(x) + ",";
                }

                txtsign.setVisibility(View.INVISIBLE);
                createQR(temp);

            }
        });
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


    private void createQR(String modelName) {
        Toast.makeText(getApplicationContext(),  "" +modelName, Toast.LENGTH_LONG).show();

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

        try {
            BitMatrix bitMatrix = multiFormatWriter.encode( modelName, BarcodeFormat.QR_CODE,500,500);
            BarcodeEncoder barcodeEncoder =  new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            qrbox.setImageBitmap(bitmap);
        }catch (Exception e){
            e.printStackTrace();
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
            Intent startIntent=new Intent(SetLocationGroup.this, Login.class);
            startActivity(startIntent);
            finish();
        }

        return true;

    }
}