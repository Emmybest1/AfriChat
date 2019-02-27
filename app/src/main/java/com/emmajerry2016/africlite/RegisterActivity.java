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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
private Button createButton;
private EditText userEmail,userPassword;
private TextView alreadyHaveAccount;

private FirebaseAuth myAuth;
private DatabaseReference rootRef;
private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

         myAuth=FirebaseAuth.getInstance();

         rootRef= FirebaseDatabase.getInstance().getReference();

        intitialiseFields();

        alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLoginActivity();
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAccount();
            }
        });
    }

    private void createNewAccount() {

        String email=userEmail.getText().toString();
        String password=userPassword.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"please enter your email",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"please enter your password to join AfricLite.",Toast.LENGTH_SHORT).show();
        }
        else
            {
            myAuth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){

                        // adding users id to my firebase acc
                        String currentUser=myAuth.getCurrentUser().getUid();
                        rootRef.child("Users").child(currentUser).setValue("");
                        openMainActivity();

                        Toast.makeText(RegisterActivity.this,"Thanks for complying From jerryjoeTech group",Toast.LENGTH_SHORT).show();

                        loadingBar.dismiss();
                    }
                    else
                        {
                        String message=task.getException().toString();
                        Toast.makeText(RegisterActivity.this, "Your error is:" + message + ".from jerrjoeTech group", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                    loadingBar.setTitle("Creating new account");
                    loadingBar.setMessage("please wait while we create your account from jerrjoeTech group");
                    loadingBar.setCanceledOnTouchOutside(true);
                    loadingBar.show();
                }

            });

        }

    }

    private void intitialiseFields() {
        createButton=findViewById(R.id.register_button);
        userEmail=findViewById(R.id.register_email);
        userPassword=findViewById(R.id.register_password);
        alreadyHaveAccount=findViewById(R.id.already_have_account1);

        loadingBar=new ProgressDialog(this);
    }
    private void openLoginActivity(){
        Intent intent=new Intent(this,LoginActivity.class);
        startActivity(intent);
    }
    private void openMainActivity(){
        Intent intent=new Intent(this,MainActivity.class);
        intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
