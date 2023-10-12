package com.example.biketrackcba;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SosAlertSignal {
    private static final long UPDATE_INTERVAL = 5000;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private FirebaseUser user;


    public SosAlertSignal(Context context) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        user = FirebaseAuth.getInstance().getCurrentUser();
        createLocationCallback();

        startSosFirebaseUpdates(context);

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
                    Boolean sosSignal = true;
                    String currentDate = dateFormat.format(new Date());
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("BikersAvailable").child(userId);
                    DatabaseReference rtRef = ref.child("RT_Location");
                    GeoFire geoFire = new GeoFire(rtRef);
                    GeoLocation userLocation = new GeoLocation(location.getLatitude(), location.getLongitude());
                    geoFire.setLocation("UserLocation", userLocation);
                    rtRef.child("sosAlert").setValue(sosSignal);
                    rtRef.child("timestamp").setValue(currentDate);
                }
            }
        };
    }

    private void startSosFirebaseUpdates(Context c) {
        LocationRequest locationRequest = new LocationRequest.Builder(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY, UPDATE_INTERVAL).build();

        if (ActivityCompat.checkSelfPermission(c, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(c,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    public void stopSosUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }


}
