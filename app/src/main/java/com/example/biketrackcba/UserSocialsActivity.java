package com.example.biketrackcba;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class UserSocialsActivity extends AppCompatActivity {
private BottomNavigationView bottomNavigationView;
private MaterialToolbar socialTop;
private ProgressBar pbarsocial;
private FloatingActionButton fabby;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_socials);

        socialTop = findViewById(R.id.socialTopApp);
        pbarsocial= findViewById(R.id.socialprogressbar);
        fabby= findViewById(R.id.directtomapsSOS);
        socialsPosts();
        socialTop.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(this, CreatePostActivity.class);
            startActivity(intent);
            finish();
        });

        fabby.setOnClickListener(v -> {
            Intent intent = new Intent(this, MapsSampleActivity.class);
            intent.putExtra("showSnackbar", true);
            startActivity(intent);
            finish();
        });

        socialTop.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if(id==R.id.createpost_inSocials){
                Intent intent = new Intent(this, CreatePostActivity.class);
                startActivity(intent);
            }
            return false;
        });
        bottomNavigationView = findViewById(R.id.bottomNavView);
        Menu menu = bottomNavigationView.getMenu();
        bottomNavigationView.setSelectedItemId(R.id.miSocials);
        MenuItem menuItem2 = menu.getItem(2);
        menuItem2.setEnabled(false);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id==R.id.miHome){
                finish();
            } else if (id==R.id.miSocials) {
                startActivity(getIntent());
                finish();
                overridePendingTransition(0,0);

            } else if (id==R.id.miDiscover) {
                Intent intent = new Intent(this,DiscoverUserActivity.class);
                startActivity(intent);
                finish();

            } else if (id==R.id.miProfile) {
                Intent intent = new Intent(this,UserProfileActivity.class);
                startActivity(intent);
                finish();

            }

            return false;
        });
    }

    private void socialsPosts(){
        pbarsocial.setVisibility(View.VISIBLE);
        DatabaseReference regisUser = FirebaseDatabase.getInstance().getReference("Registered Users");
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("posts");
        regisUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot usernamesnapshot) {
                LinearLayout linearLayout = findViewById(R.id.listpost_layout);
                for(DataSnapshot snapusers: usernamesnapshot.getChildren()){
                    String userIdsocials = snapusers.getKey();
                    postRef.child(userIdsocials).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot postshot) {
                            for(DataSnapshot postsnaps: postshot.getChildren()){
                                View itemView = getLayoutInflater().inflate(R.layout.socialposts_lists, null);

                                String title = postsnaps.child("titlepost").getValue(String.class);
                                String desc = postsnaps.child("bodydesc").getValue(String.class);
                                List<String> imageUrlslist = new ArrayList<>();
                                for(DataSnapshot imagesnaps: postsnaps.child("imageurls").getChildren()) {
                                    String imageUrls = imagesnaps.getValue(String.class);
                                    imageUrlslist.add(imageUrls);
                                }
                                    RecyclerView recyclerView = itemView.findViewById(R.id.fetchRecyclerView);
                                    ImageAdapterFetch imageAdapterFetch = new ImageAdapterFetch(imageUrlslist);
                                    imageAdapterFetch.setImageUrls(imageUrlslist);
                                    recyclerView.setAdapter(imageAdapterFetch);
                                TextView titletext = itemView.findViewById(R.id.titleofpost);
                                TextView descbody = itemView.findViewById(R.id.bodyofpost);
                                TextView userText = itemView.findViewById(R.id.userpostsocial);
                                userText.setText("Post by: " + snapusers.child("username").getValue(String.class));
                                titletext.setText(title);
                                descbody.setText(desc);
                                    linearLayout.addView(itemView);
                                    pbarsocial.setVisibility(View.GONE);

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
}