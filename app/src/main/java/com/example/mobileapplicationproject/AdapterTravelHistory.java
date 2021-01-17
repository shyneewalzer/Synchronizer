package com.example.mobileapplicationproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class AdapterTravelHistory extends BaseExpandableListAdapter {

    ArrayList<String>listGroup;
    HashMap<String,ArrayList<String>>listChild;

    ArrayList<String>destination;
    ArrayList<String>timee;
    ArrayList<String>datee;

    public AdapterTravelHistory(ArrayList<String>listGroup, HashMap<String,ArrayList<String>>listChild, ArrayList<String>destination, ArrayList<String>timee, ArrayList<String>datee)
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
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.travelrow, parent, false);

        TextView tvdestination = convertView.findViewById(R.id.tdestination);
        TextView tvtime = convertView.findViewById(R.id.ttime);
        TextView tvdate = convertView.findViewById(R.id.tdate);

        tvdestination.setText(destination.get(groupPosition));
        tvtime.setText(timee.get(groupPosition));
        tvdate.setText(datee.get(groupPosition));

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.expand_person_item, parent, false);

        TextView tvname = convertView.findViewById(R.id.txt_companionNames);

        tvname.setText(getChild(groupPosition, childPosition).toString());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
