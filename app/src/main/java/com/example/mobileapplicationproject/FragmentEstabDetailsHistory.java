package com.example.mobileapplicationproject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mobileapplicationproject.DataController.ConnectionController;
import com.example.mobileapplicationproject.DataController.DataHolder;
import com.example.mobileapplicationproject.DataController.DataProcessor;
import com.example.mobileapplicationproject.DataController.DebugMode;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class FragmentEstabDetailsHistory extends Fragment implements View.OnClickListener{

    ConnectionController cc = new ConnectionController();
    DataHolder dh = new DataHolder();
    DataProcessor dp = new DataProcessor();
    DebugMode dm = new DebugMode();

    ArrayList<String> parentid;
    ArrayList<String> timee;
    ArrayList<String> datee;
    ArrayList<String> searcher;
    ArrayList<String> empid;
    ArrayList<String> empname;

    DatePickerDialog.OnDateSetListener dateSetListener;

    ExpandableListView expandableListView;
    ArrayList<String>listGroup;
    ArrayList<String>listPerson;
    HashMap<String,ArrayList<String>> listChild = new HashMap<>();
    AdapterDetailsHistory expandAdapter;

    ///////////////////UI ELEMENTS////////////////
    View fragestab;
    LinearLayout travelviewer;
    ProgressBar pbar;
    ListView listView;

    Spinner spr_search;
    EditText edt_search;
    Button btn_search;


    public FragmentEstabDetailsHistory() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        fragestab = inflater.inflate(R.layout.fragment_estab_details_history, container, false);

        travelviewer = fragestab.findViewById(R.id.travelviewer);
//        pbar = findViewById(R.id.pbar);
        listView = fragestab.findViewById(R.id.listView);

        spr_search = fragestab.findViewById(R.id.spr_search);
        edt_search = fragestab.findViewById(R.id.edt_search);
        btn_search = fragestab.findViewById(R.id.btn_search);

        expandableListView = fragestab.findViewById(R.id.expandableListView);

        btn_search.setOnClickListener(this);
        edt_search.setOnClickListener(this);

        searcher = new ArrayList<>();

        searcher.add("Search by");
        searcher.add("Individuals");
        searcher.add("Employees");
        searcher.add("Date");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_format, searcher);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spr_search.setAdapter(dataAdapter);

        spr_search.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(parent.getItemAtPosition(position).equals("Search by"))
                {
                    dp.toasterlong(getContext(), "Please Select Search Criteria");
                    edt_search.setEnabled(false);
                }
                else if(parent.getItemAtPosition(position).equals("Date"))
                {
                    edt_search.setEnabled(true);
                    edt_search.setFocusableInTouchMode(false);
                }
                else
                {
                    edt_search.setEnabled(true);
                    edt_search.setFocusableInTouchMode(true);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                dp.toasterlong(getContext(), "Please Select Search Criteria");

            }
        });

        Dbread dbread = new Dbread();
        dbread.execute();

        return fragestab;
    }


    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.btn_search)
        {
            if(spr_search.getSelectedItem().toString().equals("Search by"))
            {
                dp.toasterlong(getContext(), "Please Select Search Criteria");
            }
            else
            {
                Dbsearch dbsearch = new Dbsearch();
                dbsearch.execute();
            }
        }
        else if(v.getId()==R.id.edt_search)
        {
            if(spr_search.getSelectedItem().equals("Date"))
            {
                Calendar cal = Calendar.getInstance();
                int cal_yr = cal.get(Calendar.YEAR);
                int cal_mo = cal.get(Calendar.MONTH);
                int cal_dy = cal.get(Calendar.DAY_OF_MONTH);
                dm.displayMessage(getContext(),cal_yr + "-" + cal_mo + "-" + cal_dy);

                DatePickerDialog datepicker = new DatePickerDialog(getContext(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        edt_search.setText(year + "-" + month + "-" + dayOfMonth);
                    }
                },cal_yr,cal_mo,cal_dy);
                datepicker.show();
            }

        }

    }

    private class Dbread extends AsyncTask<String, String, String>
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

                    ResultSet rs=con.createStatement().executeQuery("SELECT * FROM employee_scanned WHERE est_id = '"+ dh.getEstID() +"' GROUP BY batch ORDER BY date_entered ASC, time_entered ASC");

                    if(rs.isBeforeFirst())
                    {
                        isSuccess=true;
                        while (rs.next())
                        {
                            parentid.add(rs.getString("account_id"));
                            timee.add(rs.getString("time_entered"));
                            datee.add(rs.getString("date_entered"));
                            empid.add(rs.getString("employee_id"));

                            listGroup.add(rs.getString("batch"));

                        }
                    }
                    rs.close();

                    if(isSuccess==true)
                    {
                        for (int x=0;x<listGroup.size();x++)
                        {
                            ResultSet rsparent=con.createStatement().executeQuery("SELECT * FROM user_profile WHERE account_id = '"+ parentid.get(x) +"' ");

                            while (rsparent.next())
                            {
                                parentid.set(x, rsparent.getString("firstname") + " " + rsparent.getString("lastname") + "_" + rsparent.getString("contactnumber"));
                            }
                            rsparent.close();
                        }
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

            listGroup = new ArrayList<>();
            parentid = new ArrayList<>();
            timee = new ArrayList<>();
            datee = new ArrayList<>();
            empid = new ArrayList<>();

            travelviewer.setVisibility(View.GONE);
//            pbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String a){

            travelviewer.setVisibility(View.VISIBLE);
//            pbar.setVisibility(View.GONE);

            Dbreademp dbreademp = new Dbreademp();
            dbreademp.execute();

            dm.displayMessage(getContext(), listGroup+"");
        }
    }

    private class Dbreademp extends AsyncTask<String, String, String>
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
                    for(int x=0;x<listGroup.size();x++)
                    {

                    }
                    ResultSet rs=con.createStatement().executeQuery("SELECT * FROM user_profile WHERE user_id = '"+ dh.getEstID() +"' ");

                    while (rs.next())
                    {
                        empname.add(rs.getString("firstname") + " " + rs.getString("lastname"));
                    }

                    rs.close();

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

            travelviewer.setVisibility(View.GONE);
//            pbar.setVisibility(View.VISIBLE);

            empname = new ArrayList<>();
        }

        @Override
        protected void onPostExecute(String a){

            travelviewer.setVisibility(View.VISIBLE);
//            pbar.setVisibility(View.GONE);

            Dbreadsecond dbreadsecond = new Dbreadsecond();
            dbreadsecond.execute();

            dm.displayMessage(getContext(), listGroup+"");
        }
    }

    private class Dbreadsecond extends AsyncTask<String, String, String>
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

                    for(int x=0;x<listGroup.size();x++)
                    {
                        listPerson = new ArrayList<>();
                        listPerson.add(parentid.get(x));
                        ResultSet rs=con.createStatement().executeQuery("SELECT * FROM employee_scanned WHERE batch = '"+ listGroup.get(x) +"' ORDER BY date_entered ASC, time_entered ASC");

                        isSuccess=true;
                        while (rs.next())
                        {
                            String fnamechecker = rs.getString("firstname");
                            String lnamechecker = rs.getString("lastname");
                            String contactnum = rs.getString("contact_number");
                            if((fnamechecker!=null && !fnamechecker.isEmpty()) && (lnamechecker!=null && !lnamechecker.isEmpty()))
                            {
                                if(contactnum!=null && !contactnum.isEmpty())
                                {

                                }
                                else
                                {
                                    contactnum = "No Contact Provided";
                                }
                                listPerson.add(rs.getString("firstname") + " " + rs.getString("lastname") + "_" + contactnum);
                            }
                        }

                        listChild.put(listGroup.get(x), listPerson);

                        rs.close();
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

            travelviewer.setVisibility(View.GONE);
//            pbar.setVisibility(View.VISIBLE);
            listChild.clear();
        }

        @Override
        protected void onPostExecute(String a){

            travelviewer.setVisibility(View.VISIBLE);
//            pbar.setVisibility(View.GONE);

            expandAdapter = new AdapterDetailsHistory(listGroup, listChild, empname, timee, datee);
            expandableListView.setAdapter(expandAdapter);
            dm.displayMessage(getContext(), listPerson+"");
        }
    }

    private class Dbsearch extends AsyncTask<String, String, String>
    {
        int xxx;
        String idholder;
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

                    if(spr_search.getSelectedItem().equals("Individuals"))
                    {
                        ResultSet rs=con.createStatement().executeQuery("SELECT * FROM user_profile WHERE concat_ws(' ', firstname, lastname) LIKE '%"+ edt_search.getText() +"%' ");

                        if(rs.isBeforeFirst())
                        {
                            isSuccess=true;
                            while (rs.next())
                            {
                                idholder = rs.getString("account_id");

                            }
                        }
                        rs.close();

                        ResultSet rsid=con.createStatement().executeQuery("SELECT * FROM employee_scanned WHERE account_id = '"+ idholder +"' AND est_id = '"+ dh.getEstID() +"' GROUP BY batch");

                        while (rsid.next())
                        {

                            parentid.add(rsid.getString("account_id"));
                            timee.add(rsid.getString("time_entered"));
                            datee.add(rsid.getString("date_entered"));

                            listGroup.add(rsid.getString("batch"));

                        }
                        rsid.close();

                        if(isSuccess==true)
                        {
                            for (int x=0;x<listGroup.size();x++)
                            {
                                ResultSet rsparent=con.createStatement().executeQuery("SELECT * FROM user_profile WHERE account_id = '"+ parentid.get(x) +"' ");

                                while (rsparent.next())
                                {
                                    parentid.set(x, rsparent.getString("firstname") + " " + rsparent.getString("lastname") + "_" + rsparent.getString("contactnumber"));
                                }
                                rsparent.close();
                            }
                        }
                    }
                    //TODO: fix employee search
                    else if(spr_search.getSelectedItem().equals("Employees"))
                    {
                        ResultSet rs=con.createStatement().executeQuery("SELECT * FROM user_profile WHERE concat_ws(' ', firstname, lastname) LIKE '%"+ edt_search.getText() +"%' ");

                        if(rs.isBeforeFirst())
                        {
                            isSuccess=true;
                            while (rs.next())
                            {
                                idholder = rs.getString("account_id");

                            }
                        }
                        rs.close();

                        ResultSet rsid=con.createStatement().executeQuery("SELECT * FROM employee_scanned WHERE employee_id = '"+ idholder +"' AND est_id = '"+ dh.getEstID() +"' GROUP BY batch");

                        while (rsid.next())
                        {

                            parentid.add(rsid.getString("account_id"));
                            timee.add(rsid.getString("time_entered"));
                            datee.add(rsid.getString("date_entered"));

                            listGroup.add(rsid.getString("batch"));

                        }
                        rsid.close();

                        if(isSuccess==true)
                        {
                            for (int x=0;x<listGroup.size();x++)
                            {
                                ResultSet rsparent=con.createStatement().executeQuery("SELECT * FROM user_profile WHERE employee_id = '"+ parentid.get(x) +"' ");

                                while (rsparent.next())
                                {
                                    parentid.set(x, rsparent.getString("firstname") + " " + rsparent.getString("lastname") + "_" + rsparent.getString("contactnumber"));
                                }
                                rsparent.close();
                            }
                        }
                    }
                    else if(spr_search.getSelectedItem().equals("Date"))
                    {
                        ResultSet rs=con.createStatement().executeQuery("SELECT * FROM employee_scanned WHERE date_entered = '"+ edt_search.getText() +"' AND est_id = '"+ dh.getEstID() +"' GROUP BY batch");

                        while (rs.next())
                        {
                            parentid.add(rs.getString("account_id"));
                            timee.add(rs.getString("time_entered"));
                            datee.add(rs.getString("date_entered"));

                            listGroup.add(rs.getString("batch"));

                        }
                        rs.close();

                        if(isSuccess==true)
                        {
                            for (int x=0;x<listGroup.size();x++)
                            {
                                ResultSet rsparent=con.createStatement().executeQuery("SELECT * FROM user_profile WHERE employee_id = '"+ parentid.get(x) +"' ");

                                while (rsparent.next())
                                {
                                    parentid.set(x, rsparent.getString("firstname") + " " + rsparent.getString("lastname") + "_" + rsparent.getString("contactnumber"));
                                }
                                rsparent.close();
                            }
                        }
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

            listGroup = new ArrayList<>();
            timee = new ArrayList<>();
            datee = new ArrayList<>();


            travelviewer.setVisibility(View.GONE);
//            pbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String a){

            travelviewer.setVisibility(View.VISIBLE);
//            pbar.setVisibility(View.GONE);
            dm.displayMessage(getContext(), "row"+xxx);


        }
    }
}
