package com.emmajerry2016.africlite;

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
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupChatActivity extends AppCompatActivity {

private Toolbar mToolbar;
private ImageButton sendMessageButton;
private EditText userMessageInput;
private ScrollView mScrollView;
private TextView displayTextMessages;
private Button flyButton;
private FirebaseAuth mAuth;
private DatabaseReference userRef,groupNameRef,groupMessagekeyRef;

private String currentGroupName,currentUserId,currentUserName,currentDate,currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);


        //capable of retrieving the current group names in database that are in that group
        currentGroupName=getIntent().getExtras().get("groupName").toString();
        Toast.makeText(GroupChatActivity.this, currentGroupName, Toast.LENGTH_SHORT).show();

        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid();
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");//referencing to users in my db
        groupNameRef=FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName);//referencing to Groups Table in my firebase


        initializeFields();

        getUserInfo();//its a function to get users info from my db

        //this method of sendMessageButton,will send message to db and save it
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMessageInfoToDatabase();

                userMessageInput.setText("");//so this command,sets the editText box to null after messeage have sent
            }
        });
    }

    @Override
    protected void onStart() {

        super.onStart();

        groupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){

                    displayMessages(dataSnapshot);
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if(dataSnapshot.exists()){

                    displayMessages(dataSnapshot);
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void initializeFields() {
      mToolbar=findViewById(R.id.group_chat);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(currentGroupName);

        sendMessageButton=findViewById(R.id.send_message_button);
        userMessageInput=findViewById(R.id.input_group_message);
        displayTextMessages=findViewById(R.id.group_chat_text_display);
        mScrollView=findViewById(R.id.my_scroll_view);

    }


    private void getUserInfo() {
     userRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
      if(dataSnapshot.exists()){
          currentUserName=dataSnapshot.child("name").getValue().toString();//this will get a username from the db

      }

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
  });
    }

    private void saveMessageInfoToDatabase() {

        String message=userMessageInput.getText().toString();
        String messageKey=groupNameRef.push().getKey();//it creates a key for messages i.e unique keys

        if(TextUtils.isEmpty(message)){
            Toast.makeText(GroupChatActivity.this,"Please enter message first",Toast.LENGTH_SHORT).show();
        }
        else{
            Calendar calForDate= Calendar.getInstance(); //this gets the time message was sent
            SimpleDateFormat currentDateFormat=new SimpleDateFormat("MMM dd,YYYY");
            currentDate=currentDateFormat.format(calForDate.getTime());//currentDate will be the Table name in my db

            Calendar calForTime= Calendar.getInstance(); //this gets the time message was sent
            SimpleDateFormat currentTimeFormat=new SimpleDateFormat("hh:mm:ss a");
            currentTime=currentTimeFormat.format(calForTime.getTime());//currentDate will be the Table name in my db

            HashMap<String,Object> groupMessageKey=new HashMap<>();
            groupNameRef.updateChildren(groupMessageKey);
            groupMessagekeyRef=groupNameRef.child(messageKey);

            HashMap<String,Object> messageInfoMap=new HashMap<>();//So this hash map array, collect the messages info and store them in created attribute or Tables as displayed below

            messageInfoMap.put("name",currentUserName);
            messageInfoMap.put("message",message);
            messageInfoMap.put("date",currentDate);
            messageInfoMap.put("time",currentTime);

            groupMessagekeyRef.updateChildren(messageInfoMap);
        }

    }
    private void displayMessages(DataSnapshot dataSnapshot) {

        Iterator iterator=dataSnapshot.getChildren().iterator();

        while(iterator.hasNext()){

            String chatDate=(String)((DataSnapshot)iterator.next()).getValue();
            String chatMessage=(String)((DataSnapshot)iterator.next()).getValue();
            String chatName=(String)((DataSnapshot)iterator.next()).getValue();
            String chatTime=(String)((DataSnapshot)iterator.next()).getValue();

            displayTextMessages.append(chatName + ":\n " + chatMessage + "\n " + chatTime +"       " + chatDate + "\n\n\n\n" );


        }
    }


    }

