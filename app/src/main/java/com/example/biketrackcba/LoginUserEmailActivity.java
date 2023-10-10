package com.example.biketrackcba;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginUserEmailActivity extends AppCompatActivity {
    private EditText nEmail, nPass;
    private EditText nrName, nrEmail, nrUsername, nrPass, nrMobile, nrBdate;
    private TextView regisLayout, loginLayout;
    private DatePickerDialog datepd;
    private Button nLogin, nRegister;
    private FirebaseAuth nAuth;
    private RadioGroup radioGroupRegisterGender;
    private RadioButton radioButtonRegisterGenderSelected;
    private ProgressBar progressBar1, progressBar;
    private static final String TAG = "RegisterLayoutActivity";
    private static final String TAG1 = "LoginLayoutActivity";
    private CheckBox termCheckbox;
    FirebaseDatabase database;
    DatabaseReference reference;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login_user_email);
        regisLayout = findViewById(R.id.registertextbutton);
        loginLayout = findViewById(R.id.logintextbutton);
        nEmail = findViewById(R.id.idemail);
        nPass = findViewById(R.id.idpassw);
        nLogin = findViewById(R.id.nLoginbut);
        progressBar1 = findViewById(R.id.progressBarLogin);
        nAuth = FirebaseAuth.getInstance();

        ImageView hidePwdbutton = findViewById(R.id.show_hidepw);
        hidePwdbutton.setImageResource(R.drawable.baseline_remove_red_eye_24);
        hidePwdbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nPass.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    nPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    hidePwdbutton.setImageResource(R.drawable.baseline_remove_red_eye_24);
                }else{
                    nPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    hidePwdbutton.setImageResource(R.drawable.baseline_hide_source_24);
                }
            }
        });
        nLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String Lemail = nEmail.getText().toString();
                String Lpwd = nPass.getText().toString();

                if (TextUtils.isEmpty(Lemail)) {
                    Toast.makeText(LoginUserEmailActivity.this, "Please enter your Email", Toast.LENGTH_SHORT).show();
                    nEmail.setError("Email is required");
                    nEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(Lemail).matches()) {
                    Toast.makeText(LoginUserEmailActivity.this, "Please re-enter your Email", Toast.LENGTH_SHORT).show();
                    nEmail.setError("Valid Email is required");
                    nEmail.requestFocus();
                } else if (TextUtils.isEmpty(Lpwd)) {
                    Toast.makeText(LoginUserEmailActivity.this, "Please enter your Password", Toast.LENGTH_SHORT).show();
                    nEmail.setError("Password is required");
                    nEmail.requestFocus();
                } else {
                    progressBar1.setVisibility(View.VISIBLE);
                    loginUser(Lemail, Lpwd);
                }
            }
        });


    }


    private void loginUser(String textemail, String textpwd) {
        nAuth.signInWithEmailAndPassword(textemail,textpwd).addOnCompleteListener(LoginUserEmailActivity.this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(LoginUserEmailActivity.this,"You are logged in now",Toast.LENGTH_LONG).show();
                    FirebaseUser firebaseUser = nAuth.getCurrentUser();
                    Toast.makeText(LoginUserEmailActivity.this,"You are logged in now",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(LoginUserEmailActivity.this, MapsSampleActivity.class);
                    //Intent intent = new Intent(Intent.ACTION_MAIN);
                    //intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    /*
                    if(firebaseUser.isEmailVerified()){

                    }else{
                        firebaseUser.sendEmailVerification();
                        nAuth.signOut();
                        showAlertDialog();
                    }
                    */

                }else{
                    try{
                        throw task.getException();
                    } catch (FirebaseAuthUserCollisionException e){
                     nEmail.setError("User does not Exist or is no longer valid. Please register again.");
                     nEmail.requestFocus();

                    }catch(FirebaseAuthInvalidCredentialsException e){
                        nEmail.setError("Invalid credentials. Kindly, check and re-enter.");
                        nEmail.requestFocus();

                    }catch(Exception e){
                        Log.e(TAG1,e.getMessage());
                        Toast.makeText(LoginUserEmailActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(LoginUserEmailActivity.this,"Something went wrong!",Toast.LENGTH_LONG).show();

                }
                progressBar1.setVisibility(View.GONE);
            }
        });
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginUserEmailActivity.this);
        builder.setTitle("Email Not Verified");
        builder.setMessage("Please verify your email now. You can not login without email verification");
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        AlertDialog alertDialog = builder.create();

        // show
        alertDialog.show();
    }


    public void loginlayoutbutton(View view){
        setContentView(R.layout.activity_login_user_email);
        nEmail = findViewById(R.id.idemail);
        nPass = findViewById(R.id.idpassw);
        nLogin = findViewById(R.id.nLoginbut);
        progressBar1 = findViewById(R.id.progressBarLogin);
        nAuth = FirebaseAuth.getInstance();
        ImageView hidePwdbutton = findViewById(R.id.show_hidepw);
        hidePwdbutton.setImageResource(R.drawable.baseline_remove_red_eye_24);
        hidePwdbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nPass.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    nPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    hidePwdbutton.setImageResource(R.drawable.baseline_remove_red_eye_24);
                }else{
                    nPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    hidePwdbutton.setImageResource(R.drawable.baseline_hide_source_24);
                }
            }
        });
        nLogin.setOnClickListener(view1 -> {

            String Lemail = nEmail.getText().toString();
            String Lpwd = nPass.getText().toString();

            if (TextUtils.isEmpty(Lemail)) {
                Toast.makeText(LoginUserEmailActivity.this, "Please enter your Email", Toast.LENGTH_SHORT).show();
                nEmail.setError("Email is required");
                nEmail.requestFocus();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(Lemail).matches()) {
                Toast.makeText(LoginUserEmailActivity.this, "Please re-enter your Email", Toast.LENGTH_SHORT).show();
                nEmail.setError("Valid Email is required");
                nEmail.requestFocus();
            } else if (TextUtils.isEmpty(Lpwd)) {
                Toast.makeText(LoginUserEmailActivity.this, "Please enter your Password", Toast.LENGTH_SHORT).show();
                nEmail.setError("Password is required");
                nEmail.requestFocus();
            } else {
                progressBar1.setVisibility(View.VISIBLE);
                loginUser(Lemail, Lpwd);
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();

        if(nAuth.getCurrentUser()!=null){
            Toast.makeText(this, "Already Logged In!", Toast.LENGTH_SHORT).show();
            //START
            startActivity(new Intent(LoginUserEmailActivity.this,UserProfileActivity.class));
            finish();
        }else{
            Log.d(TAG,"User Pass");
        }
    }

    @SuppressLint("MissingInflatedId")
    public void registerlayoutbutton(View view){
        setContentView(R.layout.activity_register_user);

        termCheckbox=findViewById(R.id.termsCheckBox);
        nrName=findViewById(R.id.idname);
        nrEmail=findViewById(R.id.idemailregis);
        nrUsername=findViewById(R.id.idusername);
        //date
        nrMobile=findViewById(R.id.idmobile);
        nrPass=findViewById(R.id.idpasswregis);
        nrBdate=findViewById(R.id.editText_register_dob);
        initDatePicker();

        //rbutton gender
        radioGroupRegisterGender=findViewById(R.id.radio_group_register_gender);
        radioGroupRegisterGender.clearCheck();

        nRegister=findViewById(R.id.nRegisterbut);
        progressBar = findViewById(R.id.progressBar);



        SpannableString spannableString = new SpannableString(termCheckbox.getText());
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Log.d(TAG,"i am clickable");
                showCustomDialogTerms();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
            }
        };

        int startIndex = termCheckbox.getText().toString().indexOf("Terms And Conditions");
        int endIndex = startIndex + "Terms And Conditions".length();

        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        termCheckbox.setText(spannableString);
        termCheckbox.setMovementMethod(LinkMovementMethod.getInstance());
        termCheckbox.setHighlightColor(Color.TRANSPARENT);



        nRegister.setOnClickListener(view1 -> {

            int selectedGenderId = radioGroupRegisterGender.getCheckedRadioButtonId();
            radioButtonRegisterGenderSelected = findViewById(selectedGenderId);

            String name = nrName.getText().toString();
            String email = nrEmail.getText().toString();
            String username = nrUsername.getText().toString();
            String bdate = nrBdate.getText().toString();
            String mobile = nrMobile.getText().toString();
            String textGender;
            String password = nrPass.getText().toString();

            String mobileRegex = "[0][0-9]{9}"; //First no. will be 0 to 11 digits
            Matcher mobileMatcher;
            Pattern mobilePattern = Pattern.compile(mobileRegex);
            mobileMatcher = mobilePattern.matcher(mobile);

            if (TextUtils.isEmpty(name)) {
                Toast.makeText(LoginUserEmailActivity.this, "Please enter your First Name and Last Name", Toast.LENGTH_LONG).show();
                nrName.setError("Name is Required");
                nrName.requestFocus();
            } else if (!name.matches("[a-zA-Z]+")) {
                Toast.makeText(LoginUserEmailActivity.this, "Your name should have no numbers", Toast.LENGTH_LONG).show();
                nrName.setError("No number in Name");
                nrName.requestFocus();
            } else if (TextUtils.isEmpty(email)) {
                Toast.makeText(LoginUserEmailActivity.this, "Please enter your Email Address", Toast.LENGTH_LONG).show();
                nrEmail.setError("Email is Required");
                nrEmail.requestFocus();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(LoginUserEmailActivity.this, "Please re-enter your Email Address", Toast.LENGTH_LONG).show();
                nrEmail.setError("Valid email is Required");
                nrEmail.requestFocus();
            } else if (TextUtils.isEmpty(username)) {
                Toast.makeText(LoginUserEmailActivity.this, "Please enter your Username", Toast.LENGTH_LONG).show();
                nrUsername.setError("Username is Required");
                nrUsername.requestFocus();
            } else if (username.length()<5) {
                Toast.makeText(LoginUserEmailActivity.this, "Please enter your Username", Toast.LENGTH_LONG).show();
                nrUsername.setError("Username too short");
                nrUsername.requestFocus();
            } else if (TextUtils.isEmpty(bdate)) {
                Toast.makeText(LoginUserEmailActivity.this, "Please enter your Birthdate", Toast.LENGTH_LONG).show();
                nrBdate.setError("Birthdate is Required");
                nrBdate.requestFocus();
            } else if (TextUtils.isEmpty(mobile)) {
                Toast.makeText(LoginUserEmailActivity.this, "Please enter your Mobile Number", Toast.LENGTH_LONG).show();
                nrMobile.setError("Mobile No. is Required");
                nrMobile.requestFocus();
            }else if(!mobileMatcher.find()){
                Toast.makeText(LoginUserEmailActivity.this, "Please enter your Mobile Number", Toast.LENGTH_LONG).show();
                nrMobile.setError("Mobile No. is not Valid");
                nrMobile.requestFocus();
            }else if (radioGroupRegisterGender.getCheckedRadioButtonId()== -1) {
                Toast.makeText(LoginUserEmailActivity.this,"Please select your Gender",Toast.LENGTH_LONG).show();
                radioButtonRegisterGenderSelected.setError("Gender is Required");
                radioButtonRegisterGenderSelected.requestFocus();
            } else if (nrMobile.length()!=11) {
                Toast.makeText(LoginUserEmailActivity.this,"Please re-enter your Mobile Number",Toast.LENGTH_LONG).show();
                nrMobile.setError("Mobile No. should be 11 digits");
                nrMobile.requestFocus();
            } else if (TextUtils.isEmpty(password)) {
                Toast.makeText(LoginUserEmailActivity.this,"Please enter your Password",Toast.LENGTH_LONG).show();
               nrPass.setError("Password is Required");
                nrPass.requestFocus();
            } else if (password.length()<6) {
                Toast.makeText(LoginUserEmailActivity.this,"Password should be at least 6 digits",Toast.LENGTH_LONG).show();
                nrPass.setError("Password too short");
               nrPass.requestFocus();
            } else if (!termCheckbox.isChecked()) {
                Toast.makeText(LoginUserEmailActivity.this,"Read Terms and Conditions",Toast.LENGTH_LONG).show();
                termCheckbox.setError("Please check and read Terms and Conditions");
                termCheckbox.requestFocus();
            } else{
                textGender = radioButtonRegisterGenderSelected.getText().toString();
                progressBar.setVisibility(View.VISIBLE);
                registerUser(name,email,username,bdate,mobile,textGender,password);
            }

        });
        
    }

    private TextView textTerm1, textTerm2;

    private void showCustomDialogTerms(){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_terms);
        textTerm1 =dialog.findViewById(R.id.startTextTerm);
        textTerm2= dialog.findViewById(R.id.longTermText);
        textTerm1.setText("Please carefully read these Terms and Services before using the BikeTrack mobile application. " +
                "By using this App, you agree to be bound by these Terms.");
        String formatTextLong = "<h3>Acceptance of Terms</h3> \n" +
                "<p>By using the App, you agree to comply with and be bound by these Terms. If you do not " +
                "agree with these Terms, please refrain from using the App.</p> \n" +
                "<h3>Privacy and Data Collection</h3>\n" +
                "<p>a. User Data: " +
                "The App may collect and store certain personal information, including but not limited to your name, " +
                "age, phone number, email address, location data, and contact information from your device.</p> \n" +
                "<p>b. Usage Information: " +
                "The App may collect usage information, such as the features you access and how you interact with the App.</p> \n" +
                "<p>c. Location Data: " +
                "To provide certain features, the App may request access to your device's location data. " +
                "You can choose to deny this access or disable location services in your device settings " +
                "but the primary features of the application will no longer work.</p> \n" +
                "<p>d. Public Sharing of location: \n" +
                "To provide a group ride feature, the application must display " +
                "your current location to users that you allow to see them.</p> \n" +

                "<h3>Use of Data</h3> \n" +
                "<p>a. Purpose: " +
                "We collect and use your data for purposes such as providing and " +
                "improving the App's services, customizing your experience, and communicating with you.</p>\n" +
                "<p>b. Third Parties: \n" +
                "We may share your data with third-party service providers who assist us " +
                "in operating the App. These service providers are obligated to protect your " +
                "data and use it only for the purposes specified.</p> \n" +

                " <h3>User Responsibilities</h3> \n" +
                " <p>a. Accuracy: \n" +
                " You are responsible for providing accurate and up-to-date information when using the App.</p>\n" +
                "<p>b. Security: \n" +
                "Keep your login credentials confidential and do not share them with others. " +
                "You are responsible for all activities that occur under your account.</p>\n" +

                "<h3>Termination</h3> \n" +
                "<p>We reserve the right to terminate or suspend your access to the App at our discretion," +
                "without notice, for any violation of these Terms or for any other reason.</p>";
        textTerm2.setText(HtmlCompat.fromHtml(formatTextLong, HtmlCompat.FROM_HTML_MODE_LEGACY));
        dialog.show();

    }

    private void registerUser(String Fname, String email, String username, String bdate, String mobile, String textGender, String password) {
        nAuth = FirebaseAuth.getInstance();
        nAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(LoginUserEmailActivity.this,
                task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(LoginUserEmailActivity.this,"User Registered successfully",Toast.LENGTH_SHORT).show();
                        FirebaseUser firebaseUser = nAuth.getCurrentUser();

                        //update
                        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(Fname).build();
                        firebaseUser.updateProfile(profileChangeRequest);

                        ReadWrite_UserDetails writeUserDetails = new ReadWrite_UserDetails(username,bdate,textGender,mobile);

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Users");

                        reference.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful()){
                                    firebaseUser.sendEmailVerification();

                                    Toast.makeText(LoginUserEmailActivity.this,"User Registered successfully. Please verify your Email",
                                            Toast.LENGTH_LONG).show();

                                    Intent intent = new Intent(LoginUserEmailActivity.this,UserProfileActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();

                                }else{
                                    Toast.makeText(LoginUserEmailActivity.this,"User Registered failed. Please try again",
                                            Toast.LENGTH_LONG).show();
                                }
                                progressBar.setVisibility(View.GONE);
                            }
                        });



                    }else{
                        try{
                            throw task.getException();

                        }catch (FirebaseAuthWeakPasswordException e){
                            nrPass.setError("Your password is too weak. Kindly use a mix of alphabets, numbers and special characters.");
                        nrPass.requestFocus();
                    }catch(FirebaseAuthInvalidCredentialsException e){
                        nrPass.setError("Your email is invalid or already in use. Kindly re-enter.");
                        nrPass.requestFocus();
                    }catch(FirebaseAuthUserCollisionException e) {
                            nrEmail.setError("User is already registered with this email. Use another email.");
                            nrEmail.requestFocus();
                        }catch (Exception e){
                            Log.e(TAG,e.getMessage());
                            Toast.makeText(LoginUserEmailActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                        progressBar.setVisibility(View.GONE);
                        }
                });

    }

    private String getTodaysDate(){
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month+1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day,month,year);
    }


    private void initDatePicker(){

        nrBdate.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            datepd = new DatePickerDialog(LoginUserEmailActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                    nrBdate.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                }
            },year,month,day);
            datepd.show();
        });

    }
    private  String makeDateString(int day, int month, int year){
        return getMonthFormat(month) +" "+day+" "+year;
    }
    private String getMonthFormat(int month){
        if (month==1)
            return "JAN";
        if (month==2)
            return "FEB";
        if (month==3)
            return "MAR";
        if (month==4)
            return "APR";
        if (month==5)
            return "MAY";
        if (month==6)
            return "JUN";
        if (month==7)
            return "JUL";
        if (month==8)
            return "AUG";
        if (month==9)
            return "SEP";
        if (month==10)
            return "OCT";
        if (month==11)
            return "NOV";
        if (month==12)
            return "DEC";

        return "JAN";
    }

}