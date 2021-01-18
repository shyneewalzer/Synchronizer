package com.example.mobileapplicationproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobileapplicationproject.DataController.ConnectionController;
import com.example.mobileapplicationproject.DataController.DataHolder;
import com.example.mobileapplicationproject.DataController.DataProcessor;
import com.example.mobileapplicationproject.DataController.DebugMode;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserDriverProfile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener{

    Uri imageuri;

    ConnectionController cc = new ConnectionController();
    DataHolder dh = new DataHolder();
    DataProcessor dp = new DataProcessor();
    DebugMode dm = new DebugMode();

    Calendar datenow = Calendar.getInstance();
    Calendar c = Calendar.getInstance();
    int age;

    InputStream imageStream;

    DatePickerDialog.OnDateSetListener dateSetListener;

    ///////////UI ELEMENTS////////////

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    NavigationView navigationView;
    TextView draw_name, draw_type;
    CircleImageView draw_img_user;

    LinearLayout profileviewer;
    ProgressBar pbar;

    TextInputEditText edt_firstname, edt_middlename, edt_lastname, edt_age, edt_contact, edt_house, edt_brgy, edt_city;
    TextView accttab;
    Button btn_image, btn_update;
    CircleImageView img_profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_driver_profile);

        edt_firstname = findViewById(R.id.txt_firstname);
        edt_lastname = findViewById(R.id.txt_lastname);
        edt_middlename = findViewById(R.id.middle);
        edt_age = findViewById(R.id.age);
        edt_contact = findViewById(R.id.contact);
        edt_house = findViewById(R.id.house);
        edt_brgy = findViewById(R.id.barangay);
        edt_city = findViewById(R.id.city);

        img_profile = findViewById(R.id.user_image);

        btn_image = findViewById(R.id.user_image_button);
        btn_update = findViewById(R.id.updateProfileButton);

        profileviewer = findViewById(R.id.profileview);
        pbar = findViewById(R.id.pbar);

        accttab = findViewById(R.id.account_user_tab);


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("User Panel");

        drawerLayout = findViewById(R.id.drawer);
        drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open_drawer,R.string.close_drawer);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.setDrawerIndicatorEnabled(true);//hamburger icon
        drawerToggle.syncState();

        navigationView = findViewById(R.id.navigationView);
        if(dh.getType().equals("Driver"))
        {
            navigationView.inflateMenu(R.menu.drawer_driver_menu);
        }
        navigationView.setNavigationItemSelectedListener(this);

        Menu drawer_menu = navigationView.getMenu();

        View headerView = navigationView.getHeaderView(0);
        draw_name = (TextView) headerView.findViewById(R.id.lbl_draw_name);
        draw_type = (TextView) headerView.findViewById(R.id.lbl_draw_type);
        draw_img_user = headerView.findViewById(R.id.cimg_user);

        draw_name.setText(dh.getpFName() + " " + dh.getpLName());
        draw_type.setText(dh.getType());
        draw_img_user.setImageBitmap(dp.createImage(dh.getpImage()));
        img_profile.setImageBitmap(dp.createImage(dh.getpImage()));
        if(dh.getpImage()==null)
        {
            draw_img_user.setImageResource(R.drawable.ic_person);
            img_profile.setImageResource(R.drawable.ic_person);
        }

        dataSet();

        btn_update.setOnClickListener(this);
        btn_image.setOnClickListener(this);
        accttab.setOnClickListener(this);
        edt_age.setOnClickListener(this);

        dataSet();
    }

    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.updateProfileButton)
        {
            if(btn_update.getText().toString().equals("EDIT"))
            {
                edt_firstname.setEnabled(true);
                edt_lastname.setEnabled(true);
                edt_middlename.setEnabled(true);
                edt_age.setEnabled(true);
                edt_contact.setEnabled(true);
                edt_house.setEnabled(true);
                edt_brgy.setEnabled(true);
                edt_city.setEnabled(true);

                edt_age.setText(dh.getpBday()+"");
                btn_update.setText("SAVE");
            }
            else if(btn_update.getText().toString().equals("SAVE"))
            {
                Dbupdate dbupdate = new Dbupdate();
                dbupdate.execute();
            }
        }
        else if(v.getId()==R.id.user_image_button)
        {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 100);
        }
        else if(v.getId()==R.id.account_user_tab)
        {

        }
        else if(v.getId()==R.id.age)
        {
            int cal_yr = dh.getTempDate().get(Calendar.YEAR);
            int cal_mo = dh.getTempDate().get(Calendar.MONTH);
            int cal_dy = dh.getTempDate().get(Calendar.DAY_OF_MONTH);
            dm.displayMessage(getApplicationContext(),cal_yr + "-" + cal_mo + "-" + cal_dy);
            @SuppressLint({"NewApi", "LocalSuppress"}) DatePickerDialog datepicker = new DatePickerDialog(UserDriverProfile.this, R.style.Theme_AppCompat_DayNight_Dialog_MinWidth, dateSetListener,cal_yr, cal_mo, cal_dy);
            datepicker.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            datepicker.show();

            dateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    month = month + 1;
                    edt_age.setText(year + "-" + month + "-" + dayOfMonth);

                    dm.displayMessage(getApplicationContext(),year + "-" + month + "-" + dayOfMonth );
                }
            };
        }
    }


    private class Dbupdate extends AsyncTask<String, String, String>
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

                    con.createStatement().executeUpdate("UPDATE user_profile SET firstname = '" + edt_firstname.getText() + "', lastname = '" + edt_lastname.getText() + "', middlename = '" + edt_middlename.getText() + "', birthday = '" + edt_age.getText() + "', contactnumber = '" + edt_contact.getText() + "', image='"+ dh.getpImage() +"' where account_id='" + dh.getUserid() + "' ");

                    con.createStatement().executeUpdate("UPDATE address_table SET house_lot_number = '" + edt_house.getText() + "', barangay = '" + edt_brgy.getText() + "', city = '" + edt_city.getText() + "'  where account_id='" + dh.getUserid() + "' ");

                    isSuccess=true;
                    con.close();
                }
            }
            catch (Exception ex){
                msger="Exception" + ex;
            }
            return msger;


        }

        @SuppressLint("WrongThread")
        @Override
        protected void onPreExecute() {
            profileviewer.setVisibility(View.GONE);
            pbar.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(String a){

            if(isSuccess==true)
            {
                dh.setProfile(edt_firstname.getText()+"", edt_lastname.getText()+"", edt_middlename.getText()+"", dp.stringToDate(edt_age.getText()+""), edt_contact.getText()+"", dh.getpImage());
                dh.setAddress(edt_house.getText()+"", edt_brgy.getText()+"", edt_city.getText()+"");
                dataSet();
                Toast.makeText(getApplicationContext(), "Profile Updated", Toast.LENGTH_LONG);

                dm.displayMessage(getApplicationContext(), ""+dp.stringToDate(dp.stringToDate(edt_age+"")+""));
            }
            else
            {
                Toast.makeText(getApplicationContext(),msger,Toast.LENGTH_LONG);
                dm.displayMessage(getApplicationContext(), dh.getpBday()+"");
            }
            profileviewer.setVisibility(View.VISIBLE);
            pbar.setVisibility(View.GONE);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK &&
                data != null && data.getData() != null) {

            imageuri=data.getData();
            img_profile.setImageURI(imageuri);

            try {
                imageStream = getContentResolver().openInputStream(imageuri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            dh.setpImage(dp.encodeImage(selectedImage));

        }
    }

    private void dataSet()
    {
        btn_update.setText("EDIT");
        edt_firstname.setEnabled(false);
        edt_lastname.setEnabled(false);
        edt_middlename.setEnabled(false);
        edt_age.setEnabled(false);
        edt_contact.setEnabled(false);
        edt_house.setEnabled(false);
        edt_brgy.setEnabled(false);
        edt_city.setEnabled(false);

        edt_firstname.setText(dh.getpFName());
        edt_lastname.setText(dh.getpLName());
        edt_middlename.setText(dh.getpMName());

        age = datenow.get(Calendar.YEAR) - dh.getTempDate().get(Calendar.YEAR);
        edt_age.setText(age+"");

        edt_contact.setText(dh.getpContact());
        edt_house.setText(dh.getHouse());
        edt_brgy.setText(dh.getBrgy());
        edt_city.setText(dh.getCity());

        draw_name.setText(dh.getpFName() + " " + dh.getpLName());
        draw_img_user.setImageBitmap(dp.createImage(dh.getpImage()));
        if(dh.getpImage()==null)
        {
            draw_img_user.setImageResource(R.drawable.ic_person);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.prof)
        {
//            Intent startIntent=new Intent(UserPanel.this, Profile.class);
//            startActivity(startIntent);
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
            Intent startIntent=new Intent(UserDriverProfile.this, Login.class);
            startActivity(startIntent);
            finish();
        }
        return true;

    }

}


