package com.vasukotadiya.bbclient;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.vasukotadiya.bbclient.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Button btnLogin,VerifyPhone;
    private TextView RedirectRegister, forgotPassword;
    private EditText et_Phone,et_Otp;
    private ProgressDialog progressDialog,loadingBar;
    private String verificationId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Initialize();
        Buttons();

    }

    private void Initialize() {
        mAuth = FirebaseAuth.getInstance();
        btnLogin = findViewById(R.id.LoginButton);
        et_Phone = findViewById(R.id.Phone);
        RedirectRegister = findViewById(R.id.RedirectRegister);
        progressDialog = new ProgressDialog(this);
    }

    private void Buttons() {
//        forgotPassword.setOnClickListener(View -> ShowForgotPassDialog());
//
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Phone = et_Phone.getText().toString().trim();


                if (!Phone.isEmpty()) {
                    progressDialog.setTitle("Processing");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    setContentView(R.layout.activity_verify_phone);
                    et_Otp=findViewById(R.id.et_Otp);
                    VerifyPhone=findViewById(R.id.VerifyButton);
                    VerifyPhone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            progressDialog.dismiss();
                            String code=et_Otp.getText().toString();
                            if(code!=null){
                                verifyCode(code);
                            }else{

                            Toast.makeText(LoginActivity.this, "Please Enter OTP", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    sendVerificationCode(Phone);
                    progressDialog.dismiss();
                } else {
                    Toast.makeText(LoginActivity.this, "Please Enter Phone Number", Toast.LENGTH_SHORT).show();
                }


            }
        });

        RedirectRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);

            }
        });

    }


//    private void ShowForgotPassDialog() {
//        AlertDialog.Builder builder=new AlertDialog.Builder(this);
//        builder.setTitle("Forgot Password...?").setCancelable(false).setMessage("Please provide your registered email.").setIcon(R.drawable.ic_user);
//        LinearLayout linearLayout=new LinearLayout(this);
//        final EditText editText= new EditText(this);
//
//        // write the email using which you registered
//        editText.setHint("Enter Registered Email");
//        editText.setMinEms(16);
//        editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
//        linearLayout.addView(editText);
//        linearLayout.setPadding(10,10,10,10);
//        builder.setView(linearLayout);
//
//
//        // Click on Recover and a email will be sent to your registered email id
//        builder.setPositiveButton("Reset Password", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                String email = editText.getText().toString().trim();
//                if(!email.isEmpty()){
//                    beginRecovery(email);
//                }else{
////                    editText.setError("Enter Email");
//                    Toast.makeText(LoginActivity.this, "Sorry, You Not provided Email", Toast.LENGTH_LONG).show();
//                }
//
//            }
//        });
//
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        builder.create().show();
//    }
//
//    private void beginRecovery(String email) {
//        loadingBar=new ProgressDialog(this);
//        loadingBar.setMessage("Sending Email....");
//        loadingBar.setCanceledOnTouchOutside(false);
//        loadingBar.show();
//
//
//        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                loadingBar.dismiss();
//                if(task.isSuccessful())
//                {
//                    Toast.makeText(LoginActivity.this,"Email Sent successfully...",Toast.LENGTH_SHORT).show();
//                }
//                else {
//                    Toast.makeText(LoginActivity.this,"Error Occurred",Toast.LENGTH_SHORT).show();
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                loadingBar.dismiss();
//                Toast.makeText(LoginActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
//            }
//        });
//    }

    private void sendVerificationCode(String phone){
        PhoneAuthOptions options=
                PhoneAuthOptions.newBuilder(mAuth)
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
            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d("Errrrrorrrrr",e.getMessage());
        }
    };

    private void verifyCode(String code) {
        progressDialog.show();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);


        Login(credential);
    }

//    private void Login(String phone) {
      private void Login(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                        progressDialog.dismiss();
                        Intent in = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(in);
                        finish();

                } else {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Phone Verification Failed", Toast.LENGTH_SHORT).show();

                }
            }
        });
//        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                if (task.isSuccessful()) {
//                    if (Objects.requireNonNull(mAuth.getCurrentUser()).isEmailVerified()) {
//                        progressDialog.dismiss();
//                        Intent in = new Intent(LoginActivity.this, HomeActivity.class);
//                        startActivity(in);
//                        finish();
//                    } else {
//                        progressDialog.dismiss();
//                        Toast.makeText(LoginActivity.this, "Please verify your email", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    progressDialog.dismiss();
//                    Toast.makeText(LoginActivity.this, "Wrong login credentials", Toast.LENGTH_SHORT).show();
//
//                }
//
//            }
//        });


    }
}