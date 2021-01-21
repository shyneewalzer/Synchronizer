package com.example.mobileapplicationproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.mobileapplicationproject.DataController.ConnectionController;
import com.example.mobileapplicationproject.DataController.DataHolder;
import com.example.mobileapplicationproject.DataController.DataProcessor;
import com.example.mobileapplicationproject.DataController.DebugMode;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;

import de.hdodenhof.circleimageview.CircleImageView;

public class EstabProfile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener{

    ConnectionController cc = new ConnectionController();
    DataHolder dh = new DataHolder();
    DataProcessor dp = new DataProcessor();
    DebugMode dm = new DebugMode();

    Uri imageuri;
    InputStream imageStream;

    ////////////UI ELEMENTS////////////

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    NavigationView navigationView;
    TextView draw_name, draw_type;
    CircleImageView draw_img_user;

    TextInputEditText edt_name, edt_owner, edt_contact, edt_street;
    Button btn_estUpdate, btn_estUpload;
    CircleImageView img_estprof;

    ProgressBar pbar;
    LinearLayout lo_estabprofileviewer;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.estab_profile);

        edt_name = findViewById(R.id.edt_estname);
        edt_owner = findViewById(R.id.edt_estowner);
        edt_contact = findViewById(R.id.edt_estcontact);
        edt_street = findViewById(R.id.edt_eststreet);
        btn_estUpdate = findViewById(R.id.btn_estUpdate);
        btn_estUpload = findViewById(R.id.btn_estUpload);
        img_estprof = findViewById(R.id.img_estprof);

        pbar = findViewById(R.id.pbar);
        lo_estabprofileviewer = findViewById(R.id.lo_estabprofileviewer);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("User Panel");

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

        draw_name.setText(dh.getEstName());
        draw_type.setText(dh.getType());
        draw_img_user.setImageBitmap(dp.createImage(dh.getEstImage()));
        img_estprof.setImageBitmap(dp.createImage(dh.getEstImage()));
        if(dh.getEstImage()==null)
        {
            draw_img_user.setImageResource(R.drawable.ic_person);
            img_estprof.setImageResource(R.drawable.ic_person);
        }

        btn_estUpload.setOnClickListener(this);
        btn_estUpdate.setOnClickListener(this);

        dataSet();
    }

    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.btn_estUpload)
        {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 100);
        }
        else if(v.getId()==R.id.btn_estUpdate)
        {
            if(btn_estUpdate.getText().toString().equals("EDIT"))
            {
                edt_name.setEnabled(true);
                edt_owner.setEnabled(true);
                edt_contact.setEnabled(true);
                edt_street.setEnabled(true);

                btn_estUpdate.setText("SAVE");
            }
            else if(btn_estUpdate.getText().toString().equals("SAVE"))
            {
                Dbupdate dbupdate = new Dbupdate();
                dbupdate.execute();

                btn_estUpdate.setText("EDIT");
            }
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

                    con.createStatement().executeUpdate("UPDATE establishments SET name = '" + edt_name.getText() + "', street = '" + edt_street.getText() + "', telephone_number = '" + edt_contact.getText() + "', est_owner = '" + edt_owner.getText() + "', image = '" + dh.getEstImage() + "' where est_id = '"+ dh.getEstID() +"'");

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
            lo_estabprofileviewer.setVisibility(View.GONE);
            pbar.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(String a){

            if(isSuccess==true)
            {
                dh.setEstProfile(edt_name.getText()+"", edt_street.getText()+"", edt_contact.getText()+"", edt_owner.getText()+"");
                dataSet();
                dp.toasterlong(getApplicationContext(), "Profile Successfully Updated");
            }
            else
            {
                dp.toasterlong(getApplicationContext(), msger+"");

            }
            lo_estabprofileviewer.setVisibility(View.VISIBLE);
            pbar.setVisibility(View.GONE);

        }
    }

    private void dataSet()
    {
        edt_name.setEnabled(false);
        edt_owner.setEnabled(false);
        edt_contact.setEnabled(false);
        edt_street.setEnabled(false);

        edt_name.setText(dh.getEstName());
        edt_street.setText(dh.getEstStreet());
        edt_owner.setText(dh.getEstOwner());
        edt_contact.setText(dh.getEstContact());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK &&
                data != null && data.getData() != null) {

            imageuri=data.getData();
            img_estprof.setImageURI(imageuri);

            try {
                imageStream = getContentResolver().openInputStream(imageuri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            dh.setEstImage(dp.encodeImage(selectedImage));

        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.home)
        {
            dp.toastershort(getApplicationContext(), "Home");
        }
        else if(item.getItemId()==R.id.prof)
        {
            Intent startIntent=new Intent(EstabProfile.this, EmployeeeProfile.class);
            startActivity(startIntent);
            finish();
        }


        return false;
    }


}
