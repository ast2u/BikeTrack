package com.example.biketrackcba;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

public class MainStartLoadingScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main_start_loading_screen);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                        Intent i = new Intent(MainStartLoadingScreen.this, Loginstarter.class);


                        startActivity(i);

                        finish();

                        SmsPermissionHelper.requestSendSmsPermission(MainStartLoadingScreen.this);

                }
            }, 2000);
    }
}