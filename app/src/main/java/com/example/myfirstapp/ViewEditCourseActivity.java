package com.example.myfirstapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.drm.DrmStore;
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

public class ViewEditCourseActivity extends AppCompatActivity {

    private TextView courseNameTV;
    private EditText meetingET;
    private EditText instructorET;
    private Spinner spinnerColor;
    private Button savecourseButton;
    private Button cancelButton;
    private Button deleteBttn;
    private Button viewTasks;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private String uid = firebaseAuth.getUid();
    private String selectedCourseID;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_edit_course);

        selectedCourseID = getIntent().getExtras().get("courseId").toString();

        //Toast.makeText(this, "Course id" + selectedCourseID, Toast.LENGTH_SHORT).show();
        Log.d("TAG", "onCreate: " + selectedCourseID);

        //setting up action bar
        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2196f3")));
        actionBar.setTitle("View/Edit Course " + selectedCourseID);

        courseNameTV = findViewById(R.id.NameOfCourseTV);
        meetingET = findViewById(R.id.editMeetingDays);
        instructorET = findViewById(R.id.editInstructorName);
        spinnerColor = findViewById(R.id.ColorSpinner);
        savecourseButton = findViewById(R.id.saveCourseButton);
        cancelButton = findViewById(R.id.cancelButton);
        deleteBttn = findViewById(R.id.deleteButton);
        viewTasks = findViewById(R.id.viewTaskButton);

        //populate data from database to edit text and spinner fields
        this.firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("courses/" + uid + "/" + selectedCourseID);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String courseName = dataSnapshot.child("courseName").getValue().toString();
                    String meetingDays = dataSnapshot.child("meetingDays").getValue().toString();
                    String instructor = dataSnapshot.child("instructor").getValue().toString();
                    String colors = dataSnapshot.child("color").getValue().toString();

                    courseNameTV.setText(courseName);
                    meetingET.setText(meetingDays);
                    instructorET.setText(instructor);


                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ViewEditCourseActivity.this, R.array.Colors, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerColor.setAdapter(adapter);
                    if (colors != null) {
                        int spinnerPosition = adapter.getPosition(colors);
                        spinnerColor.setSelection(spinnerPosition);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        savecourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // add error handling: Make sure all fields are inputted, else ask user to finish filling out form.
                if (instructorET.getText().toString().isEmpty()) {
                    instructorET.setError("Please the instructor name");
                    instructorET.requestFocus();
                }
                if (meetingET.getText().toString().isEmpty()) {
                    meetingET.setError("Please enter the meeting days");
                    meetingET.requestFocus();
                }
                if (spinnerColor.getSelectedItem().toString().equals("Choose a color")) {
                    ((TextView) spinnerColor.getSelectedView()).setError("Please choose a color");
                    spinnerColor.requestFocus();
                }
                if (!instructorET.getText().toString().isEmpty() && !meetingET.getText().toString().isEmpty()
                        && !spinnerColor.getSelectedItem().toString().equals("Choose a color")) {

                    Toast.makeText(ViewEditCourseActivity.this, "You clicked the save a course button!", Toast.LENGTH_SHORT).show();

                    Course course = new Course();
                    course.setMeetingDays(meetingET.getText().toString());
                    course.setInstructor(instructorET.getText().toString());
                    course.setColor(spinnerColor.getSelectedItem().toString());


                    databaseReference.child("meetingDays").setValue(meetingET.getText().toString());
                    databaseReference.child("instructor").setValue(instructorET.getText().toString());
                    databaseReference.child("color").setValue(spinnerColor.getSelectedItem().toString());


                    Intent intToHome = new Intent(ViewEditCourseActivity.this, HomeActivity.class);
                    startActivity(intToHome);
                    //finish();
                }
            }
        });

        //handle cancel button
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ViewEditCourseActivity.this, "You clicked the cancel button!", Toast.LENGTH_SHORT).show();

                Intent intToHome = new Intent(ViewEditCourseActivity.this, HomeActivity.class);
                startActivity(intToHome);
                //finish();
            }
        });

        //handle delete button
        deleteBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ViewEditCourseActivity.this, "You clicked the delete button!", Toast.LENGTH_SHORT).show();

                Query query = FirebaseDatabase.getInstance().getReference("tasks/" + uid).orderByChild("course").equalTo(selectedCourseID);
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

                databaseReference.removeValue();

                finish();
            }
        });

        viewTasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ViewEditCourseActivity.this, "Showing you the courses tasks", Toast.LENGTH_SHORT).show();
                Intent intToTask = new Intent(ViewEditCourseActivity.this, CoursesTasksActivity.class);
                intToTask.putExtra("courseId", selectedCourseID);
                intToTask.putExtra("userId", uid);
                intToTask.putExtra("showDetailsFlag", true);
                startActivity(intToTask);
            }
        });
    }
}
