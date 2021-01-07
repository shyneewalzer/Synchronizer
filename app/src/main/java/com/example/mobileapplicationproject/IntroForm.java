package com.example.mobileapplicationproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.mobileapplicationproject.DataController.DebugMode;

public class IntroForm extends AppCompatActivity {

    Handler handler;
    Runnable runnable;

    DebugMode dm = new DebugMode();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_form);

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent dsp = new Intent(IntroForm.this, Login.class);
                startActivity(dsp);
                finish();
            }
        }, 1000);//3000

    }
}