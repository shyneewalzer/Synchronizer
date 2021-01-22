package com.example.mobileapplicationproject;

import android.content.Intent;
import android.os.Bundle;

import com.example.mobileapplicationproject.DataController.ConnectionController;
import com.example.mobileapplicationproject.DataController.DataHolder;
import com.example.mobileapplicationproject.DataController.DataProcessor;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.mobileapplicationproject.ui.main.SectionsPagerAdapter;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserHistory extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    ConnectionController cc = new ConnectionController();
    DataHolder dh = new DataHolder();
    DataProcessor dp = new DataProcessor();

    SectionsPagerAdapter sectionsPagerAdapter;

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
        if(dh.getType().equals("Establishment"))
        {
            sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), "DETAILS", "COUNTS");
        }
        else
        {
            sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), "TRAVEL", "ESTABLISHMENT");
        }

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

        if(dh.getType().equals("Establishment"))
        {
            navigationView.inflateMenu(R.menu.drawer_estmenu);
            draw_name.setText(dh.getEstName());
            draw_img_user.setImageBitmap(dp.createImage(dh.getEstImage()));
            if(dh.getEstImage()==null)
            {
                draw_img_user.setImageResource(R.drawable.ic_person);
            }
        }
        else
        {
            draw_name.setText(dh.getpFName() + " " + dh.getpLName());
            draw_img_user.setImageBitmap(dp.createImage(dh.getpImage()));
            if(dh.getpImage()==null)
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
            Intent startIntent=new Intent(UserHistory.this, UserDashboard.class);
            startActivity(startIntent);
            finish();
        }
        else if(item.getItemId()==R.id.prof)
        {
            Intent startIntent=new Intent(UserHistory.this, UserDriverProfile.class);
            startActivity(startIntent);
            finish();
        }
        else if(item.getItemId()==R.id.logout)
        {
            Intent startIntent=new Intent(UserHistory.this, Login.class);
            startActivity(startIntent);
            finish();
        }


        return false;
    }
}