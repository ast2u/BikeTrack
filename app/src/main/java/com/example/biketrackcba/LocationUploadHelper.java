package com.example.biketrackcba;

import static android.content.ContentValues.TAG;

import android.location.Location;
import android.os.Handler;
import android.util.Log;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class LocationUploadHelper {

    private Handler handler = new Handler();

    Runnable uploadLocationTask;
    int delay = 50000;


    public void startLocationUpload(FirebaseUser user, Location location) {
   handler.postDelayed(uploadLocationTask = () -> {
       handler.postDelayed(uploadLocationTask,delay);

           String userId = user.getUid();
           SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
           String currentDate = dateFormat.format(new Date());
           DatabaseReference ref = FirebaseDatabase.getInstance().getReference("BikersAvailable").child(userId);
           DatabaseReference rtRef = ref.child("RT_Location");
           GeoFire geoFire = new GeoFire(rtRef);
           GeoLocation userLocation = new GeoLocation(location.getLatitude(), location.getLongitude());
           geoFire.setLocation("UserLocation", userLocation);
           rtRef.child("timestamp").setValue(currentDate);


   },delay);

    //10*60*1000
}
public void stopLocationUpdates(){
        if(uploadLocationTask!=null){
            handler.removeCallbacks(uploadLocationTask);

        }
}
}