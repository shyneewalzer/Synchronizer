package com.example.mobileapplicationproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.mobileapplicationproject.DataController.DataProcessor;

import java.util.ArrayList;
import java.util.HashMap;

public class AdapterDetailsHistory extends BaseExpandableListAdapter {

    ArrayList<String>listGroup;
    HashMap<String,ArrayList<String>>listChild;

    ArrayList<String>destination;
    ArrayList<String>timee;
    ArrayList<String>datee;
    ArrayList<String>personinfo;

    DataProcessor dp = new DataProcessor();

    public AdapterDetailsHistory(ArrayList<String>listGroup, HashMap<String,ArrayList<String>>listChild, ArrayList<String>destination, ArrayList<String>timee, ArrayList<String>datee)
    {
        this.listGroup = listGroup;
        this.listChild = listChild;
        this.destination = destination;
        this.timee = timee;
        this.datee = datee;
    }

    @Override
    public int getGroupCount() {
        return listGroup.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listChild.get(listGroup.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listGroup.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listChild.get(listGroup.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_estab, parent, false);

        TextView tvdestination = convertView.findViewById(R.id.edestination);
        TextView tvtime = convertView.findViewById(R.id.etime);
        TextView tvdate = convertView.findViewById(R.id.edate);
        TextView tvadr = convertView.findViewById(R.id.eadr);

        tvdestination.setText(destination.get(groupPosition));
        tvtime.setText(timee.get(groupPosition));
        tvdate.setText(datee.get(groupPosition));
        tvadr.setVisibility(View.GONE);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.expand_trace_person_item, parent, false);

        TextView tvname = convertView.findViewById(R.id.txt_traceNames);
        TextView tvcontact = convertView.findViewById(R.id.txt_traceContacts);
        TextView tvdestination = convertView.findViewById(R.id.txt_traceDestination);

        String temppp = getChild(groupPosition, childPosition).toString();

        personinfo = new ArrayList<>();
        personinfo = dp.splitter(temppp, "_");

        tvname.setText("   " + personinfo.get(0));
        tvcontact.setText("   " + personinfo.get(1));
        tvdestination.setVisibility(View.GONE);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
