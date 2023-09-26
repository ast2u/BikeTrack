package com.example.biketrackcba;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class UserSocialsActivity extends AppCompatActivity {
private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_socials);
        bottomNavigationView = findViewById(R.id.bottomNavView);
        Menu menu = bottomNavigationView.getMenu();
        bottomNavigationView.setSelectedItemId(R.id.miSocials);
        MenuItem menuItem2 = menu.getItem(2);
        menuItem2.setEnabled(false);

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();
                if (id==R.id.miHome){
                    Intent intent = new Intent(UserSocialsActivity.this, MapsSampleActivity.class);
                    startActivity(intent);
                    finish();
                } else if (id==R.id.miSocials) {
                    startActivity(getIntent());
                    finish();
                    overridePendingTransition(0,0);

                } else if (id==R.id.miDiscover) {
                    Intent intent = new Intent(UserSocialsActivity.this,DiscoverUserActivity.class);
                    startActivity(intent);
                    finish();

                } else if (id==R.id.miProfile) {
                    Intent intent = new Intent(UserSocialsActivity.this,UserProfileActivity.class);
                    startActivity(intent);
                    finish();

                }

                return false;
            }
        });
    }
}