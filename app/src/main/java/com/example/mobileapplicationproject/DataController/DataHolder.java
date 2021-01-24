package com.example.mobileapplicationproject.DataController;

import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;

public class DataHolder {

    private static int userid;
    private static String email, type;
    private static String pFName, pLName, pMName, pImage, pBdayHolder, pContact;
    private static Calendar pBday = Calendar.getInstance();
    private static String house, brgy, city;
    private static String estAcctID, estName, estStreet, estContact, estOwner, estImage, estID;
    private static String prevAct;
    private static ArrayList<String> listRoutes;
    private static ArrayList<String> listDestination;
    private static ArrayList<String> listBrgy;
    private static String visitmode = "travel";




    public void setAcct(int iptuserid,String iptemail, String ipttype)
    {
        userid = iptuserid;
        type = ipttype;
        email=iptemail;
    }

    public int getUserid()
    {
        return userid;
    }

    public String getEmail()
    {
        return email;
    }

    public String getType()
    {
        return type;
    }

    public void setUserid(int iptuserid)
    {
        userid = iptuserid;
    }

    public void setProfile(String iptFname, String iptLname, String iptMname, Date iptBday, String iptContact, String iptImage)
    {
        pFName = iptFname;
        pLName = iptLname;
        pMName = iptMname;
        pBday.setTime(iptBday);
        pBdayHolder = pBday.get(Calendar.YEAR) + "-" + (pBday.get(Calendar.MONTH)+1) + "-" + pBday.get(Calendar.DAY_OF_MONTH);
        pContact = iptContact;
        pImage = iptImage;
    }

    public void setEstProfile(String iptAcctID, String iptName, String iptStreet, String iptContact, String iptOwner, String iptImage, String iptID)
    {
        estAcctID = iptAcctID;
        estID = iptID;
        estName = iptName;
        estStreet = iptStreet;
        estContact = iptContact;
        estOwner = iptOwner;
        estImage = iptImage;
    }

    public void setEstProfile(String iptName, String iptStreet, String iptContact, String iptOwner)
    {
        estName = iptName;
        estStreet = iptStreet;
        estContact = iptContact;
        estOwner = iptOwner;
    }

    public String getEstAcctID()
    {
        return estAcctID;
    }

    public String getEstID()
    {
        return estID;
    }

    public String getEstName()
    {
        return estName;
    }

    public String getEstStreet()
    {
        return estStreet;
    }

    public String getEstContact()
    {
        return estContact;
    }

    public String getEstOwner()
    {
        return estOwner;
    }

    public String getEstImage()
    {
        return estImage;
    }

    public void setEstImage(String iptEstImage)
    {
        estImage = iptEstImage;
    }

    public String getpFName()
    {
        return pFName;
    }

    public String getpLName()
    {
        return pLName;
    }

    public String getpMName()
    {
        return pMName;
    }

    public String getpBday()
    {
        return pBdayHolder;
    }

    public String getpContact()
    {
        return pContact;
    }

    public String getpImage()
    {
        return pImage;
    }

    public void setpImage(String iptImage)
    {
        pImage = iptImage;
    }

    public Calendar getTempDate()
    {
        return pBday;
    }

    public void setAddress(String ipthouse, String iptbrgy, String iptcity)
    {
        house = ipthouse;
        brgy = iptbrgy;
        city = iptcity;
    }

    public String getHouse()
    {
        return house;
    }

    public String getBrgy()
    {
        return brgy;
    }

    public String getCity()
    {
        return city;
    }

    public void setLocations(ArrayList<String>iptroutes, ArrayList<String>iptdestination, ArrayList<String>iptbrgy)
    {
        listRoutes = iptroutes;
        listDestination = iptdestination;
        listBrgy = iptbrgy;
    }

    public ArrayList<String>getListRoutes()
    {
        return listRoutes;
    }

    public ArrayList<String>getListDestination()
    {
        return listDestination;
    }

    public ArrayList<String>getListBrgy()
    {
        return listBrgy;
    }

    public void setVisitmode(String iptvisit)
    {
        visitmode = iptvisit;
    }

    public String getVisitmode()
    {
        return visitmode;
    }

}
