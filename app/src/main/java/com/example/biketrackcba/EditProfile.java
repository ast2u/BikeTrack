package com.example.biketrackcba;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;

import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity {
private TextInputLayout emC1,emC2;
private TextView TV_Fname,title_em;
private Spinner edit_gender;
private ProgressBar pBar;
private TextInputLayout edit_fname, edit_uname, edit_bdate, edit_mobilen;
private String uname, fname, bdate, mobilen,gender;
private String contactem1, contactem2;
private DatabaseReference refer;
private ImageView backButton;
private CircleImageView userProfilePic;
private static final int REQUEST_CODE_IMAGE_PICKER = 1001;

private String encodedImage;
private FirebaseAuth nAuth;
private Button updateButton;
private FirebaseUser Fuser;
private ArrayAdapter<CharSequence> adapter;
private Uri uriImage;
private StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        emC1 = findViewById(R.id.PeditText_Econtact1);
        emC2 = findViewById(R.id.PeditText_Econtact2);
        pBar = findViewById(R.id.edit_Pbar);
        userProfilePic=findViewById(R.id.user_Profilepic);
        backButton = findViewById(R.id.edit_goback);
        updateButton = findViewById(R.id.update_profButton);
        title_em = findViewById(R.id.textView_titleEmcontacts);
        edit_fname = findViewById(R.id.PeditText_fullname);
        edit_uname = findViewById(R.id.PeditText_username);
        edit_bdate = findViewById(R.id.PeditText_bdate);
        initDatePicker();
        edit_mobilen = findViewById(R.id.PeditText_usernumber);

        TV_Fname = findViewById(R.id.textedit_name);
        edit_gender = findViewById(R.id.spinner_gender);
        nAuth = FirebaseAuth.getInstance();
        Fuser = nAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("Profilepics");

        Uri uri = Fuser.getPhotoUrl();

        Picasso.get().load(uri).into(userProfilePic);


        if(Fuser==null){
            Toast.makeText(EditProfile.this,"Something went wrong! User's Details " +
                    "are not available at the moment", Toast.LENGTH_LONG).show();

        }else {
            pBar.setVisibility(View.VISIBLE);
            showUserData(Fuser);
        }
        adapter = ArrayAdapter.createFromResource(
                this,
                R.array.gender_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        refer = FirebaseDatabase.getInstance().getReference("Registered Users").child(Fuser.getUid());
        backButton.setOnClickListener(view -> {
            onBackPressed();
        });
        updateButton.setOnClickListener(view -> {
           UserUpdate();
           pBar.setVisibility(View.VISIBLE);
           UploadPic();
            finish();
        });

        userProfilePic.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageLauncher.launch(intent);
        });
    }

    private void UploadPic() {
        if(uriImage!=null){
            //Save the image
            StorageReference fileref = storageReference.child(nAuth.getCurrentUser().getUid()+"."+
                    getFileExtension(uriImage));
            fileref.putFile(uriImage).addOnSuccessListener(taskSnapshot -> {
                fileref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Uri downloadUri = uri;
                        refer.child("photoUrl").setValue(downloadUri.toString());
                        Fuser = nAuth.getCurrentUser();
                        UserProfileChangeRequest profileupdates = new UserProfileChangeRequest.Builder()
                                .setPhotoUri(downloadUri)
                                .build();
                        Fuser.updateProfile(profileupdates);
                    }
                });
                pBar.setVisibility(View.GONE);
                Toast.makeText(EditProfile.this,"Upload Successful!",Toast.LENGTH_SHORT).show();

                //intent
            }).addOnFailureListener(e -> Toast.makeText(EditProfile.this,e.getMessage(),Toast.LENGTH_SHORT).show());
        }else{
            refer.child("photoUrl").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String photourl =snapshot.getValue().toString();
                    refer.child("photoUrl").setValue(photourl);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(EditProfile.this,"Error null",Toast.LENGTH_SHORT).show();
                }
            });
            Toast.makeText(EditProfile.this,"No File Selected!",Toast.LENGTH_SHORT).show();
        }
    }
    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }



    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if(data!=null && data.getData()!=null){
                        uriImage = data.getData();
                        userProfilePic.setImageURI(uriImage);
                    }
                }
            }
    );
    private void showUserData(FirebaseUser firebaseUser){
        String useruid = firebaseUser.getUid();
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
        referenceProfile.child(useruid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                fname= firebaseUser.getDisplayName();
                uname = snapshot.child("username").getValue().toString();
                bdate = snapshot.child("bdate").getValue().toString();
                mobilen = snapshot.child("mobile").getValue().toString();
                gender = snapshot.child("gender").getValue().toString();
                contactem1 = snapshot.child("emnumber1").getValue().toString();
                contactem2 = snapshot.child("emnumber2").getValue().toString();
                Uri uri = firebaseUser.getPhotoUrl();
                if(uri==null) {
                    Picasso.get()
                            .load(R.drawable.userprofilepic)
                            .into(userProfilePic);
                }else{
                    Picasso.get().load(uri).into(userProfilePic);
                }

                EditText editFname = edit_fname.getEditText();
                if (editFname != null) {
                    editFname.setText(fname);
                }
                EditText edituname = edit_uname.getEditText();
                if (edituname != null) {
                    edituname.setText(uname);
                }
                EditText editbdate = edit_bdate.getEditText();
                if (editbdate != null) {
                    editbdate.setText(bdate);
                }
                EditText editmobilen = edit_mobilen.getEditText();
                if (editmobilen != null) {
                    editmobilen.setText(mobilen);
                }

               EditText editemc1 = emC1.getEditText();
                if (editemc1 != null) {
                    editemc1.setText(contactem1);
                }

                EditText editemc2 = emC2.getEditText();
                if (editemc2 != null) {
                    editemc2.setText(contactem2);
                }

                String temc1 = editemc1.getText().toString().trim();
                String temc2 = editemc1.getText().toString().trim();
                if (temc1.isEmpty() && temc2.isEmpty()){
                    title_em.setVisibility(View.VISIBLE);
                }else{
                    title_em.setVisibility(View.GONE);
                }
                edit_gender.setAdapter(adapter);
                int position = adapter.getPosition(gender);
                edit_gender.setSelection(position);

                TV_Fname.setText(fname);
                pBar.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditProfile.this,"Something went wrong! Please restart Application",
                        Toast.LENGTH_LONG).show();
                pBar.setVisibility(View.GONE);
            }
        });

    }


    private void UserUpdate(){
        String newName = edit_fname.getEditText().getText().toString();
        String outputnewName = capitalizeFirstLetter(newName);

        String new_uname = edit_uname.getEditText().getText().toString();
        String new_mobile = edit_mobilen.getEditText().getText().toString();
        String new_bdate = edit_bdate.getEditText().getText().toString();
        String new_gender = edit_gender.getSelectedItem().toString();
        String new_em1 = emC1.getEditText().getText().toString();
        String new_em2 = emC2.getEditText().getText().toString();
        String mobileRegex = "[0][0-9]{9}"; //First no. will be 0 to 11 digits
        Matcher mobileMatcher,mobileMatcher1,mobileMatcher2;
        Pattern mobilePattern = Pattern.compile(mobileRegex);
        mobileMatcher = mobilePattern.matcher(new_mobile);
        mobileMatcher1 = mobilePattern.matcher(new_em1);
        mobileMatcher2 = mobilePattern.matcher(new_em2);
        if(TextUtils.isEmpty(new_em1)){
            Toast.makeText(EditProfile.this, "Please enter an Emergency Number 1", Toast.LENGTH_LONG).show();
            emC1.setError("Mobile No. is Required");
            emC1.requestFocus();
        }else if(TextUtils.isEmpty(new_em2)){
            Toast.makeText(EditProfile.this, "Please enter an Emergency Number 1", Toast.LENGTH_LONG).show();
            emC1.setError("Mobile No. is Required");
            emC1.requestFocus();
        }else if(!fname.equals(outputnewName) || !uname.equals(new_uname) || !mobilen.equals(new_mobile)
                || !bdate.equals(new_bdate) || !gender.equals(new_gender) || !contactem1.equals(new_em1) ||
                !contactem2.equals(new_em2)) {
            if (TextUtils.isEmpty(outputnewName)) {
                Toast.makeText(EditProfile.this, "Please enter your First Name and Last Name", Toast.LENGTH_LONG).show();
                edit_fname.setError("Name is Required");
                edit_fname.requestFocus();
            } else if (!outputnewName.matches("[a-zA-Z\\s]+")) {
                Toast.makeText(EditProfile.this, "Your name should have no numbers", Toast.LENGTH_LONG).show();
                edit_fname.setError("No number in Name");
                edit_fname.requestFocus();
            } else if (outputnewName.length() > 23) {
                Toast.makeText(EditProfile.this, "Shorten your name", Toast.LENGTH_LONG).show();
                edit_fname.setError("Name too long");
                edit_fname.requestFocus();
            }
        else if (TextUtils.isEmpty(new_uname)) {
                Toast.makeText(EditProfile.this, "Please enter your Username", Toast.LENGTH_LONG).show();
                edit_uname.setError("Username is Required");
                edit_uname.requestFocus();
            } else if (new_uname.length()<5) {
                Toast.makeText(EditProfile.this, "Please enter your Username", Toast.LENGTH_LONG).show();
                edit_uname.setError("Username too short");
                edit_uname.requestFocus();
            }else if(!mobileMatcher1.find()){
                Toast.makeText(EditProfile.this, "Please enter an Emergency Number 1", Toast.LENGTH_LONG).show();
                emC1.setError("Mobile No. is not Valid");
                emC1.requestFocus();
            }else if (new_em1.length()!=11) {
                Toast.makeText(EditProfile.this,"Please re-enter the Emergency Number 1",Toast.LENGTH_LONG).show();
                emC1.setError("Mobile No. should be 11 digits");
                emC1.requestFocus();
            }//
            else if(TextUtils.isEmpty(new_em1)){
                Toast.makeText(EditProfile.this, "Please enter an Emergency Number 1", Toast.LENGTH_LONG).show();
                emC1.setError("Mobile No. is Required");
                emC1.requestFocus();
            }else if(TextUtils.isEmpty(new_em2)) {
                Toast.makeText(EditProfile.this, "Please enter an Emergency Number 1", Toast.LENGTH_LONG).show();
                emC1.setError("Mobile No. is Required");
                emC1.requestFocus();

                //
            } else if(!mobileMatcher2.find()){
                Toast.makeText(EditProfile.this, "Please enter an Emergency Number 2", Toast.LENGTH_LONG).show();
                emC2.setError("Mobile No. is not Valid");
                emC2.requestFocus();
            }else if (new_em2.length()!=11) {
                Toast.makeText(EditProfile.this,"Please re-enter the Emergency Number 2",Toast.LENGTH_LONG).show();
                emC2.setError("Mobile No. should be 11 digits");
                emC2.requestFocus();
            }
            else if (TextUtils.isEmpty(new_mobile)) {
                Toast.makeText(EditProfile.this, "Please enter your Mobile Number", Toast.LENGTH_LONG).show();
                edit_mobilen.setError("Mobile No. is Required");
                edit_mobilen.requestFocus();
            }else if(!mobileMatcher.find()){
                Toast.makeText(EditProfile.this, "Please enter your Mobile Number", Toast.LENGTH_LONG).show();
                edit_mobilen.setError("Mobile No. is not Valid");
                edit_mobilen.requestFocus();
            }else if (new_mobile.length()!=11) {
                Toast.makeText(EditProfile.this,"Please re-enter your Mobile Number",Toast.LENGTH_LONG).show();
                edit_mobilen.setError("Mobile No. should be 11 digits");
                edit_mobilen.requestFocus();
            } else if (TextUtils.isEmpty(new_bdate)) {
                Toast.makeText(EditProfile.this, "Please enter your Birthdate", Toast.LENGTH_LONG).show();
                edit_bdate.setError("Birthdate is Required");
                edit_bdate.requestFocus();
            } else{
                UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                        .setDisplayName(outputnewName)
                        .build();
                Fuser.updateProfile(profileChangeRequest).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        ReadWrite_UserDetails userDetails = new ReadWrite_UserDetails(new_uname,new_bdate,new_gender,new_mobile,new_em1,new_em2);
                        refer.setValue(userDetails).addOnCompleteListener(task1 -> {
                            if(task1.isSuccessful()){
                                Toast.makeText(EditProfile.this,"Your Profile has been updated.",
                                                Toast.LENGTH_LONG).show();

                            }else {
                                Toast.makeText(EditProfile.this, "Something went wrong! Please try again later",
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }else{
                        Toast.makeText(EditProfile.this, "Something went wrong! Please try again later",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        }else {
            Toast.makeText(EditProfile.this,"Profile has no changes.",
                    Toast.LENGTH_SHORT).show();

        }
    }
    private DatePickerDialog datepd;
    private void initDatePicker(){

        edit_bdate.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            try {
                Date defaultDate = sdf.parse(bdate);
                calendar.setTime(defaultDate);
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
            } catch (ParseException e) {
                e.printStackTrace(); // Handle the exception appropriately
            }
            datepd = new DatePickerDialog(EditProfile.this, (datePicker, year1, month1, dayOfMonth) -> {
                EditText editbdate = edit_bdate.getEditText();
                if (editbdate != null) {
                    editbdate.setText(dayOfMonth+"/"+(month1 +1)+"/"+ year1);
                }
            },year,month,day);
            datepd.show();
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private static String capitalizeFirstLetter(String input){
        StringBuilder result = new StringBuilder();
        String[] words = input.split("\\s");

        for (String word : words) {
            if (!word.isEmpty()) {
                char firstChar = Character.toUpperCase(word.charAt(0));
                String rest = word.substring(1).toLowerCase();
                result.append(firstChar).append(rest).append(" ");
            }
        }

        return result.toString().trim();
    }
}