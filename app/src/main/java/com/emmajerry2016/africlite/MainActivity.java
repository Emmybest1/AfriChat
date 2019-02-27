package com.emmajerry2016.africlite;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private Toolbar   toolbar;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private TabsAccessorAdapter mTabsAccessorAdapter;

    private FirebaseUser currentUser;
    private FirebaseAuth Auth;
    private DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Auth = FirebaseAuth.getInstance();
        currentUser = Auth.getCurrentUser();
        rootRef= FirebaseDatabase.getInstance().getReference();

        toolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("jerryjoeTech group");
        //getSupportActionBar().setIcon(R.drawable.aff3);

        mViewPager = findViewById(R.id.view_pager);

        mTabLayout = findViewById(R.id.tab_layout);

        mTabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mTabsAccessorAdapter);


        mTabLayout.setupWithViewPager(mViewPager);
    }

    //checking if user is currently having an account or have to create new
    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser == null) {
            sendUserToLoginActivity();
        }
        else{
            VerifyUserExistance();
        }


    }

    private void VerifyUserExistance() {
        //to user ID from my firebase
        String currentUserID=Auth.getCurrentUser().getUid();
        rootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //checking if username is set already
                if((dataSnapshot.child("name").exists())){

                    Toast.makeText(MainActivity.this,"Welcome to AfricLite",Toast.LENGTH_SHORT).show();
                }
                else{
                   sendUserToSettingsActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //This enable you to go to login-signup page
    private void sendUserToLoginActivity() {
        Intent loginActivity = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginActivity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.logout_menu) {
            Auth.signOut();
            sendUserToLoginActivity();

        }
        if (item.getItemId() == R.id.find_friends) {
        }
        if (item.getItemId() == R.id.architecturing_option) {
            sendUserToSettingsActivity();
        }
        if (item.getItemId() == R.id.africa_menu) {
            Intent intent=new Intent(this,AfricanHistory.class);
            startActivity(intent);
           // getIntent().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
           // finish();
        }
        if(item.getItemId()==R.id.developer_option){
            Intent intent=new Intent(this,Developer.class);
            startActivity(intent);
           // getIntent().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
           // finish();
        }
        if(item.getItemId()==R.id.create_group_menu){
            requestNewGroup();

        }
        if(item.getItemId()==R.id.search_engine_option){
           Intent search=new Intent(this,Search_engine.class);
           startActivity(search);
         //  getIntent().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
          // finish();
        }
        if(item.getItemId()==R.id.transfer_chat_option){
           sendUserToTransfer();
        }

    return true;
}

//method to allow users create group chat
    private void requestNewGroup() {
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this,R.style.AlertDialog);
        builder.setTitle("Enter Group Name: ");
        final EditText groupNameField=new EditText(MainActivity.this);
        groupNameField.setHint("e.g:Onah's Family");
        builder.setView(groupNameField);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {

                String groupName=groupNameField.getText().toString();//To get the group name the user entred

                if(TextUtils.isEmpty(groupName)){
                    Toast.makeText(MainActivity.this,"Please provide a group name...",Toast.LENGTH_SHORT).show();

                }
                else{
              //Method for new group
                    createNewGroup(groupName);
                }

            }
        });

        //Setting button for cancelling group name
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.cancel();

            }
        });
        builder.show();
    }
//method for creating new group in my firebase database
    private void createNewGroup(final String groupName) {
        rootRef.child("Groups").child(groupName).setValue("") //So basically,the Groups in quote is the Column name that will hold the attributes of group entred
                .addOnCompleteListener(new OnCompleteListener<Void>() {
    @Override
    public void onComplete(@NonNull Task<Void> task) {
      if (task.isSuccessful()){
          Toast.makeText(MainActivity.this, groupName+ " group is created succefully...",Toast.LENGTH_SHORT).show();
      }
    }
});
    }

    private void sendUserToSettingsActivity(){
        Intent intent=new Intent(this,SettingsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
}
    private void sendUserToMainActivity(){
        Intent intent=new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
}
private void sendUserToTransfer(){
        Intent transfer=new Intent(this,TransferChatActivity.class);
        startActivity(transfer);
}
}