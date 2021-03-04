package com.example.mobileapplicationproject.DataController;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Patterns;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class DataProcessor {

    private static Calendar birthdate = Calendar.getInstance();
    private static Calendar datenow = Calendar.getInstance();
    private static int age;
    private static byte[] decodedString;
    private static Bitmap decodedByte;
    private static Date datee = new Date();
    private static boolean dlgresult = false;
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    "(?=.*[a-z])" +         //at least 1 lower case letter
                    "(?=.*[A-Z])" +         //at least 1 upper case letter
                    //"(?=.*[a-zA-Z])" +      //any letter
                    //"(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{8,}" +               //at least 8 characters
                    "$");


    public void toastershort(Context context, String iptmsg)
    {
        Toast.makeText(context, iptmsg, Toast.LENGTH_SHORT).show();
    }

    public void toasterlong(Context context, String iptmsg)
    {
        Toast.makeText(context, iptmsg, Toast.LENGTH_LONG).show();
    }

    public Bitmap createQR(String modelName) {
//        Toast.makeText(getApplicationContext(),  "a" +modelName, Toast.LENGTH_LONG).show();
        Bitmap bitmapthrower=null;
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

        try {
            BitMatrix bitMatrix = multiFormatWriter.encode( modelName, BarcodeFormat.QR_CODE,500,500);
            BarcodeEncoder barcodeEncoder =  new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            bitmapthrower = bitmap;
        }catch (Exception e){
            e.printStackTrace();
        }

        return bitmapthrower;
    }

    public Bitmap createImage(String iptEncodedImg)
    {
        if(iptEncodedImg!=null && !iptEncodedImg.isEmpty())
        {
            decodedString = Base64.decode(iptEncodedImg, Base64.DEFAULT);
            decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        }
        else
        {
            decodedByte = null;
        }
        return decodedByte;
    }

    public String encodeImage(Bitmap iptbitmap)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        iptbitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encImage;
    }

    @SuppressLint("NewApi")
    public Date stringToDate(String iptdate)
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            datee = format.parse(iptdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return datee;
    }

    public ArrayList<String>splitter(String iptScanned, String iptdelimer)
    {
        ArrayList<String>tokenholder = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(iptScanned, iptdelimer);
        while (tokenizer.hasMoreTokens())
        {
            tokenholder.add(tokenizer.nextToken());
        }
        return tokenholder;
    }

    public ArrayList<String>splitternull(String iptScanned, String iptdelimer)
    {
        ArrayList<String>tokenholder = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(iptScanned, iptdelimer);
        while (tokenizer.hasMoreTokens())
        {
            String temp = tokenizer.nextToken();
            if(temp!=null && !temp.isEmpty())
            {
                tokenholder.add(temp);
            }
            else
            {
                tokenholder.add(null);
            }

        }
        return tokenholder;
    }

    public boolean passwordValidator(String iptpassword)
    {
        boolean passwordValidatorResponse;
        if(!PASSWORD_PATTERN.matcher(iptpassword).matches())
        {
            passwordValidatorResponse = false;
        }
        else
        {
            passwordValidatorResponse = true;
        }
        return passwordValidatorResponse;
    }


}
