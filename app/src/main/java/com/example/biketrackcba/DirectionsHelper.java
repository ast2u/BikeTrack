package com.example.biketrackcba;
import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.EncodedPolyline;

import java.util.ArrayList;
import java.util.List;

public class DirectionsHelper {

        private static final String API_KEY = "AIzaSyDMINsKu9fJHa_Phb0kq6xYXgDOh3nUXU8"; // Replace with your Google Maps API Key

        public static Polyline getDirections(LatLng origin, LatLng destination, GoogleMap map) {
            GeoApiContext context = new GeoApiContext.Builder().apiKey(API_KEY).build();
            DirectionsApiRequest request = DirectionsApi.getDirections(context,
                    String.format("%f,%f", origin.latitude, origin.longitude),
                    String.format("%f,%f", destination.latitude, destination.longitude));

            try {
                DirectionsResult result = request.await();
                return drawRouteOnMap(result.routes[0], map);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public static Polyline drawRouteOnMap(DirectionsRoute route, GoogleMap map) {
            List<LatLng> path = new ArrayList<>();


            EncodedPolyline encodedPolyline = route.overviewPolyline;
            List<com.google.maps.model.LatLng> decodedPolyline = encodedPolyline.decodePath();
            for (com.google.maps.model.LatLng point : decodedPolyline) {
                path.add(new LatLng(point.lat, point.lng));
            }

            PolylineOptions polylineOptions = new PolylineOptions()
                    .addAll(path)
                    .color(Color.GREEN)
                    .width(20);

            return map.addPolyline(polylineOptions);
        }

        public static void getDirectionsAndDrawRoute(LatLng origin, LatLng destination, GoogleMap map) {
            getDirections(origin, destination, map);
        }
    }



