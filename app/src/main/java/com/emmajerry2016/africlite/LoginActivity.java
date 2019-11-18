package com.emmajerry2016.africlite;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {
private ProgressDialog loadingBar;
private FirebaseAuth Auth;
private Button loginButton,phoneButton;
private EditText userEmail,userPassword;
private TextView needNewAccountLink,forgetAccountLink;
private DatabaseReference usersRef;
private CheckBox showPassWord;



            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_login);

                Auth= FirebaseAuth.getInstance();
                usersRef= FirebaseDatabase.getInstance().getReference().child("Users");

                initializeFields();
                needNewAccountLink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openRegisterActivity();
                    }
                });
                loginButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                  AllowUserToLogin();

                    }
                });

                //when user clicks this,it will take the user to phone login activity
                phoneButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openPhoneLoginActivity();
                    }
                });
                }


            private void AllowUserToLogin() {
                String email=userEmail.getText().toString();
                String password=userPassword.getText().toString();

                if(TextUtils.isEmpty(email) && TextUtils.isEmpty(password)){
                    Toast.makeText(this,"enter email & password to continue",Toast.LENGTH_SHORT).show();
                }

                else if(TextUtils.isEmpty(email)){
                    Toast.makeText(this,"enter your email to continue",Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(password)){
                    Toast.makeText(this, "enter password to continue", Toast.LENGTH_SHORT).show();
                }

                else{
        Auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
        if(task.isSuccessful()) {

            String currentUserID=Auth.getCurrentUser().getUid();
            String deviceToken= FirebaseInstanceId.getInstance().getToken();

            usersRef.child(currentUserID).child("device_token")
                    .setValue(deviceToken)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                sendUserToMainAcivity();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
        else{
            loadingBar.setMessage("loading");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            Toast.makeText(LoginActivity.this, "Incorrect password or email", Toast.LENGTH_SHORT).show();
            loadingBar.dismiss();
        }
                    }
                });
                }
                }

            private void initializeFields() {
                loginButton=findViewById(R.id.login_button);
                phoneButton=findViewById(R.id.phone_login_button);
                userEmail=findViewById(R.id.login_email);
                userPassword=findViewById(R.id.password);
                showPassWord=findViewById(R.id.showpass);
                needNewAccountLink=findViewById(R.id.need_new_account);
                forgetAccountLink=findViewById(R.id.forget_password_link);
                loadingBar=new ProgressDialog(this);

                forgetAccountLink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(LoginActivity.this,ResetPasswordActivity.class);
                        startActivity(intent);
                    }
                });

                //To show and hide password
                showPassWord.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                        if (!isChecked) {
                            // show password
                            userPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        } else {
                            // hide password
                            userPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        }
                    }
                });
            }

            private void sendUserToMainAcivity(){
                Intent intent=new Intent(this,MainActivity.class);
                intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            private void openRegisterActivity(){
                Intent intent=new Intent(this,RegisterActivity.class);
                startActivity(intent);
            }
            private void openPhoneLoginActivity(){
                Intent phoneLoginIntent=new Intent(this,PhoneActivity.class);
                startActivity(phoneLoginIntent);
            }
        }
