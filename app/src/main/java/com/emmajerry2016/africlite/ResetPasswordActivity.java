package com.emmajerry2016.africlite;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {
private EditText resetEmail;
private Button resetButton;
private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);


        resetEmail=findViewById(R.id.resetEmail);
        resetButton=findViewById(R.id.resetButton);

        mFirebaseAuth=FirebaseAuth.getInstance();
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getEmail=resetEmail.getText().toString();

                if(getEmail.equals("")){
                    Toast.makeText(ResetPasswordActivity.this, "insert a valid email to continue with the reset!", Toast.LENGTH_SHORT).show();
                }
                else{
                    mFirebaseAuth.sendPasswordResetEmail(getEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ResetPasswordActivity.this, "Please check your email to continue with the password reset thanks.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ResetPasswordActivity.this,LoginActivity.class));
                            }
                            else{
                                String getErrorMessage=task.getException().getMessage();
                                Toast.makeText(ResetPasswordActivity.this, "error:" + getErrorMessage + ".***AfricLite group***", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            }
        });
    }
}
