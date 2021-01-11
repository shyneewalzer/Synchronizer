package com.example.mobileapplicationproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.mobileapplicationproject.DataController.ConnectionController;
import com.example.mobileapplicationproject.DataController.DataHolder;
import com.example.mobileapplicationproject.DataController.DataProcessor;
import com.example.mobileapplicationproject.DataController.DebugMode;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class SetLocation extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener{

    APIInterface apiInterface;
    ArrayList<String> fname;
    ArrayList<String> mname;
    ArrayList<String> lname;
    ArrayList<String> contact;
    ArrayList<String> adr;

    ArrayList<String>personinfo;
    List<List<String>>personlists;

    ArrayList<String>travelinfo;

    CustomAdapter customAdapter;

    String qrscan, batch;

    SimpleDateFormat timeformatter;
    SimpleDateFormat dateformatter;
    SimpleDateFormat batchformatter;
    Timestamp timestamp;

    ConnectionController cc = new ConnectionController();
    DataHolder dh = new DataHolder();
    DataProcessor dp = new DataProcessor();
    DebugMode dm = new DebugMode();

    ///////////UI ELEMENTS//////////
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    NavigationView navigationView;
    ImageView personalQR;

    LinearLayout lo_locationviewer;
    LinearLayout lo_location, lo_companion;

    ListView lv;

    ProgressBar pbar;
    SwipeRefreshLayout refresher;

    Button btn_scan, btn_destination, btn_addCompanion;

    TextInputEditText edt_destination;
    TextView tv_location;

    TextInputEditText edt_cFname, edt_cMname, edt_cLname, edt_cContact, edt_cAdr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        setContentView(R.layout.set_location);

        lv = (ListView) findViewById(R.id.listview);
        refresher = findViewById(R.id.lo_refresher);
        pbar = findViewById(R.id.pbar);

        lo_locationviewer = findViewById(R.id.locationviewer);
        lo_location = findViewById(R.id.lo_location);
        lo_companion = findViewById(R.id.lo_companion);

        btn_scan = findViewById(R.id.btn_scanner);
        btn_destination = findViewById(R.id.btn_destination);
        btn_addCompanion = findViewById(R.id.btn_addCompanion);

        tv_location = findViewById(R.id.tv_location);

        edt_destination = findViewById(R.id.edt_destination);

        edt_cFname = findViewById(R.id.edt_cFname);
        edt_cMname = findViewById(R.id.edt_cMname);
        edt_cLname = findViewById(R.id.edt_cLname);
        edt_cContact = findViewById(R.id.edt_cContact);
        edt_cAdr = findViewById(R.id.edt_cAdr);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Destination");

        drawerLayout = findViewById(R.id.drawer);
        drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open_drawer,R.string.close_drawer);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.setDrawerIndicatorEnabled(true);//hamburger icon
        drawerToggle.syncState();

        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView draw_name = (TextView) headerView.findViewById(R.id.lbl_draw_name);
        draw_name.setText(dh.getpFName() + " " + dh.getpLName());
        personalQR = headerView.findViewById(R.id.personal_qr);
        personalQR.setImageBitmap(dp.createQR(dh.getpFName() + "," + dh.getpLName() + "," + dh.getpMName() + "," + dh.getpBday() + "," + dh.getpContact() + "," + dh.getpPosition() + "," + dh.getpEstab()));

        fname = new ArrayList<>();
        mname = new ArrayList<>();
        lname = new ArrayList<>();
        contact = new ArrayList<>();
        adr = new ArrayList<>();

        personinfo = new ArrayList<>();
        personlists = new ArrayList<List<String>>();

        timeformatter = new SimpleDateFormat("HH:mm");
        dateformatter = new SimpleDateFormat("yyyy-MM-dd");
        batchformatter = new SimpleDateFormat("yyyyMMddHHmmss");

        btn_destination.setOnClickListener(this);
        btn_addCompanion.setOnClickListener(this);
        btn_scan.setOnClickListener(this);


        customAdapter = new CustomAdapter();
        lv.setAdapter(customAdapter);

        refresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recreate();
            }
        });


        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                SparseBooleanArray positionchecker = lv.getCheckedItemPositions();

                int count = lv.getCount();
                for(int item = count-1; item>=0; item--){

                    if(positionchecker.get(item)){
                        fname.remove(item);
                        mname.remove(item);
                        lname.remove(item);
                        contact.remove(item);
                        adr.remove(item);

                        personlists.remove(item);

                        customAdapter.notifyDataSetChanged();
                        dp.toasterlong(getApplicationContext(), "Item Delete Successfully");
                    }
                }

                positionchecker.clear();
                customAdapter.notifyDataSetChanged();
                return false;
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox cb = (CheckBox)view.findViewById(R.id.list_cb);

                if(cb.isChecked()==false)
                {
                    cb.setChecked(true);
                }
                else
                {
                    cb.setChecked(false);
                }
            }
        });

    }

    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.btn_destination)
        {
            YoYo.with(Techniques.ZoomOut).duration(1000).repeat(0).playOn(lo_location);
            lo_location.setVisibility(View.GONE);
            YoYo.with(Techniques.ZoomIn).duration(1000).repeat(0).playOn(lo_companion);
            lo_companion.setVisibility(View.VISIBLE);
            dm.displayMessage(getApplicationContext(), edt_destination.getText()+"");
        }
        else if(v.getId()==R.id.btn_addCompanion)
        {
            fname.add(edt_cFname.getText()+"");
            mname.add(edt_cMname.getText()+"");
            lname.add(edt_cLname.getText()+"");
            contact.add(edt_cContact.getText()+"");
            adr.add(edt_cAdr.getText()+"");

            personinfo.clear();
            personinfo.add(edt_cFname.getText()+"");
            personinfo.add(edt_cMname.getText()+"");
            personinfo.add(edt_cLname.getText()+"");
            personinfo.add(edt_cContact.getText()+"");
            personinfo.add(edt_cAdr.getText()+"");
            personlists.add(new ArrayList<>(personinfo));

            customAdapter.notifyDataSetChanged();

            edt_cFname.setText("");
            edt_cMname.setText("");
            edt_cLname.setText("");
            edt_cContact.setText("");
            edt_cAdr.setText("");
        }
        else if(v.getId()==R.id.btn_scanner)
        {
            scanCode();
        }

    }

    private class Dbinsert extends AsyncTask<String, String, String>
    {
        boolean isSuccess=false;
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
                    if(personlists.size()>0)
                    {
                        for(int x = 0; x<personlists.size();x++)
                        {
                            con.createStatement().executeUpdate("INSERT into travel_history (batch, firstname, middlename, lastname, contact_number, address, destination, driver_id, plate_number, parent_id, time_boarded, date_boarded) " +
                                    "VALUES('"+ batch +"', '"+ personlists.get(x).get(0) +"', '"+ personlists.get(x).get(1) +"', '"+ personlists.get(x).get(2) +"', '"+ personlists.get(x).get(3) +"', '"+ personlists.get(x).get(4) +"', '"+ edt_destination.getText() +"', '"+  travelinfo.get(0) +"', '"+ travelinfo.get(1) +"', '"+ dh.getUserid() +"', '"+ timeformatter.format(timestamp) +"', '"+ dateformatter.format(timestamp) +"')");
                            isSuccess = true;
                        }
                    }
                    else
                    {
                        con.createStatement().executeUpdate("INSERT into travel_history (batch, destination, driver_id, plate_number, parent_id, time_boarded, date_boarded) " +
                                "VALUES('"+ batch +"', '"+ edt_destination.getText() +"', '"+  travelinfo.get(0) +"', '"+ travelinfo.get(1) +"', '"+ dh.getUserid() +"', '"+ timeformatter.format(timestamp) +"', '"+ dateformatter.format(timestamp) +"')");
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
            String temp = "";

            timestamp = new Timestamp(System.currentTimeMillis());
            travelinfo = new ArrayList<>(dp.splitter(qrscan, ","));
            batch = dh.getUserid() + batchformatter.format(timestamp);

            for (int a = 0; a < personlists.size(); a++) {
                for (int b = 0; b < personlists.get(a).size(); b++) {
                    temp = temp + personlists.get(a).get(b) + "+";
                }
                temp = temp + ".";
            }

            dm.displayMessage(getApplicationContext(),  temp+"");

            lo_locationviewer.setVisibility(View.GONE);
            pbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String a){

            if(isSuccess==true)
            {
                recreate();
            }
            else
            {
                dp.toasterlong(getApplicationContext(), msger+"");
                lo_locationviewer.setVisibility(View.VISIBLE);
                pbar.setVisibility(View.GONE);
            }
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

    class CustomAdapter extends BaseAdapter {
        @Override
        public int getCount()
        {
            return fname.size();
        }

        @Override
        public Object getItem(int i)
        {
            return null;
        }

        @Override
        public long getItemId(int i)
        {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view= getLayoutInflater().inflate(R.layout.row_companion,null);

            TextView tv_fname=(TextView)view.findViewById(R.id.list_txt_cFname);
            TextView tv_mname=(TextView)view.findViewById(R.id.list_txt_cMname);
            TextView tv_lname=(TextView)view.findViewById(R.id.list_txt_cLname);
            TextView tv_contact=(TextView)view.findViewById(R.id.list_txt_cContact);
            TextView tv_adr=(TextView)view.findViewById(R.id.list_txt_cAdr);
            CheckBox cb = (CheckBox)view.findViewById(R.id.list_cb);


            tv_fname.setText(fname.get(i));
            tv_mname.setText(mname.get(i));
            tv_lname.setText(lname.get(i));
            tv_contact.setText(tv_contact.getText() + contact.get(i));
            tv_adr.setText(tv_adr.getText() + adr.get(i));


            return view;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.prof)
        {
            Intent startIntent=new Intent(SetLocation.this, UserDriverProfile.class);
            startActivity(startIntent);
            finish();
        }
        else if(item.getItemId()==R.id.group)
        {
            Intent startIntent=new Intent(SetLocation.this, SetLocationGroup.class);
            startActivity(startIntent);
            finish();
        }

        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int optid=item.getItemId();

        if(optid==R.id.about)
        {
            dp.toasterlong(getApplicationContext(), "about");
        }
        else if(optid==R.id.logout)
        {
            Intent startIntent=new Intent(SetLocation.this, Login.class);
            startActivity(startIntent);
            finish();
        }
        return true;

    }
}