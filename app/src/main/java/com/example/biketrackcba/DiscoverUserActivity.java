package com.example.biketrackcba;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.checkerframework.checker.units.qual.A;

import java.lang.ref.Reference;
import java.util.ArrayList;

public class DiscoverUserActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private FloatingActionButton fab;
    private FirebaseUser firebaseUser;
    private FirebaseAuth nAuthprof;
    private ProgressBar pBarDiscover;
    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_user);
        nAuthprof = FirebaseAuth.getInstance();
        firebaseUser = nAuthprof.getCurrentUser();
        pBarDiscover = findViewById(R.id.progressBat_discover);
        fab= findViewById(R.id.start_sosButton);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(this, MapsSampleActivity.class);
            intent.putExtra("showSnackbar", true);
            startActivity(intent);
            finish();
        });

        bottomNavigationView = findViewById(R.id.bottomNavView);
        Menu menu = bottomNavigationView.getMenu();
        bottomNavigationView.setSelectedItemId(R.id.miDiscover);
        MenuItem menuItem2 = menu.getItem(2);
        menuItem2.setEnabled(false);
        RouteListfix();

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(this);




        bottomNavigationView.setOnItemSelectedListener(item -> {

            int id = item.getItemId();
            if (id==R.id.miHome){
                finish();
            } else if (id==R.id.miSocials) {
                Intent intent = new Intent(this, UserSocialsActivity.class);
                startActivity(intent);
                finish();

            } else if (id==R.id.miDiscover) {
                startActivity(getIntent());
                finish();
                overridePendingTransition(0,0);

            } else if (id==R.id.miProfile) {
                Intent intent = new Intent(this,UserProfileActivity.class);
                startActivity(intent);
                finish();

            }

            return false;
        });
    }


    private void RouteListfix(){
        pBarDiscover.setVisibility(View.VISIBLE);
        DatabaseReference regisUser = FirebaseDatabase.getInstance().getReference("Registered Users");
        DatabaseReference routesRef = FirebaseDatabase.getInstance().getReference("Routes");
        regisUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                LinearLayout linearLayout = findViewById(R.id.layout_discover_list);
                for(DataSnapshot usersnapsuser : snapshot.getChildren()){
                    String userIdfromusers = usersnapsuser.getKey();
                    routesRef.child(userIdfromusers).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snaprouteshot) {
                            for(DataSnapshot routesnapss: snaprouteshot.getChildren()){

                                String title = routesnapss.child("title").getValue(String.class);
                                String desc = routesnapss.child("desc").getValue(String.class);
                                String imageUrl = routesnapss.child("imageUrl").getValue(String.class);

                                // Inflate the item_route.xml layout
                                View itemView = getLayoutInflater().inflate(R.layout.displayroutes_design_list, null);

                                TextView userText = itemView.findViewById(R.id.route_user_discover);
                                userText.setText("Route Post by: " + usersnapsuser.child("username").getValue(String.class));

                                // Set the title and description
                                TextView titleTextView = itemView.findViewById(R.id.route_title_discover);
                                titleTextView.setText(title);

                                TextView descriptionTextView = itemView.findViewById(R.id.route_desc_discover);
                                if(desc == null || desc.isEmpty()) {
                                    descriptionTextView.setText("There is no Description for this Route Post");
                                } else {
                                    descriptionTextView.setText(desc);
                                }

                                // Load and display the image using Picasso
                                ImageView imageView = itemView.findViewById(R.id.route_image_discover);
                                Picasso.get().load(imageUrl).into(imageView);

                                // Add the item to the LinearLayout
                                linearLayout.addView(itemView);
                                pBarDiscover.setVisibility(View.GONE);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }
    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }
}