package com.example.biketrackcba;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
                }else{
                    Toast.makeText(LoginUserEmailActivity.this,"Something went wrong!",Toast.LENGTH_LONG).show();

                }
                progressBar1.setVisibility(View.GONE);
            }
        });
    }


    public void loginlayoutbutton(View view){
        setContentView(R.layout.activity_login_user_email);
        nEmail = findViewById(R.id.idemail);
        nPass = findViewById(R.id.idpassw);
        nLogin = findViewById(R.id.nLoginbut);
        progressBar1 = findViewById(R.id.progressBarLogin);
        nAuth = FirebaseAuth.getInstance();
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


    @SuppressLint("MissingInflatedId")
    public void registerlayoutbutton(View view){
        setContentView(R.layout.activity_register_user);


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

        nRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                    Toast.makeText(LoginUserEmailActivity.this, "Please enter your Full Name", Toast.LENGTH_LONG).show();
                    nrName.setError("Full Name is Required");
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
                    radioButtonRegisterGenderSelected.setError("Password is Required");
                    radioButtonRegisterGenderSelected.requestFocus();
                } else if (password.length()<6) {
                    Toast.makeText(LoginUserEmailActivity.this,"Password should be at least 6 digits",Toast.LENGTH_LONG).show();
                    radioButtonRegisterGenderSelected.setError("Password too short");
                    radioButtonRegisterGenderSelected.requestFocus();

                }else {
                    textGender = radioButtonRegisterGenderSelected.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    registerUser(name,email,username,bdate,mobile,textGender,password);
                }

            }
        });
                /**
                database = FirebaseDatabase.getInstance();

                reference= database.getReference("Users");
                String name = nrName.getText().toString();
                String email = nrEmail.getText().toString();
                String username = nrUsername.getText().toString();
                String password = nrPass.getText().toString();
                String bdate = datebutton.getText().toString();

                if (name==null&&email==null&&username==null&&password==null){
                    Toast.makeText(LoginUserEmailActivity.this, "Error Registration, need to provide all details", Toast.LENGTH_SHORT).show();
                }else {
                    HelperClass helperClass = new HelperClass(name, email, username, password, bdate);
                    reference.child(name).setValue(helperClass);
                    Toast.makeText(LoginUserEmailActivity.this, "You have Registered successfully", Toast.LENGTH_SHORT).show();
                    Intent regisdone = new Intent(LoginUserEmailActivity.this, Mainmenu.class);
                    startActivity(regisdone);
                    finish();
                    return;
                }
                **/

        /**

        nRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = nEmail.getText().toString();
                final String password = nPass.getText().toString();

                nAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(LoginUserEmailActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(LoginUserEmailActivity.this, "Register Error", Toast.LENGTH_SHORT).show();
                        }else{
                            String user_id = nAuth.getCurrentUser().getUid();
                            DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child("Bikers").child(user_id);
                            current_user_db.setValue(true);
                        }
                    }
                });
            }
        });


         **/

    }

    private void registerUser(String Fname, String email, String username, String bdate, String mobile, String textGender, String password) {
        nAuth = FirebaseAuth.getInstance();
        nAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(LoginUserEmailActivity.this,
                new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
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
                                /*
                                Intent intent = new Intent(LoginUserEmailActivity.this,UserProfileActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();

                                 */

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

        nrBdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
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