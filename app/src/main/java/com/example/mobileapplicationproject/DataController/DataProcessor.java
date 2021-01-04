package com.example.mobileapplicationproject.DataController;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.sql.Date;
import java.util.Calendar;

public class DataProcessor {

    private static Calendar birthdate = Calendar.getInstance();
    private static Calendar datenow = Calendar.getInstance();
    private static int age;


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

}
