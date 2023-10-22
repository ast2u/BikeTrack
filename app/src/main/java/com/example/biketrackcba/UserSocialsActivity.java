package com.example.biketrackcba;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

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
        pbarsocial = findViewById(R.id.socialprogressbar);
        fabby = findViewById(R.id.directtomapsSOS);
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
            if (id == R.id.createpost_inSocials) {
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
            if (id == R.id.miHome) {
                finish();
            } else if (id == R.id.miSocials) {
                startActivity(getIntent());
                finish();
                overridePendingTransition(0, 0);

            } else if (id == R.id.miDiscover) {
                Intent intent = new Intent(this, DiscoverUserActivity.class);
                startActivity(intent);
                finish();

            } else if (id == R.id.miProfile) {
                Intent intent = new Intent(this, UserProfileActivity.class);
                startActivity(intent);
                finish();

            }

            return false;
        });
    }

    private void socialsPosts() {
        pbarsocial.setVisibility(View.VISIBLE);
        DatabaseReference regisUser = FirebaseDatabase.getInstance().getReference("Registered Users");
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("posts");
        regisUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot usernamesnapshot) {
                LinearLayout linearLayout = findViewById(R.id.listpost_layout);
                for (DataSnapshot snapusers : usernamesnapshot.getChildren()) {
                    String userIdsocials = snapusers.getKey();
                    postRef.child(userIdsocials).orderByChild("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot postshot) {
                            for (DataSnapshot postsnaps : postshot.getChildren()) {
                                View itemView = getLayoutInflater().inflate(R.layout.socialposts_lists, null);
                                String photos = snapusers.child("photoUrl").getValue().toString();
                                Uri uri = Uri.parse(photos);
                                String title = postsnaps.child("titlepost").getValue(String.class);
                                String desc = postsnaps.child("bodydesc").getValue(String.class);
                               long timecheckers = postsnaps.child("timestamp").getValue(long.class);
                                String timeago = calculateTimeDifference(timecheckers);
                                List<String> imageUrlslist = new ArrayList<>();
                                for (DataSnapshot imagesnaps : postsnaps.child("imageurls").getChildren()) {
                                    String imageUrls = imagesnaps.getValue(String.class);
                                    imageUrlslist.add(imageUrls);
                                }
                                RecyclerView recyclerView = itemView.findViewById(R.id.fetchRecyclerView);
                                ImageAdapterFetch imageAdapterFetch = new ImageAdapterFetch(imageUrlslist);
                                imageAdapterFetch.setImageUrls(imageUrlslist);
                                recyclerView.setAdapter(imageAdapterFetch);
                                CircleImageView circleImageView = itemView.findViewById(R.id.userprofile_picsocials);
                                TextView titletext = itemView.findViewById(R.id.titleofpost);
                                TextView descbody = itemView.findViewById(R.id.bodyofpost);
                                TextView timecheck = itemView.findViewById(R.id.usertimestamp_id);
                                TextView userText = itemView.findViewById(R.id.userpostsocial);

                                if (uri.equals(Uri.EMPTY)) {
                                    Picasso.get()
                                            .load(R.drawable.userprofilepic)
                                            .into(circleImageView);
                                } else {
                                    Picasso.get().load(uri).into(circleImageView);
                                }

                                userText.setText("Post by: " + snapusers.child("username").getValue(String.class));
                                titletext.setText(title);
                                timecheck.setText(timeago);
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

    public String calculateTimeDifference(long timestamp) {
        long currentTime = System.currentTimeMillis();
        long timeDifference = currentTime - timestamp;

        long seconds = timeDifference / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) {
            return days + (days == 1 ? " day ago" : " days ago");
        } else if (hours > 0) {
            return hours + (hours == 1 ? " hour ago" : " hours ago");
        } else if (minutes > 0) {
            return minutes + (minutes == 1 ? " minute ago" : " minutes ago");
        } else {
            return seconds + (seconds == 1 ? " second ago" : " seconds ago");
        }
}
}