package com.emmajerry2016.africlite;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatFragment extends Fragment {
    private  View privateChatsView;
    private RecyclerView chatList;
    private DatabaseReference chatRef,usersRef;
    private String currentUserID;
    private FirebaseAuth mAuth;

    public ChatFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        privateChatsView = inflater.inflate(R.layout.fragment_chat, container, false);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            currentUserID = mAuth.getCurrentUser().getUid();
            chatRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
            usersRef = FirebaseDatabase.getInstance().getReference().child("Users");


            chatList = privateChatsView.findViewById(R.id.Chat_list);
            chatList.setLayoutManager(new LinearLayoutManager(getContext()));


        }
        else{
            Intent intent = new Intent(getActivity(),LoginActivity.class);
            startActivity(intent);
        }
        return privateChatsView;
    }
    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options=
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(chatRef,Contacts.class)
                        .build();

        FirebaseRecyclerAdapter<Contacts,chatsViewHolder> adapter=
                new FirebaseRecyclerAdapter<Contacts, chatsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final chatsViewHolder holder, int position, @NonNull Contacts model) {

                        final String userIDs=getRef(position).getKey();
                        final String[] retImage = {"default_image"};

                        usersRef.child(userIDs)
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()){
                                            if(dataSnapshot.hasChild("image")){
                                                retImage[0] =dataSnapshot.child("image").getValue().toString();
                                                Picasso.get().load(retImage[0]).into(holder.profileImage);
                                            }

                                            final  String retName=dataSnapshot.child("name").getValue().toString();
                                            final  String retStatus=dataSnapshot.child("status").getValue().toString();

                                            holder.userName.setText(retName);

                                            if(dataSnapshot.child("userState").hasChild("state")){
                                                String state=dataSnapshot.child("userState").child("state").getValue().toString();
                                                String date=dataSnapshot.child("userState").child("date").getValue().toString();
                                                String time=dataSnapshot.child("userState").child("time").getValue().toString();

                                                if(state.equals("online")){
                                                    holder.userStatus.setText("online ");
                                                }

                                                else if(state.equals("offline")){
                                                    holder.userStatus.setText("Last Seen: " + date + " " + time);
                                                }
                                            }
                                            else{

                                                holder.userStatus.setText("offline");

                                            }

                                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent=new Intent(getContext(),ChatActivity.class);
                                                    intent.putExtra("visit_user_id",userIDs);
                                                    intent.putExtra("visit_user_name",retName);
                                                    intent.putExtra("visit_image", retImage[0]);

                                                    startActivity(intent);
                                                }
                                            });

                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {


                                    }
                                });

                    }

                    @NonNull
                    @Override
                    public chatsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                        View view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout,viewGroup,false);
                        return new chatsViewHolder(view);
                    }
                };
        if (mAuth.getCurrentUser() != null) {
            chatList.setAdapter(adapter);
            adapter.startListening();
        }
        else{
            Intent intent = new Intent(getActivity(),LoginActivity.class);
            startActivity(intent);
        }
    }

    public static class chatsViewHolder extends RecyclerView.ViewHolder{
        CircleImageView profileImage;
        TextView userName,userStatus;


        public chatsViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage=itemView.findViewById(R.id.users_profile_image);
            userName=itemView.findViewById(R.id.user_profile_name);
            userStatus=itemView.findViewById(R.id.user_profile_status);
        }
    }

}