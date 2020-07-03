package com.example.myfirstapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.ArrayList;
import java.util.UUID;

public class ViewFriendProfileActivity extends AppCompatActivity {

    private ImageView profilePictureImage;
    private TextView usersName;
    private Button ViewFriendButton;
    private Button ViewCourseButton;
    private RecyclerView FriendRecycerView;
    private RecyclerView CourseRecyclerView;
    private TextView noCourses;
    private Button backButton;
    private TextView noFriends;
    private Button deleteFriend;
    private ArrayList<Course> courses = new ArrayList<>();
    private ArrayList<String> friends = new ArrayList<>();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private static final int IMAGE_REQUEST = 234;
    private String uID = firebaseAuth.getUid();
    private Uri imageUri;
    private StorageTask uploadTask;
    private String url;
    private String usersId;
    private ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_friend_profile);

        //setting up action bar
        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2196f3")));
        actionBar.setTitle("");

        //getting the users id
        usersId = getIntent().getExtras().get("friendUserId").toString();
        DatabaseReference databaseReferenceUsers
                = FirebaseDatabase.getInstance().getReference("users/" + usersId);


        final DatabaseReference databaseReferenceCourses = FirebaseDatabase.getInstance().getReference("courses/" + usersId);

        //profile picture image button.
        storageReference = FirebaseStorage.getInstance().getReference();

        //getting all references
        profilePictureImage = findViewById(R.id.FriendPicImageView);
        FriendRecycerView = findViewById(R.id.FriendsFriendRecyclerView);
        CourseRecyclerView = findViewById(R.id.FriendsCourseRecyclerView);
        ViewFriendButton = findViewById(R.id.FriendsfriendButton);
        ViewCourseButton = findViewById(R.id.FriendscourseButton);
        usersName = findViewById(R.id.FriendNameTextView);
        noCourses = findViewById(R.id.Friendstv_no_data);
        backButton = findViewById(R.id.backButton);
        noFriends = findViewById(R.id.Friendstv_no_data2);
        deleteFriend = findViewById(R.id.unfriendButton);

        FriendRecycerView.setHasFixedSize(true);
        FriendRecycerView.setLayoutManager(new LinearLayoutManager(ViewFriendProfileActivity.this));
        FriendRecycerView.addItemDecoration(new SimpleDividerItemDecoration(ViewFriendProfileActivity.this));
        FriendRecycerView.setVisibility(View.VISIBLE);
        displayFriends();
        CourseRecyclerView.setHasFixedSize(true);
        CourseRecyclerView.setLayoutManager(new LinearLayoutManager(ViewFriendProfileActivity.this));
        CourseRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(ViewFriendProfileActivity.this));

        //get users name and put on screen
        databaseReferenceUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    String name = dataSnapshot.child("name").getValue().toString();
                    usersName.setText(name);

                    //setting up action bar
                    actionBar = getSupportActionBar();
                    actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2196f3")));
                    actionBar.setTitle(name + "'s Profile");

                    url = dataSnapshot.child("imageURL").getValue().toString();
                    if (!user.getImageURL().equals("default")) {
                        Glide.with(ViewFriendProfileActivity.this).load(user.getImageURL()).into(profilePictureImage);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //setting default to show the list of friends
        if (CourseRecyclerView.getVisibility() == View.VISIBLE) {
            FriendRecycerView.setVisibility(View.VISIBLE);
            CourseRecyclerView.setVisibility(View.INVISIBLE);
        }

        //handle friend button
        ViewFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ViewFriendProfileActivity.this, "You clicked the friend button!", Toast.LENGTH_SHORT).show();
                if (CourseRecyclerView.getVisibility() == View.VISIBLE) {
                    CourseRecyclerView.setVisibility(View.INVISIBLE);
                    noCourses.setVisibility(View.INVISIBLE);
                }
                FriendRecycerView.setVisibility(View.VISIBLE);
                displayFriends();
            }
        });

        //handle course button
        ViewCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ViewFriendProfileActivity.this, "You clicked the course button!", Toast.LENGTH_SHORT).show();
                if (FriendRecycerView.getVisibility() == View.VISIBLE) {
                    FriendRecycerView.setVisibility(View.INVISIBLE);
                    noFriends.setVisibility(View.INVISIBLE);
                }
                CourseRecyclerView.setVisibility(View.VISIBLE);
                displayCourses();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        deleteFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: code delete
                Toast.makeText(ViewFriendProfileActivity.this, "You clicked the unfriend button!", Toast.LENGTH_SHORT).show();
                DatabaseReference databaseReferenceCurrentUser = FirebaseDatabase.getInstance().getReference("friends/" + uID + "/" + usersId);
                Query query = databaseReferenceCurrentUser;

                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot d : dataSnapshot.getChildren()) {
                                d.getRef().removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                databaseReferenceCurrentUser.removeValue();

                DatabaseReference databaseReferenceOtherUser = FirebaseDatabase.getInstance().getReference("friends/" + usersId + "/" + uID);
                Query query2 = databaseReferenceOtherUser;

                query2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot d : dataSnapshot.getChildren()) {
                                d.getRef().removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                databaseReferenceOtherUser.removeValue();

                finish();
            }
        });

    }

    private void displayFriends() {
        noCourses.setVisibility(View.INVISIBLE);
        Toast.makeText(ViewFriendProfileActivity.this, "Finding Friends", Toast.LENGTH_SHORT).show();

        //get all friend id's
        Query queryFriends = FirebaseDatabase.getInstance().getReference("friends/" + usersId);

        queryFriends.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        Log.d("TAG", "onDataChange friend id: " + snapshot.getValue());
                        friends.add(snapshot.getValue().toString());

                    }//end of for loop
                    //printing courses to log for debugging purposes
                    for (int i = 0; i < friends.size(); i++) {
                        Log.d("TAG", "displayFriends (loop): " + friends.get(i));
                    }
                }else{
                    noFriends.setVisibility(View.VISIBLE);
                    FriendRecycerView.setVisibility(View.INVISIBLE);
                }
            }//end of snapshot


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });


        FirebaseRecyclerAdapter<String, FriendViewHolder> firebaseRecyclerAdapter2 = new FirebaseRecyclerAdapter<String, FriendViewHolder>(String.class, R.layout.friends_layout, FriendViewHolder.class, queryFriends) {


            @Override
            protected void populateViewHolder(FriendViewHolder friendViewHolder, String s, final int i) {

                Log.d("TAG", "populateViewHolder: friends.size() " + friends.size());

                if (friends.size() == 0) {
                    return;
                }

                Log.d("TAG", "populateViewHolder: " + friends + " i " + i);
                friendViewHolder.setDetails(friends.get(i), ViewFriendProfileActivity.this);

                Log.d("TAG", "populateViewHolder: i " + i);



            }


        };

        FriendRecycerView.setAdapter(firebaseRecyclerAdapter2);
        //firebaseRecyclerAdapter2.notifyDataSetChanged();
        Log.d("TAG", "displayFriends: setting adapter");


    }

    private void displayCourses() {
        noFriends.setVisibility(View.INVISIBLE);
        Toast.makeText(ViewFriendProfileActivity.this, "Finding Your Courses", Toast.LENGTH_LONG).show();
        DatabaseReference databaseReferenceCourses = FirebaseDatabase.getInstance().getReference("courses/" + usersId);
        Query firebaseSearchQuery = databaseReferenceCourses.orderByKey();

        Log.d("TAG", "displayCourses: " + firebaseSearchQuery.toString());


        firebaseSearchQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot d : dataSnapshot.getChildren()) {

                        //get all of the courses (put it into arraylist)
                        courses.add(d.getValue(Course.class));
                        Log.d("TAG", "onDataChange: " + d.getValue(Course.class).getCourseName());

                    }

                    //printing courses to log for debugging purposes
                    for (int i = 0; i < courses.size(); i++) {
                        Log.d("TAG", "displayCourses (loop): " + courses.get(i).getCourseName());
                    }
                } else {
                    noCourses.setVisibility(View.VISIBLE);
                    CourseRecyclerView.setVisibility(View.INVISIBLE);
                }
            }//onDataChange

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseRecyclerAdapter<Course, CourseViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Course, CourseViewHolder>(Course.class, R.layout.courses_layout, CourseViewHolder.class, firebaseSearchQuery) {

            @Override
            protected void populateViewHolder(CourseViewHolder courseViewHolder, Course course, final int i) {

                if(courses.size() == 0){
                    Toast.makeText(ViewFriendProfileActivity.this, "Error loading your courses.\nPlease come back and try again", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.d("TAG", "populateViewHolder: " + courses + " i " + i);
                courseViewHolder.setDetails(courses.get(i), ViewFriendProfileActivity.this);

                Log.d("TAG", "populateViewHolder: i " + i);

                courseViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String courseId = getRef(i).getKey();

                        Intent IntToCourse = new Intent(ViewFriendProfileActivity.this, ViewCourseActivity.class);
                        IntToCourse.putExtra("courseId", courseId);
                        IntToCourse.putExtra("userId", usersId);
                        startActivity(IntToCourse);
                    }
                });

            }
        };

        CourseRecyclerView.setAdapter(firebaseRecyclerAdapter);
        Log.d("TAG", "displayCourse: setting adapter");

    }

}
