package com.example.mobileapplicationproject.DataController;

import java.util.Date;
import java.util.Calendar;

public class DataHolder {

    private static int userid;
    private static String email, type;
    private static String pFName, pLName, pMName, pImage,pPosition, pBdayHolder, pContact;
    private static Calendar pBday = Calendar.getInstance();
    private static int pEstab;

    private static String house, brgy, city;

    private static String estName, estStreet, estContact, estOwner, estImage;



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
        pBdayHolder = pBday.get(Calendar.YEAR) + "-" + (pBday.get(Calendar.MONTH)+1) + "-" + pBday.get(Calendar.DAY_OF_MONTH);
        pContact = iptContact;
        pImage = iptImage;
        pPosition = iptPosition;
        pEstab = iptEstab;
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

    public void setProfile(String iptName, String iptStreet, String iptContact, String iptOwner, String iptImage)
    {
        estName = iptOwner;
        estStreet = iptStreet;
        estContact = iptContact;
        estOwner = iptOwner;
        estImage = iptImage;
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
}
