package com.example.mobileapplicationproject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.mobileapplicationproject.DataController.ConnectionController;
import com.example.mobileapplicationproject.DataController.DataHolder;
import com.example.mobileapplicationproject.DataController.DataProcessor;
import com.example.mobileapplicationproject.DataController.DebugMode;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class FragmentProfileUD extends Fragment implements View.OnClickListener{

    View fragprof;
    ConnectionController cc = new ConnectionController();
    DataHolder dh = new DataHolder();
    DataProcessor dp = new DataProcessor();
    DebugMode dm = new DebugMode();

    ArrayAdapter<String> adapter;


    Calendar datenow = Calendar.getInstance();
    int age;

    Uri imageuri;

    InputStream imageStream;

    ///////////////////UI ELEMENTS////////////////
    NavigationView navigationView;
    TextView draw_name, draw_type;
    CircleImageView draw_img_user;

    LinearLayout profileviewer;
    ProgressBar pbar;

    TextInputEditText edt_firstname, edt_middlename, edt_lastname, edt_age, edt_contact, edt_house, edt_city;
    AutoCompleteTextView edt_brgy;
    Button btn_image, btn_update;
    CircleImageView img_profile;

    public FragmentProfileUD() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        fragprof = inflater.inflate(R.layout.fragment_profile_ud, container, false);

        edt_firstname = fragprof.findViewById(R.id.txt_firstname);
        edt_lastname = fragprof.findViewById(R.id.txt_lastname);
        edt_middlename = fragprof.findViewById(R.id.middle);
        edt_age = fragprof.findViewById(R.id.age);
        edt_contact = fragprof.findViewById(R.id.contact);
        edt_house = fragprof.findViewById(R.id.house);
        edt_brgy = fragprof.findViewById(R.id.barangay);
        edt_city = fragprof.findViewById(R.id.city);

        img_profile = fragprof.findViewById(R.id.user_image);

        btn_image = fragprof.findViewById(R.id.user_image_button);
        btn_update = fragprof.findViewById(R.id.updateProfileButton);

        profileviewer = fragprof.findViewById(R.id.profileview);
        pbar = fragprof.findViewById(R.id.pbar);

        navigationView = getActivity().findViewById(R.id.navigationView);
        View headerView = navigationView.getHeaderView(0);
        draw_name = (TextView) headerView.findViewById(R.id.lbl_draw_name);
        draw_type = (TextView) headerView.findViewById(R.id.lbl_draw_type);
        draw_img_user = headerView.findViewById(R.id.cimg_user);

        btn_update.setOnClickListener(this);
        btn_image.setOnClickListener(this);
        edt_age.setOnClickListener(this);


        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, dh.getListBrgy());
        edt_brgy.setAdapter(adapter);

        edt_brgy.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                edt_brgy.setText(adapter.getItem(position));
            }
        });

        dataSet();

        return fragprof;
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
                btn_image.setVisibility(View.VISIBLE);
            }
            else if(btn_update.getText().toString().equals("SAVE"))
            {
                Dbupdate dbupdate = new Dbupdate();
                dbupdate.execute();

                btn_update.setText("EDIT");
                btn_image.setVisibility(View.INVISIBLE);
            }
        }
        else if(v.getId()==R.id.user_image_button)
        {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 100);
        }
        else if(v.getId()==R.id.age)
        {
            int cal_yr = dh.getTempDate().get(Calendar.YEAR);
            int cal_mo = dh.getTempDate().get(Calendar.MONTH);
            int cal_dy = dh.getTempDate().get(Calendar.DAY_OF_MONTH);
            dm.displayMessage(getContext(),cal_yr + "-" + cal_mo + "-" + cal_dy);

            DatePickerDialog datepicker = new DatePickerDialog(getContext(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    month = month + 1;
                    edt_age.setText(year + "-" + month + "-" + dayOfMonth);
                }
            },cal_yr,cal_mo,cal_dy);
            datepicker.show();

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
                Toast.makeText(getContext(), "Profile Updated", Toast.LENGTH_LONG);

                dm.displayMessage(getContext(), ""+dp.stringToDate(dp.stringToDate(edt_age+"")+""));
            }
            else
            {
                Toast.makeText(getContext(),msger,Toast.LENGTH_LONG);
                dm.displayMessage(getContext(), dh.getpBday()+"");
            }
            profileviewer.setVisibility(View.VISIBLE);
            pbar.setVisibility(View.GONE);

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK &&
                data != null && data.getData() != null) {

            imageuri=data.getData();
            img_profile.setImageURI(imageuri);

            try {
                imageStream = getActivity().getContentResolver().openInputStream(imageuri);
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
        img_profile.setImageBitmap(dp.createImage(dh.getpImage()));
        if(dh.getpImage()==null)
        {
            draw_img_user.setImageResource(R.drawable.ic_person);
            img_profile.setImageResource(R.drawable.ic_person);
        }
    }

}
