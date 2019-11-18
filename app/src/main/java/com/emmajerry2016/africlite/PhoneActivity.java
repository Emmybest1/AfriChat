package com.emmajerry2016.africlite;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneActivity extends AppCompatActivity {
private Button verifyButton,sendVerificationButton,backButton;
private EditText phoneNoInput,veriCodeInput;

private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
private String mVerificationId;
private PhoneAuthProvider.ForceResendingToken mResendToken;
private FirebaseAuth mAuth;
private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        backButton=findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToLoginPage();
            }
        });

        mAuth=FirebaseAuth.getInstance();
        loadingBar=new ProgressDialog(this);

        //Find my fields from xml activity to java activity
        verifyButton = findViewById(R.id.verify_button);
        sendVerificationButton = findViewById(R.id.send_veri_code_button);
        phoneNoInput = findViewById(R.id.phone_number_input);
        veriCodeInput = findViewById(R.id.verification_code_input);

        sendVerificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phoneNumber = phoneNoInput.getText().toString();

                if (TextUtils.isEmpty(phoneNumber)) {
                    Toast.makeText(PhoneActivity.this, "provide your phone number to continue", Toast.LENGTH_SHORT).show();
                } else {

                    loadingBar.setTitle("phone number verification");
                    loadingBar.setMessage("verifying....");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            PhoneActivity.this, // Activity (for callback binding)
                            callbacks);        // OnVerificationStateChangedCallbacks

                }
            }
        });


        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerificationButton.setVisibility(View.INVISIBLE);
                phoneNoInput.setVisibility(View.INVISIBLE);

                String verificationCode=veriCodeInput.getText().toString();
                if(TextUtils.isEmpty(verificationCode)){
                    Toast.makeText(PhoneActivity.this,"enter verification code to continue",Toast.LENGTH_SHORT).show();
                }
                else{

                    loadingBar.setTitle("Code verification");
                    loadingBar.setMessage("loading....");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                    signInWithPhoneAuthCredential(credential);
                }

            }
        });



        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                loadingBar.dismiss();
                Toast.makeText(PhoneActivity.this, "invalid number,enter a correct phone number with your country code e.g +234...", Toast.LENGTH_SHORT).show();

                sendVerificationButton.setVisibility(View.VISIBLE);
                phoneNoInput.setVisibility(View.VISIBLE);

                verifyButton.setVisibility(View.INVISIBLE);
                veriCodeInput.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token)
            {

                loadingBar.dismiss();
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                Toast.makeText(PhoneActivity.this,"enter the verification code here",Toast.LENGTH_SHORT).show();

                sendVerificationButton.setVisibility(View.INVISIBLE);
                phoneNoInput.setVisibility(View.INVISIBLE);

                verifyButton.setVisibility(View.VISIBLE);
                veriCodeInput.setVisibility(View.VISIBLE);
            }
        };
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            loadingBar.dismiss();
                            sendUserToMainActivity();
                        }
                        else
                            {
                                String message=task.getException().toString();
                                Toast.makeText(PhoneActivity.this, "incorrect code.", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }

                });
    }

    private void sendUserToMainActivity() {
        Intent intent=new Intent(PhoneActivity.this,MainActivity.class);
        startActivity(intent);
    }
    public void backToLoginPage(){
        Intent intent=new Intent(this,LoginActivity.class);
        startActivity(intent);
    }


}


