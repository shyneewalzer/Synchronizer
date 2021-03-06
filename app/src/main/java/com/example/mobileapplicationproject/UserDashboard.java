package com.example.mobileapplicationproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserDashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, ConfirmDialog.ConfirmDialogListener{

    ArrayList<String> fname;
    ArrayList<String> lname;
    ArrayList<String> contact;
    ArrayList<String>personinfo;
    List<List<String>>personlists;
    ArrayList<String>estinfo;

    ArrayAdapter<String> adapter;

    String qrcode ="", qrscan, batch;


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

    TextInputEditText edt_cFname, edt_cLname, edt_cContact;
    AutoCompleteTextView edt_destinaiton;

    ImageView img_scanbox;
    Button btn_eAddCompanion, btn_scan, btn_backdestination, btn_eAddCompanionCancel, btn_destination;
    TextView txt_companionExpander, txt_destination, txt_location;
    ListView lv;

    LinearLayout locationviewer, lo_companionlist, lo_addcompanion, lo_qr, lo_destination;
    ProgressBar pbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_dashboard);

        lv = findViewById(R.id.listview);
        img_scanbox = findViewById(R.id.scanbox);
        btn_eAddCompanion = findViewById(R.id.btn_eAddCompanion);
        btn_eAddCompanionCancel = findViewById(R.id.btn_eAddCompanionCancel);
        btn_scan = findViewById(R.id.btn_scan);
        txt_companionExpander = findViewById(R.id.txt_companionExpander);
        lo_companionlist = findViewById(R.id.lo_companionlist);
        lo_addcompanion = findViewById(R.id.lo_addcompanion);
        lo_destination = findViewById(R.id.lo_destination);
        lo_qr = findViewById(R.id.lo_qr);
        btn_destination = findViewById(R.id.btn_destination);
        btn_backdestination = findViewById(R.id.btn_backdestination);
        txt_destination = findViewById(R.id.txt_destination);
        txt_location = findViewById(R.id.txt_location);

        edt_cFname = findViewById(R.id.edt_cFname);
        edt_cLname = findViewById(R.id.edt_cLname);
        edt_cContact = findViewById(R.id.edt_cContact);

        edt_destinaiton = findViewById(R.id.edt_destination);

        fname = new ArrayList<>();
        lname = new ArrayList<>();
        contact = new ArrayList<>();

        personinfo = new ArrayList<>();
        personlists = new ArrayList<List<String>>();

        locationviewer = findViewById(R.id.locationviewer);
        pbar = findViewById(R.id.pbar);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Set Destination");

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
        btn_backdestination.setOnClickListener(this);
        btn_destination.setOnClickListener(this);

        customAdapter = new CustomAdapter();
        lv.setAdapter(customAdapter);

        adapter = new ArrayAdapter<String>(UserDashboard.this, android.R.layout.simple_list_item_1, dh.getListDestination());
        edt_destinaiton.setAdapter(adapter);

        edt_destinaiton.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                edt_destinaiton.setText(adapter.getItem(position));
            }
        });

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
                        contact.remove(item);

                        personlists.remove(item);

                        customAdapter.notifyDataSetChanged();

                        qrcode =dh.getUserid() + "#" + txt_destination.getText() + "#";//needed to refresh qr value
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

                        Toast.makeText(UserDashboard.this,"Item Delete Successfully",Toast.LENGTH_LONG).show();
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

        img_scanbox.setImageBitmap(dp.createQR(dh.getUserid() + "#" + txt_destination.getText() + "#"));//generate qr code for solo

        if(dh.getVisitmode().equals("estab"))
        {
            lo_destination.setVisibility(View.GONE);
            lo_qr.setVisibility(View.VISIBLE);
            btn_backdestination.setVisibility(View.GONE);
            btn_scan.setVisibility(View.VISIBLE);
            txt_destination.setVisibility(View.GONE);
            txt_location.setText("Establishment");
        }

    }

    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.btn_eAddCompanion)
        {
            if(edt_cFname.getText().toString().trim().isEmpty())
            {
                edt_cFname.setError("First name cannot be empty!");
            }
            else if(edt_cLname.getText().toString().trim().isEmpty())
            {
                edt_cLname.setError("Last name cannot be empty!");
            }
            else
            {
                fname.add(edt_cFname.getText()+"");
                lname.add(edt_cLname.getText()+"");
                contact.add(edt_cContact.getText()+"");

                personinfo.clear();
                personinfo.add(edt_cFname.getText()+"");
                personinfo.add(edt_cLname.getText()+"");
                personinfo.add(edt_cContact.getText()+"");
                personlists.add(new ArrayList<>(personinfo));

                customAdapter.notifyDataSetChanged();

                edt_cFname.setText("");
                edt_cLname.setText("");
                edt_cContact.setText("");

                qrcode =dh.getUserid() + "#" + txt_destination.getText() + "#";//needed to refresh qr value
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
        else if(v.getId()==R.id.btn_destination)
        {
            if(edt_destinaiton.getText().toString().trim().isEmpty())
            {
                dp.toasterlong(getApplicationContext(), "Please Set Destination");
            }
            else
            {
                lo_destination.setVisibility(View.GONE);
                lo_qr.setVisibility(View.VISIBLE);
                txt_destination.setText(edt_destinaiton.getText()+"");
                img_scanbox.setImageBitmap(dp.createQR(dh.getUserid() + "#" + txt_destination.getText() + "#"));//generate qr code for solo

            }

        }
        else if(v.getId()==R.id.btn_backdestination)
        {
            lo_destination.setVisibility(View.VISIBLE);
            lo_qr.setVisibility(View.GONE);
        }

    }

    @Override
    public void getDialogResponse(boolean dialogResponse, String purpose) {

        if(purpose.equals("logout") && dialogResponse==true)
        {
            Intent startIntent=new Intent(UserDashboard.this, Login.class);
            startActivity(startIntent);
            finish();
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
                            con.createStatement().executeUpdate("INSERT into employee_scanned (batch, firstname, lastname, contact_number, est_id, employee_id, account_id, time_entered, date_entered) " +
                                    "VALUES('"+ batch +"', '"+ personlists.get(x).get(0) +"', '"+ personlists.get(x).get(1) +"', '"+ personlists.get(x).get(2) +"','"+ estinfo.get(1) +"','"+ estinfo.get(0) +"', '"+ dh.getUserid() +"', '"+ timeformatter.format(timestamp) +"', '"+ dateformatter.format(timestamp) +"')");
                            isSuccess = true;
                        }
                    }
                    else
                    {
                        con.createStatement().executeUpdate("INSERT into employee_scanned (batch, est_id, employee_id, account_id, time_entered, date_entered) VALUES('"+ batch +"', '"+ estinfo.get(1) +"', '"+  estinfo.get(0) +"', '"+ dh.getUserid() +"', '"+ timeformatter.format(timestamp) +"', '"+ dateformatter.format(timestamp) +"')");
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

            estinfo = new ArrayList<>();
            estinfo = dp.splitter(qrscan, ",");

            batch = dh.getUserid() + estinfo.get(0) + batchformatter.format(timestamp);

            locationviewer.setVisibility(View.GONE);
            pbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String a){

            if(isSuccess==true)
            {
                dp.toastershort(getApplicationContext(), "Data Successfully Sent");
                Dbreadest dbreadest = new Dbreadest();
                dbreadest.execute();
            }
            else if(isSuccess==false)
            {
                dp.toastershort(getApplicationContext(), msger);
            }
            locationviewer.setVisibility(View.VISIBLE);
            pbar.setVisibility(View.GONE);
        }
    }

    private class Dbreadest extends AsyncTask<String, String, String>
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
                    ResultSet rs=con.createStatement().executeQuery("select * from establishments where est_id = '"+ estinfo.get(1) +"' ");
                    while (rs.next())
                    {
                        dh.setViewEstInfo(rs.getString("name"), rs.getString("street"), rs.getString("telephone_number"), rs.getString("image"));
                    }
                    rs.close();

                    ResultSet rsemp=con.createStatement().executeQuery("select * from user_profile where user_id = '"+ estinfo.get(0) +"' ");
                    while (rsemp.next())
                    {
                        dh.setViewEstEmp(rsemp.getString("firstname") + " " + rsemp.getString("lastname"));
                    }
                    rsemp.close();

                    isSuccess = true;
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

            locationviewer.setVisibility(View.GONE);
            pbar.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(String a){

            locationviewer.setVisibility(View.VISIBLE);
            pbar.setVisibility(View.GONE);

            if(isSuccess==true)
            {
                Intent startIntent = new Intent(UserDashboard.this, UserScanEstabViewer.class);
                startActivity(startIntent);
            }
            else
            {
                dp.toasterlong(getApplicationContext(), msger+"");
                dm.displayMessage(getApplicationContext(), dh.getViewEstName() + " " + dh.getViewEstAdr() + " " + dh.getViewEstContact() + " " + dh.getViewEstEmp());
            }

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
            TextView tv_contact=(TextView)view.findViewById(R.id.list_txt_cContact);

            CheckBox cb = (CheckBox)view.findViewById(R.id.list_cb);

            tv_fname.setText(fname.get(i));
            tv_lname.setText(lname.get(i));
            tv_contact.setText(tv_contact.getText() + contact.get(i));

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
                dm.displayMessage(getApplicationContext(), qrscan);

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

        if(item.getItemId()==R.id.home)
        {
            dh.setVisitmode("travel");
            Intent startIntent=new Intent(UserDashboard.this, UserDashboard.class);
            startActivity(startIntent);
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else if(item.getItemId()==R.id.prof)
        {
            Intent startIntent=new Intent(UserDashboard.this, ProfileTabbed.class);
            startActivity(startIntent);
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else if(item.getItemId()==R.id.history)
        {
            Intent startIntent=new Intent(UserDashboard.this, UserHistory.class);
            startActivity(startIntent);
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else if(item.getItemId()==R.id.estab)
        {
            dh.setVisitmode("estab");
            Intent startIntent=new Intent(UserDashboard.this, UserDashboard.class);
            startActivity(startIntent);
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else if(item.getItemId()==R.id.about || item.getItemId()==R.id.driveabout || item.getItemId()==R.id.estabout)
        {
            Intent startIntent=new Intent(UserDashboard.this, About.class);
            startActivity(startIntent);
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else if(item.getItemId()==R.id.logout)
        {
            ConfirmDialog confirmDialog = new ConfirmDialog("Confirmation", "You are about to log out\nAre you sure?", "logout");
            confirmDialog.show(getSupportFragmentManager(), "confirm dialog");
        }


        return false;
    }

}