package com.emmajerry2016.africlite;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsActivity extends AppCompatActivity {
private Toolbar mToolbar;
private EditText searchBar;
private FirebaseUser mFirebaseUser;
private RecyclerView findFriendsRecyclerList;
private DatabaseReference userRef;
private ArrayList<Contacts> contactsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
        findFriendsRecyclerList=findViewById(R.id.findfrieds_recycler_list);
        findFriendsRecyclerList.setLayoutManager(new LinearLayoutManager(this));


        searchBar=findViewById(R.id.search_bar);

        mToolbar=findViewById(R.id.find_friend_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//back button
        getSupportActionBar().setDisplayShowHomeEnabled(true);//back button
        getSupportActionBar().setTitle("find friends");
    }



    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options=new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(userRef,Contacts.class)
                .build();

       FirebaseRecyclerAdapter<Contacts,FindFriendViewHolder> adapter=
               new FirebaseRecyclerAdapter<Contacts,FindFriendViewHolder>(options) {

           // this method gets the image,name and status.then save it to the Firbase recycler adapter array and finally returns the viewHolder
           @Override
           protected void onBindViewHolder(@NonNull FindFriendViewHolder holder, final int position, @NonNull Contacts model) {

               holder.userName.setText(model.getName());
               holder.userStatus.setText(model.getStatus());
               Picasso.get().load(model.getImage()).into(holder.profileImage);


               holder.itemView.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {

                       String visit_userId=getRef(position).getKey();


                       Intent profileIntent=new Intent(FindFriendsActivity.this,ProfileActivity.class);
                       profileIntent.putExtra("visit_userId",visit_userId);
                       startActivity(profileIntent);
                   }
               });
           }

           @NonNull
           @Override
           public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

               View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout,viewGroup,false);
               FindFriendViewHolder viewHolder=new FindFriendViewHolder(view);
               return  viewHolder;
           }
       };
       findFriendsRecyclerList.setAdapter(adapter);
       adapter.startListening();
    }

    public static class FindFriendViewHolder extends RecyclerView.ViewHolder{

        TextView userName,userStatus;
        CircleImageView profileImage;


        public FindFriendViewHolder(@NonNull View itemView) {
            super(itemView);

            userName=itemView.findViewById(R.id.user_profile_name);
            userStatus=itemView.findViewById(R.id.user_profile_status);
            profileImage=itemView.findViewById(R.id.users_profile_image);
        }
    }
}
