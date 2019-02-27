package com.emmajerry2016.africlite;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

private ProgressDialog loadingBar;
private FirebaseAuth Auth;
private Button loginButton,phoneButton;
private EditText userEmail,userPassword;
private TextView needNewAccountLink,forgetAccountLink;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Auth= FirebaseAuth.getInstance();
        //currentUser=Auth.getCurrentUser();
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
        }

    private void AllowUserToLogin() {
        String email=userEmail.getText().toString();
        String password=userPassword.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"please enter your email",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"please enter your password to join AfricLite.",Toast.LENGTH_SHORT).show();
        }

        else{
Auth.signInWithEmailAndPassword(email,password)
        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
if(task.isSuccessful()){
    sendUserToMainAcivity();
    Toast.makeText(LoginActivity.this,"Logged successfully from jerryJoeTech group",Toast.LENGTH_SHORT).show();
    loadingBar.dismiss();
}
else{
    loadingBar.setTitle("signing pls wait from jerryJoeTech group");
    loadingBar.setMessage("please wait,your will be sign in from jerrJoeTech group");
    loadingBar.setCanceledOnTouchOutside(true);
    loadingBar.show();

    String message=task.getException().toString();
    Toast.makeText(LoginActivity.this, "Your error is:" + message + ".from jerrjoeTech group", Toast.LENGTH_SHORT).show();

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
        needNewAccountLink=findViewById(R.id.need_new_account);
        forgetAccountLink=findViewById(R.id.forget_password_link);
        loadingBar=new ProgressDialog(this);
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
}
