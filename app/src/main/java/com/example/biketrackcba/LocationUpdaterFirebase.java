package com.example.biketrackcba;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

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

public class LocationUpdaterFirebase {
    private static final long UPDATE_INTERVAL = 30000; // 5 minutes
    //30*60*1000 = 30mins
    //10*60*1000 = 10mins

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private FirebaseUser user;


    public LocationUpdaterFirebase(Context context) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
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
                    Boolean sosSignal = false;
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

    public void startLocationFirebaseUpdates(Context c) {
        LocationRequest locationRequest = new LocationRequest.Builder(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY, UPDATE_INTERVAL).build();

        if (ActivityCompat.checkSelfPermission(c, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(c,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    public void stopLocationFirebaseUpdates(){
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

}
