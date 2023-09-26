package com.example.biketrackcba;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
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
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapsSampleActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private GoogleMap mMap;
    private Button sViewB, centerB;
    private Location prevLocation;
    BottomNavigationView bottomNavigationView;
    private SearchView sView;
    FloatingActionButton sos_button;
    private PlacesClient placesC;

    LinearLayout layoutSearch;
    ListView suggestionsListView;
    private ArrayAdapter<String> suggestionAdapter;
    private List<String> suggestionList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_sample);
        checkLocationPermission();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);

        // Search Fragment
        Places.initialize(getApplicationContext(), "AIzaSyDMINsKu9fJHa_Phb0kq6xYXgDOh3nUXU8");
        placesC = Places.createClient(this);


        layoutSearch = findViewById(R.id.mSearch_layout);
        suggestionsListView = findViewById(R.id.lsuggestions_list);
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

            }
        });
        sView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String nText) {
                performSearch(nText);
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
        sView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                hideSearchView();
                return true;
            }
        });

        centerB = findViewById(R.id.my_location_button);
        centerB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                centerMapOnUserLocation();
            }
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
                    Intent intent = new Intent(MapsSampleActivity.this,UserSocialsActivity.class);
                    startActivity(intent);
                    finish();
                } else if (id==R.id.miDiscover) {
                    Intent intent = new Intent(MapsSampleActivity.this,DiscoverUserActivity.class);
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

/*
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult != null) {
                    Location location = locationResult.getLastLocation();
                }

            }
        };

 */
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

    // Search Method
    private void performSearch(String query) {
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setCountry("PH")
                .setQuery(query)
                .setSessionToken(token)
                .build();
        placesC.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
            List<AutocompletePrediction> predictions = response.getAutocompletePredictions();
            suggestionList.clear();
            for (AutocompletePrediction prediction : predictions) {
                suggestionList.add(prediction.getFullText(null).toString());
            }
            suggestionAdapter.notifyDataSetChanged();
        }).addOnFailureListener((exception) -> {
            Log.e("Place Prediction", "Error getting place predictions", exception);
        });
    }

    private void getPlaceDetails(String placeName) {

       // List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG);
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setCountries("PH") // Set your desired country (optional)
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
                            LatLng location = place.getLatLng();
                            mMap.addMarker(new MarkerOptions().position(location).title(placeName));

                            // Move the camera to the marker
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16));
                        }).addOnFailureListener((exception)->{
                            Log.e("Place Details", "Error getting place details", exception);
                        });
                    }
        }).addOnFailureListener((exception) -> {
            Log.e("Place Details", "Error getting place details", exception);
        });


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
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }

                startLocationUpdates();
            } else {
                // Permission denied
                Toast.makeText(getApplicationContext(),"Location Permission Access must be enabled!",Toast.LENGTH_LONG).show();
                // You might want to show a message to the user or handle this case differently
            }
        }
    }
    private boolean shouldAutoCenterCamera = true;
    private void startLocationUpdates() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Location location = locationResult.getLastLocation();
                    if(location!=null){

                        Location smoothLocate = smoothLocationUpdate(location);

                        double latit = smoothLocate.getLatitude();
                        double longit = smoothLocate.getLongitude();

                        LatLng userL = new LatLng(latit,longit);

                        if (shouldAutoCenterCamera) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userL,17));
                            shouldAutoCenterCamera = false;
                        }
                        prevLocation = smoothLocate;

                    }

                }
            }
        };
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(5000); // Set the desired interval for location updates (in milliseconds)
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    Looper.getMainLooper());
        }


    }
    private void stopLocationUpdates() {
        if(fusedLocationClient !=null && locationCallback !=null){
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }

    }
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



    private Location smoothLocationUpdate(Location newLocation) {
        if (prevLocation == null) {
            return newLocation;
        }

        double weight = 0.5; // Adjust this value to control smoothing level
        double lat = prevLocation.getLatitude() + weight * (newLocation.getLatitude() - prevLocation.getLatitude());
        double lng = prevLocation.getLongitude() + weight * (newLocation.getLongitude() - prevLocation.getLongitude());

        Location smoothedLocation = new Location(newLocation);
        smoothedLocation.setLatitude(lat);
        smoothedLocation.setLongitude(lng);

        return smoothedLocation;
    }


    private void centerMapOnUserLocation(){
        if (mMap != null) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mMap.clear();

                FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                fusedLocationClient.getLastLocation().addOnSuccessListener(this,
                        new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 20));

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

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }

}


}