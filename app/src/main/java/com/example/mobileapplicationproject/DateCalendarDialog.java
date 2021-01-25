package com.example.mobileapplicationproject;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.mobileapplicationproject.DataController.DataHolder;
import com.example.mobileapplicationproject.DataController.DebugMode;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class DateCalendarDialog extends AppCompatDialogFragment {

    DataHolder dh = new DataHolder();
    DebugMode dm = new DebugMode();
    DatePickerDialogListener listener;
    Context maincontext;

    public DateCalendarDialog()
    {
        maincontext = getContext();
    }

    public DateCalendarDialog(Context iptContext)
    {
        maincontext = iptContext;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        int cal_yr = dh.getTempDate().get(Calendar.YEAR);
        int cal_mo = dh.getTempDate().get(Calendar.MONTH);
        int cal_dy = dh.getTempDate().get(Calendar.DAY_OF_MONTH);
        dm.displayMessage(getContext(),cal_yr + "-" + cal_mo + "-" + cal_dy);

        DatePickerDialog datepicker = new DatePickerDialog(maincontext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String dateResult = year + "-" + month + "-" + dayOfMonth;
                listener.getDateResult(dateResult);
            }
        },cal_yr,cal_mo,cal_dy);

        return datepicker;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (DatePickerDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "Must implement Dialog Listener");
        }
    }

    public interface DatePickerDialogListener
    {
        void getDateResult(String dateResult);
    }
}
