package com.example.mobileapplicationproject;

import android.annotation.SuppressLint;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class FragmentProfileEstab extends Fragment implements View.OnClickListener{

    View fragprof;
    ConnectionController cc = new ConnectionController();
    DataHolder dh = new DataHolder();
    DataProcessor dp = new DataProcessor();
    DebugMode dm = new DebugMode();

    Uri imageuri;
    InputStream imageStream;
    String imageholder;

    ///////////////////UI ELEMENTS////////////////
    NavigationView navigationView;
    TextView draw_name, draw_type;
    CircleImageView draw_img_user;

    TextInputEditText edt_name, edt_owner, edt_contact, edt_street;
    Button btn_estUpdate, btn_estUpload;
    CircleImageView img_estprof;

    ProgressBar pbar;
    LinearLayout lo_estabprofileviewer;


    public FragmentProfileEstab() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        fragprof = inflater.inflate(R.layout.fragment_profile_estab, container, false);

        edt_name = fragprof.findViewById(R.id.edt_estname);
        edt_owner = fragprof.findViewById(R.id.edt_estowner);
        edt_contact = fragprof.findViewById(R.id.edt_estcontact);
        edt_street = fragprof.findViewById(R.id.edt_eststreet);
        btn_estUpdate = fragprof.findViewById(R.id.btn_estUpdate);
        btn_estUpload = fragprof.findViewById(R.id.btn_estUpload);
        img_estprof = fragprof.findViewById(R.id.img_estprof);

        lo_estabprofileviewer = fragprof.findViewById(R.id.lo_estabprofileviewer);
        pbar = fragprof.findViewById(R.id.pbar);

        navigationView = getActivity().findViewById(R.id.navigationView);
        View headerView = navigationView.getHeaderView(0);
        draw_name = (TextView) headerView.findViewById(R.id.lbl_draw_name);
        draw_type = (TextView) headerView.findViewById(R.id.lbl_draw_type);
        draw_img_user = headerView.findViewById(R.id.cimg_user);

        btn_estUpload.setOnClickListener(this);
        btn_estUpdate.setOnClickListener(this);

        dataSet();

        return fragprof;
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
                btn_estUpload.setVisibility(View.VISIBLE);
            }
            else if(btn_estUpdate.getText().toString().equals("SAVE"))
            {
                Dbupdate dbupdate = new Dbupdate();
                dbupdate.execute();

                btn_estUpdate.setText("EDIT");
                btn_estUpload.setVisibility(View.INVISIBLE);
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
                    if(imageholder!=null && !imageholder.isEmpty())
                    {
                        con.createStatement().executeUpdate("UPDATE establishments SET name = '" + edt_name.getText() + "', street = '" + edt_street.getText() + "', telephone_number = '" + edt_contact.getText() + "', est_owner = '" + edt_owner.getText() + "', image = '" + imageholder + "' where est_id = '"+ dh.getEstID() +"'");
                    }
                    else
                    {
                        con.createStatement().executeUpdate("UPDATE establishments SET name = '" + edt_name.getText() + "', street = '" + edt_street.getText() + "', telephone_number = '" + edt_contact.getText() + "', est_owner = '" + edt_owner.getText() + "', image = 'NULL' where est_id = '"+ dh.getEstID() +"'");
                    }

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
                dh.setEstProfile(edt_name.getText()+"", edt_street.getText()+"", edt_contact.getText()+"", edt_owner.getText()+"", imageholder);
                dataSet();
                dp.toasterlong(getContext(), "Profile Successfully Updated");
            }
            else
            {
                dp.toasterlong(getContext(), msger+"");

            }
            lo_estabprofileviewer.setVisibility(View.VISIBLE);
            pbar.setVisibility(View.GONE);

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK &&
                data != null && data.getData() != null) {

            imageuri=data.getData();
            img_estprof.setImageURI(imageuri);

            try {
                imageStream = getActivity().getContentResolver().openInputStream(imageuri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            imageholder = dp.encodeImage(selectedImage);
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

        draw_name.setText(dh.getEstName());
        draw_img_user.setImageBitmap(dp.createImage(dh.getEstImage()));
        img_estprof.setImageBitmap(dp.createImage(dh.getEstImage()));
        if(dh.getEstImage()==null)
        {
            draw_img_user.setImageResource(R.drawable.ic_person);
            img_estprof.setImageResource(R.drawable.ic_person);
        }

    }

}
