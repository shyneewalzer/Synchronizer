package com.example.mobileapplicationproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.example.mobileapplicationproject.DataController.ConnectionController;
import com.example.mobileapplicationproject.DataController.DataHolder;
import com.example.mobileapplicationproject.DataController.DataProcessor;
import com.example.mobileapplicationproject.ui.main.SPASecond;
import com.example.mobileapplicationproject.ui.main.SectionsPagerAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileTabbed extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ConfirmDialog.ConfirmDialogListener{

    ConnectionController cc = new ConnectionController();
    DataHolder dh = new DataHolder();
    DataProcessor dp = new DataProcessor();

    SPASecond sectionsPagerAdapter;

    ///////////////UI ELEMENTS/////////////////

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    NavigationView navigationView;
    TextView draw_name, draw_type;
    CircleImageView draw_img_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_history);

        sectionsPagerAdapter = new SPASecond(this, getSupportFragmentManager());

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Profile");

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
            {
                draw_img_user.setImageResource(R.drawable.ic_person);
            }
        }
        else if(dh.getType().equals("Driver"))
        {
            draw_name.setText(dh.getpFName() + " " + dh.getpLName());
            draw_img_user.setImageBitmap(dp.createImage(dh.getpImage()));
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.drawer_driver_menu);
            {
                draw_img_user.setImageResource(R.drawable.ic_person);
            }
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
        }
        draw_type.setText(dh.getType());

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.home)
        {
            dh.setVisitmode("travel");
            Intent startIntent=new Intent(ProfileTabbed.this, UserDashboard.class);
            startActivity(startIntent);
            finish();
        }
        else if(item.getItemId()==R.id.history)
        {
            Intent startIntent=new Intent(ProfileTabbed.this, UserHistory.class);
            startActivity(startIntent);
            finish();
        }
        else if(item.getItemId()==R.id.estab)
        {
            dh.setVisitmode("estab");
            Intent startIntent=new Intent(ProfileTabbed.this, UserDashboard.class);
            startActivity(startIntent);
            finish();
        }
        else if(item.getItemId()==R.id.drivehome)
        {
            Intent startIntent=new Intent(ProfileTabbed.this, DriverDashboard.class);
            startActivity(startIntent);
            finish();
        }
        else if(item.getItemId()==R.id.drivehistory)
        {
            Intent startIntent=new Intent(ProfileTabbed.this, DriverHistory.class);
            startActivity(startIntent);
            finish();
        }
        else if(item.getItemId()==R.id.esthome)
        {
            Intent startIntent=new Intent(ProfileTabbed.this, EstabDashboard.class);
            startActivity(startIntent);
            finish();
        }
        else if(item.getItemId()==R.id.esthistory)
        {
            Intent startIntent=new Intent(ProfileTabbed.this, UserHistory.class);
            startActivity(startIntent);
            finish();
        }
        else if(item.getItemId()==R.id.logout || item.getItemId()==R.id.drivelogout || item.getItemId()==R.id.estlogout)
        {
            ConfirmDialog confirmDialog = new ConfirmDialog("Confirmation", "You are about to log out\nAre you sure?", "logout");
            confirmDialog.show(getSupportFragmentManager(), "confirm dialog");
        }

        return false;
    }

    @Override
    public void getDialogResponse(boolean dialogResponse, String purpose) {

        if(purpose.equals("logout") && dialogResponse==true)
        {
            Intent startIntent=new Intent(ProfileTabbed.this, Login.class);
            startActivity(startIntent);
            finish();
        }
    }
}