package com.emmajerry2016.africlite;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private String messageReceiverID,messageReceiverName,messageReceiverImage,messageSenderID;
    private TextView userName,userLastSeen;
    private CircleImageView userImage;
    private Toolbar chatToolbar;
    private ImageButton sendMessageButton,sendFilesButton;
    private EditText inputMessageEditText;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef,messagesRef;
    private final List<Messages> messagesList=new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessagesAdapter messageAdapter;
    private RecyclerView userMessagesList;
    private String saveCurrentTime,saveCurrentDate;
    private String currentUserID;
    private String Checker="",myUrl="";
    private StorageTask uploadTask;
    private Uri fileUri;
    private ProgressDialog loadingBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth=FirebaseAuth.getInstance();
        messageSenderID=mAuth.getCurrentUser().getUid();
        rootRef= FirebaseDatabase.getInstance().getReference();
        messagesRef= FirebaseDatabase.getInstance().getReference().child("Messages");
        currentUserID=mAuth.getCurrentUser().getUid();

        messageReceiverID=getIntent().getExtras().get("visit_user_id").toString();
        messageReceiverName=getIntent().getExtras().get("visit_user_name").toString();
        messageReceiverImage=getIntent().getExtras().get("visit_image").toString();


        InitializeController();
        userName.setText(messageReceiverName);
        Picasso.get().load(messageReceiverImage).placeholder(R.drawable.profile).into(userImage);


        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        displayLastSeen();

    }

    private void InitializeController() {
        chatToolbar=findViewById(R.id.chat_toolBar);
        setSupportActionBar(chatToolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater=(LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.custom_chat_bar,null);
        actionBar.setCustomView(actionBarView);


        userImage=findViewById(R.id.custom_profile_image);
        userName=findViewById(R.id.custom_profile_name);
        userLastSeen=findViewById(R.id.custom_userLast_seen);
        sendMessageButton=findViewById(R.id.send_message_btn);
        inputMessageEditText=findViewById(R.id.input_messages);
        messageAdapter=new MessagesAdapter(messagesList);
        userMessagesList=(RecyclerView)findViewById(R.id.private_messages);
        linearLayoutManager=new LinearLayoutManager(this);

        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messageAdapter);

        loadingBar=new ProgressDialog(this);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate =new SimpleDateFormat("dd/MM/yyyy");
        saveCurrentDate =currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime =new SimpleDateFormat("hh:mm a");
        saveCurrentTime =currentTime.format(calendar.getTime());

    }

    //To get image from gallery and save to db

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        //Checking if result code matches with the one we set to be 500
        if(requestCode==483 && resultCode==RESULT_OK && data !=null && data.getData() !=null){

            loadingBar.setTitle("Sending File");
            loadingBar.setMessage("Please wait your image is being sent");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            fileUri = data.getData();//So we get the file type user selects and stores it inside the fileUri type
            //So anything you select will be converted to a Uri and presented as such
            if(!Checker.equals("image")){//That is if the file type is not equals to email,the argument will be executed


            }
            else if(Checker.equals("image")){//Get,save and display to user

                StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("Image Files");

                final String messageSenderRef="Messages/" + messageSenderID + "/" + messageReceiverID;
                final String messageReceiverRef="Messages/" + messageReceiverID + "/" + messageSenderID;

                DatabaseReference userMessageKeyRef=rootRef.child("Messages")
                        .child(messageSenderID).child(messageReceiverID).push();

                final String messagePushID=userMessageKeyRef.getKey();
                final StorageReference filePath=storageReference.child(messagePushID + "." + ".jpg"); //So by using this child,it will reference to the Image Files storage path we created
                uploadTask=filePath.putFile(fileUri);
                uploadTask.continueWith(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {

                        if(!task.isSuccessful()){
                            throw task.getException();
                        }
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task <Uri>task) {

                      if(task.isSuccessful()){
                          Uri downloadUri=task.getResult();
                          myUrl=downloadUri.toString();//to convert the above Uri to String which is presentable format

                          Map messageImageBody=new HashMap();
                          messageImageBody.put("message",myUrl);
                          messageImageBody.put("name",fileUri.getLastPathSegment());
                          messageImageBody.put("type",Checker);
                          messageImageBody.put("from",messageSenderID);
                          messageImageBody.put("to",messageReceiverID);
                          messageImageBody.put("messageID",messagePushID);
                          messageImageBody.put("time",saveCurrentTime);
                          messageImageBody.put("date",saveCurrentDate);

                          Map messageBodyDetails=new HashMap();
                          messageBodyDetails.put(messageSenderRef + "/" + messagePushID,messageImageBody);
                          messageBodyDetails.put(messageReceiverRef + "/" + messagePushID,messageImageBody);


                          rootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                              @Override
                              public void onComplete(@NonNull Task task) {

                                  if(task.isSuccessful()){
                                      Toast.makeText(ChatActivity.this,"sent",Toast.LENGTH_SHORT).show();
                                      loadingBar.dismiss();
                                  }
                                  else{
                                      String message=task.getException().toString();
                                      Toast.makeText(ChatActivity.this,"Error: " + message,Toast.LENGTH_SHORT).show();
                                      loadingBar.dismiss();
                                  }
                                  inputMessageEditText.setText("");
                              }
                          });

                      }
                    }
                });
            }
            else{
                loadingBar.dismiss();
              Toast.makeText(this,"nothing selected",Toast.LENGTH_SHORT).show();
            }
        }
    }


    //method to display users last seen
    private void displayLastSeen(){
        rootRef.child("Users").child(messageReceiverID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.child("userState").hasChild("state")){
                            String state=dataSnapshot.child("userState").child("state").getValue().toString();
                            String date=dataSnapshot.child("userState").child("date").getValue().toString();
                            String time=dataSnapshot.child("userState").child("time").getValue().toString();

                            if(state.equals("online")){
                                userLastSeen.setText("online ");

                            }

                            else if(state.equals("offline")){
                                userLastSeen.setText("last Seen: " + date + " " + time);

                            }


                        }
                        else{ //this is basically for those user who have not updated their app
                            userLastSeen.setText("offline");

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    @Override
    protected void onStart() {
        super.onStart();

        rootRef.child("Messages").child(messageSenderID).child(messageReceiverID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        Messages messages=dataSnapshot.getValue(Messages.class);
                        messagesList.add(messages);

                        messageAdapter.notifyDataSetChanged();

                        userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private  void sendMessage(){

        String messageText=inputMessageEditText.getText().toString();

        if(TextUtils.isEmpty(messageText)){
            Toast.makeText(ChatActivity.this,"chat field can't be empty",Toast.LENGTH_SHORT).show();
        }
        else{
            String messageSenderRef="Messages/" + messageSenderID + "/" + messageReceiverID;
            String messageReceiverRef="Messages/" + messageReceiverID + "/" + messageSenderID;

            DatabaseReference userMessageKeyRef=rootRef.child("Messages")
                    .child(messageSenderID).child(messageReceiverID).push();

            String messagePushID=userMessageKeyRef.getKey();

            Map messageTextBody=new HashMap();
            messageTextBody.put("message",messageText);
            messageTextBody.put("type","text");
            messageTextBody.put("from",messageSenderID);
            messageTextBody.put("to",messageReceiverID);
            messageTextBody.put("messageID",messagePushID);
            messageTextBody.put("time",saveCurrentTime);
            messageTextBody.put("date",saveCurrentDate);

            Map messageBodyDetails=new HashMap();
            messageBodyDetails.put(messageSenderRef + "/" + messagePushID,messageTextBody);
            messageBodyDetails.put(messageReceiverRef + "/" + messagePushID,messageTextBody);


            rootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                    if(task.isSuccessful()){
                        Toast.makeText(ChatActivity.this,"sent",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        String message=task.getException().toString();
                        Toast.makeText(ChatActivity.this,"Error: " + message,Toast.LENGTH_SHORT).show();
                    }
                    inputMessageEditText.setText("");
                }
            });

        }

    }
}
