package com.example.biketrackcba;

import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class SmsPermissionHelper {

    private static final int SEND_SMS_PERMISSION_REQUEST_CODE = 1;

    public static void requestSendSmsPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{android.Manifest.permission.SEND_SMS},
                    SEND_SMS_PERMISSION_REQUEST_CODE);
        }
    }

    public static boolean isSendSmsPermissionGranted(Activity activity) {
        return ContextCompat.checkSelfPermission(activity, android.Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED;
    }
}