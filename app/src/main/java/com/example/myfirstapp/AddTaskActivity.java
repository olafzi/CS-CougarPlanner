package com.example.myfirstapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddTaskActivity extends AppCompatActivity {

    private ActionBar actionBar;
    private Spinner PrioritySpinner;
    private Button cancelButton;
    private Button submitTask;
    private EditText TaskNameET;
    private Spinner CourseNameSpinner;
    private EditText DueDateET;
    private EditText NotesET;
    private ArrayList<String> courseNames = new ArrayList<>();
    private FirebaseDatabase mDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        //setting up action bar
        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2196f3")));
        actionBar.setTitle("Add Task");

        //getting references
        cancelButton = findViewById(R.id.cancelButton);
        PrioritySpinner = findViewById(R.id.prioritySpinner);
        submitTask = findViewById(R.id.addTaskButton);
        TaskNameET = findViewById(R.id.editTaskName);
        CourseNameSpinner = findViewById(R.id.editTaskCourseName);
        DueDateET = findViewById(R.id.editTaskDueDate);
        NotesET = findViewById(R.id.editNotes);


        //populate priority spinner with correct data
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(AddTaskActivity.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.PriorityLevel));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        PrioritySpinner.setAdapter(adapter);


        //populate course spinner with correct data
        final ArrayAdapter<String> adapter2 =
                new ArrayAdapter<>(AddTaskActivity.this, android.R.layout.simple_spinner_item, courseNames);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        CourseNameSpinner.setAdapter(adapter2);

        //fill the course array
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String uid = firebaseAuth.getUid();

        DatabaseReference courseRef = FirebaseDatabase.getInstance().getReference("courses/" + uid);

        courseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        Course course = d.getValue(Course.class);
                        courseNames.add(course.getCourseName());
                        adapter2.notifyDataSetChanged();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //When user clicks the "Add Task" button, add the task to the users database and connect to course
        //error handling: ensure a course exists: this is done with the drop down of courses
        submitTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // add error handling: Make sure all fields are inputted, else ask user to finish filling out form.
                if (TaskNameET.getText().toString().isEmpty()) {
                    TaskNameET.setError("Please enter a task name");
                    TaskNameET.requestFocus();
                }
                if (DueDateET.getText().toString().isEmpty() || !DueDateET.getText().toString().matches("(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])[- /.](19|20)\\d\\d")) {
                    DueDateET.setError("Please the due date in the format MM/DD/YYYY");
                    DueDateET.requestFocus();
                }
                if (PrioritySpinner.getSelectedItem().toString().equals("Priority Level")) {
                    ((TextView) PrioritySpinner.getSelectedView()).setError("Please choose a priority level");
                    PrioritySpinner.requestFocus();
                }
                if (!TaskNameET.getText().toString().isEmpty()
                        && (!DueDateET.getText().toString().isEmpty() && DueDateET.getText().toString().matches("(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])[- /.](19|20)\\d\\d"))
                        && !PrioritySpinner.getSelectedItem().toString().equals("Priority Level")) {

                    Toast.makeText(AddTaskActivity.this, "You clicked the submit a task button!", Toast.LENGTH_SHORT).show();

                    Task task = new Task();
                    task.setTaskName(TaskNameET.getText().toString());
                    task.setCourse(CourseNameSpinner.getSelectedItem().toString());
                    task.setDueDate(DueDateET.getText().toString());
                    task.setPriorityLevel(PrioritySpinner.getSelectedItem().toString());

                    if (NotesET.getText().toString().isEmpty()) {
                        task.setNotes("No Notes");
                    } else {
                        task.setNotes(NotesET.getText().toString());
                    }

                    task.setCompleted("false");

                    String uid = firebaseAuth.getUid();
                    mDatabase = FirebaseDatabase.getInstance();
                    databaseReference = mDatabase.getReference("tasks/" + uid);
                    databaseReference.child(task.getTaskName()).setValue(task);
                    Log.d(null, "addTask: task course name: " + task.getCourse());
                    finish();
                }
            }
        });

        //handle cancel button
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AddTaskActivity.this, "You clicked the cancel button!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
