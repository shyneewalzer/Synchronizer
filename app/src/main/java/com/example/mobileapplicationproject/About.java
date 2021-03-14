package com.example.mobileapplicationproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mobileapplicationproject.DataController.ConnectionController;
import com.example.mobileapplicationproject.DataController.DataHolder;
import com.example.mobileapplicationproject.DataController.DataProcessor;
import com.google.android.material.navigation.NavigationView;

import de.hdodenhof.circleimageview.CircleImageView;

public class About extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ConfirmDialog.ConfirmDialogListener{

    ConnectionController cc = new ConnectionController();
    DataHolder dh = new DataHolder();
    DataProcessor dp = new DataProcessor();

    //////////////UI ELEMENTS///////////////////

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    NavigationView navigationView;
    TextView draw_name, draw_type;
    CircleImageView draw_img_user;

    LinearLayout lo_indivabout, lo_driverabout, lo_estabout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        lo_indivabout = findViewById(R.id.lo_indivabout);
        lo_driverabout = findViewById(R.id.lo_drivervabout);
        lo_estabout = findViewById(R.id.lo_estabout);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("About");

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

        if(dh.getType().equals("Individual"))
        {
            draw_name.setText(dh.getpFName() + " " + dh.getpLName());
            draw_img_user.setImageBitmap(dp.createImage(dh.getpImage()));
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.drawer_menu);
            if(dh.getpImage()==null)
            {
                draw_img_user.setImageResource(R.drawable.ic_person);
            }

            lo_indivabout.setVisibility(View.VISIBLE);
        }
        else if(dh.getType().equals("Driver"))
        {
            draw_name.setText(dh.getpFName() + " " + dh.getpLName());
            draw_img_user.setImageBitmap(dp.createImage(dh.getpImage()));
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.drawer_driver_menu);
            if(dh.getpImage()==null)
            {
                draw_img_user.setImageResource(R.drawable.ic_person);
            }

            lo_driverabout.setVisibility(View.VISIBLE);
        }
        else if(dh.getType().equals("Establishment"))
        {
            draw_img_user.setImageBitmap(dp.createImage(dh.getEstImage()));
            draw_name.setText(dh.getEstName());
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.drawer_estmenu);
            if(dh.getEstImage()==null)
            {
                draw_img_user.setImageResource(R.drawable.ic_person);
            }

            lo_estabout.setVisibility(View.VISIBLE);
        }
        draw_type.setText(dh.getType());
    }

    @Override
    public void getDialogResponse(boolean dialogResponse, String purpose) {

        if(purpose.equals("logout") && dialogResponse==true)
        {
            Intent startIntent=new Intent(About.this, Login.class);
            startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startIntent);
            finishAffinity();

            SharedPreferences.Editor editor = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).edit();
            editor.clear();
            editor.apply();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.home)
        {
            dh.setVisitmode("travel");
            Intent startIntent=new Intent(About.this, UserDashboard.class);
            startActivity(startIntent);
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else if(item.getItemId()==R.id.estab)
        {
            dh.setVisitmode("estab");
            Intent startIntent=new Intent(About.this, UserDashboard.class);
            startActivity(startIntent);
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else if(item.getItemId()==R.id.prof)
        {
            Intent startIntent=new Intent(About.this, ProfileTabbed.class);
            startActivity(startIntent);
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else if(item.getItemId()==R.id.history)
        {
            Intent startIntent=new Intent(About.this, UserHistory.class);
            startActivity(startIntent);
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else if(item.getItemId()==R.id.drivehome)
        {
            Intent startIntent=new Intent(About.this, DriverDashboard.class);
            startActivity(startIntent);
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else if(item.getItemId()==R.id.driveprof)
        {
            Intent startIntent=new Intent(About.this, ProfileTabbed.class);
            startActivity(startIntent);
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else if(item.getItemId()==R.id.drivehistory)
        {
            Intent startIntent=new Intent(About.this, DriverHistory.class);
            startActivity(startIntent);
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else if(item.getItemId()==R.id.esthome)
        {
            Intent startIntent=new Intent(About.this, EstabDashboard.class);
            startActivity(startIntent);
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else if(item.getItemId()==R.id.estprof)
        {
            Intent startIntent=new Intent(About.this, ProfileTabbed.class);
            startActivity(startIntent);
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else if(item.getItemId()==R.id.esthistory)
        {
            Intent startIntent=new Intent(About.this, UserHistory.class);
            startActivity(startIntent);
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else if(item.getItemId()==R.id.logout || item.getItemId()==R.id.drivelogout || item.getItemId()==R.id.estlogout)
        {
            ConfirmDialog confirmDialog = new ConfirmDialog("Confirmation", "You are about to log out\nAre you sure?", "logout");
            confirmDialog.show(getSupportFragmentManager(), "confirm dialog");
        }

        return false;
    }
}