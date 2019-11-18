package com.emmajerry2016.africlite;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private TabsAccessorAdapter mTabsAccessorAdapter;
    private FirebaseAuth Auth;
    private DatabaseReference rootRef;
    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Auth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();

        toolbar = findViewById(R.id.main_page_toolbar);
         setSupportActionBar(toolbar);
         getSupportActionBar().setTitle("AfriChat");
         /**getSupportActionBar().setIcon(R.drawable.logooo);**/
        //Typeface tf = Typeface.createFromAsset(getAssets(), "Asap-Medium.otf");

        mViewPager = findViewById(R.id.view_pager);
        mTabLayout = findViewById(R.id.tab_layout);
        mTabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mTabsAccessorAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        isNetworkConnected();//Internet checker
    }

    //checking if user is currently having an account or have to create new
    @Override
    protected void onStart()
    {

        super.onStart();

        FirebaseUser currentUser=Auth.getCurrentUser();
        if (currentUser == null) {
            sendUserToLoginActivity();

        }
        else
            {

                updateUserStatus("online");// executed when a user comes online that mean for a use r to come online his registered already
                VerifyUserExistance();
        }

    }

    //on stop method tell the users the offline state of the victim so its executed when a user goes offline
    @Override
    protected void onStop() {

        super.onStop();

        FirebaseUser currentUser=Auth.getCurrentUser();
        if(currentUser !=null){
            updateUserStatus("offline");
        }
    }

    //This method is executed when the app destroys or crash,then it tells the users the victims state
    @Override
    protected void onDestroy() {

        super.onDestroy();
        FirebaseUser currentUser=Auth.getCurrentUser();
        if(currentUser !=null){
            updateUserStatus("offline");
        }
    }

    private void VerifyUserExistance() {
        //to user ID from my firebase
        String currentUserID = Auth.getCurrentUser().getUid();

        rootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //checking if username is set already
                if ((dataSnapshot.child("name").exists())) {

                } else {
                    sendUserToSettingsActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendUserToLoginActivity() {
        Intent loginActivity = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginActivity);
    }

    //Drop down menu bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.logout_menu) {

            updateUserStatus("offline");
            Auth.signOut();
            sendUserToLoginActivity();
        }

        if (item.getItemId() == R.id.find_friends) {
            sendUserToFindFriendsActivity();
        }

        if (item.getItemId() == R.id.search_engine) {
            Intent intent=new Intent(this,Search_engine.class);
            startActivity(intent);
        }
        if (item.getItemId() == R.id.architecturing_option) {
            sendUserToSettingsActivity();
        }
        if (item.getItemId() == R.id.transfer_chat) {
            Intent intent=new Intent(MainActivity.this,TransferChat.class);
                    startActivity(intent);
        }
        return true;
    }

    private void sendUserToSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);

    }

    private void sendUserToFindFriendsActivity() {
        Intent intent = new Intent(this, FindFriendsActivity.class);
        startActivity(intent);
    }

    private void updateUserStatus(String state){
        String saveCurrentTime,saveCurrentDate;

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate =new SimpleDateFormat("dd/MM/yyyy");
        saveCurrentDate =currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime =new SimpleDateFormat("hh:mm a");
        saveCurrentTime =currentTime.format(calendar.getTime());

        HashMap<String,Object> onlineStateMap=new HashMap<>();
        onlineStateMap.put("time", saveCurrentTime);
        onlineStateMap.put("date", saveCurrentDate);
        onlineStateMap.put("state", state);

        currentUserID=Auth.getCurrentUser().getUid();

        rootRef.child("Users").child(currentUserID).child("userState")
                .updateChildren(onlineStateMap);


    }

    //Method to check internet connection
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if(cm.getActiveNetworkInfo()==null){
            Toast.makeText(this, "You need internet connection to use AfricLite", Toast.LENGTH_SHORT).show();
        }
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

}