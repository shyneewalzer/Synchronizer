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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
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

public class FragmentUserEstabHistory extends Fragment implements View.OnClickListener{

    ConnectionController cc = new ConnectionController();
    DataHolder dh = new DataHolder();
    DataProcessor dp = new DataProcessor();
    DebugMode dm = new DebugMode();

    ArrayList<String> destid;
    ArrayList<String> destination;
    ArrayList<String> timee;
    ArrayList<String> datee;
    ArrayList<String> adr;
    ArrayList<String> searcher;

    String sqlsearch="";

    DatePickerDialog.OnDateSetListener dateSetListener;

    ExpandableListView expandableListView;
    ArrayList<String>listGroup;
    ArrayList<String>listPerson;
    HashMap<String,ArrayList<String>> listChild = new HashMap<>();
    AdapterEstabHistory expandAdapter;

    ///////////////////UI ELEMENTS////////////////
    View fragestab;
    LinearLayout estabviewer;
    ProgressBar pbar;
    ListView listView;

    Spinner spr_search;
    EditText edt_search;
    Button btn_search;

    SwipeRefreshLayout lo_userestabrefresher;


    public FragmentUserEstabHistory() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        fragestab = inflater.inflate(R.layout.fragment_user_estab_history, container, false);

        estabviewer = fragestab.findViewById(R.id.estabviewer);
        pbar = fragestab.findViewById(R.id.pbar);
        listView = fragestab.findViewById(R.id.listView);

        spr_search = fragestab.findViewById(R.id.spr_search);
        edt_search = fragestab.findViewById(R.id.edt_search);
        btn_search = fragestab.findViewById(R.id.btn_search);
        lo_userestabrefresher = fragestab.findViewById(R.id.lo_userestabrefresher);

        expandableListView = fragestab.findViewById(R.id.expandableListView);

        btn_search.setOnClickListener(this);
        edt_search.setOnClickListener(this);

        searcher = new ArrayList<>();

        searcher.add("Search by");
        searcher.add("Establishments");
        searcher.add("Date");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_format, searcher);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spr_search.setAdapter(dataAdapter);

        spr_search.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(parent.getItemAtPosition(position).equals("Establishments"))
                {
                    sqlsearch="est";
                    edt_search.setEnabled(true);
                    edt_search.setFocusableInTouchMode(true);
                }
                else if(parent.getItemAtPosition(position).equals("Date"))
                {
                    sqlsearch="date";
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

        lo_userestabrefresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Dbread dbread = new Dbread();
                dbread.execute();
            }
        });

        expandableListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (expandableListView.getChildAt(0) != null) {
                    lo_userestabrefresher.setEnabled(expandableListView.getFirstVisiblePosition() == 0 && expandableListView.getChildAt(0).getTop() == 0);
                }
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

                    ResultSet rs=con.createStatement().executeQuery("SELECT * FROM employee_scanned WHERE account_id = '"+ dh.getUserid() +"' GROUP BY batch ORDER BY date_entered ASC, time_entered ASC");

                    if(rs.isBeforeFirst())
                    {
                        isSuccess=true;
                        while (rs.next())
                        {
                            destid.add(rs.getString("est_id"));
                            timee.add(rs.getString("time_entered"));
                            datee.add(rs.getString("date_entered"));

                            listGroup.add(rs.getString("batch"));

                        }
                    }
                    rs.close();

////////////////////////////////////////////////////////////////////////////////////////////////////

                    for(int x=0;x<destid.size();x++)
                    {
                        listPerson = new ArrayList<>();
                        ResultSet rsest=con.createStatement().executeQuery("SELECT * FROM establishments WHERE est_id = '"+ destid.get(x) +"' ");

                        isSuccess=true;
                        while (rsest.next())
                        {
                            destination.add(rsest.getString("name"));
                            adr.add(rsest.getString("street"));

                        }

                        rsest.close();
                    }
////////////////////////////////////////////////////////////////////////////////////////////////////

                    for(int x=0;x<listGroup.size();x++)
                    {
                        listPerson = new ArrayList<>();
                        ResultSet rsemp=con.createStatement().executeQuery("SELECT * FROM employee_scanned WHERE batch = '"+ listGroup.get(x) +"' ORDER BY date_entered ASC, time_entered ASC");

                        isSuccess=true;
                        while (rsemp.next())
                        {
                            String fnamechecker = rsemp.getString("firstname");
                            String lnamechecker = rsemp.getString("lastname");
                            if((fnamechecker!=null && !fnamechecker.isEmpty()) && (lnamechecker!=null && !lnamechecker.isEmpty()))
                            {
                                String contactchecker = rsemp.getString("contact_number");
                                if(contactchecker!=null && !contactchecker.isEmpty())
                                {
                                    listPerson.add(rsemp.getString("firstname") + " " + rsemp.getString("lastname") + " - " + rsemp.getString("contact_number"));
                                }
                                else
                                {
                                    listPerson.add(rsemp.getString("firstname") + " " + rsemp.getString("lastname") + "- No Contact Provided");
                                }
                            }
                        }

                        listChild.put(listGroup.get(x), listPerson);

                        rsemp.close();
                    }

////////////////////////////////////////////////////////////////////////////////////////////////////

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
            destid = new ArrayList<>();
            timee = new ArrayList<>();
            datee = new ArrayList<>();
            destination = new ArrayList<>();
            adr = new ArrayList<>();
            listChild.clear();

            lo_userestabrefresher.setVisibility(View.GONE);
            estabviewer.setVisibility(View.GONE);
            pbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String a){

            if(isSuccess==true)
            {
                expandAdapter = new AdapterEstabHistory(listGroup, listChild, destination, timee, datee, adr);
                expandableListView.setAdapter(expandAdapter);

            }
            else
            {
                expandableListView.setAdapter((ExpandableListAdapter) null);

                dp.toasterlong(getContext(), "Nothing Found");
                Log.d("Search Results", msger+"");
            }

            lo_userestabrefresher.setVisibility(View.VISIBLE);
            estabviewer.setVisibility(View.VISIBLE);
            pbar.setVisibility(View.GONE);
        }
    }

    private class Dbsearch extends AsyncTask<String, String, String>
    {
        ArrayList<String> idholder;
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

                    if(sqlsearch.equals("est"))
                    {
                        ResultSet rs=con.createStatement().executeQuery("SELECT * FROM establishments WHERE name like '%"+ edt_search.getText() +"%' ");
                        isSuccess=true;
                        if(rs.isBeforeFirst())
                        {
                            isSuccess=true;
                            while (rs.next())
                            {
                                idholder.add(rs.getString("est_id"));

                            }
                        }
                        rs.close();

                        if(isSuccess==true)
                        {
                            for(int x=0;x<idholder.size();x++)
                            {
                                ResultSet rsid=con.createStatement().executeQuery("SELECT * FROM employee_scanned WHERE est_id = '"+ idholder.get(x) +"' GROUP BY batch");
                                isSuccess = true;
                                if(rsid.isBeforeFirst())
                                {
                                    while (rsid.next())
                                    {
                                        destid.add(rsid.getString("est_id"));
                                        timee.add(rsid.getString("time_entered"));
                                        datee.add(rsid.getString("date_entered"));

                                        listGroup.add(rsid.getString("batch"));

                                    }
                                }

                                rsid.close();
                            }

                        }


                    }
                    else if(sqlsearch.equals("date"))
                    {
                        ResultSet rs=con.createStatement().executeQuery("SELECT * FROM employee_scanned WHERE date_entered = '"+ edt_search.getText() +"' AND account_id = '"+ dh.getUserid() +"' GROUP BY batch");
                        isSuccess = true;
                        if(rs.isBeforeFirst())
                        {
                            isSuccess = true;
                            while (rs.next())
                            {
                                destid.add(rs.getString("est_id"));
                                timee.add(rs.getString("time_entered"));
                                datee.add(rs.getString("date_entered"));

                                listGroup.add(rs.getString("batch"));

                            }
                        }
                        else
                        {
                            isSuccess = false;
                        }

                        rs.close();
                    }

////////////////////////////////////////////////////////////////////////////////////////////////////

                    for(int x=0;x<destid.size();x++)
                    {
                        listPerson = new ArrayList<>();
                        ResultSet rsest=con.createStatement().executeQuery("SELECT * FROM establishments WHERE est_id = '"+ destid.get(x) +"' ");

                        isSuccess=true;
                        while (rsest.next())
                        {
                            destination.add(rsest.getString("name"));
                            adr.add(rsest.getString("street"));

                        }

                        rsest.close();
                    }
////////////////////////////////////////////////////////////////////////////////////////////////////

                    for(int x=0;x<listGroup.size();x++)
                    {
                        listPerson = new ArrayList<>();
                        ResultSet rsemp=con.createStatement().executeQuery("SELECT * FROM employee_scanned WHERE batch = '"+ listGroup.get(x) +"' ORDER BY date_entered ASC, time_entered ASC");

                        isSuccess=true;
                        while (rsemp.next())
                        {
                            String fnamechecker = rsemp.getString("firstname");
                            String lnamechecker = rsemp.getString("lastname");
                            if((fnamechecker!=null && !fnamechecker.isEmpty()) && (lnamechecker!=null && !lnamechecker.isEmpty()))
                            {
                                String contactchecker = rsemp.getString("contact_number");
                                if(contactchecker!=null && !contactchecker.isEmpty())
                                {
                                    listPerson.add(rsemp.getString("firstname") + " " + rsemp.getString("lastname") + " - " + rsemp.getString("contact_number"));
                                }
                                else
                                {
                                    listPerson.add(rsemp.getString("firstname") + " " + rsemp.getString("lastname") + "- No Contact Provided");
                                }
                            }
                        }

                        listChild.put(listGroup.get(x), listPerson);

                        rsemp.close();
                    }

////////////////////////////////////////////////////////////////////////////////////////////////////

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

            idholder = new ArrayList<>();
            listGroup = new ArrayList<>();
            destid = new ArrayList<>();
            timee = new ArrayList<>();
            datee = new ArrayList<>();
            destination = new ArrayList<>();
            adr = new ArrayList<>();
            listChild.clear();

            dm.displayMessage(getContext(), sqlsearch+"");
            lo_userestabrefresher.setVisibility(View.GONE);
            estabviewer.setVisibility(View.GONE);
            pbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String a){

            if(isSuccess==true)
            {
                expandAdapter = new AdapterEstabHistory(listGroup, listChild, destination, timee, datee, adr);
                expandableListView.setAdapter(expandAdapter);

            }
            else
            {
                expandableListView.setAdapter((ExpandableListAdapter) null);

                dp.toasterlong(getContext(), "Nothing Found");
                Log.d("Search Results", msger+"");
            }

            lo_userestabrefresher.setVisibility(View.VISIBLE);
            estabviewer.setVisibility(View.VISIBLE);
            pbar.setVisibility(View.GONE);

        }
    }
}
