package com.example.biketrackcba;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;

import com.firebase.geofire.GeoFire;


import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.Priority;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;


import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.pm.PackageManager;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;

import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
;
import android.os.PowerManager;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
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



import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Ref;
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
    private ProgressBar progressBar, sosProgressBar;
    private ValueEventListener valueEventListener;
    private GoogleMap mMap;

    private LatLng destinationLocation;
    private DirectionsResult directionsResult;

    private Location prevLocation;
    private Thread directionsThread;

    private Button sViewB, centerB, start_destin1, cancel_destin1, cancel_destin2;
    private Button button_StartRTrack;

    BottomNavigationView bottomNavigationView;
    private SearchView sView;
    private FloatingActionButton sos_button;

    private PlacesClient placesC;

    private LinearLayout layoutSearch;
    private RelativeLayout layoutDestination, layoutD_startRouting2;
    private CardView RcardView;

    private ListView suggestionsListView;
    private ArrayAdapter<String> suggestionAdapter;
    private List<String> suggestionList;
    private Polyline currentPolyLine;
    private List<Polyline> routePolyline = new ArrayList<>();

    private TextView text_Destination, text_Location, text_Speed, text_Time, text_Distance;

    private boolean destination_enabled = false;
    private boolean isDestination_canceled = true;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private TimerService timerService;
    private DatabaseReference otherUsersRef, refUserSignal;
    MediaPlayer sosMediaplayer;
    private String currentUserId;
    private LocationUpdaterFirebase locationUpdaterFirebase;
    private SosAlertSignal sosAlertSignal;
    private List<LatLng> routePoints = new ArrayList<>();
    private Button startBDialog, continueBDialog, cancelBDialog, startSaveRoute, cancelSaveRoute;
    private Button finalstartSaveRoute, finalcancelSaveRoute;
    private EditText title_Route, desc_Route;
    private TextView saveDialogText;
    private CheckBox routeprivacy;
    private MenuItem menuItem,menuItem1,menuItem2,menuItem3,menuItem4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_sample);
        progressBar = findViewById(R.id.progressBarMaps);
        sosProgressBar = findViewById(R.id.start_sosButtonLoading);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        user = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = user.getUid();
        database = FirebaseDatabase.getInstance();
        checkLocationPermission();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
        LocationUtils.checkLocationSettings(this);
        locationUpdaterFirebase = new LocationUpdaterFirebase(this);
        sosMediaplayer = MediaPlayer.create(this, R.raw.sos_sound);

        text_Time = findViewById(R.id.D_time_text);
        timerService = new TimerService(new Handler(Looper.getMainLooper()), text_Time);

        otherUsersRef = database.getReference("BikersAvailable");
        refUserSignal = database.getReference("SosSignal");

        Places.initialize(getApplicationContext(), "AIzaSyDMINsKu9fJHa_Phb0kq6xYXgDOh3nUXU8");
        placesC = Places.createClient(this);

        layoutSearch = findViewById(R.id.mSearch_layout);

        RcardView = findViewById(R.id.routing_CardView);
        layoutD_startRouting2 = findViewById(R.id.layoutstart_routing);
        Intent intentfromotheractivity = getIntent();
        boolean showSnackbar = intentfromotheractivity.getBooleanExtra("showSnackbar", false);

        if (showSnackbar) {
            Snackbar.make(findViewById(android.R.id.content), "Start the SOS in Maps menu!", Snackbar.LENGTH_LONG).show();
        }
        layoutDestination = findViewById(R.id.mDestination_starter);

        text_Speed = findViewById(R.id.D_speed_text);
        button_StartRTrack = findViewById(R.id.start_RouteTrackingBT);
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
        suggestionsListView.setOnItemClickListener((adapterView, view, pos, id) -> {
            String selectedSuggestion = suggestionList.get(pos);
            progressBar.setVisibility(View.VISIBLE);

            getPlaceDetails(selectedSuggestion);
            hideSearchView();
            sViewB.setVisibility(View.GONE);

        });
        sView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String nText) {
                PlaceSearchHelper.performSearch(nText, placesC, suggestionAdapter, suggestionList);
                return true;
            }
        });
        bottomNavigationView = findViewById(R.id.bottomNavView);
        Menu menu = bottomNavigationView.getMenu();
        bottomNavigationView.setSelectedItemId(R.id.miHome);
        menuItem = menu.getItem(0);
        menuItem1 = menu.getItem(1);
        menuItem2 = menu.getItem(2);
        menuItem3 = menu.getItem(3);
        menuItem4 = menu.getItem(4);

        menuItem2.setEnabled(false);
        sViewB = findViewById(R.id.mSearch_butt);
        sos_button = findViewById(R.id.start_sosButton);


        sos_button.setOnClickListener(view -> {
            SosStarted();


        });
        button_StartRTrack.setOnClickListener(view -> {
            startRoutePoints = true;
            toggleroutingLayout();
        });

        sViewB.setOnClickListener(view -> toggleSearchView());
        sView.setOnCloseListener(() -> {
            hideSearchView();
            return true;
        });

        centerB = findViewById(R.id.my_location_button);
        centerB.setOnClickListener(view -> centerMapOnUserLocation());

        //end Button
        cancel_destin2.setOnClickListener(view -> {
            clearMarkers();
            if (layoutD_startRouting2.getVisibility() == View.VISIBLE) {
                TransitionManager.beginDelayedTransition(RcardView, new AutoTransition());
                layoutD_startRouting2.setVisibility(View.GONE);
                button_StartRTrack.setVisibility(View.VISIBLE);
            }
            if (startRoutePoints == true) {
                startRoutePoints = false;
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(mMap.getCameraPosition().target) // Keep the same target position
                        .zoom(mMap.getCameraPosition().zoom) // Keep the same zoom level
                        .bearing(mMap.getCameraPosition().bearing) // Keep the same bearing (if needed)
                        .tilt(0) // Reset the tilt to 0 degrees
                        .build();
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                saveRouteToDatabaseWithDialog();
                mMap.getUiSettings().setAllGesturesEnabled(true);

            }
            sViewB.setVisibility(View.VISIBLE);
            timerService.resetTimer();

            if (destination_enabled == true && isDestination_canceled == false) {
                stopDirections();

            }
        });
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.miHome) {
                   // Something to do here
                } else if (id == R.id.miSocials) {
                    Intent intent = new Intent(MapsSampleActivity.this, UserSocialsActivity.class);
                    startActivity(intent);
                } else if (id == R.id.miDiscover) {
                    Intent intent = new Intent(MapsSampleActivity.this, DiscoverUserActivity.class);
                    startActivity(intent);
                } else if (id == R.id.miProfile) {
                    Intent intent = new Intent(MapsSampleActivity.this, UserProfileActivity.class);
                    startActivity(intent);

                }
                return false;
            }
        });
    }

    private boolean isSosStarted = false;

    public void SosStarted() {
        if (!isSosStarted) {
            sosProgressBar.setVisibility(View.VISIBLE);
            RcardView.setVisibility(View.GONE);
            sViewB.setVisibility(View.GONE);
            ConstraintLayout constraintLayout = findViewById(R.id.warningConstraintLayout);
            View vC = LayoutInflater.from(this).inflate(R.layout.warning_dialog, constraintLayout);
            startBDialog = vC.findViewById(R.id.dialogButtonDone);
            cancelBDialog = vC.findViewById(R.id.dialogButtonCancel);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(vC);
            final AlertDialog alertDialog = builder.create();

            startBDialog.findViewById(R.id.dialogButtonDone).setOnClickListener(view1 -> {
                menuItem.setEnabled(false);
                menuItem1.setEnabled(false);
                menuItem3.setEnabled(false);
                menuItem4.setEnabled(false);
                alertDialog.dismiss();
                Snackbar.make(findViewById(android.R.id.content), "You won't be able to navigate to other menu", Snackbar.LENGTH_LONG).show();
                playSOSsound();
                locationUpdaterFirebase.stopLocationFirebaseUpdates();
                sosAlertSignal = new SosAlertSignal(this);
                ConstraintLayout constraintLayout2 = findViewById(R.id.warningConstraintLayout2);
                View vC2 = LayoutInflater.from(this).inflate(R.layout.warning_dialog2, constraintLayout2);
                continueBDialog = vC2.findViewById(R.id.dialogContinueDone);
                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                builder2.setView(vC2);
                AlertDialog alertDialog2 = builder2.create();
                continueBDialog.findViewById(R.id.dialogContinueDone).setOnClickListener(view2 -> {
                    alertDialog2.dismiss();

                    if(ContextCompat.checkSelfPermission(MapsSampleActivity.this, Manifest.permission.SEND_SMS)
                            == PackageManager.PERMISSION_GRANTED) {
                        sendSms();
                    }else{
                        ActivityCompat.requestPermissions(MapsSampleActivity.this,
                                new String[]{Manifest.permission.SEND_SMS},100);
                    }


                    /*
                    FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        fusedLocationClient.getLastLocation()
                                .addOnSuccessListener(this, location -> {
                                    if (location != null) {
                                        double latitude = location.getLatitude();
                                        double longitude = location.getLongitude();

                                        // Use latitude and longitude to compose the message
                                        String message = "SOS! I need help! Automated Message from BikeTrack Application" +
                                                "\nCheck out my location on Google Maps: " +
                                                "https://maps.google.com/?q=" + latitude + "," + longitude;
                                        //sendSmsIfSignalAvailable(message);

                                    }
                                });
                    }
                    */
                });
                alertDialog2.show();

            });
            cancelBDialog.findViewById(R.id.dialogButtonCancel).setOnClickListener(view1 -> {
                alertDialog.dismiss();
                sosProgressBar.setVisibility(View.GONE);
            });
            if (alertDialog.getWindow() != null) {
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            alertDialog.show();
        } else {
            sosProgressBar.setVisibility(View.GONE);
            RcardView.setVisibility(View.VISIBLE);
            sViewB.setVisibility(View.VISIBLE);
            menuItem.setEnabled(true);
            menuItem1.setEnabled(true);
            menuItem3.setEnabled(true);
            menuItem4.setEnabled(true);
            stopSOSsound();

            sosAlertSignal.stopSosUpdates();
            locationUpdaterFirebase = new LocationUpdaterFirebase(this);

        }
        isSosStarted = !isSosStarted;
    }

    private void sendSmsIfSignalAvailable(String message) {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isNetworkAvailable = networkInfo != null && networkInfo.isConnectedOrConnecting();

        if (isNetworkAvailable) {
            // Internet is available, send SMS immediately
            sendSms();
        } else {
            /*
            // No internet, check for cellular signal
            boolean isCellularSignalAvailable = isCellularSignalAvailable();

            if (isCellularSignalAvailable) {
                // Queue the SMS for later sending
                queueSmsForSendingLater(message);
            } else {
                // No signal, inform the user or take appropriate action
                Toast.makeText(this, "No cellular signal available.", Toast.LENGTH_SHORT).show();
            }

             */
        }
    }

    private boolean isCellularSignalAvailable() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            return false;
        }
        return telephonyManager.getNetworkType() != TelephonyManager.NETWORK_TYPE_UNKNOWN;
    }

    private void queueSmsForSendingLater(String message) {
        // Implement logic to store the SMS message and send it when signal is available
        // This could involve saving it to a database, SharedPreferences, or some other storage mechanism.
    }


    private void sendSms(){
        String userId = user.getUid();
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            // Use latitude and longitude to compose the message
                            String message = "SOS! I need help!" +
                                    "\nCheck out my location on Google Maps: " +
                                    "https://maps.google.com/?q=" + latitude + "," + longitude;
                            //sendSmsIfSignalAvailable(message);


        SmsPermissionHelper.requestSendSmsPermission(this);
        boolean isPermissionGranted = SmsPermissionHelper.isSendSmsPermissionGranted(this);
        if(isPermissionGranted){
        DatabaseReference userref = FirebaseDatabase.getInstance().getReference("Registered Users");
        userref.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String number1 = snapshot.child("emnumber1").getValue().toString();

                if (number1.startsWith("0")) {
                    number1 = "+63" + number1.substring(1);
                }
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(number1,null,message,null,null);
                    Toast.makeText(MapsSampleActivity.this, "SMS sent successfully.", Toast.LENGTH_SHORT).show();


            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MapsSampleActivity.this,"Something went wrong!",
                        Toast.LENGTH_LONG).show();
            }
        });
        }else{
            Toast.makeText(MapsSampleActivity.this, "Please Allow to send SMS.", Toast.LENGTH_SHORT).show();
        }
                        }
                    });
        }

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
        if (locationCallback != null || fusedLocationClient != null) {
            Log.d(TAG, "location have stopped");
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: is running");
        locationUpdaterFirebase.stopLocationFirebaseUpdates();
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
        otherUsersRef.removeEventListener(valueEventListener);
        locationUpdaterFirebase.stopLocationFirebaseUpdates();
        String userId = user.getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("BikersAvailable");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);
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
        stopLocationUpdates();

    }


    private void toggleroutingLayout() {
        if (layoutD_startRouting2.getVisibility() == View.GONE) {
            timerService.run();
            TransitionManager.beginDelayedTransition(RcardView, new AutoTransition());
            layoutD_startRouting2.setVisibility(View.VISIBLE);
            button_StartRTrack.setVisibility(View.GONE);
        }


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
                                progressBar.setVisibility(View.GONE);
                                BitmapDescriptor coloredMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                                Marker mPlaceMarker = mMap.addMarker(new MarkerOptions().position(destinationLocation)
                                        .icon(coloredMarker)
                                        .title(placeName));
                                Placemarkers.add(mPlaceMarker);
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destinationLocation, 16));
                                LatLng originLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                text_Destination.setText(placeName);
                                layoutDestination.setVisibility(View.VISIBLE);
                                start_destin1.setOnClickListener(view -> {
                                    layoutDestination.setVisibility(View.GONE);
                                    toggleroutingLayout();
                                    getDirections(originLocation, destinationLocation);
                                    destination_enabled = true;
                                    isDestination_canceled = false;

                                    Log.d(TAG, "Success");
                                });
                                cancel_destin1.setOnClickListener(view -> {
                                    clearMarkers();
                                    layoutDestination.setVisibility(View.GONE);
                                    sViewB.setVisibility(View.VISIBLE);
                                });

                            }
                        }
                    });
                }).addOnFailureListener((exception) -> {
                    Log.e("Place Details", "Error getting place details", exception);
                });
            }
        }).addOnFailureListener((exception) -> {
            Log.e("Place Details", "Error getting place details", exception);
        });
    }

    private void stopDirectionsThread() {
        if (directionsThread != null && directionsThread.isAlive()) {
            directionsThread.interrupt();
        }
    }

    private void getDirections(LatLng origin, LatLng destination) {
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
                .apiKey(String.valueOf(R.string.google_maps_key))
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
        if (currentPolyLine == null) {
            // currentPolyLine.remove();
            currentPolyLine = mMap.addPolyline(new PolylineOptions()
                    .addAll(path)
                    .color(Color.GREEN)
                    .width(25));
        } else {
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
                Toast.makeText(getApplicationContext(), "Location Permission Access must be enabled!", Toast.LENGTH_LONG).show();
                // You might want to show a message to the user or handle this case differently
            }
        }

        if(requestCode == 100 && grantResults.length>0&& grantResults[0]==PackageManager.PERMISSION_GRANTED){
            sendSms();
        }else{
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    private List<Marker> Placemarkers = new ArrayList<>();

    private void clearMarkers() {
        for (Marker marker : Placemarkers) {
            marker.remove();
        }
        Placemarkers.clear();
    }

    private void startRouteTrackingUser(){
            PolylineOptions rOptions = new PolylineOptions()
                    .addAll(routePoints)
                    .color(Color.GREEN)
                    .width(20);
            Polyline rPolyline = mMap.addPolyline(rOptions);
                routePolyline.add(rPolyline);

        }
    private void saveRouteToDatabaseWithDialog(){

        ConstraintLayout constraintLayout = findViewById(R.id.layoutDialogContainer);
        View vC = LayoutInflater.from(this).inflate(R.layout.route_save_dialog,constraintLayout);
        startSaveRoute = vC.findViewById(R.id.buttonSaveAction);
        cancelSaveRoute = vC.findViewById(R.id.buttonCancelAction);
        saveDialogText = vC.findViewById(R.id.Vtext_desc);
        saveDialogText.setText("Do you want to save the route?");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(vC);
        final AlertDialog alertDSave = builder.create();
        startSaveRoute.findViewById(R.id.buttonSaveAction).setOnClickListener(view1 -> {
            alertDSave.dismiss();
            ConstraintLayout ccL = findViewById(R.id.layoutDialogContainer2);
            View vC2 = LayoutInflater.from(this).inflate(R.layout.route_save_dialog2,ccL);
            AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
            builder2.setView(vC2);
            AlertDialog dialog2=builder2.create();
            dialog2.show();
            finalstartSaveRoute = vC2.findViewById(R.id.buttonSaveAction2);
            finalcancelSaveRoute = vC2.findViewById(R.id.buttonCancelAction2);
            routeprivacy = vC2.findViewById(R.id.route_privacy);
            title_Route = vC2.findViewById(R.id.title_edit_Route);
            desc_Route = vC2.findViewById(R.id.desc_edit_Route);
            finalstartSaveRoute.findViewById(R.id.buttonSaveAction2).setOnClickListener(view2 -> {
                dialog2.dismiss();
                String titleR = title_Route.getText().toString();
                String descR = desc_Route.getText().toString();
                boolean privacyR = routeprivacy.isChecked();

                if (titleR.isEmpty()) {
                    Toast.makeText(this, "Please enter a title for the Route", Toast.LENGTH_LONG).show();
                    title_Route.setError("Title is required");
                    title_Route.requestFocus();
                } else {
                    LatLng midpoint = calculateMidpoint(routePoints);
                    String encodedPolyline = PolyUtil.encode(routePoints);
                    LatLng startPoint = routePoints.get(0);
                    LatLng endPoint = routePoints.get(routePoints.size() - 1);
                String staticMapUrl = "https://maps.googleapis.com/maps/api/staticmap" +
                        "?size=600x400" + // Set the size of the image
                        "&center=" + midpoint.latitude + "," + midpoint.longitude + // Set the center of the map
                        "&zoom=16" +
                        "&markers=color:green|" + startPoint.latitude + "," + startPoint.longitude +
                        "&markers=color:red|" + endPoint.latitude + "," + endPoint.longitude +
                        "&path=color:0x00ff00|weight:4|enc:" + encodedPolyline +
                        "&key=AIzaSyDMINsKu9fJHa_Phb0kq6xYXgDOh3nUXU8"; // Replace with your actual API Key

                downloadImageInBackground(staticMapUrl, currentUserId, titleR, descR, privacyR, String.valueOf(startPoint));

                for (Polyline polyline : routePolyline) {
                    polyline.remove();
                }
                routePolyline.clear();
                routePoints.clear();
            }
            });

            finalcancelSaveRoute.findViewById(R.id.buttonCancelAction2).setOnClickListener(view2 ->{
                dialog2.dismiss();
                for (Polyline polyline : routePolyline){
                    polyline.remove();
                }
                routePolyline.clear();
                routePoints.clear();
            });


        });

        cancelSaveRoute.findViewById(R.id.buttonCancelAction).setOnClickListener(view -> {
            alertDSave.dismiss();
            for (Polyline polyline : routePolyline){
                polyline.remove();
            }
            routePolyline.clear();
            routePoints.clear();
        });
        if(alertDSave.getWindow()!=null){
            alertDSave.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDSave.show();

    }

    private LatLng calculateMidpoint(List<LatLng> points) {
        double totalLat = 0;
        double totalLng = 0;

        for (LatLng point : points) {
            totalLat += point.latitude;
            totalLng += point.longitude;
        }

        double avgLat = totalLat / points.size();
        double avgLng = totalLng / points.size();

        return new LatLng(avgLat, avgLng);
    }
    private void downloadImageInBackground(String imageUrl, String currentUserId,
                                           String title,String desc,boolean privacyR,String startP) {
        new Thread(() -> {
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                InputStream inputStream = connection.getInputStream();

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                }

                byte[] result = byteArrayOutputStream.toByteArray();
                DatabaseReference routesref = FirebaseDatabase.getInstance().getReference("Routes").child(currentUserId);
                String routeId = routesref.push().getKey();
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
                String currentDate = dateFormat.format(new Date());
                // Upload image to Firebase Storage
                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                StorageReference mapRef = storageRef.child("maps").child(currentUserId).child(routeId+"_route_map.jpg");

                mapRef.putBytes(result).addOnSuccessListener(taskSnapshot -> {
                    mapRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrlFirebase = uri.toString();


                        // Save the URL in Realtime Database
                        routesref.child(routeId).child("imageUrl").setValue(imageUrlFirebase);
                        routesref.child(routeId).child("imageUrl").setValue(imageUrlFirebase);
                        routesref.child(routeId).child("dateCreated").setValue(currentDate);
                        routesref.child(routeId).child("private").setValue(privacyR);
                        routesref.child(routeId).child("startPoint").setValue(startP);
                        routesref.child(routeId).child("title").setValue(title);
                        routesref.child(routeId).child("desc").setValue(desc);

                        // Use the result (image byte array) here

                        Toast.makeText(this,"Route uploaded/save successfully! ", Toast.LENGTH_LONG).show();
                    });
                }).addOnFailureListener(exception -> {
                    Toast.makeText(this,"Failed to save/create route points", Toast.LENGTH_LONG).show();
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }



    private boolean shouldAutoCenterCamera = true;
    private boolean startRoutePoints = false;
    private Location prevDestinationLocatiom;

    private void startLocationUpdates() {
        progressBar.setVisibility(View.VISIBLE);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                Location location = locationResult.getLastLocation();
                if (location != null) {
                  //  Location smoothLocate = smoothLocationUpdate(location);
                    prevLocation = new Location(location);
                    if (prevDestinationLocatiom == null) {
                        prevDestinationLocatiom = location;
                    }
                    double latit = location.getLatitude();
                    double longit = location.getLongitude();
                    LatLng userL = new LatLng(latit, longit);

                    if(progressBar.getVisibility()==View.VISIBLE) {
                        progressBar.setVisibility(View.GONE);
                    }

                    float speed = location.getSpeed();
                    // if using a emulator it is bugged
                    String formatspeedTxt = String.format("%.1f", speed);
                    String speedtext = formatspeedTxt + " m/s";
                    text_Speed.setText(speedtext);

                    if(startRoutePoints==true){
                        routePoints.add(userL);
                        startRouteTrackingUser();
                        float heading = location.getBearing();
                        // Set the camera position with updated bearing
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(userL) // Keep the same target position
                                .zoom(20) // Keep the same zoom level
                                .bearing(heading) // Set the updated bearing
                                .tilt(50) // Reset the tilt to 0 degrees
                                .build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        //mMap.getUiSettings().setAllGesturesEnabled(false);
                       // mMap.getUiSettings().setRotateGesturesEnabled(true);
                    }
                    if(isSosStarted==false) {
                        checkMarkersProximity(location);
                    }

                    if (shouldAutoCenterCamera) {

                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userL, 17));
                        shouldAutoCenterCamera = false;
                    }

                    if (destination_enabled && isDestination_canceled == false) {
                        float heading = location.getBearing();
                        // Set the camera position with updated bearing
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(userL) // Keep the same target position
                                .zoom(20) // Keep the same zoom level
                                .bearing(heading) // Set the updated bearing
                                .tilt(50) // Reset the tilt to 0 degrees
                                .build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                     //   mMap.getUiSettings().setAllGesturesEnabled(false);
                       // mMap.getUiSettings().setRotateGesturesEnabled(true);
                    }
                    float distance = prevDestinationLocatiom.distanceTo(location);
                    if (distance >= 4 && isDestination_canceled == false && destination_enabled == true) {
                        prevDestinationLocatiom = location;
                        if (destinationLocation != null) {
                            getDirections(userL, destinationLocation);
                        }
                    }
                    if (destinationLocation != null) {
                        float[] results = new float[1];
                        Location.distanceBetween(
                                userL.latitude, userL.longitude,
                                destinationLocation.latitude, destinationLocation.longitude,
                                results);
                        float distanceInMeters = results[0];
                        if (distanceInMeters < 20) {
                            clearMarkers();
                            layoutDestination.setVisibility(View.GONE);
                            sViewB.setVisibility(View.VISIBLE);
                            layoutD_startRouting2.setVisibility(View.GONE);
                            stopDirections();
                        }
                    }
                }
            }

        };

        LocationRequest locationRequest = new LocationRequest.Builder
                (Priority.PRIORITY_BALANCED_POWER_ACCURACY, 1000)
                .build();

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    Looper.getMainLooper());
        }
    }
    private void checkMarkersProximity(Location userLocation){
        for (Marker marker : otherUserSignalMarker) {
            Location markerLocation = new Location("");
            markerLocation.setLatitude(marker.getPosition().latitude);
            markerLocation.setLongitude(marker.getPosition().longitude);

            float distance = userLocation.distanceTo(markerLocation);

            if (distance < 1000) { // Adjust the proximity threshold as needed
                showSnackbar("There's a marker nearby!");
                return; // Stop checking once a nearby marker is found
            }
        }
    }
    private void showSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }

    private void stopDirections() {
        stopDirectionsThread();

        if (currentPolyLine != null) {
            currentPolyLine.remove();
            currentPolyLine = null;
        }
        destination_enabled = false;
        isDestination_canceled = true;
        timerService.resetTimer();
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(mMap.getCameraPosition().target) // Keep the same target position
                .zoom(mMap.getCameraPosition().zoom) // Keep the same zoom level
                .bearing(mMap.getCameraPosition().bearing) // Keep the same bearing (if needed)
                .tilt(0) // Reset the tilt to 0 degrees
                .build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mMap.getUiSettings().setAllGesturesEnabled(true);
    }
/*
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
 */


    private void centerMapOnUserLocation(){
        if (mMap != null) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);

                fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                fusedLocationClient.getLastLocation().addOnSuccessListener(this,
                        location -> {
                            if (location != null) {
                                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 17));

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

    private List<Marker> otherUsersMarkers = new ArrayList<>();
    private List<Marker> otherUserSignalMarker = new ArrayList<>();
    private void drawOtherUsersMarker(){
        CompletableFuture.runAsync(()->{
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {

                    for (Marker marker : otherUsersMarkers) {
                        marker.remove();
                    }
                     otherUsersMarkers.clear();
                    for (Marker marker : otherUserSignalMarker) {
                        marker.remove();
                    }
                    otherUserSignalMarker.clear();
                    // Assuming each user node has 'latitude' and 'longitude' keys
                    double latitude = userSnapshot.child("RT_Location/UserLocation/l/0").getValue(Double.class);
                    double longitude = userSnapshot.child("RT_Location/UserLocation/l/1").getValue(Double.class);
                    Boolean sosSignal = userSnapshot.child("RT_Location/sosAlert").getValue(Boolean.class);
                    String timestamp = userSnapshot.child("RT_Location/timestamp").getValue(String.class);


                        if (sosSignal == false) {
                            if (userSnapshot.getKey().equals(currentUserId)) {
                                continue;
                            }
                        }


                    // Create a LatLng object from the retrieved coordinates
                    LatLng userLocation = new LatLng(latitude, longitude);

                    String userId = userSnapshot.getKey();

                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Registered Users").child(userId);

                    usersRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            String username = snapshot.child("username").getValue(String.class);
                            Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.defaultusermarker);
                            Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap,65, 65, true);
                            BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(resizedBitmap);
                            String timeandTxt = timestamp+"\nI Need Help";
                            
                            if(sosSignal==false) {
                                MarkerOptions markerOptions = new MarkerOptions()
                                        .position(userLocation)
                                        .title(username)
                                        .icon(icon);
                                Marker marker = mMap.addMarker(markerOptions);
                                marker.setTag(marker);

                                otherUsersMarkers.add(marker);
                            } else if (sosSignal==true) {
                                MarkerOptions markerOptions = new MarkerOptions()
                                        .position(userLocation)
                                        .title(username)
                                        .snippet(timeandTxt);
                                Marker marker = mMap.addMarker(markerOptions);
                                marker.setTag(marker);
                                otherUserSignalMarker.add(marker);

                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e(TAG, "Failed to retrieve other users' locations", error.toException());
                        }
                    });
            }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to retrieve other users' locations", error.toException());
            }
        };
        otherUsersRef.addValueEventListener(valueEventListener);
        });
    }

    /*

    private void drawSignalMarker(){
        CompletableFuture.runAsync(()->{
            refUserSignal.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataS) {
                    for (DataSnapshot userS : dataS.getChildren()) {
                        for (Marker marker : otherUserSignalMarker) {
                            marker.remove();
                        }
                        otherUserSignalMarker.clear();
                        // Assuming each user node has 'latitude' and 'longitude' keys
                        double latitude = userS.child("0").getValue(Double.class);
                        double longitude = userS.child("1").getValue(Double.class);
                        String dateSD = userS.child("timestamp").getValue(String.class);

                        // Create a LatLng object from the retrieved coordinates
                        LatLng userLocate = new LatLng(latitude, longitude);

                        String userId = userS.getKey();

                        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Registered Users").child(userId);

                        usersRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot datasnaps) {

                                String username = datasnaps.child("username").getValue(String.class);

                                MarkerOptions markerOptions = new MarkerOptions()
                                        .position(userLocate)
                                        .title(username)
                                        .snippet(dateSD);
                                Marker marker = mMap.addMarker(markerOptions);
                                marker.setTag(marker);

                                otherUserSignalMarker.add(marker);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e(TAG, "Failed to retrieve other users' locations", error.toException());
                            }
                        });
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Failed to retrieve other users' locations", error.toException());
                }
            });
        });
    }

     */
    @SuppressLint("PotentialBehaviorOverride")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        Log.d(TAG, "Map is ready!");
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.setInfoWindowAdapter(new InfoWindowAdapter(MapsSampleActivity.this));
            drawOtherUsersMarker();

            mMap.setOnCameraMoveListener(() -> {
                float zoomLevel = mMap.getCameraPosition().zoom;
                if (zoomLevel < 15) {
                    for (Marker marker : otherUsersMarkers) {
                        marker.setVisible(false);
                    }
                } else {
                    for (Marker marker : otherUsersMarkers) {
                        marker.setVisible(true);
                    }
                }
            });

        }
/*
        mMap.setOnMarkerClickListener(marker -> {
            if(otherUserSignalMarker.contains(marker)){
                marker.showInfoWindow();
            }
            return true;
        });

 */

}
    public void playSOSsound(){
        sosMediaplayer.setLooping(true);
        sosMediaplayer.start();
    }
    public void stopSOSsound(){
        sosMediaplayer.setLooping(false);
        if (sosMediaplayer.isPlaying()) {
            sosMediaplayer.stop(); // Stop playing the SOS sound
            sosMediaplayer.prepareAsync();
        }
    }

}