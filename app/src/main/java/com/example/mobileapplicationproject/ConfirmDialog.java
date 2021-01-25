package com.example.mobileapplicationproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.material.textfield.TextInputEditText;

public class ConfirmDialog extends AppCompatDialogFragment {

    String dialogMessage = "", dialogTitle = "", purpose = "";
    ConfirmDialogListener listener;

    public ConfirmDialog(String ipttitle, String iptmessage, String iptpurpose)
    {
        dialogTitle = ipttitle;
        dialogMessage = iptmessage;
        purpose = iptpurpose;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {


        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        builder.setTitle(dialogTitle);
        builder.setMessage(dialogMessage);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                // call function show alert dialog again
                boolean dialogResponse = true;
                listener.getDialogResponse(dialogResponse, purpose);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                boolean dialogResponse = false;
                listener.getDialogResponse(dialogResponse, purpose);

            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (ConfirmDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "Must implement Dialog Listener");
        }
    }

    public interface ConfirmDialogListener
    {
        void getDialogResponse(boolean dialogResponse, String purpose);
    }
}
