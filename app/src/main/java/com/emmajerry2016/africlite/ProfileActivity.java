package com.emmajerry2016.africlite;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private String receiverUserId,senderUserID,Current_State;
    private CircleImageView userProfileImage;
    private TextView userProfileName,userProfileStatus;
    private Button sendMessageButton,cancelChatButton;
    private DatabaseReference userRef,chatRequestRef,contactRef,notificationReference;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth=FirebaseAuth.getInstance();
        senderUserID=mAuth.getCurrentUser().getUid();//never place your sendUserId before mAuth else it will be bindWidget declaration

        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
        chatRequestRef= FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        contactRef= FirebaseDatabase.getInstance().getReference().child("Contacts");
        notificationReference= FirebaseDatabase.getInstance().getReference().child("Notifications");


        receiverUserId=getIntent().getExtras().get("visit_userId").toString();
        userProfileImage=findViewById(R.id.profile_background);
        userProfileName=findViewById(R.id.user_name);
        userProfileStatus=findViewById(R.id.user_status);
        sendMessageButton=findViewById(R.id.send_text_Button);
        cancelChatButton=findViewById(R.id.decline_chat_Button);

        Current_State="new";

        //method for retrieving user details from the database and displaying it on the activity
        RetrieveUserInfo();

    }

    private void RetrieveUserInfo() {

        userRef.child(receiverUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists() && dataSnapshot.hasChild("image")) {
                    String userImage = dataSnapshot.child("image").getValue().toString();
                    String userName = dataSnapshot.child("name").getValue().toString();
                    String userStatus = dataSnapshot.child("status").getValue().toString();

                    //With playholder,it helps to display user without profile image
                    Picasso.get().load(userImage).placeholder(R.drawable.profile).into(userProfileImage);
                    userProfileName.setText(userName);
                    userProfileStatus.setText(userStatus);

                    ManageChatRequest();
                }

                else if(dataSnapshot.exists() && dataSnapshot.hasChild("name") && dataSnapshot.hasChild("status")){


                    String userName = dataSnapshot.child("name").getValue().toString();
                    String userStatus = dataSnapshot.child("status").getValue().toString();
                    userProfileName.setText(userName);
                    userProfileStatus.setText(userStatus);

                    ManageChatRequest();

                }
                else if(dataSnapshot.exists() && dataSnapshot.hasChild("name")){
                    String userName = dataSnapshot.child("name").getValue().toString();                    userProfileName.setText(userName);

                    ManageChatRequest();

                }
                else{
                    Toast.makeText(ProfileActivity.this, "AfricLite", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void ManageChatRequest() {

        chatRequestRef.child(senderUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild(receiverUserId)){

                            String request_type=dataSnapshot.child(receiverUserId).child("request_type")
                                    .getValue().toString();

                            if(request_type.equals("sent")){

                                Current_State="request_sent";
                                sendMessageButton.setText("Cancel friend Request");

                            }
                            else if(request_type.equals("received")){

                                Current_State="request_received";
                                sendMessageButton.setText("Accept friend Request");

                                cancelChatButton.setVisibility(View.VISIBLE);
                                cancelChatButton.setEnabled(true);

                                cancelChatButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        CancelChatRequest();
                                    }
                                });
                            }
                        }
                        else{
                            contactRef.child(senderUserID)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.hasChild(receiverUserId)){
                                                Current_State="friends";
                                                sendMessageButton.setText("Remove this Contact");
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        if(!senderUserID.equals(receiverUserId)){

            sendMessageButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    sendMessageButton.setEnabled(false);

                    if(Current_State.equals("new")){
                        sendChatRequest();
                    }

                    //This enables the user to cancel chat request
                    if(Current_State.equals("request_sent")){
                        CancelChatRequest();

                    }

                    if(Current_State.equals("request_received")){
                        AcceptChatRequest();

                    }
                    if(Current_State.equals("friends")){
                        removeSpecificContact();

                    }

                }
            });
        }

        //This else command will make the send request button invisible if its the sender's profile
        else {
            sendMessageButton.setVisibility(View.INVISIBLE);
        }

    }
    //Method to remove contacts
    private void removeSpecificContact() {

        contactRef.child(senderUserID).child(receiverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            contactRef.child(receiverUserId).child(senderUserID)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                sendMessageButton.setEnabled(true);
                                                Current_State="new";
                                                sendMessageButton.setText("Send Chat Request");

                                                cancelChatButton.setVisibility(View.INVISIBLE);
                                                cancelChatButton.setEnabled(false);

                                            }

                                        }
                                    });
                        }
                    }
                });


    }


    private void AcceptChatRequest() {

        contactRef.child(senderUserID).child(receiverUserId)
                .child("Contacts").setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            contactRef.child(receiverUserId).child(senderUserID)
                                    .child("Contacts").setValue("Saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>()
                                    {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){

                                                chatRequestRef.child(senderUserID).child(receiverUserId)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if(task.isSuccessful()){
                                                                    chatRequestRef.child(receiverUserId).child(senderUserID)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                                    sendMessageButton.setEnabled(true);
                                                                                    Current_State="friends";
                                                                                    sendMessageButton.setText("Remove this Contact");

                                                                                    cancelChatButton.setVisibility(View.INVISIBLE);
                                                                                    cancelChatButton.setEnabled(false);
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });

                                            }

                                        }
                                    });


                        }

                    }
                });

    }





    private void CancelChatRequest() {
        chatRequestRef.child(senderUserID).child(receiverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            chatRequestRef.child(receiverUserId).child(senderUserID)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                sendMessageButton.setEnabled(true);
                                                Current_State="new";
                                                sendMessageButton.setText("Send Chat Request");

                                                cancelChatButton.setVisibility(View.INVISIBLE);
                                                cancelChatButton.setEnabled(false);

                                            }

                                        }
                                    });
                        }
                    }
                });

    }

    private void sendChatRequest() {
        chatRequestRef.child(senderUserID).child(receiverUserId)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {

                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){
                            chatRequestRef.child(receiverUserId).child(senderUserID)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {

                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){


                                                HashMap<String,String> chatNotificationMap=new HashMap<>();
                                                chatNotificationMap.put("from",senderUserID);
                                                chatNotificationMap.put("type","request");

                                                notificationReference.child(receiverUserId).push()
                                                        .setValue(chatNotificationMap)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if(task.isSuccessful()){

                                                                    sendMessageButton.setEnabled(true);
                                                                    Current_State="sent";
                                                                    sendMessageButton.setText("Cancel Chat Request");

                                                                }
                                                            }
                                                        });
                                            }

                                        }
                                    });
                        }
                    }
                });
    }
}

/****  Group AfricLite ****/