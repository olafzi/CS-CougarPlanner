package com.example.myfirstapp;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {


    private Button ViewFriendButton;
    private Button ViewCourseButton;
    private Button searchFriendButton;
    private Button addCourseButton;
    private Button logoutButton;
    private Button editProfileButton;
    private TextView usersName;
    private TextView noCourses;
    private TextView noFriends;

    private ImageView profilePictureImage;
    private RecyclerView FriendRecycerView;
    private RecyclerView CourseRecyclerView;

    private ArrayList<Course> courses = new ArrayList<>();
    private ArrayList<String> friends = new ArrayList<>();

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private String uid = firebaseAuth.getUid();

    private DatabaseReference databaseReferenceUsers
            = FirebaseDatabase.getInstance().getReference("users/" + uid);

    private DatabaseReference databaseReferenceCourses = FirebaseDatabase.getInstance().getReference("courses/" + uid);

    private StorageReference storageReference;
    private static final int IMAGE_REQUEST = 234;
    private Uri imageUri;
    private StorageTask uploadTask;
    private String url;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //profile picture image button.
        storageReference = FirebaseStorage.getInstance().getReference();

        //getting all references
        profilePictureImage = view.findViewById(R.id.ProfilePicImageView);
        FriendRecycerView = view.findViewById(R.id.FriendRecyclerView);
        CourseRecyclerView = view.findViewById(R.id.CourseRecyclerView);
        searchFriendButton = view.findViewById(R.id.addFriendButton);
        addCourseButton = view.findViewById(R.id.AddCourseButton);
        editProfileButton = view.findViewById(R.id.EditProfileButton); // allow user to switch image with this button
        ViewFriendButton = view.findViewById(R.id.friendButton);
        ViewCourseButton = view.findViewById(R.id.courseButton);
        logoutButton = view.findViewById(R.id.LogoutButton);
        usersName = view.findViewById(R.id.NameTextView);
        noCourses = view.findViewById(R.id.tv_no_data);
        noFriends = view.findViewById(R.id.tv_no_data2);


        FriendRecycerView.setHasFixedSize(true);
        FriendRecycerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        FriendRecycerView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        FriendRecycerView.setVisibility(View.VISIBLE);
        displayFriends();
        CourseRecyclerView.setHasFixedSize(true);
        CourseRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        CourseRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));


        //get users name and put on screen
        databaseReferenceUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    String name = dataSnapshot.child("name").getValue().toString();
                    usersName.setText(name);

                    url = dataSnapshot.child("imageURL").getValue().toString();
                    if (!user.getImageURL().equals("default")) {
                        Glide.with(getContext()).load(user.getImageURL()).into(profilePictureImage);
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

        //handle image button
        profilePictureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "You clicked the profile picture button!", Toast.LENGTH_SHORT).show();
                chooseImage();
                //uploadImage();
            }
        });

        //handle friend button
        ViewFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "You clicked the friend button!", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity(), "You clicked the course button!", Toast.LENGTH_SHORT).show();
                if (FriendRecycerView.getVisibility() == View.VISIBLE) {
                    FriendRecycerView.setVisibility(View.INVISIBLE);
                    noFriends.setVisibility(View.INVISIBLE);
                }
                CourseRecyclerView.setVisibility(View.VISIBLE);
                displayCourses();
            }
        });

        //handle search / add friend button
        searchFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "You clicked the search/add friend button!", Toast.LENGTH_SHORT).show();
                Intent intToAddFriend = new Intent(getActivity(), AddFriendActivity.class);
                startActivity(intToAddFriend);
            }
        });

        //handle add course button
        addCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "You clicked the add course button!", Toast.LENGTH_SHORT).show();
                Intent intToAddCourse = new Intent(getActivity(), AddCourseActivity.class);
                startActivity(intToAddCourse);
            }
        });

        //handle edit profile button
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "You clicked the edit profile button!", Toast.LENGTH_SHORT).show();
                Intent intToEditProfile = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intToEditProfile);
            }
        });

        //handle logout button
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        return view;
    }

    private void displayFriends() {
        noCourses.setVisibility(View.INVISIBLE);
        Toast.makeText(getActivity(), "Finding Your Friends", Toast.LENGTH_SHORT).show();

        //get all friend id's
        Query queryFriends = FirebaseDatabase.getInstance().getReference("friends/" + uid);

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
                friendViewHolder.setDetails(friends.get(i), getContext());

                Log.d("TAG", "populateViewHolder: i " + i);

                friendViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
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

        FriendRecycerView.setAdapter(firebaseRecyclerAdapter2);
        //firebaseRecyclerAdapter2.notifyDataSetChanged();
        Log.d("TAG", "displayFriends: setting adapter");


    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);

    }

    private String getFileExtension(Uri imageUri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        Log.d("TAG", "getFileExtension : " + mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri)));
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }

    private void uploadImage() {

        if (imageUri != null) {

            final ProgressDialog pd = new ProgressDialog(getContext());
            pd.setTitle("Uploading your photo...");
            pd.show();

            final StorageReference fileReference = storageReference.child("images/" + System.currentTimeMillis() + "." + getFileExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String url = uri.toString();
                                    Log.d("TAG", "onSuccess: " + url);
                                    databaseReferenceUsers = FirebaseDatabase.getInstance().getReference("users").child(uid);
                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("imageURL", url);
                                    //Log.d("TAG", "onSuccess: " + mUri);
                                    databaseReferenceUsers.updateChildren(map);
                                }
                            });
                            pd.dismiss();

                            //and displaying a success toast
                            Toast.makeText(getContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            //displaying percentage in progress dialog
                            pd.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        } else {
            Toast.makeText(getActivity(), "No image selected...", Toast.LENGTH_SHORT).show();
        }
    }
    //This is the end of Upload image

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Toast.makeText(getActivity(), "Beginning Upload...", Toast.LENGTH_SHORT).show();

            imageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);
                profilePictureImage.setImageBitmap(bitmap);
                uploadImage();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void displayCourses() {
        noFriends.setVisibility(View.INVISIBLE);
        Toast.makeText(getActivity(), "Finding Your Courses", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(getActivity(), "Error loading your courses.\nPlease come back and try again", Toast.LENGTH_SHORT).show();
                    return;
                }


                Log.d("TAG", "populateViewHolder: " + courses + " i " + i);
                courseViewHolder.setDetails(courses.get(i), getContext());

                Log.d("TAG", "populateViewHolder: i " + i);

                courseViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String courseId = getRef(i).getKey();

                        Intent IntToCourse = new Intent(getActivity(), ViewEditCourseActivity.class);
                        IntToCourse.putExtra("courseId", courseId);
                        startActivity(IntToCourse);
                    }
                });

            }
        };

        CourseRecyclerView.setAdapter(firebaseRecyclerAdapter);
        Log.d("TAG", "displayCourse: setting adapter");

    }


    //signout function called from logout button
    private void signOut() {
        if (FirebaseAuth.getInstance() != null) {
            FirebaseAuth.getInstance().signOut();
            Intent intToLogin = new Intent(getActivity(), LoginActivity.class);
            Toast.makeText(getActivity(), "You are logged out", Toast.LENGTH_SHORT).show();
            startActivity(intToLogin);
        }


    }


}