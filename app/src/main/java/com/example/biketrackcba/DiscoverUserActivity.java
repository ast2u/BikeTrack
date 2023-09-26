package com.example.biketrackcba;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DiscoverUserActivity extends AppCompatActivity {
private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_user);
        bottomNavigationView = findViewById(R.id.bottomNavView);
        Menu menu = bottomNavigationView.getMenu();
        bottomNavigationView.setSelectedItemId(R.id.miDiscover);
        MenuItem menuItem2 = menu.getItem(2);
        menuItem2.setEnabled(false);

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

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
            }
        });
    }
}