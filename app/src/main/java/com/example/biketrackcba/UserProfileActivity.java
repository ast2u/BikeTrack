package com.example.biketrackcba;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;

import android.content.Intent;



import android.net.Uri;
import android.os.Bundle;


import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.squareup.picasso.Picasso;


import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {

    private TextView textVUsern, textVFname,textVbdate, textVgender,textVmobile;
    private ProgressBar progressBar;
    private TextView textEm1, textEm2;
    private GoogleMap gMap;
    private String usern,fname,mobile;
    private String EMnum1, EMnum2;
    private BottomNavigationView bottomNavigationView;
    private LinearLayout emergencylayout,emergencylayout2;
    private FloatingActionButton fab_sos;
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
        fab_sos = findViewById(R.id.start_sosButton);
        textEm1 = findViewById(R.id.emContacts1);
        userprofpic = findViewById(R.id.defaultprofilepic);
        textEm2 = findViewById(R.id.emContacts2);
        topAppbar = findViewById(R.id.topAppBar);
        textVmobile = findViewById(R.id.icd_mobileprofile);
        messageempty = findViewById(R.id.message_empty_route);
        emergencylayout = findViewById(R.id.edit_errornullemergency);
        emergencylayout2 = findViewById(R.id.show_emergency);
        topAppbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if(id==R.id.mLogout){
                String userId = nAuthprof.getCurrentUser().getUid();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("BikersAvailable");
                GeoFire geoFire = new GeoFire(ref);
                geoFire.removeLocation(userId);
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

            }else if (id==R.id.settingnavigation){
                Intent intent = new Intent(UserProfileActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
            return false;
        });
        fab_sos.setOnClickListener(view -> {
            Intent intent = new Intent(UserProfileActivity.this, MapsSampleActivity.class);
            intent.putExtra("showSnackbar", true);
            startActivity(intent);
            finish();


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


        nAuthprof = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = nAuthprof.getCurrentUser();

        if(firebaseUser==null){
            Toast.makeText(UserProfileActivity.this,"Something went wrong! User's Details " +
                            "are not available at the moment", Toast.LENGTH_LONG).show();

        }else {
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
            listRoutes(firebaseUser);

        }

    }

    private TextView messageempty;
    private void listRoutes(FirebaseUser firebaseUser){
        String userID = firebaseUser.getUid();
        DatabaseReference routeref = FirebaseDatabase.getInstance().getReference("Routes");
        routeref.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                LinearLayout linearLayout = findViewById(R.id.profile_routes_list);
                if(snapshot.exists()){
                for (DataSnapshot routes: snapshot.getChildren()){
                    messageempty.setVisibility(View.GONE);
                    String title = routes.child("title").getValue(String.class);
                    String desc = routes.child("desc").getValue(String.class);
                    String imageUrl = routes.child("imageUrl").getValue(String.class);
                    View itemView1 = getLayoutInflater().inflate(R.layout.displayroutes_forprofile_list, null);


                        messageempty.setVisibility(View.VISIBLE);

                        // Set the title and description
                        TextView titleTextView = itemView1.findViewById(R.id.route_title_profile);
                        titleTextView.setText(title);

                        TextView descriptionTextView = itemView1.findViewById(R.id.route_desc_profile);
                        if (desc == null || desc.isEmpty()) {
                            descriptionTextView.setText("There is no Description for this Route Post");
                        } else {
                            descriptionTextView.setText(desc);
                        }

                        // Load and display the image using Picasso
                        ImageView imageView = itemView1.findViewById(R.id.route_image_profile);
                        Picasso.get().load(imageUrl).into(imageView);

                        // Add the item to the LinearLayout
                        linearLayout.addView(itemView1);

                }
                }else{
                    messageempty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

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
                    usern = readUserDetails.username;
                    mobile = readUserDetails.mobile;
                    EMnum1 = readUserDetails.emnumber1;
                    EMnum2 = readUserDetails.emnumber2;
                    textVUsern.setText("Welcome, "+usern+"!");
                    textVFname.setText(fname);
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
}