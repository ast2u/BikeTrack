package com.example.biketrackcba;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;


import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.Priority;

import com.google.android.gms.maps.model.CameraPosition;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import com.google.firebase.database.ValueEventListener;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import android.content.Intent;

import android.content.pm.PackageManager;

import android.graphics.Color;
import android.location.Location;

import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;

import android.view.animation.AnimationUtils;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;

import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;

import com.google.android.libraries.places.api.net.PlacesClient;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;


public class MapsSampleActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private GoogleMap mMap;

    private LatLng destinationLocation;
    private DirectionsResult directionsResult;
    private Location prevLocation;
    private Thread directionsThread;

    private Button sViewB, centerB,start_destin1,cancel_destin1,cancel_destin2;

    BottomNavigationView bottomNavigationView;
    private SearchView sView;
    FloatingActionButton sos_button;


    private PlacesClient placesC;

    private LinearLayout layoutSearch;
    private RelativeLayout layoutDestination,layoutD_userDataF;
    private ListView suggestionsListView;
    private ArrayAdapter<String> suggestionAdapter;
    private List<String> suggestionList;
    private Polyline currentPolyLine;

    private TextView text_Destination, text_Location,text_Speed,text_Time,text_Distance;

    private boolean destination_enabled = false;
    private boolean isDestination_canceled =true;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private TimerService timerService;
    private DatabaseReference otherUsersRef;
    private String currentUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_sample);
        checkLocationPermission();
        LocationUtils.checkLocationSettings(this);
        user = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = user.getUid();
        database = FirebaseDatabase.getInstance();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
        text_Time=findViewById(R.id.D_time_text);
        timerService = new TimerService(new Handler(Looper.getMainLooper()),text_Time);

        otherUsersRef=database.getReference("BikersAvailable");

        Places.initialize(getApplicationContext(), "AIzaSyDMINsKu9fJHa_Phb0kq6xYXgDOh3nUXU8");
        placesC = Places.createClient(this);


        layoutSearch = findViewById(R.id.mSearch_layout);
        layoutD_userDataF = findViewById(R.id.layoutstart_routing);
        layoutDestination = findViewById(R.id.mDestination_starter);
        text_Speed = findViewById(R.id.D_speed_text);

        suggestionsListView = findViewById(R.id.lsuggestions_list);
        text_Destination = findViewById(R.id.text_printDirection);
        start_destin1 = findViewById(R.id.D_startDestination);
        cancel_destin1 = findViewById(R.id.D_cancel);
        cancel_destin2 = findViewById(R.id.DD_cancel);
        text_Location = findViewById(R.id.text_printcLocation);
        suggestionList = new ArrayList<>();
        suggestionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, suggestionList);

        sView = findViewById(R.id.mSearch1_location);
        suggestionsListView.setAdapter(suggestionAdapter);
        suggestionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                String selectedSuggestion = suggestionList.get(pos);
                getPlaceDetails(selectedSuggestion);
                hideSearchView();
                sViewB.setVisibility(View.GONE);

            }
        });
        sView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String nText) {
                PlaceSearchHelper.performSearch(nText,placesC,suggestionAdapter,suggestionList);
                return true;
            }
        });


        //
        bottomNavigationView = findViewById(R.id.bottomNavView);
        Menu menu = bottomNavigationView.getMenu();
        bottomNavigationView.setSelectedItemId(R.id.miHome);
        MenuItem menuItem2 = menu.getItem(2);
        menuItem2.setEnabled(false);


        sViewB = findViewById(R.id.mSearch_butt);
        sos_button = findViewById(R.id.fab);
        sViewB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSearchView();

            }
        });
        sView.setOnCloseListener(() -> {
            hideSearchView();

            return true;
        });

        centerB = findViewById(R.id.my_location_button);
        centerB.setOnClickListener(view -> centerMapOnUserLocation());

        clearMarkers();
        cancel_destin2.setOnClickListener(view -> {
            layoutDestination.setVisibility(View.GONE);
            sViewB.setVisibility(View.VISIBLE);
            layoutD_userDataF.setVisibility(View.GONE);
            stopDirections();
        });
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.miHome) {
                    startActivity(getIntent());
                    finish();
                    overridePendingTransition(0, 0);

                } else if (id == R.id.miSocials) {
                    Intent intent = new Intent(MapsSampleActivity.this, UserSocialsActivity.class);
                    startActivity(intent);
                    finish();
                } else if (id == R.id.miDiscover) {
                    Intent intent = new Intent(MapsSampleActivity.this, DiscoverUserActivity.class);
                    startActivity(intent);
                    finish();

                } else if (id == R.id.miProfile) {
                    Intent intent = new Intent(MapsSampleActivity.this, UserProfileActivity.class);
                    startActivity(intent);
                    finish();

                }


                return false;
            }
        });


    }
    private void toggleSearchView() {
        if (sView.getVisibility() == View.VISIBLE) {
            hideSearchView();
        } else {
            showSearchView();
        }

    }

    private void hideSearchView() {
        Animation hideAnimation = AnimationUtils.loadAnimation(this, R.anim.animation_searchhide);
        layoutSearch.startAnimation(hideAnimation);
        layoutSearch.setVisibility(View.GONE);
        sView.startAnimation(hideAnimation);
        sView.setVisibility(View.GONE);

        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.animation_fade_in);
        sViewB.startAnimation(fadeInAnimation);
        sViewB.setVisibility(View.VISIBLE);
    }

    private void showSearchView() {
        Animation showAnimation = AnimationUtils.loadAnimation(this, R.anim.animation_searchshow);
        sView.startAnimation(showAnimation);
        sView.setVisibility(View.VISIBLE);
        sView.setIconified(false);
        layoutSearch.setVisibility(View.VISIBLE);
        sViewB.setVisibility(View.INVISIBLE);
    }

    private void stopLocationUpdates() {


        if(locationCallback!=null || fusedLocationClient!=null) {
            Log.d(TAG, "location have stopped");
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }


    @Override
    protected void onResume() {
        super.onResume();
        checkLocationPermission();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        stopLocationUpdates();
    }


    @Override
    protected void onStop() {
        super.onStop();
        String userId = user.getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("BikersAvailable");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);
        stopLocationUpdates();

    }



    private void getPlaceDetails(String placeName) {
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setCountry("PH") // Set your desired country (optional)
                .setSessionToken(token)
                .setQuery(placeName)
                .build();
        placesC.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
            if (!response.getAutocompletePredictions().isEmpty()) {
                AutocompletePrediction prediction = response.getAutocompletePredictions().get(0);
                String placeId = prediction.getPlaceId();

                List<Place.Field> placeDetailFields = Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG);
                FetchPlaceRequest placeRequest = FetchPlaceRequest.newInstance(placeId, placeDetailFields);

                placesC.fetchPlace(placeRequest).addOnSuccessListener((placeResponse) -> {
                    Place place = placeResponse.getPlace();
                    destinationLocation = place.getLatLng();
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    }
                        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    mMap.addMarker(new MarkerOptions().position(destinationLocation).title(placeName));
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destinationLocation, 16));
                                    LatLng originLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                    text_Destination.setText(placeName);
                                    layoutDestination.setVisibility(View.VISIBLE);
                                    start_destin1.setOnClickListener(view -> {

                                        layoutDestination.setVisibility(View.GONE);
                                        layoutD_userDataF.setVisibility(View.VISIBLE);
                                            getDirections(originLocation, destinationLocation);
                                        destination_enabled=true;
                                        isDestination_canceled=false;
                                        timerService.run();

                                        Log.d(TAG,"Success");
                                    });
                                    cancel_destin1.setOnClickListener(view -> {
                                        clearMarkers();
                                        layoutDestination.setVisibility(View.GONE);
                                        sViewB.setVisibility(View.VISIBLE);
                                    });

                                }
                            }
                        });
                        }).addOnFailureListener((exception)->{
                            Log.e("Place Details", "Error getting place details", exception);
                        });
                    }
        }).addOnFailureListener((exception) -> {
            Log.e("Place Details", "Error getting place details", exception);
        });
}

private void stopDirectionsThread(){
if(directionsThread!=null&& directionsThread.isAlive()){
    directionsThread.interrupt();
}
}
    private void getDirections(LatLng origin, LatLng destination){
       directionsThread = new Thread(() -> {
            GeoApiContext context = new GeoApiContext.Builder()
                    .apiKey("AIzaSyDMINsKu9fJHa_Phb0kq6xYXgDOh3nUXU8")
                    .build();
            DirectionsApiRequest request = DirectionsApi.getDirections(context,
                    String.format("%f,%f", origin.latitude, origin.longitude),
                    String.format("%f,%f", destination.latitude, destination.longitude));

            try {
                directionsResult = request.await();
                runOnUiThread(() -> drawRouteOnMap(directionsResult.routes[0]));

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
       directionsThread.start();

/*
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey("AIzaSyDMINsKu9fJHa_Phb0kq6xYXgDOh3nUXU8")
                .build();
        DirectionsApiRequest request = DirectionsApi.getDirections(context,
                String.format("%f,%f", origin.latitude, origin.longitude),
                String.format("%f,%f", destination.latitude, destination.longitude));
        try{
             directionsResult = request.await();
            drawRouteOnMap(directionsResult.routes[0]);
        }catch (Exception e){
            e.printStackTrace();
        }


 */

    }

    private void drawRouteOnMap(DirectionsRoute route) {
      List<LatLng> path = new ArrayList<>();

        for (DirectionsLeg leg : route.legs) {
            for (DirectionsStep step : leg.steps) {
                EncodedPolyline polyline = step.polyline;
                List<com.google.maps.model.LatLng> decodedPolyline = polyline.decodePath();
                for (com.google.maps.model.LatLng point : decodedPolyline) {
                    path.add(new LatLng(point.lat, point.lng));
                }
            }
        }
        if(currentPolyLine==null){
           // currentPolyLine.remove();
            currentPolyLine = mMap.addPolyline(new PolylineOptions()
                    .addAll(path)
                    .color(Color.GREEN)
                    .width(25));
        }else{
            currentPolyLine.setPoints(path);
        }


    }

    //Check location permission for Maps
    private void checkLocationPermission() {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission not granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permission already granted, start location updates
            startLocationUpdates();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.
                        ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, android.Manifest.
                        permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }

                mMap.setMyLocationEnabled(true);
                startLocationUpdates();
            } else {
                // Permission denied
                Toast.makeText(getApplicationContext(),"Location Permission Access must be enabled!",Toast.LENGTH_LONG).show();
                // You might want to show a message to the user or handle this case differently
            }
        }
    }

    private List<Marker> markers = new ArrayList<>();
    private void clearMarkers(){
        for (Marker marker: markers){
            marker.remove();
        }
        markers.clear();
    }
    private boolean shouldAutoCenterCamera = true;
    private Location prevDestinationLocatiom;
    private void startLocationUpdates() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                    Location location = locationResult.getLastLocation();
                    if(location!=null){
                        Location smoothLocate = smoothLocationUpdate(location);
                        prevLocation=new Location(smoothLocate);
                        if(prevDestinationLocatiom==null){
                            prevDestinationLocatiom=location;
                        }
                        double latit = smoothLocate.getLatitude();
                        double longit = smoothLocate.getLongitude();
                        LatLng userL = new LatLng(latit,longit);



                            String userId = user.getUid();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            String currentDate = dateFormat.format(new Date());
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("BikersAvailable").child(userId);
                            DatabaseReference rtRef = ref.child("RT_Location");
                            GeoFire geoFire = new GeoFire(rtRef);
                            GeoLocation userLocation = new GeoLocation(location.getLatitude(),location.getLongitude());
                            geoFire.setLocation("UserLocation",userLocation);
                            rtRef.child("timestamp").setValue(currentDate);





                        float speed= location.getSpeed();
                        String formatspeedTxt = String.format("%.1f",speed);
                        String speedtext = formatspeedTxt + " m/s";
                        text_Speed.setText(speedtext);




                        if (shouldAutoCenterCamera) {

                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userL,17));
                            shouldAutoCenterCamera = false;
                        }

                        if(destination_enabled && isDestination_canceled==false){
                            float heading = location.getBearing();

                            // Set the camera position with updated bearing
                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(userL) // Keep the same target position
                                    .zoom(20) // Keep the same zoom level
                                    .bearing(heading) // Set the updated bearing
                                    .tilt(50) // Reset the tilt to 0 degrees
                                    .build();
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                            mMap.getUiSettings().setAllGesturesEnabled(false);
                            mMap.getUiSettings().setRotateGesturesEnabled(true);
                        }
                        float distance = prevDestinationLocatiom.distanceTo(location);



                        if(distance >=4 && isDestination_canceled==false && destination_enabled==true){
                            prevDestinationLocatiom=location;
                            if(destinationLocation!=null){
                                getDirections(userL,destinationLocation);
                            }
                        }

                        if(destinationLocation!=null){
                            float[] results = new float[1];
                            Location.distanceBetween(
                                    userL.latitude, userL.longitude,
                                    destinationLocation.latitude, destinationLocation.longitude,
                                    results);

                            float distanceInMeters = results[0];
                            if(distanceInMeters<20){
                                clearMarkers();

                                layoutDestination.setVisibility(View.GONE);
                                sViewB.setVisibility(View.VISIBLE);
                                layoutD_userDataF.setVisibility(View.GONE);
                                stopDirections();
                            }

                        }

                    }

            }

        };

        LocationRequest locationRequest = new LocationRequest.Builder
                (Priority.PRIORITY_BALANCED_POWER_ACCURACY,1000)
                .build();
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    Looper.getMainLooper());
        }


    }



    private void stopDirections(){
        stopDirectionsThread();
        if(currentPolyLine!=null){
            currentPolyLine.remove();
            currentPolyLine=null;
        }
        destination_enabled=false;
        isDestination_canceled=true;
        timerService.resetTimer();
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(mMap.getCameraPosition().target) // Keep the same target position
                .zoom(mMap.getCameraPosition().zoom) // Keep the same zoom level
                .bearing(mMap.getCameraPosition().bearing) // Keep the same bearing (if needed)
                .tilt(0) // Reset the tilt to 0 degrees
                .build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        layoutD_userDataF.setVisibility(View.GONE);
        mMap.getUiSettings().setAllGesturesEnabled(true);
    }

    private Location smoothLocationUpdate(Location newLocation) {
        if (prevDestinationLocatiom == null) {
            return newLocation;
        }

        double weight = 0.5; // Adjust this value to control smoothing level
        double lat = prevDestinationLocatiom.getLatitude() + weight * (newLocation.getLatitude() - prevDestinationLocatiom.getLatitude());
        double lng = prevDestinationLocatiom.getLongitude() + weight * (newLocation.getLongitude() - prevDestinationLocatiom.getLongitude());

        Location smoothedLocation = new Location(newLocation);
        smoothedLocation.setLatitude(lat);
        smoothedLocation.setLongitude(lng);

        return smoothedLocation;
    }




    /*

    // Sample Interpolate
    private Location interpolateLocation(Location start, Location end, long elapsedTime) {
        double fraction = (double) elapsedTime / (end.getTime() - start.getTime());
        double lat = start.getLatitude() + (end.getLatitude() - start.getLatitude()) * fraction;
        double lng = start.getLongitude() + (end.getLongitude() - start.getLongitude()) * fraction;

        Location interpolatedLocation = new Location("interpolated");
        interpolatedLocation.setLatitude(lat);
        interpolatedLocation.setLongitude(lng);

        return interpolatedLocation;
    }

     */


    private void centerMapOnUserLocation(){
        if (mMap != null) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);

                fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                fusedLocationClient.getLastLocation().addOnSuccessListener(this,
                        new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 18));

                                }
                            }
                        });
            }else{
                // Request location permission if not granted
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
    }

    private List<Circle> otherUsersCircles = new ArrayList<>();

    private void drawOtherUsersCircle(){
        CompletableFuture.runAsync(()->{
        otherUsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {


                    if(userSnapshot.getKey().equals(currentUserId)){
                        continue;
                    }
                    // Assuming each user node has 'latitude' and 'longitude' keys
                    double latitude = userSnapshot.child("RT_Location/UserLocation/l/0").getValue(Double.class);
                    double longitude = userSnapshot.child("RT_Location/UserLocation/l/1").getValue(Double.class);

                    // Create a LatLng object from the retrieved coordinates
                    LatLng userLocation = new LatLng(latitude, longitude);

                    // Create a CircleOptions object to define the circle
                    CircleOptions circleOptions = new CircleOptions()
                            .center(userLocation)
                            .radius(8) // Adjust the radius as needed
                            .strokeColor(Color.GREEN)
                            .fillColor(Color.BLUE);

                    // Add the circle to the map
                    Circle circle = mMap.addCircle(circleOptions);
                    otherUsersCircles.add(circle);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to retrieve other users' locations", error.toException());
            }
        });
        });
    }




    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        Log.d(TAG, "Map is ready!");
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            drawOtherUsersCircle();


        }

}

}