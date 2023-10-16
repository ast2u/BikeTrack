package com.example.biketrackcba;

import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class cellularPermission {

    private static final int PERMISSION_REQUEST_CODE = 100;

    private static final String[] PERMISSIONS = {
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.READ_PHONE_STATE
    };

    public static void requestPermissions(Activity activity) {
        if (!arePermissionsGranted(activity)) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS, PERMISSION_REQUEST_CODE);
        }
    }

    public static boolean arePermissionsGranted(Activity activity) {
        for (String permission : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(activity, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

}