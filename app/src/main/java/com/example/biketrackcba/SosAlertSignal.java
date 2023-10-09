package com.example.biketrackcba;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SosAlertSignal {
    private FusedLocationProviderClient fusedLocationProviderClient;
    private FirebaseUser user;
    private LocationCallback locationCallback;

    private static final long UPDATE_INTERVAL = 5000;

    public SosAlertSignal(Context context) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        user = FirebaseAuth.getInstance().getCurrentUser();
        createLocationCallback();
        startLocationFirebaseUpdates(context);

    }
    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult == null)
                    return;
                Location location = locationResult.getLastLocation();
                if (location != null) {

                    String userId = user.getUid();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
                    String currentDate = dateFormat.format(new Date());
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("SosSignal").child(userId);
                    ref.child("timestamp").setValue(currentDate);
                    ref.child("0").setValue(location.getLatitude());
                    ref.child("1").setValue(location.getLongitude());

                }
            }
        };
    }

    private void startLocationFirebaseUpdates(Context c) {
        LocationRequest locationRequest = new LocationRequest.Builder(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY, UPDATE_INTERVAL).build();

        if (ActivityCompat.checkSelfPermission(c, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(c,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    public void stopLocationFirebaseUpdates(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

}
