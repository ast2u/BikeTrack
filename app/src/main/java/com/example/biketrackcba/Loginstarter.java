package com.example.biketrackcba;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;


public class Loginstarter extends AppCompatActivity {
private Button lgEmail, lgGmail;
private FirebaseAuth nAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lgEmail=findViewById(R.id.loginEmail);
        nAuth=FirebaseAuth.getInstance();
        lgGmail=findViewById(R.id.loginGmail);
        if(NetworkUtils.isNetworkAvailable(this)) {
            lgEmail.setOnClickListener(view -> {
                Intent intnext = new Intent(Loginstarter.this, LoginUserEmailActivity.class);
                startActivity(intnext);


            });
        }else{
            lgEmail.setOnClickListener(view -> {
                NetworkUtils.showNoInternetDialog(this);

            });
        }
        lgGmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(NetworkUtils.isNetworkAvailable(this)) {
            if (nAuth.getCurrentUser() != null) {
                Toast.makeText(this, "Already Logged In!", Toast.LENGTH_SHORT).show();
                //START
                startActivity(new Intent(Loginstarter.this, MapsSampleActivity.class));
                finish();
            } else {
                Toast.makeText(this, "You can login now!", Toast.LENGTH_SHORT).show();
            }
        }else{
            NetworkUtils.showNoInternetDialog(this);
        }

    }
}