package com.example.biketrackcba;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DiscoverUserActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    RecyclerView recyclerView;
    DatabaseReference database;
    adapter adapter;
    ArrayList<routesinfo> list;

    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_user);

        recyclerView = findViewById(R.id.userlist);
        database = FirebaseDatabase.getInstance().getReference("Routes");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        adapter = new adapter(this,list);
        recyclerView.setAdapter(adapter);

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    routesinfo routesinfo = dataSnapshot.getValue(routesinfo.class);
                    list.add(routesinfo);
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        bottomNavigationView = findViewById(R.id.bottomNavView);
        Menu menu = bottomNavigationView.getMenu();
        bottomNavigationView.setSelectedItemId(R.id.miDiscover);
        MenuItem menuItem2 = menu.getItem(2);
        menuItem2.setEnabled(false);

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(this);

        FirebaseApp.initializeApp(this);



        bottomNavigationView.setOnItemSelectedListener(item -> {

            int id = item.getItemId();
            if (id==R.id.miHome){
                Intent intent = new Intent(DiscoverUserActivity.this, MapsSampleActivity.class);
                startActivity(intent);
                finish();
            } else if (id==R.id.miSocials) {
                Intent intent = new Intent(DiscoverUserActivity.this, UserSocialsActivity.class);
                startActivity(intent);
                finish();

            } else if (id==R.id.miDiscover) {
                startActivity(getIntent());
                finish();
                overridePendingTransition(0,0);

            } else if (id==R.id.miProfile) {
                Intent intent = new Intent(DiscoverUserActivity.this,UserProfileActivity.class);
                startActivity(intent);
                finish();

            }

            return false;
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