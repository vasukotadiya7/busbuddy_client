package com.vasukotadiya.bbclient;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.vasukotadiya.bbclient.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity {
    private Button RegButton,VerifyButton;
    private TextView RedirectBtn,ResendOTP;
    private EditText et_Email;
//    private EditText et_Password;
//    private EditText Confirm_Password;
    private EditText et_Phone;
    private EditText et_Name;
    private EditText et_Otp;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;
    private FirebaseUser currentUser;
    private String Phone,Name,Password,Email,confirmPassword;
    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Initialize();
        buttons();
    }


    private void Initialize(){
        progressDialog = new ProgressDialog(this);
        auth = FirebaseAuth.getInstance();

        et_Name = findViewById(R.id.RegName);
        et_Phone = findViewById(R.id.RegPhoneNo);
        et_Email = findViewById(R.id.RegEmailID);
//        et_Password = findViewById(R.id.RegPasswd);
//        Confirm_Password  = findViewById(R.id.RegConformPasswd);
        RegButton = findViewById(R.id.RegButton);
//        et_Otp=findViewById(R.id.et_Otp);
//        VerifyButton=findViewById(R.id.VerifyButton);
//        ResendOTP=findViewById(R.id.ResendOTP);
        RedirectBtn = findViewById(R.id.RedirectLogin);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

    }


    private void  buttons(){

        RegButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Email = et_Email.getText().toString().trim();
//                Password = et_Password.getText().toString().trim();
                Phone = et_Phone.getText().toString().trim();
                Name = et_Name.getText().toString();
//                confirmPassword = Confirm_Password.getText().toString().trim();

//                if(confirmPassword.equals(Password)){
//                if(!Phone.isEmpty() && !Password.isEmpty() && !Name.isEmpty()&& !Email.isEmpty()){
                    if(!Phone.isEmpty() &&  !Name.isEmpty() && !Email.isEmpty()){
                        progressDialog.setTitle("Authenticating");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        setContentView(R.layout.activity_verify_phone);
                        sendVerificationCode(Phone);
                        progressDialog.dismiss();
                        et_Otp=findViewById(R.id.et_Otp);
                        VerifyButton=findViewById(R.id.VerifyButton);
                        ResendOTP=findViewById(R.id.ResendOTP);
                        VerifyButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(TextUtils.isEmpty(et_Otp.getText().toString())){
                                    Toast.makeText(RegisterActivity.this, "Please Enter OTP !", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    verifyCode(et_Otp.getText().toString());
                                }
                            }
                        });
//                        SignIn(Email,Password);
                    }else {
                        Toast.makeText(RegisterActivity.this,"Please fill each box",Toast.LENGTH_SHORT).show();

                    }

//                }else{
//                    Confirm_Password.setError("Password Do Not Match");
//                    Toast.makeText(RegisterActivity.this,"Password do not match",Toast.LENGTH_LONG).show();
//
//                }



            }
        });




        RedirectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });



    }



//    private void SignIn(String email, String password,PhoneAuthCredential credential){
//        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                if(task.isSuccessful()){
//                    Objects.requireNonNull(auth.getCurrentUser()).sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if(task.isSuccessful()){
//                                Toast.makeText(RegisterActivity.this,"Please verify your email address",Toast.LENGTH_SHORT).show();
//                                RegisterUser(Phone,Name,Email,Password,credential);
//                            }else {
//                                Toast.makeText(RegisterActivity.this,"Wrong email address",Toast.LENGTH_SHORT).show();
//                            }
//
//                        }
//                    });
//
//
//                }else {
//                    progressDialog.dismiss();
//                    Toast.makeText(RegisterActivity.this,"Please try again",Toast.LENGTH_SHORT).show();
//
//                }
//
//            }
//        });


//    }
    private void RegisterUser(String Phone,String Name, String Email){
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;
        String Current_Uid = currentUser.getUid();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Users").child(Current_Uid);
        Log.e(TAG, "RegisterPhone: "+ database);
        HashMap<String, String> user = new HashMap<>();
        user.put("Name",Name);
        user.put("Phone",Phone);
        user.put("Email",Email);
        database.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(RegisterActivity.this,"Registered Successfully",Toast.LENGTH_SHORT).show();
//                    AuthCredential credential1= EmailAuthProvider.getCredential(Email,Password);
//                    auth.signInWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            if(task.isSuccessful()){
//                                auth.getCurrentUser().linkWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<AuthResult> task) {
//                                        if(task.isSuccessful()){
//
                                            progressDialog.dismiss();
                                            Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                                            startActivity(intent);
                                            finish();
//                                        }
//                                        else{
//                                            progressDialog.dismiss();
//                                            Log.d("errrrrrroroorrr", "onErrrrrrrrrrorrrrr: "+task.getResult().toString());
//                                            Toast.makeText(RegisterActivity.this, "Linking Failed", Toast.LENGTH_SHORT).show();
//                                        }
//                                    }
//                                });
//                            }
//                            else{
//                                progressDialog.dismiss();
//                                Toast.makeText(RegisterActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });

                }

                else{
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this,"Filed to register info",Toast.LENGTH_SHORT).show();
                }
            }


        });


    }

    private void sendVerificationCode(String phone){
        PhoneAuthOptions options=
                PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber("+91"+phone)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallBack)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String s,PhoneAuthProvider.ForceResendingToken forceResendingToken){
            super.onCodeSent(s,forceResendingToken);

            verificationId =s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            final String code = phoneAuthCredential.getSmsCode();



                et_Otp.setText(code);

                verifyCode(code);

        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d("Errrrrorrrrr",e.getMessage());
        }
    };

    private void verifyCode(String code) {
        progressDialog.show();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);


        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential){


        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    RegisterUser(Phone,Name,Email);
//                SignIn(Email,Password,credential);

                }
                else{
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Failed to Register Phone Number", Toast.LENGTH_SHORT).show();

                }

            }
        });
    }


}