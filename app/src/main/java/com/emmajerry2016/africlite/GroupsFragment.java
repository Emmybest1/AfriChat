package com.emmajerry2016.africlite;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFragment extends Fragment {
private ListView listView;
private View groupFragmentView;
private ArrayAdapter<String> arrayAdapter;
private ArrayList<String> listOfGroups=new ArrayList<>();
private DatabaseReference groupRef;
    public GroupsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       groupFragmentView = inflater.inflate(R.layout.fragment_groups, container, false);

       groupRef= FirebaseDatabase.getInstance().getReference().child("Groups");//Connection to my Groups column/field


       initializeFields();

       retrieveAndDisplayGroups();//Retrieve and display function

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String currentGroupname=adapterView.getItemAtPosition(position).toString();//This helps to get any group name u click,it gets it and display it to string .i.e in readable form.

                Intent groupChatIntent=new Intent(getContext(),GroupChatActivity.class);
                groupChatIntent.putExtra("groupName",currentGroupname);
                startActivity(groupChatIntent);


            }
        });
       return groupFragmentView;
    }


    private void initializeFields() {

        listView=groupFragmentView.findViewById(R.id.list_view);
        arrayAdapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_expandable_list_item_1,listOfGroups);
        listView.setAdapter(arrayAdapter);

    }
    private void retrieveAndDisplayGroups() {
groupRef.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

        Set<String> set=new HashSet<>();
        Iterator iterator=dataSnapshot.getChildren().iterator();

        while(iterator.hasNext()){
          set.add(((DataSnapshot)iterator.next()).getKey());
        }
        listOfGroups.clear();
        listOfGroups.addAll(set);
        arrayAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
});
    }


}
