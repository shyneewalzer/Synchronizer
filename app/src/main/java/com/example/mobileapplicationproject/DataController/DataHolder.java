package com.example.mobileapplicationproject.DataController;

import java.sql.Date;
import java.util.Calendar;

public class DataHolder {

    private static int userid;
    private static String email, type;
    private static String pFName, pLName, pMName, pImage,pPosition, pBdayHolder, pContact;
    private static Calendar pBday = Calendar.getInstance();
    private static int pEstab;



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

    public void setProfile(String iptFname, String iptLname, String iptMname, Date iptBday, String iptContact, String iptImage, String iptPosition, int iptEstab)
    {
        pFName = iptFname;
        pLName = iptLname;
        pMName = iptMname;
        pBday.setTime(iptBday);
        pBdayHolder = pBday.get(Calendar.MONTH) + "-" + pBday.get(Calendar.DAY_OF_MONTH) + "-" + pBday.get(Calendar.YEAR);
        pContact = iptContact;
        pImage = iptImage;
        pPosition = iptPosition;
        pEstab = iptEstab;
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

    public String getpQR()
    {
        return pImage;
    }

    public String getpPosition()
    {
        return pPosition;
    }

    public String getpEstab()
    {
        return pEstab+"";
    }

    public Calendar getTempDate()
    {
        return pBday;
    }
}
