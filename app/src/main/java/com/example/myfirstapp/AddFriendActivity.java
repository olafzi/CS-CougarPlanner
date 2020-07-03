package com.example.myfirstapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddFriendActivity extends AppCompatActivity {

    private Button backButton;
    private Button searchBttn;
    private EditText searchET;
    private ActionBar actionBar;

    private RecyclerView recyclerView;

    private DatabaseReference mUserDatabase;
    private  TextView nodata;

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private String uid = firebaseAuth.getUid();
    ArrayList<String> friendID = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        //setting up action bar
        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2196f3")));
        actionBar.setTitle("Add a Friend");

        //getting references
        backButton = findViewById(R.id.backButton);
        searchBttn = findViewById(R.id.searchButton);
        searchET = findViewById(R.id.searchField);
        nodata = findViewById(R.id.no_data);

        mUserDatabase = FirebaseDatabase.getInstance().getReference("users");

        //setting up the recycler view for layout
        recyclerView = (RecyclerView) findViewById(R.id.FriendRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));

        Query queryFriends = FirebaseDatabase.getInstance().getReference("friends/" + uid);

        queryFriends.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        friendID.add(snapshot.getValue().toString());
                        Log.d("TAG", "onDataChange friend id: " + snapshot.getValue());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //handle back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AddFriendActivity.this, "You clicked the back button!", Toast.LENGTH_SHORT).show();
                //Intent intToHome = new Intent(AddFriendActivity.this, HomeActivity.class);
                //startActivity(intToHome);
                finish();
            }
        });

        //handle search button
        searchBttn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(AddFriendActivity.this, "You clicked the search button!", Toast.LENGTH_SHORT).show();

                String searchText = searchET.getText().toString();

                firebaseUserSearch(searchText);

            }
        });


    }

    //connect search bar to database for querying when a user wants to search for a friend
    //When user searches for user, and if user exists, then display user
    //display user with the option to send friend request
    private void firebaseUserSearch(String searchText) {

        Toast.makeText(AddFriendActivity.this, "Started Search", Toast.LENGTH_LONG).show();


        Query firebaseSearchQuery = mUserDatabase.orderByChild("name").startAt(searchText).endAt(searchText + "\uf8ff");

        firebaseSearchQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){

                    nodata.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);
                }
                else
                {
                    nodata.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseRecyclerAdapter<User, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, UsersViewHolder>(

                User.class,
                R.layout.searchuser_layout,
                UsersViewHolder.class,
                firebaseSearchQuery

        ) {

            @Override
            protected void populateViewHolder(UsersViewHolder viewHolder, User user, int i) {

                Log.d("TAG", "populateViewHolder: " + user.getName());
                boolean setButton = true;


                if(friendID.contains(user.getEmail())){
                    setButton = false;
                }
                viewHolder.setDetails(user.getName(), user.getEmail(), user.getUniname(), setButton);


            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }

    //View holder class
    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        //setting the details for each user
        public void setDetails(String username, String useremail, String useruni, boolean setButton){

            Log.d("TAG", "setDetails: " + username + useremail + useruni);
            final TextView userName = (TextView) mView.findViewById(R.id.NameTextView);
            final TextView userEmail = (TextView) mView.findViewById(R.id.emailTextView);
            TextView userUni = (TextView) mView.findViewById(R.id.universityTextView);
            Button addFriend = (Button) mView.findViewById(R.id.addButton);
            Button alreadyFriend = (Button) mView.findViewById(R.id.alreadyFriends);

            if(setButton == false){
                addFriend.setVisibility(View.INVISIBLE);
                alreadyFriend.setVisibility(View.VISIBLE);

            }

            addFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("TAG", "onClick: button clicked");
                    Query query = FirebaseDatabase.getInstance().getReference("users/").orderByChild("email").equalTo(userEmail.getText().toString());

                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot d : dataSnapshot.getChildren()) {
                                    Log.d("TAG", "onDataChange: " + d.getKey());

                                    final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                                    final String uid = firebaseAuth.getUid();
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("requests/" + d.getKey());

                                    FriendRequests friendRequest = new FriendRequests(uid);
                                    databaseReference.child("sender").setValue(friendRequest);

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }
            });

            userName.setText(username);
            userEmail.setText(useremail);
            userUni.setText(useruni);

            Log.d("TAG", "setDetails: " + userName.getText().toString() + userEmail.getText().toString() + userUni.getText().toString());
        }




    }
}