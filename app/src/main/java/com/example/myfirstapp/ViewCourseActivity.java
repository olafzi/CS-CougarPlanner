package com.example.myfirstapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewCourseActivity extends AppCompatActivity {

    private TextView courseNameTV;
    private TextView meetingTV;
    private TextView instructorTV;
    private TextView colorTV;
    private Button addcourseButton;
    private Button cancelButton;
    private Button viewTasks;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private String selectedCourseID;
    private String selectedUserId;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_course);

        selectedCourseID = getIntent().getExtras().get("courseId").toString();
        selectedUserId = getIntent().getExtras().get("userId").toString();

        //setting up action bar
        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2196f3")));
        actionBar.setTitle("View Course " + selectedCourseID);

        courseNameTV = findViewById(R.id.NameOfCourseTV);
        meetingTV = findViewById(R.id.MeetingDaysTextView);
        instructorTV = findViewById(R.id.InstructorNameTV);
        colorTV = findViewById(R.id.ColorTextView);
        addcourseButton = findViewById(R.id.addFriendCourseButton);
        cancelButton = findViewById(R.id.cancelButton);
        viewTasks = findViewById(R.id.viewTaskButton);

        //populate data from database to edit text and spinner fields
        this.firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("courses/" + selectedUserId + "/" + selectedCourseID);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String courseName = dataSnapshot.child("courseName").getValue().toString();
                    String meetingDays = dataSnapshot.child("meetingDays").getValue().toString();
                    String instructor = dataSnapshot.child("instructor").getValue().toString();
                    String colors = dataSnapshot.child("color").getValue().toString();

                    courseNameTV.setText(courseName);
                    meetingTV.setText(meetingDays);
                    instructorTV.setText(instructor);
                    colorTV.setText(colors);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        addcourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Toast.makeText(ViewCourseActivity.this, "You clicked the add course button!", Toast.LENGTH_SHORT).show();

                Course course = new Course();
                course.setCourseName(courseNameTV.getText().toString());
                course.setMeetingDays(meetingTV.getText().toString());
                course.setInstructor(instructorTV.getText().toString());
                course.setColor(colorTV.getText().toString());

                final ArrayList<Task> tasks = new ArrayList<>();
                final FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
                final String uid = firebaseAuth.getUid();

                Query query = FirebaseDatabase.getInstance().getReference("tasks/" + selectedUserId).orderByChild("course").equalTo(selectedCourseID);
                Log.d("TAG", "onClick: have query for friends tasks associated with course");
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot d : dataSnapshot.getChildren()) {
                            //tasks.add(d.getValue(Task.class));

                            databaseReference = mDatabase.getReference("tasks/" + uid);
                            Log.d("TAG", "onClick for adding friend tasks associated with course: " + d.getValue(Task.class).getTaskName());
                            databaseReference.child(d.getValue(Task.class).getTaskName()).setValue(d.getValue(Task.class));
                            Log.d("TAG", "onDataChange: tasks name is: " + d.getValue(Task.class));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                databaseReference = mDatabase.getReference("courses/" + uid);
                databaseReference.child(course.getCourseName()).setValue(course);




                //Intent intToHome = new Intent(ViewCourseActivity.this, HomeActivity.class);
                //startActivity(intToHome);
                finish();

            }
        });

        //handle cancel button
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ViewCourseActivity.this, "You clicked the cancel button!", Toast.LENGTH_SHORT).show();

                //Intent intToHome = new Intent(ViewCourseActivity.this, HomeActivity.class);
                //startActivity(intToHome);
                finish();
            }
        });

        viewTasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ViewCourseActivity.this, "Showing you the courses tasks", Toast.LENGTH_SHORT).show();
                Intent intToTask = new Intent(ViewCourseActivity.this, CoursesTasksActivity.class);
                intToTask.putExtra("courseId", selectedCourseID);
                intToTask.putExtra("userId", selectedUserId);
                intToTask.putExtra("showDetailsFlag", false);
                startActivity(intToTask);
            }
        });


    }
}
