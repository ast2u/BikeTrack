package com.example.biketrackcba;

import android.location.Location;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class BearingMapUtils {
    public static void setCameraBearing(GoogleMap googleMap, LatLng userLocation, LatLng prevLocation) {
        if (googleMap == null || userLocation == null || prevLocation== null) return;

        double bearing = getBearing(prevLocation,userLocation);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(userLocation)
                .bearing((float) bearing)
                .tilt(80)
                .zoom(25)
                .build();

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }



    private static double getBearing(LatLng start, LatLng end) {
        double lat1 = Math.toRadians(start.latitude);
        double long1 = Math.toRadians(start.longitude);
        double lat2 = Math.toRadians(end.latitude);
        double long2 = Math.toRadians(end.longitude);

        double dLon = long2 - long1;

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon);

        double bearing = Math.atan2(y, x);

        // Convert radians to degrees
        bearing = Math.toDegrees(bearing);

        // Normalize to a range of [0,360)
        bearing = (bearing + 360) % 360;

        return bearing;
    }
}
