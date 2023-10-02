package com.example.biketrackcba;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;

import androidx.fragment.app.FragmentActivity;

public class LocationUtils {
    public static final int PERMISSION_REQUEST_ENABLE_GPS = 9002;

    public static void checkLocationSettings(FragmentActivity activity) {
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!isGpsEnabled) {
            // Prompt the user to enable GPS
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Enable GPS")
                    .setMessage("Please enable GPS for accurate location tracking.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            activity.startActivityForResult(intent, PERMISSION_REQUEST_ENABLE_GPS);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}
