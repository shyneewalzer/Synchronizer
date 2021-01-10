package com.example.mobileapplicationproject.DataController;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.threeten.bp.DateTimeUtils;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;
import org.threeten.bp.ZoneId;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DataProcessor {

    private static Calendar birthdate = Calendar.getInstance();
    private static Calendar datenow = Calendar.getInstance();
    private static int age;

    private static byte[] decodedString;
    private static Bitmap decodedByte;

    private static String strdate;
    private static DateFormat dateformatter;
    private static Date datee = new Date();


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

}
