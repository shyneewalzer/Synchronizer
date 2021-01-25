package com.example.mobileapplicationproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.mobileapplicationproject.DataController.DataHolder;
import com.example.mobileapplicationproject.DataController.DataProcessor;
import com.google.android.material.navigation.NavigationView;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserScanEstabViewer extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    DataHolder dh = new DataHolder();
    DataProcessor dp = new DataProcessor();

    ///////////////UI ELEMENTS/////////////////
    Toolbar toolbar;

    TextView txt_estviewname, txt_estviewadr, txt_estviewcontact, txt_estviewemp;
    CircleImageView img_estviewprof;
    Button btn_estviewclose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_scan_estab_viewer);

        txt_estviewname = findViewById(R.id.txt_estviewname);
        txt_estviewadr = findViewById(R.id.txt_estviewadr);
        txt_estviewcontact = findViewById(R.id.txt_estviewcontact);
        txt_estviewemp = findViewById(R.id.txt_estviewemp);
        img_estviewprof = findViewById(R.id.img_estviewprof);
        btn_estviewclose = findViewById(R.id.btn_estviewclose);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_backarrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Establishment Details");

        btn_estviewclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        img_estviewprof.setImageBitmap(dp.createImage(dh.getViewEstImg()));
        txt_estviewname.setText(dh.getViewEstName());
        txt_estviewadr.setText(dh.getViewEstAdr());
        txt_estviewcontact.setText(dh.getViewEstContact());
        txt_estviewemp.setText(dh.getViewEstEmp());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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

        onBackPressed();

        return true;

    }
}