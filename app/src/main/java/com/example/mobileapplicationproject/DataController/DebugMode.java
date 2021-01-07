package com.example.mobileapplicationproject.DataController;

import android.content.Context;
import android.widget.Toast;

public class DebugMode {

    private static boolean debugmode = true;


    public boolean getDebugMode()
    {
        return debugmode;
    }

    public void displayMessage(Context context, String iptmsg)
    {
        if (debugmode==true)
        {
            Toast.makeText(context, iptmsg, Toast.LENGTH_SHORT).show();
        }
    }
}
