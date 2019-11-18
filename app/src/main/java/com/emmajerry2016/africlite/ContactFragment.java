package com.emmajerry2016.africlite;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactFragment extends Fragment {

private  View contactsView;
private RecyclerView myContactList;
private DatabaseReference contactRef,usersRef;
private FirebaseAuth mAuth;

private String currentUserId;

    public ContactFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        contactsView=inflater.inflate(R.layout.fragment_contact, container, false);
        myContactList=contactsView.findViewById(R.id.Contacts_list);
        myContactList.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth=FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            currentUserId=mAuth.getCurrentUser().getUid();
            contactRef= FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserId);
            usersRef=FirebaseDatabase.getInstance().getReference().child("Users");

        }
        else{
            Intent intent = new Intent(getActivity(),LoginActivity.class);
            startActivity(intent);
        }

        return  contactsView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options=
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(contactRef,Contacts.class)
                .build();



       final FirebaseRecyclerAdapter<Contacts,contactViewHolder> adapter=
                new FirebaseRecyclerAdapter<Contacts, contactViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final contactViewHolder holder, int position, @NonNull Contacts model) {

                       final String userIDs=getRef(position).getKey();

                        usersRef.child(userIDs).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if(dataSnapshot.exists()){

                                    if(dataSnapshot.child("userState").hasChild("state")){
                                        String state=dataSnapshot.child("userState").child("state").getValue().toString();
                                        String date=dataSnapshot.child("userState").child("date").getValue().toString();
                                        String time=dataSnapshot.child("userState").child("time").getValue().toString();

                                        if(state.equals("online")){
                                            holder.onlineIcon.setVisibility(View.VISIBLE);
                                            holder.offlineIcon.setVisibility(View.INVISIBLE);

                                        }

                                        else if(state.equals("offline")){
                                            holder.offlineIcon.setVisibility(View.VISIBLE);
                                            holder.onlineIcon.setVisibility(View.INVISIBLE);

                                        }


                                    }
                                    else{

                                        holder.offlineIcon.setVisibility(View.INVISIBLE);
                                        holder.onlineIcon.setVisibility(View.INVISIBLE);
                                    }

                                    if(dataSnapshot.hasChild("image")){

                                        String userImage=dataSnapshot.child("image").getValue().toString();
                                        String profileName=dataSnapshot.child("name").getValue().toString();
                                        String profileStatus=dataSnapshot.child("status").getValue().toString();

                                        holder.userName.setText(profileName);
                                        holder.userStatus.setText(profileStatus);
                                        Picasso.get().load(userImage).placeholder(R.drawable.profile).into(holder.profileImage);
                                    }
                                    else{
                                        String profileName=dataSnapshot.child("name").getValue().toString();
                                        String profileStatus=dataSnapshot.child("status").getValue().toString();

                                        holder.userName.setText(profileName);
                                        holder.userStatus.setText(profileStatus);
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
                    public contactViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                       View view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout,viewGroup,false);

                       contactViewHolder viewHolder=new contactViewHolder(view);
                       return viewHolder;
                    }
                };

        if (mAuth.getCurrentUser() != null) {
            myContactList.setAdapter(adapter);
            adapter.startListening();
        }
        else{
            Intent intent = new Intent(getActivity(),LoginActivity.class);
            startActivity(intent);
        }


    }



    public static class  contactViewHolder extends RecyclerView.ViewHolder{
        TextView userName,userStatus;
        CircleImageView profileImage;
        ImageView onlineIcon;
        ImageView offlineIcon;

        public contactViewHolder(@NonNull View itemView) {
            super(itemView);

            userName=itemView.findViewById(R.id.user_profile_name);
            userStatus=itemView.findViewById(R.id.user_profile_status);
            profileImage= itemView.findViewById(R.id.users_profile_image);
            onlineIcon=itemView.findViewById(R.id.user_online_status);
            offlineIcon=itemView.findViewById(R.id.user_offline_status);


        }
    }
}
