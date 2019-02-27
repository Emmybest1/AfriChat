package com.emmajerry2016.africlite;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    private Button updateButton;
    private EditText userName;
    private EditText userStatus;
    private CircleImageView setProfileImage;
    private String currentUserId;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid();
        rootRef= FirebaseDatabase.getInstance().getReference();


        //userName.setVisibility(View.INVISIBLE);
        initializeFields();

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSettings();
            }
        });
        retrieveUserInfo();//method for user updated info retrieval
    }


    private void initializeFields() {

        updateButton=findViewById(R.id.update_settings_buttons);
        userStatus=findViewById(R.id.set_profile_status);
        userName=findViewById(R.id.set_user_name);
        setProfileImage=findViewById(R.id.set_profile_image);
    }
    private void updateSettings()
    {
     String setUser=userName.getText().toString();
     String setStatus=userStatus.getText().toString();

     if(TextUtils.isEmpty(setUser)){
         Toast.makeText(SettingsActivity.this,"please provide a user_name",Toast.LENGTH_SHORT).show();
     }
        if(TextUtils.isEmpty(setStatus)){
            Toast.makeText(SettingsActivity.this,"please write your status",Toast.LENGTH_SHORT).show();
        }

        else{
            //To save data to firebase using Hashmap encryption
            HashMap<String,String> profileMap=new HashMap<>();
            profileMap.put("uid",currentUserId);
            profileMap.put("name",setUser);
            profileMap.put("status",setStatus);

            rootRef.child("Users").child(currentUserId).setValue(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                         if(task.isSuccessful()){
                             sendUserToMainAcivity();
                             Toast.makeText(SettingsActivity.this,"Profile successfully updated",Toast.LENGTH_SHORT).show();
                         }
                         else{
                             String message=task.getException().toString();
                             Toast.makeText(SettingsActivity.this,"Error:" + message,Toast.LENGTH_SHORT).show();
                         }
                        }
                    });


        }

    }
    //To retriev user profile updated info
    private void retrieveUserInfo() {
        rootRef.child("Users").child(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if((dataSnapshot.exists())&& (dataSnapshot.hasChild("name")&& (dataSnapshot.hasChild("image")))){
                            String retrieveUserName=dataSnapshot.child("name").getValue().toString();
                            String retrieveStatus=dataSnapshot.child("status").getValue().toString();
                            String retrieveProfilePicture=dataSnapshot.child("picture").getValue().toString();

                            //to retrieve and display ur username in ur profile setting
                            userName.setText(retrieveUserName);

                            //to retrieve ur user status in ur setting
                            userStatus.setText(retrieveStatus);
                        }

                        else if((dataSnapshot.exists())&& (dataSnapshot.hasChild("name"))){

                            String retrieveUserName=dataSnapshot.child("name").getValue().toString();
                            String retrieveStatus=dataSnapshot.child("status").getValue().toString();

                            //to retrieve and display ur username in ur profile setting
                            userName.setText(retrieveUserName);

                            //to retrieve ur user status in ur setting
                            userStatus.setText(retrieveStatus);
                        }
                        else{
                            userName.setVisibility(View.VISIBLE);
                            Toast.makeText(SettingsActivity.this, "Please set & update your profile information", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
    private void sendUserToMainAcivity(){
        Intent intent=new Intent(this,MainActivity.class);
        intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}