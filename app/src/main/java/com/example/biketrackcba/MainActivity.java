package com.example.biketrackcba;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {
private Button lgEmail, lgGmail;
private FirebaseAuth nAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lgEmail=findViewById(R.id.loginEmail);
        nAuth=FirebaseAuth.getInstance();

        lgEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intnext = new Intent(MainActivity.this, LoginUserEmailActivity.class);
                startActivity(intnext);
                finish();
                return;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(nAuth.getCurrentUser()!=null){
            Toast.makeText(this, "Already Logged In!", Toast.LENGTH_SHORT).show();
            //START
            startActivity(new Intent(MainActivity.this,UserProfileActivity.class));
            finish();
        }else{
            Toast.makeText(this, "You can login now!", Toast.LENGTH_SHORT).show();
        }
    }
}