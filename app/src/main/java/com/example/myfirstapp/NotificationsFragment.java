package com.example.myfirstapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.sql.Struct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NotificationsFragment extends Fragment {

    RecyclerView recyclerView;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private String uid = firebaseAuth.getUid();
    private ArrayList<FriendRequests> friendRequests = new ArrayList<>();
    private TextView noData;


    public static NotificationsFragment newInstance() {
        return new NotificationsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.notifications_fragment, container, false);

        recyclerView = v.findViewById(R.id.NotificationRecyclerView);
        noData = v.findViewById(R.id.no_notifications);


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        recyclerView.setVisibility(View.VISIBLE);
        displayNotifications();

        return v;
    }

    private void displayNotifications() {

        Query queryFriends = FirebaseDatabase.getInstance().getReference("requests/" + uid);
        Log.d("TAG", "displayNotifications: HERE");

        queryFriends.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Log.d("TAG", "onDataChange: HERE");
                        Log.d("TAG", "onDataChange: " + snapshot.getValue(FriendRequests.class).getSenderID());
                        //friendRequests.put(snapshot.getKey(), snapshot.getValue().toString());
                        friendRequests.add(snapshot.getValue(FriendRequests.class));

                    }//end of for loop
                    //printing courses to log for debugging purposes
                    for(int i = 0; i < friendRequests.size(); i++){
                        Log.d("TAG", "onDataChange (loop): " + friendRequests.get(i).getSenderID());
                    }
                }else{
                    noData.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);
                }
            }//end of snapshot


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

       FirebaseRecyclerAdapter<FriendRequests, NotificationViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<FriendRequests, NotificationViewHolder>(FriendRequests.class, R.layout.notification_layout, NotificationViewHolder.class, queryFriends) {


            @Override
            protected void populateViewHolder(NotificationViewHolder notificationViewHolder, FriendRequests s, final int i) {

                Log.d("TAG", "populateViewHolder: friends.size() " + friendRequests.size());

                if (friendRequests.size() == 0) {
                    return;
                }

                Log.d("TAG", "populateViewHolder: " + friendRequests + " i " + i);
                notificationViewHolder.setDetails(friendRequests.get(i), getContext());

                Log.d("TAG", "populateViewHolder: i " + i);

                notificationViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String friendUserId = getRef(i).getKey();
                        Log.d("TAG", "onClick: " + getRef(i).getKey());

                        Intent IntToFriendProfile = new Intent(getActivity(), ViewFriendProfileActivity.class);
                        IntToFriendProfile.putExtra("friendUserId", friendUserId);
                        startActivity(IntToFriendProfile);
                    }
                });

            }


        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
        Log.d("TAG", "displayFriends: setting adapter");



    }// end display

}


