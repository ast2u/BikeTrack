package com.example.biketrackcba;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class ReportfeedbackActivity extends AppCompatActivity {
private MaterialToolbar feedtop;
private Button reportsnap;
private reportadapter reportadapter;
private RecyclerView reportRecyclerView;
    private static final int REQUEST_CAMERA_PERMISSION = 1001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportfeedback);

        reportsnap= findViewById(R.id.reportaddpic);
        reportRecyclerView = findViewById(R.id.recyclerviewreport);
        List<Bitmap> dataset = new ArrayList<>(); // Initialize your dataset as needed
        reportadapter = new reportadapter(this, dataset);
        reportRecyclerView.setAdapter(reportadapter);

        // Set a layout manager (LinearLayoutManager, GridLayoutManager, etc.) if needed
        reportRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        reportsnap.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            } else {
                // Permission is already granted, you can proceed with launching the camera intent
                takePictureLauncher.launch(null);
            }
        });

        feedtop=findViewById(R.id.feedback_createTopApp);
        feedtop.setNavigationOnClickListener(v -> onBackPressed());

    }
   private final ActivityResultLauncher<Void> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicturePreview(),
            result -> {
                if (result != null) {
                    // result is a Bitmap of the captured image
                    reportadapter.addImage(result);
                } else {
                    // The user canceled the operation or an error occurred
                }
            }
    );

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with launching the camera intent
                takePictureLauncher.launch(null);
            } else {
                // Permission denied, handle accordingly (e.g., show a message to the user)
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}