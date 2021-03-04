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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

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

public class FragmentEstabCountHistory extends Fragment implements View.OnClickListener{



    ConnectionController cc = new ConnectionController();
    DataHolder dh = new DataHolder();
    DataProcessor dp = new DataProcessor();
    DebugMode dm = new DebugMode();

    ArrayList<String> employeename;
    ArrayList<String> employeeid;
    ArrayList<Integer> employeecount;
    ArrayList<String> datee;
    int finalcount;

    DatePickerDialog.OnDateSetListener dateSetListener;
    DatePickerDialog.OnDateSetListener dateSetListener2;

    Calendar dateofnow;
    Calendar starter = Calendar.getInstance();
    Calendar ender = Calendar.getInstance();

    CustomAdapter customAdapter;

    ///////////////////UI ELEMENTS////////////////
    View fragtrav;
    LinearLayout lo_countviewer;
    ProgressBar pbar;
    ListView listView;

    EditText edt_start, edt_end;
    Button btn_search;

    SwipeRefreshLayout lo_estabcountrefresher;


    public FragmentEstabCountHistory() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        fragtrav = inflater.inflate(R.layout.fragment_estab_count_history, container, false);

        lo_countviewer = fragtrav.findViewById(R.id.lo_countviewer);
        pbar = fragtrav.findViewById(R.id.pbar);
        listView = fragtrav.findViewById(R.id.listView);

        edt_start = fragtrav.findViewById(R.id.edt_start);
        edt_end = fragtrav.findViewById(R.id.edt_end);
        btn_search = fragtrav.findViewById(R.id.btn_search);
        lo_estabcountrefresher = fragtrav.findViewById(R.id.lo_estabcountrefresher);

        btn_search.setOnClickListener(this);
        edt_start.setOnClickListener(this);
        edt_end.setOnClickListener(this);

        lo_estabcountrefresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                lo_estabcountrefresher.setRefreshing(false);
                Dbdateuptonow dbdateuptonow = new Dbdateuptonow();
                dbdateuptonow.execute();
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (listView.getChildAt(0) != null) {
                    lo_estabcountrefresher.setEnabled(listView.getFirstVisiblePosition() == 0 && listView.getChildAt(0).getTop() == 0);
                }
            }
        });

        Dbdateuptonow dbdateuptonow = new Dbdateuptonow();
        dbdateuptonow.execute();

        return fragtrav;
    }


    private class Dbdateuptonow extends AsyncTask<String, String, String>
    {
        String strdate="";
        Calendar tempdate = Calendar.getInstance();
        int getcount=0;
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
                    ResultSet rsi=con.createStatement().executeQuery("SELECT * FROM user_profile WHERE account_id = '"+ dh.getEstAcctID() +"' ");
                    isSuccess = true;
                    while(rsi.next())
                    {
                        employeeid.add(rsi.getString("user_id"));
                        employeename.add(rsi.getString("firstname") + " " + rsi.getString("lastname"));
                    }

                    rsi.close();
////////////////////////////////////////////////////////////////////////////////////////////////////

                    for(int x=0;x<employeeid.size();x++)
                    {
                        ResultSet rs=con.createStatement().executeQuery("SELECT * FROM employee_scanned WHERE employee_id = '"+ employeeid.get(x) +"' AND date_entered < '"+ strdate +"' ");

                        isSuccess=true;
                        while (rs.next())
                        {
                            String fnamechecker = rs.getString("firstname");
                            String lnamechecker = rs.getString("lastname");
                            if((fnamechecker!=null && !fnamechecker.isEmpty()) && (lnamechecker!=null && !lnamechecker.isEmpty()))
                            {
                                getcount = getcount + 1;
                            }
                        }
                        rs.close();

                        ResultSet rsbatch=con.createStatement().executeQuery("SELECT * FROM employee_scanned WHERE employee_id = '"+ employeeid.get(x) +"' AND date_entered < '"+ strdate +"' GROUP BY batch ");
                        isSuccess=true;
                        while (rsbatch.next())
                        {
                            getcount = getcount + 1;
                        }
                        isSuccess = true;
                        rsbatch.close();
                        employeecount.add(getcount);
                        getcount = 0;

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

            lo_estabcountrefresher.setVisibility(View.GONE);
            lo_countviewer.setVisibility(View.GONE);
            pbar.setVisibility(View.VISIBLE);

            employeename = new ArrayList<>();
            employeeid = new ArrayList<>();

            finalcount = 0;
            employeecount = new ArrayList<>();
            dateofnow = Calendar.getInstance();
            strdate = dateofnow.get(Calendar.YEAR) + "-" + (dateofnow.get(Calendar.MONTH)+1) + "-" + (dateofnow.get(Calendar.DAY_OF_MONTH)+1);

        }

        @Override
        protected void onPostExecute(String a){

            if(isSuccess==true)
            {
                for(int x=0;x<employeecount.size();x++)
                {
                    finalcount = finalcount + employeecount.get(x);
                }

                customAdapter = new CustomAdapter();
                listView.setAdapter(customAdapter);;

                lo_estabcountrefresher.setVisibility(View.VISIBLE);
                lo_countviewer.setVisibility(View.VISIBLE);
                pbar.setVisibility(View.GONE);
            }
            else
            {
                customAdapter = new CustomAdapter();
                listView.setAdapter(customAdapter);;

                lo_estabcountrefresher.setVisibility(View.VISIBLE);
                lo_countviewer.setVisibility(View.VISIBLE);
                pbar.setVisibility(View.GONE);
                dp.toasterlong(getContext(), "No Records yet");
            }

        }
    }

    private class Dbreadbetweendates extends AsyncTask<String, String, String>
    {
        int getcount=0;
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
                    ResultSet rsi=con.createStatement().executeQuery("SELECT * FROM user_profile WHERE account_id = '"+ dh.getEstAcctID() +"' ");
                    isSuccess = true;
                    while(rsi.next())
                    {
                        employeeid.add(rsi.getString("user_id"));
                        employeename.add(rsi.getString("firstname") + " " + rsi.getString("lastname"));
                    }

                    rsi.close();
////////////////////////////////////////////////////////////////////////////////////////////////////

                    for(int x=0;x<employeeid.size();x++)
                    {
                        ResultSet rs=con.createStatement().executeQuery("SELECT * FROM employee_scanned WHERE employee_id = '"+ employeeid.get(x) +"' AND (date_entered between '"+ edt_start.getText() +"' and '"+ edt_end.getText() +"') ");

                        isSuccess=true;
                        while (rs.next())
                        {
                            getcount = getcount + 1;
                        }
                        rs.close();

                        ResultSet rsbatch=con.createStatement().executeQuery("SELECT * FROM employee_scanned WHERE employee_id = '"+ employeeid.get(x) +"' AND (date_entered between '"+ edt_start.getText() +"' and '"+ edt_end.getText() +"') GROUP BY batch ");

                        while (rsbatch.next())
                        {
                            String fnamechecker = rsbatch.getString("firstname");
                            String lnamechecker = rsbatch.getString("lastname");
                            if((fnamechecker!=null && !fnamechecker.isEmpty()) && (lnamechecker!=null && !lnamechecker.isEmpty()))
                            {
                                getcount = getcount + 1;
                            }
                        }
                        rsbatch.close();

                        employeecount.add(getcount);
                        Log.d("Employee Count", employeecount+"");
                        getcount=0;

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

            starter.setTime(dp.stringToDate(edt_start.getText()+""));
            ender.setTime(dp.stringToDate(edt_end.getText()+""));

            ender.add(Calendar.DAY_OF_MONTH, 1);
            ender.add(Calendar.MONTH, 1);
            finalcount = 0;
            employeecount = new ArrayList<>();

            employeename = new ArrayList<>();
            employeeid = new ArrayList<>();

            lo_estabcountrefresher.setVisibility(View.GONE);
            lo_countviewer.setVisibility(View.GONE);
            pbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String a){

            if(isSuccess = true)
            {
                for(int x=0;x<employeecount.size();x++)
                {
                    finalcount = finalcount + employeecount.get(x);
                }

                customAdapter.notifyDataSetChanged();

                lo_estabcountrefresher.setVisibility(View.VISIBLE);
                lo_countviewer.setVisibility(View.VISIBLE);
                pbar.setVisibility(View.GONE);
            }
            else
            {
                customAdapter.notifyDataSetChanged();

                lo_estabcountrefresher.setVisibility(View.VISIBLE);
                lo_countviewer.setVisibility(View.VISIBLE);
                pbar.setVisibility(View.GONE);
                dp.toasterlong(getContext(), "Nothing Found");
            }


        }
    }

    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.btn_search)
        {
            Dbreadbetweendates dbreadbetweendates = new Dbreadbetweendates();
            dbreadbetweendates.execute();

        }
        else if(v.getId()==R.id.edt_start)
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
                    edt_start.setText(year + "-" + month + "-" + dayOfMonth);
                }
            },cal_yr,cal_mo,cal_dy);
            datepicker.show();

        }
        else if(v.getId()==R.id.edt_end)
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
                    edt_end.setText(year + "-" + month + "-" + dayOfMonth);
                }
            },cal_yr,cal_mo,cal_dy);
            datepicker.show();
        }

    }

    class CustomAdapter extends BaseAdapter {
        @Override
        public int getCount()
        {
            return employeename.size();
        }

        @Override
        public Object getItem(int i)
        {
            return null;
        }

        @Override
        public long getItemId(int i)
        {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view= getLayoutInflater().inflate(R.layout.row_counter,null);

            TextView tvname = view.findViewById(R.id.list_txt_empnames);
            TextView tvcount = view.findViewById(R.id.list_txt_counts);
            TextView tvfinalcount = view.findViewById(R.id.list_txt_finalcount);

            LinearLayout lo_finaviewer = view.findViewById(R.id.list_lo_finalviewer);

            tvname.setText(employeename.get(i));
            tvcount.setText(employeecount.get(i)+"");
            tvfinalcount.setText(finalcount+"");

            if(employeename.size()==i+1)
            {
                lo_finaviewer.setVisibility(View.VISIBLE);
            }
            else
            {
                lo_finaviewer.setVisibility(View.GONE);
            }

            return view;
        }
    }

}
