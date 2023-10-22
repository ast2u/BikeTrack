package com.example.biketrackcba;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;

public class MainStartLoadingScreen extends AppCompatActivity {
private LocationUtils2 locationUtils2;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final int READ_STORAGE_PERMISSION_REQUEST_CODE = 1003;

    private static final int SEND_SMS_PERMISSION_REQUEST_CODE = 1;

    private static final String[] PERMISSIONS = {
            android.Manifest.permission.ACCESS_FINE_LOCATION,
           android.Manifest.permission.SEND_SMS,
          //  android.Manifest.permission.READ_MEDIA_IMAGES
    };

    private static final int PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main_start_loading_screen);
        locationUtils2 = new LocationUtils2(this);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showLocationOffDialog();
        }else{
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
        }



    }

    private void showLocationOffDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Location services are turned off. Please turn them on to proceed.\n\n" +
                        "Confirm if your location services is turned on.")
                .setCancelable(false)
                .setPositiveButton("Confirm", (dialog, which) -> {
                    Intent intent = new Intent(getApplicationContext(), MainStartLoadingScreen.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                    System.exit(0);
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private boolean arePermissionsGranted() {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && SmsPermissionHelper.isSendSmsPermissionGranted(this) && locationUtils2.isLocationEnabled()
                && ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermissions() {
        String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: IS RUNNING CHECK PLEASE");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length > 0) {
            boolean allPermissionsGranted = true;

            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                new Handler().postDelayed(() -> {
                    Intent i = new Intent(MainStartLoadingScreen.this, Loginstarter.class);
                    startActivity(i);
                    finish();
                }, 2000);
            }
        }
    }


    private void requestReadStoragePermission() {
        String[] permissions = {android.Manifest.permission.READ_EXTERNAL_STORAGE};

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, READ_STORAGE_PERMISSION_REQUEST_CODE);
        }
    }

}