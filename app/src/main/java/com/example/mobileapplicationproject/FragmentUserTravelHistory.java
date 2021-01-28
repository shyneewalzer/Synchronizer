package com.example.mobileapplicationproject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mobileapplicationproject.DataController.ConnectionController;
import com.example.mobileapplicationproject.DataController.DataHolder;
import com.example.mobileapplicationproject.DataController.DataProcessor;
import com.example.mobileapplicationproject.DataController.DebugMode;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class FragmentUserTravelHistory extends Fragment implements View.OnClickListener{



    ConnectionController cc = new ConnectionController();
    DataHolder dh = new DataHolder();
    DataProcessor dp = new DataProcessor();
    DebugMode dm = new DebugMode();

    ArrayList<String> destination;
    ArrayList<String> timee;
    ArrayList<String> datee;
    ArrayList<String> searcher;

    String testers = "";

    DatePickerDialog.OnDateSetListener dateSetListener;

    ExpandableListView expandableListView;
    ArrayList<String>listGroup;
    ArrayList<String>listPerson;
    HashMap<String,ArrayList<String>>listChild = new HashMap<>();
    AdapterTravelHistory adapterTravelHistory;

    ///////////////////UI ELEMENTS////////////////
    View fragtrav;
    LinearLayout travelviewer;
    ProgressBar pbar;
    ListView listView;

    Spinner spr_search;
    EditText edt_search;
    Button btn_search;
    SwipeRefreshLayout lo_usertravelrefresher;


    public FragmentUserTravelHistory() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        fragtrav = inflater.inflate(R.layout.fragment_user_travel_history, container, false);

        travelviewer = fragtrav.findViewById(R.id.travelviewer);
        pbar = fragtrav.findViewById(R.id.pbar);
        listView = fragtrav.findViewById(R.id.listView);

        spr_search = fragtrav.findViewById(R.id.spr_search);
        edt_search = fragtrav.findViewById(R.id.edt_search);
        btn_search = fragtrav.findViewById(R.id.btn_search);
        lo_usertravelrefresher = fragtrav.findViewById(R.id.lo_usertravelrefresher);

        expandableListView = fragtrav.findViewById(R.id.expandableListView);

        btn_search.setOnClickListener(this);
        edt_search.setOnClickListener(this);

        searcher = new ArrayList<>();

        searcher.add("Search by");
        searcher.add("Destination");
        searcher.add("Date");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_format, searcher);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spr_search.setAdapter(dataAdapter);

        spr_search.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(parent.getItemAtPosition(position).equals("Destination"))
                {
                    edt_search.setEnabled(true);
                    edt_search.setFocusableInTouchMode(true);
                }
                else if(parent.getItemAtPosition(position).equals("Date"))
                {
                    edt_search.setEnabled(true);
                    edt_search.setFocusableInTouchMode(false);
                }
                else
                {
                    edt_search.setEnabled(false);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                dp.toasterlong(getContext(), "Please Select Search Criteria");

            }
        });

        lo_usertravelrefresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                lo_usertravelrefresher.setRefreshing(false);
                Dbread dbread = new Dbread();
                dbread.execute();
            }
        });

        Dbread dbread = new Dbread();
        dbread.execute();


        return fragtrav;
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

                    ResultSet rs=con.createStatement().executeQuery("SELECT * FROM travel_history WHERE account_id = '"+ dh.getUserid() +"' GROUP BY batch ORDER BY date_boarded ASC, time_boarded ASC");
                    isSuccess = true;
                    if(rs.isBeforeFirst())
                    {
                        isSuccess=true;
                        while (rs.next())
                        {
                            destination.add(rs.getString("destination"));
                            timee.add(rs.getString("time_boarded"));
                            datee.add(rs.getString("date_boarded"));

                            listGroup.add(rs.getString("batch"));

                        }
                    }
                    else
                    {
                        isSuccess = false;
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

            listGroup = new ArrayList<>();
            destination = new ArrayList<>();
            timee = new ArrayList<>();
            datee = new ArrayList<>();

            lo_usertravelrefresher.setVisibility(View.GONE);
            travelviewer.setVisibility(View.GONE);
            pbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String a){

            if(isSuccess==true)
            {
                Dbreadsecond dbreadsecond = new Dbreadsecond();
                dbreadsecond.execute();
            }
            else
            {
                pbar.setVisibility(View.VISIBLE);
                dp.toasterlong(getContext(), "Nothing Found");
                Log.d("Search Results", msger+"");
            }

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
                        ResultSet rs=con.createStatement().executeQuery("SELECT * FROM travel_history WHERE batch = '"+ listGroup.get(x) +"' ORDER BY date_boarded ASC, time_boarded ASC");

                        isSuccess=true;
                        while (rs.next())
                        {
                            String fnamechecker = rs.getString("firstname");
                            String lnamechecker = rs.getString("lastname");
                            if((fnamechecker!=null && !fnamechecker.isEmpty()) && (lnamechecker!=null && !lnamechecker.isEmpty()))
                            {
                                listPerson.add(rs.getString("firstname") + " " + rs.getString("lastname") + " - " + rs.getString("contact_number"));
                            }
                            else
                            {
                                listPerson.add("No Companions");
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

            listChild.clear();
        }

        @Override
        protected void onPostExecute(String a){

            if(isSuccess==true)
            {
                adapterTravelHistory = new AdapterTravelHistory(listGroup, listChild, destination, timee, datee);
                expandableListView.setAdapter(adapterTravelHistory);

                lo_usertravelrefresher.setVisibility(View.VISIBLE);
                travelviewer.setVisibility(View.VISIBLE);
                pbar.setVisibility(View.GONE);
            }
            else
            {
                pbar.setVisibility(View.VISIBLE);
                dp.toasterlong(getContext(), msger+"");
            }

        }
    }

    private class Dbsearch extends AsyncTask<String, String, String>
    {
        String sqlsearch;
        int tempp;
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
                    if(spr_search.getSelectedItem().equals("Destination"))
                    {
                        sqlsearch = "SELECT * FROM travel_history WHERE destination LIKE '%"+ edt_search.getText() +"%' AND account_id = '"+ dh.getUserid() +"' GROUP BY batch";
                    }
                    else if(spr_search.getSelectedItem().equals("Date"))
                    {
                        sqlsearch = "SELECT * FROM travel_history WHERE date_boarded = '"+ edt_search.getText() +"' AND account_id = '"+ dh.getUserid() +"' GROUP BY batch";
                    }
                    ResultSet rs=con.createStatement().executeQuery(sqlsearch);
                    isSuccess = true;
                    if(rs.isBeforeFirst())
                    {
                        isSuccess=true;
                        while (rs.next())
                        {
                            tempp=rs.getRow();
                            destination.add(rs.getString("destination"));
                            timee.add(rs.getString("time_boarded"));
                            datee.add(rs.getString("date_boarded"));

                            listGroup.add(rs.getString("batch"));
                        }
                    }
                    else
                    {
                        isSuccess = false;
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

            listGroup = new ArrayList<>();
            destination = new ArrayList<>();
            timee = new ArrayList<>();
            datee = new ArrayList<>();

            lo_usertravelrefresher.setVisibility(View.GONE);
            travelviewer.setVisibility(View.GONE);
            pbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String a){


            if(isSuccess==true)
            {
                Dbreadsecond dbreadsecond = new Dbreadsecond();
                dbreadsecond.execute();
            }
            else
            {
                lo_usertravelrefresher.setVisibility(View.VISIBLE);
                pbar.setVisibility(View.VISIBLE);
                dp.toasterlong(getContext(), "Nothing Found");
                Log.d("Search results", msger+"");
            }

            dm.displayMessage(getContext(), tempp+"");
        }
    }

}
