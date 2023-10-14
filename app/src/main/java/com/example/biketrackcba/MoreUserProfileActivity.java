package com.example.biketrackcba;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MoreUserProfileActivity extends AppCompatActivity {
    private TextView textVUsern, textVFname, textVEmail,textVbdate, textVgender;
    private ProgressBar progressBar;
    private String usern,fname,Temail,bdate,gender,mobile;
    private MaterialToolbar topAppbar;
    private Button BxtraLogout, BdeleteAcc;
    private FirebaseAuth nAuthprof;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_user_profile);
        textVUsern = findViewById(R.id.text_username);
        topAppbar = findViewById(R.id.PDtopAppBar);
        textVFname = findViewById(R.id.text_fullname);
        textVEmail = findViewById(R.id.text_email);
        textVbdate = findViewById(R.id.text_bdate);
        textVgender = findViewById(R.id.text_gender);
        BxtraLogout = findViewById(R.id.profDetails_extralogout);
        BdeleteAcc = findViewById(R.id.deleteAccButton);
        progressBar = findViewById(R.id.PBprofile);
        nAuthprof = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = nAuthprof.getCurrentUser();

        if(firebaseUser==null){
            Toast.makeText(MoreUserProfileActivity.this,"Something went wrong! User's Details " +
                    "are not available at the moment", Toast.LENGTH_LONG).show();

        }else {
            progressBar.setVisibility(View.VISIBLE);
            showmoreUserDetails(firebaseUser);
        }
        BxtraLogout.setOnClickListener(view -> {
            Toast.makeText(MoreUserProfileActivity.this,"Logged Out",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(MoreUserProfileActivity.this, Loginstarter.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            nAuthprof.signOut();
            startActivity(intent);
            finish();
        });

        topAppbar.setNavigationOnClickListener(view -> {
            onBackPressed();
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void showmoreUserDetails(FirebaseUser user){
        String userID = user.getUid();
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWrite_UserDetails readUserDetails = snapshot.getValue(ReadWrite_UserDetails.class);
                if(readUserDetails!=null){

                    fname = user.getDisplayName();
                     Temail = user.getEmail();
                    usern = readUserDetails.username;
                      bdate = readUserDetails.bdate;
                      gender = readUserDetails.gender;
                    mobile = readUserDetails.mobile;


                    textVUsern.setText(usern);
                    textVFname.setText(fname);
                       textVEmail.setText(Temail);
                         textVbdate.setText(bdate);
                        textVgender.setText(gender);

                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MoreUserProfileActivity.this,"Something went wrong!",
                        Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}