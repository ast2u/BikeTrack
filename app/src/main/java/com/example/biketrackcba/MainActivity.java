package com.example.biketrackcba;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
private Button lgEmail, lgGmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lgEmail=findViewById(R.id.loginEmail);

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
}