package com.emmajerry2016.africlite;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.w3c.dom.Text;

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
    private StorageReference userProfileImagesRef;
    private ProgressDialog loadingBar;
    private Toolbar settingsToolbar;
    private DataSnapshot mdataSnapshot;


    private static  final int galleryPick=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid();

        rootRef= FirebaseDatabase.getInstance().getReference();
        userProfileImagesRef= FirebaseStorage.getInstance().getReference().child("Profile images");
        //userName.setVisibility(View.INVISIBLE);
        initializeFields();

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSettings();
                retrieveUserInfo();
            }
        });

        retrieveUserInfo();//method for user updated info retrieval

        //setting of profile picture from your gallery
        setProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent=new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,galleryPick);
            }
        });
    }


    private void initializeFields() {

        updateButton=findViewById(R.id.update_settings_buttons);
        userStatus=findViewById(R.id.set_profile_status);
        userName=findViewById(R.id.set_user_name);
        setProfileImage=findViewById(R.id.set_profile_image);
        loadingBar=new ProgressDialog(this);

        settingsToolbar=findViewById(R.id.settings_toolbar);
        setSupportActionBar(settingsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Settings");

    }

    //this get the result of that image
    @Override
    protected void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == galleryPick && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

              //Loading bar
            if (resultCode == RESULT_OK) {
                loadingBar.setTitle("loading");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                Uri resultUri = result.getUri();//using uri to get the picture first

                // For current profile update
                StorageReference filePath = userProfileImagesRef.child(currentUserId + ".jpg");// with this, it will be able to replace user curent image by deleting the old one

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()) {
                            final String downloadUrl = task.getResult().getDownloadUrl().toString();

                            //This references our app to the firebase and it stores the current user image in the unique key image
                            rootRef.child("Users").child(currentUserId).child("image")
                                    .setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        retrieveUserProfileImageAfterSettingIt();//To keep user image after setting it
                                        loadingBar.dismiss();
                                    } else {
                                        String message = task.getException().toString();
                                        Toast.makeText(SettingsActivity.this, "Could not upload your profile!", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();

                                    }
                                }
                            });
                        } else {
                            String message = task.getException().toString();
                            Toast.makeText(SettingsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                });

            }
        }
    }


    private void updateSettings() {
        String setUser=userName.getText().toString();
        String setStatus=userStatus.getText().toString();

        if(TextUtils.isEmpty(setUser)){
            Toast.makeText(SettingsActivity.this,"input username",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(setStatus)){
            Toast.makeText(SettingsActivity.this,"input status",Toast.LENGTH_SHORT).show();
        }

        else{

            HashMap<String,Object> profileMap=new HashMap<>();
            profileMap.put("uid",currentUserId);
            profileMap.put("name",setUser);
            profileMap.put("status",setStatus);

            rootRef.child("Users").child(currentUserId).updateChildren(profileMap)
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




    //To retrieve user profile updated info
    private void retrieveUserInfo() {
        rootRef.child("Users").child(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if((dataSnapshot.exists())&& (dataSnapshot.hasChild("name")&& (dataSnapshot.hasChild("image")) &&(dataSnapshot.hasChild("status")))){
                            String retrieveUserName=dataSnapshot.child("name").getValue().toString();
                            String retrieveStatus=dataSnapshot.child("status").getValue().toString();
                            String retrieveProfilePicture=dataSnapshot.child("image").getValue().toString();

                            //to retrieve and display ur username in ur profile setting
                            userName.setText(retrieveUserName);

                            //to retrieve ur user status in ur setting
                            userStatus.setText(retrieveStatus);

                            Picasso.get().load(retrieveProfilePicture).into(setProfileImage);
                        }

                        else if((dataSnapshot.exists())&& (dataSnapshot.hasChild("name")) && (dataSnapshot.hasChild(("status")))){

                            String retrieveUserName=dataSnapshot.child("name").getValue().toString();
                            String retrieveStatus=dataSnapshot.child("status").getValue().toString();

                            //to retrieve and display ur username in ur profile setting
                            userName.setText(retrieveUserName);

                            //to retrieve ur user status in ur setting
                            userStatus.setText(retrieveStatus);
                        }
                        else{
                            userName.setVisibility(View.VISIBLE);
                            Toast.makeText(SettingsActivity.this, "update profile to continue", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    //To retrieve user profile image only
    private void retrieveUserProfileImageAfterSettingIt() {
        rootRef.child("Users").child(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if((dataSnapshot.exists())&&(dataSnapshot.hasChild("image"))){

                            String retrieveProfilePicture=dataSnapshot.child("image").getValue().toString();


                            Picasso.get().load(retrieveProfilePicture).into(setProfileImage);
                        }

                        else{

                            Toast.makeText(SettingsActivity.this,"Couldn't fetch your image " ,Toast.LENGTH_LONG).show();
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