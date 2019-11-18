package com.emmajerry2016.africlite;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;


public class RequestFragment extends Fragment {

private  View requestFragmentView;
private RecyclerView myRequestList;
private DatabaseReference chatRequestRef,usersReference,contactRef;
private FirebaseAuth mAuth;
private String currentUserID;

    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        requestFragmentView= inflater.inflate(R.layout.fragment_request, container, false);

        mAuth=FirebaseAuth.getInstance();
        currentUserID=mAuth.getCurrentUser().getUid();
        usersReference=FirebaseDatabase.getInstance().getReference().child("Users");
        chatRequestRef= FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        contactRef=FirebaseDatabase.getInstance().getReference().child("Contacts");
        myRequestList=requestFragmentView.findViewById(R.id.Chat_Request_List);
        myRequestList.setLayoutManager(new LinearLayoutManager(getContext()));




       return requestFragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options=
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(chatRequestRef.child(currentUserID),Contacts.class)
                .build();


        FirebaseRecyclerAdapter<Contacts, RequestViewHolder> adapter=
                new FirebaseRecyclerAdapter<Contacts, RequestViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final RequestViewHolder holder, int position, @NonNull Contacts model) {

                        holder.itemView.findViewById(R.id.request_accept_button).setVisibility(View.VISIBLE);
                        holder.itemView.findViewById(R.id.request_cancel_button).setVisibility(View.VISIBLE);

                        final String listUserId=getRef(position).getKey();

                        DatabaseReference getTypeRef=getRef(position).child("request_type").getRef();

                        getTypeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                              if(dataSnapshot.exists()){

                                  String type=dataSnapshot.getValue().toString();

                                  if(type.equals("received")){

                                      usersReference.child(listUserId).addValueEventListener(new ValueEventListener() {
                                          @Override
                                          public void onDataChange(DataSnapshot dataSnapshot) {

                                              if(dataSnapshot.hasChild("image")){

                                                  final String requestProfileImage=dataSnapshot.child("image").getValue().toString();

                                                  Picasso.get().load(requestProfileImage).into(holder.profileImage);
                                              }

                                              final String requestUserName=dataSnapshot.child("name").getValue().toString();
                                              final String requestUserStatus=dataSnapshot.child("status").getValue().toString();

                                              holder.userName.setText(requestUserName);
                                              holder.userStatus.setText("wants to connect with you");//this status tells the receiver that you want to connect with him/her


                                              holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                  @Override
                                                  public void onClick(View v) {

                                                      CharSequence options[]=new CharSequence[]{

                                                              "Accept",
                                                              "Decline"
                                                      };

                                                      AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                                                      builder.setTitle(requestUserName + " Chat Request");//this display the title activity_chat request and the userName of the person requesting

                                                      builder.setItems(options, new DialogInterface.OnClickListener() {
                                                          @Override
                                                          public void onClick(DialogInterface dialogInterface, int i) {

                                                              if(i==0){
                                                                  contactRef.child(currentUserID).child(listUserId).child("Contact").setValue("Saved")
                                                                          .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                              @Override
                                                                              public void onComplete(@NonNull Task<Void> task) {

                                                                                  if(task.isSuccessful()){

                                                                                      contactRef.child(listUserId).child(currentUserID).child("Contact").setValue("Saved")
                                                                                              .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                  @Override
                                                                                                  public void onComplete(@NonNull Task<Void> task) {

                                                                                                      if(task.isSuccessful()){

                                                                                                          chatRequestRef.child(currentUserID).child(listUserId)
                                                                                                                  .removeValue()
                                                                                                                  .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                      @Override
                                                                                                                      public void onComplete(@NonNull Task<Void> task) {

                                                                                                                          if(task.isSuccessful()){
                                                                                                                              chatRequestRef.child(listUserId).child(currentUserID)
                                                                                                                                      .removeValue()
                                                                                                                                      .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                          @Override
                                                                                                                                          public void onComplete(@NonNull Task<Void> task) {

                                                                                                                                              if(task.isSuccessful()){

                                                                                                                                                  Toast.makeText(getContext(),"New Contact Added",Toast.LENGTH_SHORT).show();

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
                                                                          });

                                                              }

                                                              if(i==1){
                                                                  chatRequestRef.child(currentUserID).child(listUserId)
                                                                          .removeValue()
                                                                          .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                              @Override
                                                                              public void onComplete(@NonNull Task<Void> task) {

                                                                                  if(task.isSuccessful()){
                                                                                      chatRequestRef.child(listUserId).child(currentUserID)
                                                                                              .removeValue()
                                                                                              .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                  @Override
                                                                                                  public void onComplete(@NonNull Task<Void> task) {

                                                                                                      if(task.isSuccessful()){

                                                                                                          Toast.makeText(getContext(),"Contact Deleted",Toast.LENGTH_SHORT).show();

                                                                                                      }

                                                                                                  }
                                                                                              });
                                                                                  }

                                                                              }
                                                                          });

                                                              }



                                                          }
                                                      });
                                                      builder.show();


                                                  }
                                              });
                                          }

                                          @Override
                                          public void onCancelled(DatabaseError databaseError) {

                                          }
                                      });
                                  }

                                  else if(type.equals("sent")){
                                      Button request_sent_btn = holder.itemView.findViewById(R.id.request_accept_button);
                                      request_sent_btn.setText("Req Sent");

                                      holder.itemView.findViewById(R.id.request_cancel_button).setVisibility(View.INVISIBLE);


                                      usersReference.child(listUserId).addValueEventListener(new ValueEventListener() {
                                          @Override
                                          public void onDataChange(DataSnapshot dataSnapshot) {

                                              if(dataSnapshot.hasChild("image")){

                                                  final String requestProfileImage=dataSnapshot.child("image").getValue().toString();

                                                  Picasso.get().load(requestProfileImage).into(holder.profileImage);
                                              }
                                              else if(dataSnapshot.hasChild("name")&& dataSnapshot.hasChild("status")) {

                                                  final String requestUserName = dataSnapshot.child("name").getValue().toString();
                                                  final String requestUserStatus = dataSnapshot.child("status").getValue().toString();

                                                  holder.userName.setText(requestUserName);
                                                  holder.userStatus.setText("you have sent request to: " + requestUserName);//this status tells the sender they have sent request to so so
                                              }

                                              holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                  @Override
                                                  public void onClick(View v) {

                                                      CharSequence options[]=new CharSequence[]{

                                                           "Cancel Chat Request"
                                                      };

                                                      AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                                                      builder.setTitle("Already Sent Request");//this display the title activity_chat request and the userName of the person requesting

                                                      builder.setItems(options, new DialogInterface.OnClickListener() {
                                                          @Override
                                                          public void onClick(DialogInterface dialogInterface, int i) {

                                                              if(i==0){ //That is if friend request sender click the initialised character which is "cancel activity_chat request,"it will execute the below program or parameters which is basically cancel
                                                                  chatRequestRef.child(currentUserID).child(listUserId)
                                                                          .removeValue()
                                                                          .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                              @Override
                                                                              public void onComplete(@NonNull Task<Void> task) {

                                                                                  if(task.isSuccessful()){
                                                                                      chatRequestRef.child(listUserId).child(currentUserID)
                                                                                              .removeValue()
                                                                                              .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                  @Override
                                                                                                  public void onComplete(@NonNull Task<Void> task) {

                                                                                                      if(task.isSuccessful()){

                                                                                                          Toast.makeText(getContext(),"You have cancelled the request",Toast.LENGTH_SHORT).show();

                                                                                                      }

                                                                                                  }
                                                                                              });
                                                                                  }

                                                                              }
                                                                          });

                                                              }



                                                          }
                                                      });
                                                      builder.show();


                                                  }
                                              });
                                          }

                                          @Override
                                          public void onCancelled(DatabaseError databaseError) {

                                          }
                                      });

                                  }
                              }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                        View view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout,viewGroup,false);
                        RequestViewHolder holder=new RequestViewHolder(view);
                        return holder;
                    }
                };
        myRequestList.setAdapter(adapter);
        adapter.startListening();
    }


    public static class RequestViewHolder extends  RecyclerView.ViewHolder{

        TextView userName,userStatus;
        CircleImageView profileImage;
        Button acceptButton,declineButton;


        public RequestViewHolder(@NonNull View itemView) {

            super(itemView);

            userName=itemView.findViewById(R.id.user_profile_name);
            userStatus=itemView.findViewById(R.id.user_profile_status);
            profileImage=itemView.findViewById(R.id.users_profile_image);
            acceptButton=itemView.findViewById(R.id.request_accept_button);
            declineButton=itemView.findViewById(R.id.request_cancel_button);
        }
    }
}
