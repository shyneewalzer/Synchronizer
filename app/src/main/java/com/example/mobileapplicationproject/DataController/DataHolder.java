package com.example.mobileapplicationproject.DataController;

public class DataHolder {

    private static int userid;
    private static String email, type;
    private static String qrimg;



    public void setAcct(int iptuserid,String iptemail, String ipttype)
    {
        userid = iptuserid;
        type = ipttype;
        email=iptemail;
    }

    public void setEmail(String iptemail)
    {
        email= iptemail;
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

    public void setQRimg(String iptqr)
    {
        qrimg = iptqr;
    }

    public String getQrimg()
    {
        return qrimg;
    }
}
