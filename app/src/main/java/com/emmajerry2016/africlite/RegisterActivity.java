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

public class RegisterActivity extends AppCompatActivity {
private Button createButton;
private EditText userEmail,userPassword;
private TextView alreadyHaveAccount;
private FirebaseAuth myAuth;
private DatabaseReference rootRef;
private ProgressDialog loadingBar;
private CheckBox showHidePass;


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
        if(TextUtils.isEmpty(email) && TextUtils.isEmpty(password)){
            Toast.makeText(this,"provide email and password to continue",Toast.LENGTH_LONG).show();
        }

        else if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"provide email to continue",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"provide password to continue",Toast.LENGTH_SHORT).show();
        }

        else
            {
            myAuth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){

                        String deviceToken= FirebaseInstanceId.getInstance().getToken();

                        // adding users id to my firebase acc
                        String currentUser=myAuth.getCurrentUser().getUid();
                        rootRef.child("Users").child(currentUser).setValue("");

                        rootRef.child("Users").child(currentUser).child("device_token")
                                .setValue(deviceToken);

                        openMainActivity();
                        loadingBar.dismiss();
                    }
                    else
                        {
                        String message=task.getException().toString();
                        Toast.makeText(RegisterActivity.this, "error: " + message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                    loadingBar.setTitle("loading");
                    loadingBar.setCanceledOnTouchOutside(true);
                    loadingBar.show();
                    loadingBar.dismiss();
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
        showHidePass=findViewById(R.id.showpassreg);


        //To show and hide password
        showHidePass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
