package com.example.biketrackcba;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.android.material.appbar.MaterialToolbar;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {
private MaterialToolbar mtToolbar;
Button direct_account;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mtToolbar = findViewById(R.id.settingTopAppBar);
        direct_account = findViewById(R.id.direct_accountinfo);


        direct_account.setOnClickListener(view -> {
            Intent intent = new Intent(SettingsActivity.this, MoreUserProfileActivity.class);
            startActivity(intent);
            finish();
        });


        mtToolbar.setNavigationOnClickListener(view -> {
            onBackPressed();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}