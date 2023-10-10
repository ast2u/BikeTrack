package com.example.biketrackcba;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;

import android.content.Intent;


import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfileActivity extends AppCompatActivity {

    private TextView textVUsern, textVFname, textVEmail,textVbdate, textVgender,textVmobile;
    private ProgressBar progressBar;
    private Button tempButtonLogout;
    private String usern,fname,Temail,bdate,gender,mobile;
    private ImageView imageRefresh;



    private BottomNavigationView bottomNavigationView;

    private FirebaseAuth nAuthprof;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        progressBar = findViewById(R.id.progressBarprofile);
        textVUsern = findViewById(R.id.icd_unameprofile);
        textVFname = findViewById(R.id.icd_nameprofile);
        textVEmail = findViewById(R.id.icd_emailprofile);
        textVbdate = findViewById(R.id.icd_bdateprofile);
        textVgender = findViewById(R.id.icd_genderprofile);

        textVmobile = findViewById(R.id.icd_mobileprofile);

        //hook
        imageRefresh = findViewById(R.id.idB_settingButton);
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


        // Temporary Logout Button
        tempButtonLogout = findViewById(R.id.logoutbutton);
        tempButtonLogout.setOnClickListener(view -> {
            Toast.makeText(UserProfileActivity.this,"Logged Out",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(UserProfileActivity.this, Loginstarter.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            nAuthprof.signOut();
            startActivity(intent);
            finish();

        });

        //  .....
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


        imageRefresh.setOnClickListener(view -> {

            Intent intent = new Intent(UserProfileActivity.this, SettingsActivity.class);
            startActivity(intent);
            overridePendingTransition(2,2);


        });


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
                    Temail = firebaseUser.getEmail();
                    usern = readUserDetails.username;
                    bdate = readUserDetails.bdate;
                    gender = readUserDetails.gender;
                    mobile = readUserDetails.mobile;


                    textVUsern.setText("Welcome, "+usern+"!");
                    textVFname.setText(fname);
                    textVEmail.setText(Temail);
                    textVbdate.setText(bdate);
                    textVgender.setText(gender);
                    textVmobile.setText(mobile);
                }
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