package com.example.biketrackcba;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreatePostActivity extends AppCompatActivity {
private MaterialToolbar createPostToolbar;
private DatabaseReference postDatabase;
private FirebaseAuth nAuth;
private FirebaseStorage firebaseStorage;
private TextInputLayout titlesocialpost;
private EditText bodydescr;
private Switch privacy;
private StorageReference storageReference;
private RecyclerView imageRecyclerView;
private FirebaseUser userF;
private ProgressBar pbarr;
private List<Uri> selectedImages = new ArrayList<>();
private Button createpostButton, addimageButton;
private ImageAdapter imageAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        createPostToolbar = findViewById(R.id.social_createTopApp);
        createPostToolbar.setNavigationOnClickListener(v -> onBackPressed());
        addimageButton = findViewById(R.id.addphoto_button);
        createpostButton = findViewById(R.id.social_createthepost);
        bodydescr = findViewById(R.id.socials_body_edit);
        pbarr=  findViewById(R.id.pbar_createpost);
        privacy = findViewById(R.id.postswitch_privacy);
        nAuth = FirebaseAuth.getInstance();
        userF = nAuth.getCurrentUser();
        titlesocialpost = findViewById(R.id.socials_title_edit);
        imageRecyclerView = findViewById(R.id.imageRecyclerView);
        imageAdapter = new ImageAdapter(selectedImages);
        imageRecyclerView.setAdapter(imageAdapter);
        imageRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        addimageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            postImage.launch(intent);
        });


        createpostButton.setOnClickListener(v -> {
            createThepost();
        });

    }
    private final ActivityResultLauncher<Intent> postImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if(data!=null){
                        if(data.getClipData()!=null){
                            ClipData clipData = data.getClipData();
                            for(int i=0; i<clipData.getItemCount();i++){
                                Uri mImage = clipData.getItemAt(i).getUri();
                                selectedImages.add(mImage);
                            }
                            imageAdapter.notifyDataSetChanged();


                        }

                    }
                }

            }

    );

    private List<String> imageDownloadUrls = new ArrayList<>();

    private void createThepost(){
        String userId = userF.getUid();
        String titlepost = titlesocialpost.getEditText().getText().toString();
        String bodydescc = bodydescr.getText().toString();
        String pribado = String.valueOf(privacy.isChecked());

        postDatabase = FirebaseDatabase.getInstance().getReference("posts").child(userId);
        String postkey = postDatabase.push().getKey();
        if(titlepost.isEmpty()){
            Toast.makeText(CreatePostActivity.this, "Please enter a Title for your post.", Toast.LENGTH_LONG).show();
            titlesocialpost.setError("Title is Required");
            titlesocialpost.requestFocus();
        } else if (titlepost.length()>35) {
            Toast.makeText(CreatePostActivity.this, "Please enter a short Title.", Toast.LENGTH_LONG).show();
            titlesocialpost.setError("Title is too long");
            titlesocialpost.requestFocus();
        }else if (bodydescc.length()>200){
            Toast.makeText(CreatePostActivity.this, "Please enter a body only 200 Characters", Toast.LENGTH_LONG).show();
            bodydescr.setError("Body exceeds the limit");
            bodydescr.requestFocus();
        }else{
            pbarr.setVisibility(View.VISIBLE);
            createpostButton.setEnabled(false);
            addimageButton.setEnabled(false);
        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference postpics = storageReference.child("socialposts").child(userId);
        for(Uri imageuri:selectedImages){
            String filename = "image_"+System.currentTimeMillis()+".jpg";
            StorageReference imageref = postpics.child(postkey).child(filename);
            imageref.putFile(imageuri).addOnSuccessListener(taskSnapshot -> {
                imageref.getDownloadUrl().addOnSuccessListener(uri -> {
                    imageDownloadUrls.add(uri.toString());
                    if(imageDownloadUrls.size()==selectedImages.size()){
                        postDatabase.child(postkey).child("titlepost").setValue(titlepost);
                        postDatabase.child(postkey).child("timestamp").setValue(System.currentTimeMillis());
                        postDatabase.child(postkey).child("bodydesc").setValue(bodydescc);
                        postDatabase.child(postkey).child("private").setValue(pribado);
                        postDatabase.child(postkey).child("imageurls").setValue(imageDownloadUrls);
                        Toast.makeText(this, "Created successfully.",Toast.LENGTH_LONG).show();
                        pbarr.setVisibility(View.GONE);
                        createpostButton.setEnabled(true);
                        addimageButton.setEnabled(true);
                        finish();
                    }
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "error",Toast.LENGTH_LONG).show();
            });
        }
        }

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}