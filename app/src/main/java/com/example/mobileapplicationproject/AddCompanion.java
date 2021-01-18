package com.example.mobileapplicationproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobileapplicationproject.DataController.ConnectionController;
import com.example.mobileapplicationproject.DataController.DataHolder;
import com.example.mobileapplicationproject.DataController.DataProcessor;
import com.example.mobileapplicationproject.DataController.DebugMode;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.sql.Connection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddCompanion extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener{

    ArrayList<String> fname;
    ArrayList<String> lname;

    ArrayList<String>personinfo;
    List<List<String>>personlists;

    ArrayList<String>estinfo;

    String qrcode ="";
    String qrscan;
    String idholder, batch;

    SimpleDateFormat timeformatter;
    SimpleDateFormat dateformatter;
    SimpleDateFormat batchformatter;
    Timestamp timestamp;

    ConnectionController cc = new ConnectionController();
    DataHolder dh = new DataHolder();
    DataProcessor dp = new DataProcessor();
    DebugMode dm = new DebugMode();

    CustomAdapter customAdapter;

    ////////////UI ELEMENTS//////////////

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    NavigationView navigationView;
    TextView draw_name, draw_type;
    CircleImageView draw_img_user;

    TextInputEditText edt_cFname, edt_cLname;

    ImageView img_scanbox;
    Button btn_eAddCompanion, btn_scan, btn_eAddCompanionCancel;
    TextView txt_companionExpander;
    ListView lv;

    LinearLayout locationviewer, lo_companionlist, lo_addcompanion;
    ProgressBar pbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_companion);

        lv = findViewById(R.id.listview);
        img_scanbox = findViewById(R.id.scanbox);
        btn_eAddCompanion = findViewById(R.id.btn_eAddCompanion);
        btn_eAddCompanionCancel = findViewById(R.id.btn_eAddCompanionCancel);
        btn_scan = findViewById(R.id.btn_scan);
        txt_companionExpander = findViewById(R.id.txt_companionExpander);
        lo_companionlist = findViewById(R.id.lo_companionlist);
        lo_addcompanion = findViewById(R.id.lo_addcompanion);

        edt_cFname = findViewById(R.id.edt_cFname);
        edt_cLname = findViewById(R.id.edt_cLname);

        fname = new ArrayList<>();
        lname = new ArrayList<>();

        personinfo = new ArrayList<>();
        personlists = new ArrayList<List<String>>();

        locationviewer = findViewById(R.id.locationviewer);
        pbar = findViewById(R.id.pbar);

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
        draw_name = (TextView) headerView.findViewById(R.id.lbl_draw_name);
        draw_type = (TextView) headerView.findViewById(R.id.lbl_draw_type);
        draw_img_user = headerView.findViewById(R.id.cimg_user);

        draw_name.setText(dh.getpFName() + " " + dh.getpLName());
        draw_type.setText(dh.getType());
        draw_img_user.setImageBitmap(dp.createImage(dh.getpImage()));
        if(dh.getpImage()==null)
        {
            draw_img_user.setImageResource(R.drawable.ic_person);
        }

        timeformatter = new SimpleDateFormat("HH:mm");
        dateformatter = new SimpleDateFormat("yyyy-MM-dd");
        batchformatter = new SimpleDateFormat("yyyyMMddHHmmss");

        btn_eAddCompanion.setOnClickListener(this);
        btn_eAddCompanionCancel.setOnClickListener(this);
        txt_companionExpander.setOnClickListener(this);
        btn_scan.setOnClickListener(this);

        customAdapter = new CustomAdapter();
        lv.setAdapter(customAdapter);

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                SparseBooleanArray positionchecker = lv.getCheckedItemPositions();

                int count = lv.getCount();
                for(int item = count-1; item>=0; item--){

                    if(positionchecker.get(item)){
//                        adapter.remove(itemList.get(item));
                        fname.remove(item);
                        lname.remove(item);

                        personlists.remove(item);

                        customAdapter.notifyDataSetChanged();

                        qrcode =dh.getUserid() + "#";
                        for(int x = 0;x<personlists.size();x++)
                        {
                            for(int y=0;y<personlists.get(x).size();y++)
                            {
                                qrcode = qrcode + personlists.get(x).get(y) + "_";
                            }
                            qrcode = qrcode + ",";
                        }

                        img_scanbox.setImageBitmap(dp.createQR(qrcode));
                        qrcode="";

                        Toast.makeText(AddCompanion.this,"Item Delete Successfully",Toast.LENGTH_LONG).show();
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

        if(v.getId()==R.id.btn_eAddCompanion)
        {
            fname.add(edt_cFname.getText()+"");
            lname.add(edt_cLname.getText()+"");

            personinfo.clear();
            personinfo.add(edt_cFname.getText()+"");
            personinfo.add(edt_cLname.getText()+"");
            personlists.add(new ArrayList<>(personinfo));

            customAdapter.notifyDataSetChanged();

            edt_cFname.setText("");
            edt_cLname.setText("");

            qrcode =dh.getUserid() + "#";
            for(int x = 0;x<personlists.size();x++)
            {
                for(int y=0;y<personlists.get(x).size();y++)
                {
                    qrcode = qrcode + personlists.get(x).get(y) + "_";
                }
                qrcode = qrcode + ",";
            }

            img_scanbox.setImageBitmap(dp.createQR(qrcode));
            qrcode="";
            lo_companionlist.setVisibility(View.VISIBLE);
            lo_addcompanion.setVisibility(View.GONE);
        }
        else if(v.getId()==R.id.txt_companionExpander)
        {
            lo_companionlist.setVisibility(View.GONE);
            lo_addcompanion.setVisibility(View.VISIBLE);
        }
        else if(v.getId()==R.id.btn_eAddCompanionCancel)
        {
            lo_companionlist.setVisibility(View.VISIBLE);
            lo_addcompanion.setVisibility(View.GONE);

            edt_cFname.setText("");
            edt_cLname.setText("");
        }
        else if(v.getId()==R.id.btn_scan)
        {
            scanCode();
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
                            con.createStatement().executeUpdate("INSERT into employee_scanned (batch, firstname, lastname, employee_id, est_id, account_id, time_entered, date_entered) " +
                                    "VALUES('"+ batch +"', '"+ personlists.get(x).get(0) +"', '"+ personlists.get(x).get(1) +"','"+ estinfo.get(0) +"','"+ estinfo.get(1) +"' '"+ dh.getUserid() +"', '"+ timeformatter.format(timestamp) +"', '"+ dateformatter.format(timestamp) +"')");
                            isSuccess = true;
                        }
                    }
                    else
                    {
                        con.createStatement().executeUpdate("INSERT into employee_scanned (batch, employee_id, est_id, account_id, time_entered, date_entered) VALUES('"+ batch +"', '"+ estinfo.get(0) +"', '"+  estinfo.get(1) +"', '"+ dh.getUserid() +"', '"+ timeformatter.format(timestamp) +"', '"+ dateformatter.format(timestamp) +"')");
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

            estinfo = new ArrayList<>();
            personinfo = new ArrayList<>();
            personlists = new ArrayList<List<String>>();

            estinfo = dp.splitter(qrscan, ",");

            batch = dh.getUserid() + estinfo.get(0) + batchformatter.format(timestamp);
            dm.displayMessage(getApplicationContext(), qrcode+"");

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
            TextView tv_lname=(TextView)view.findViewById(R.id.list_txt_cLname);
            CheckBox cb = (CheckBox)view.findViewById(R.id.list_cb);

            tv_fname.setText(fname.get(i));
            tv_lname.setText(lname.get(i));

            return view;
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.prof)
        {
            Intent startIntent=new Intent(AddCompanion.this, UserDriverProfile.class);
            startActivity(startIntent);
            finish();
        }
        else if(item.getItemId()==R.id.history)
        {
            Intent startIntent=new Intent(AddCompanion.this, UserHistory.class);
            startActivity(startIntent);
            finish();
        }


        return false;
    }

}