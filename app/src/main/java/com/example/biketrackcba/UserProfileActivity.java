package com.example.biketrackcba;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.viewpager2.widget.ViewPager2;


import android.annotation.SuppressLint;

import android.content.Intent;


import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.model.LatLng;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {

    private TextView textVUsern, textVFname,textVbdate, textVgender,textVmobile;
    private ProgressBar progressBar;
    private TextView textEm1, textEm2;
    private GoogleMap gMap;
    private String usern,fname,Temail,bdate,gender,mobile;
    private String EMnum1, EMnum2;
    private BottomNavigationView bottomNavigationView;
    private LinearLayout emergencylayout,emergencylayout2;

    private MaterialToolbar topAppbar;
    private CircleImageView userprofpic;
    private FirebaseAuth nAuthprof;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        progressBar = findViewById(R.id.progressBarprofile);
        textVUsern = findViewById(R.id.icd_unameprofile);
        textVFname = findViewById(R.id.icd_nameprofile);
        textEm1 = findViewById(R.id.emContacts1);
        userprofpic = findViewById(R.id.defaultprofilepic);
        textEm2 = findViewById(R.id.emContacts2);
        topAppbar = findViewById(R.id.topAppBar);
        textVmobile = findViewById(R.id.icd_mobileprofile);
        emergencylayout = findViewById(R.id.edit_errornullemergency);
        emergencylayout2 = findViewById(R.id.show_emergency);
        topAppbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if(id==R.id.mLogout){
                Toast.makeText(UserProfileActivity.this,"Logged Out",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(UserProfileActivity.this, Loginstarter.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                nAuthprof.signOut();
                startActivity(intent);
                finish();
            } else if (id==R.id.more_profile_D) {
                Intent intent = new Intent(UserProfileActivity.this,MoreUserProfileActivity.class);
                startActivity(intent);
                
            } else if (id==R.id.more_editProf) {
                Intent intent = new Intent(UserProfileActivity.this,EditProfile.class);
                startActivity(intent);

            }
            return false;
        });


        //hook
        bottomNavigationView = findViewById(R.id.bottomNavView);
        Menu menu = bottomNavigationView.getMenu();
        bottomNavigationView.setSelectedItemId(R.id.miProfile);
        MenuItem menuItem2 = menu.getItem(2);
        menuItem2.setEnabled(false);

        bottomNavigationView.setOnItemSelectedListener(item -> {

            int id = item.getItemId();
            if (id==R.id.miHome){
                Intent intent = new Intent(UserProfileActivity.this, MapsSampleActivity.class);
                startActivity(intent);
                finish();


            } else if (id==R.id.miSocials) {
                Intent intent = new Intent(UserProfileActivity.this, UserSocialsActivity.class);
                startActivity(intent);
                finish();

            } else if (id==R.id.miDiscover) {
                Intent intent = new Intent(UserProfileActivity.this, DiscoverUserActivity.class);
                startActivity(intent);
                finish();

            } else if (id==R.id.miProfile) {
                startActivity(getIntent());
                finish();
                overridePendingTransition(0,0);

            }

            return false;
        });


        /*
        navigationView = findViewById(R.id.nav_view);
        setSupportActionBar(findViewById(R.id.univ_toolbar));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        navigationView.bringToFront();
        ActionBarDrawerToggle Dtoggle = new ActionBarDrawerToggle(this,mDrawerLayout,findViewById(R.id.univ_toolbar),
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(Dtoggle);
        Dtoggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

         */


        nAuthprof = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = nAuthprof.getCurrentUser();

        if(firebaseUser==null){
            Toast.makeText(UserProfileActivity.this,"Something went wrong! User's Details " +
                            "are not available at the moment", Toast.LENGTH_LONG).show();

        }else {
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);

        }



    }

    /*
    @Override
    public void onBackPressed() {

        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }

    }

     */

    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();

        //extract
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWrite_UserDetails readUserDetails = snapshot.getValue(ReadWrite_UserDetails.class);
                if(readUserDetails!=null){

                    fname = firebaseUser.getDisplayName();
                  //  Temail = firebaseUser.getEmail();
                    usern = readUserDetails.username;
                  //  bdate = readUserDetails.bdate;
                  //  gender = readUserDetails.gender;
                    mobile = readUserDetails.mobile;
                    EMnum1 = readUserDetails.emnumber1;
                    EMnum2 = readUserDetails.emnumber2;


                    textVUsern.setText("Welcome, "+usern+"!");
                    textVFname.setText(fname);
                //    textVEmail.setText(Temail);
               //     textVbdate.setText(bdate);
                //    textVgender.setText(gender);
                    textVmobile.setText(mobile);
                    Uri uri = firebaseUser.getPhotoUrl();
                    if(uri==null) {
                        Picasso.get()
                                .load(R.drawable.userprofilepic)
                                .into(userprofpic);
                    }else{
                        Picasso.get().load(uri).into(userprofpic);
                    }

                }else{
                    Toast.makeText(UserProfileActivity.this,"Something went wrong!",
                            Toast.LENGTH_LONG).show();
                }



                if(EMnum1.isEmpty() && EMnum2.isEmpty()) {
                    emergencylayout.setVisibility(View.VISIBLE);
                    emergencylayout2.setVisibility(View.GONE);
                }else{
                    emergencylayout2.setVisibility(View.VISIBLE);
                    emergencylayout.setVisibility(View.GONE);
                }
                textEm1.setText(EMnum1);
                textEm2.setText(EMnum2);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfileActivity.this,"Something went wrong!",
                        Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);

            }
        });


    }
    private void showRoutesDataUser(FirebaseUser user){
        String userID = user.getUid();

        DatabaseReference routesRef = FirebaseDatabase.getInstance().getReference("Routes");

        routesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot pointSnap : snapshot.getChildren()){
                    String routeKey = routesRef.push().getKey();
                    List<LatLng> routePoints = new ArrayList<>();
                    for (DataSnapshot pointSnapshot : pointSnap.child(routeKey).child("points").getChildren()) {
                        double latitude = pointSnapshot.child("latitude").getValue(double.class);
                        double longitude = pointSnapshot.child("longitude").getValue(double.class);

                        LatLng latLng = new LatLng(latitude, longitude); // Create a LatLng object
                        routePoints.add(latLng); // Add LatLng to the list
                    }
                    String titleR = pointSnap.child("title").getValue(String.class);
                    String descR = pointSnap.child("desc").getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
private void generateStaticMapImage(List<LatLng> routePoints,String polyline){
    String apiKey = "AIzaSyDMINsKu9fJHa_Phb0kq6xYXgDOh3nUXU8"; // Replace with your own API key
    List<LatLng> decodedRoutePoints = decodePolyline(polyline);
    String markers = "markers=";
    for (LatLng point : routePoints) {
        markers += point.lat + "," + point.lng + "|";
    }
    String staticMapUrl = "https://maps.googleapis.com/maps/api/staticmap?" +
            "center="+ decodedRoutePoints.get(0).lat + "," + decodedRoutePoints.get(0).lng +
            "&zoom=13" + // Adjust zoom level as needed
            "&size=600x300" + // Set desired size
            "&maptype=roadmap" + // Choose map type
            "&" + markers +
            "&path=enc:" + polyline + // Add the encoded polyline
            "&key=" + apiKey;
    Picasso.get()
            .load(staticMapUrl)
            .into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    saveImageToStorage(bitmap);
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
}

    private void saveImageToStorage(Bitmap bitmap) {
        String fileName = "static_map_image.png";

        try (FileOutputStream out = new FileOutputStream(new File(getExternalCacheDir(), fileName))) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // Save bitmap as PNG
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<LatLng> decodePolyline(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1F) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1F) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            double latitude = lat / 1e5;
            double longitude = lng / 1e5;
            poly.add(new LatLng(latitude, longitude));
        }

        return poly;
    }


/*
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if(nAuthprof.getCurrentUser()!=null){
            Menu menu1 = navigationView.getMenu();
            menu1.findItem(R.id.nav_logout).setVisible(false);
            menu1.findItem(R.id.nav_profile).setVisible(false);
        }

        if(id==R.id.nav_home){
            Intent intent = new Intent(UserProfileActivity.this, MainscreenActivity.class);
            startActivity(intent);
        }else if(id==R.id.nav_logout){
            Toast.makeText(UserProfileActivity.this,"Logged Out",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(UserProfileActivity.this, Loginstarter.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            nAuthprof.signOut();
            startActivity(intent);
            finish();

        }else{
            Toast.makeText(UserProfileActivity.this,"Something went wrong!",Toast.LENGTH_LONG).show();
        }

        return true;
    }

 */
}