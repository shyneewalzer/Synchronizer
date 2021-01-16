package com.example.mobileapplicationproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Menu;
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

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetLocationGroup extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener{

    ArrayList<String> fname;
    ArrayList<String> mname;
    ArrayList<String> lname;
    ArrayList<String> contact;
    ArrayList<String> adr;

    ArrayList<String>personinfo;
    List<List<String>>personlists;

    String qrcode ="";

    SimpleDateFormat timeformatter;
    SimpleDateFormat dateformatter;
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

    TextInputEditText edt_cFname, edt_cMname, edt_cLname, edt_cContact, edt_cAdr;

    ImageView img_scanbox;
    Button btn_eAddCompanion, btn_setQR;
    ListView lv;

    LinearLayout locationviewer;
    ProgressBar pbar;
    TextView txtsign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_location_group);

        lv = findViewById(R.id.listview);
        img_scanbox = findViewById(R.id.scanbox);
        txtsign = findViewById(R.id.txtsign);
        btn_eAddCompanion = findViewById(R.id.btn_eAddCompanion);
        btn_setQR = findViewById(R.id.btn_setQR);

        edt_cFname = findViewById(R.id.edt_cFname);
        edt_cMname = findViewById(R.id.edt_cMname);
        edt_cLname = findViewById(R.id.edt_cLname);
        edt_cContact = findViewById(R.id.edt_cContact);
        edt_cAdr = findViewById(R.id.edt_cAdr);

        fname = new ArrayList<>();
        mname = new ArrayList<>();
        lname = new ArrayList<>();
        contact = new ArrayList<>();
        adr = new ArrayList<>();

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

        btn_eAddCompanion.setOnClickListener(this);
        btn_setQR.setOnClickListener(this);

        customAdapter = new CustomAdapter();
        lv.setAdapter(customAdapter);
        //TODO: Fix check/uncheck behavior of listview
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                SparseBooleanArray positionchecker = lv.getCheckedItemPositions();

                int count = lv.getCount();
                for(int item = count-1; item>=0; item--){

                    if(positionchecker.get(item)){
//                        adapter.remove(itemList.get(item));
                        fname.remove(item);
                        mname.remove(item);
                        lname.remove(item);
                        contact.remove(item);
                        adr.remove(item);

                        personlists.remove(item);

                        customAdapter.notifyDataSetChanged();
                        Toast.makeText(SetLocationGroup.this,"Item Delete Successfully",Toast.LENGTH_LONG).show();
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
            mname.add(edt_cMname.getText()+"");
            lname.add(edt_cLname.getText()+"");
            contact.add(edt_cContact.getText()+"");
            adr.add(edt_cAdr.getText()+"");

            personinfo.clear();
            personinfo.add(edt_cFname.getText()+"_");
            personinfo.add(edt_cMname.getText()+"_");
            personinfo.add(edt_cLname.getText()+"_");
            personinfo.add(edt_cContact.getText()+"_");
            personinfo.add(edt_cAdr.getText()+",");
            personlists.add(new ArrayList<>(personinfo));

            customAdapter.notifyDataSetChanged();

            edt_cFname.setText("");
            edt_cMname.setText("");
            edt_cLname.setText("");
            edt_cContact.setText("");
            edt_cAdr.setText("");

        }
        else if(v.getId()==R.id.btn_setQR)
        {
            qrcode =dh.getUserid() + "#";
            for(int x = 0;x<personlists.size();x++)
            {
                for(int y=0;y<personlists.get(x).size();y++)
                {
                    qrcode = qrcode + personlists.get(x).get(y);
                }
            }

            txtsign.setVisibility(View.INVISIBLE);
            dm.displayMessage(getApplicationContext(), qrcode);
            img_scanbox.setImageBitmap(dp.createQR(qrcode));
            qrcode="";

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
            Intent startIntent=new Intent(SetLocationGroup.this, UserDriverProfile.class);
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
            Intent startIntent=new Intent(SetLocationGroup.this, Login.class);
            startActivity(startIntent);
            finish();
        }
        return true;

    }
}