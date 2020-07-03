package com.example.myfirstapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.drm.DrmStore;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

public class ViewEditTaskActivity extends AppCompatActivity {

    private String selectedTaskID;
    private TextView taskName;
    private Spinner editCoursename;
    private EditText editduedate;
    private Spinner changepriority;
    private EditText editnotes;
    private Button saveTaskButton;
    private Button cancelButton;
    private Button deleteBttn;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private String uid = firebaseAuth.getUid();
    private ArrayList<String> courseNames = new ArrayList<>();
    private CheckBox completeCheck;
    private ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_edit_task);

        selectedTaskID = getIntent().getExtras().get("taskId").toString();

        Toast.makeText(this, "Task id" + selectedTaskID, Toast.LENGTH_SHORT).show();

        //setting up action bar
        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2196f3")));
        actionBar.setTitle("View Task: " + selectedTaskID);

        //getting references
        taskName = findViewById(R.id.TaskNameTextView);
        editCoursename = findViewById(R.id.editTaskCourseName);
        editduedate = findViewById(R.id.editTaskDueDate);
        changepriority = findViewById(R.id.prioritySpinner);
        editnotes = findViewById(R.id.editNotes);
        saveTaskButton = findViewById(R.id.saveTaskButton);
        cancelButton = findViewById(R.id.cancelButton);
        deleteBttn = findViewById(R.id.deleteButton);
        completeCheck = findViewById(R.id.completeCheckBox);

        //populate data from database to edit text and spinner fields
        this.firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("tasks/" + uid + "/" + selectedTaskID);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String course = dataSnapshot.child("course").getValue().toString();
                    String duedate = dataSnapshot.child("dueDate").getValue().toString();
                    String notes = dataSnapshot.child("notes").getValue().toString();
                    String prioritylevel = dataSnapshot.child("priorityLevel").getValue().toString();
                    String taskname = dataSnapshot.child("taskName").getValue().toString();
                    String complete = dataSnapshot.child("completed").getValue().toString();


                    if (complete.equals("true")) {
                        completeCheck.setChecked(true);
                    }
                    editduedate.setText(duedate);
                    taskName.setText(taskname);
                    editnotes.setText(notes);


                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ViewEditTaskActivity.this, R.array.PriorityLevel, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    changepriority.setAdapter(adapter);
                    if (prioritylevel != null) {
                        int spinnerPosition = adapter.getPosition(prioritylevel);
                        changepriority.setSelection(spinnerPosition);
                    }

                    final ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(ViewEditTaskActivity.this, android.R.layout.simple_spinner_item, courseNames);
                    adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    editCoursename.setAdapter(adapter2);
                    if (course != null) {
                        int spinnerPosition = adapter.getPosition(course);
                        editCoursename.setSelection(spinnerPosition);
                    }
                    //fill the course array
                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
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


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //handling users request to add a course
        //When user clicks the "Add Task" button, add the task to the users database and connect to course
        saveTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (taskName.getText().toString().isEmpty()) {
                    taskName.setError("Please enter a task name");
                    taskName.requestFocus();
                }
                if (editduedate.getText().toString().isEmpty() || !editduedate.getText().toString().matches("(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])[- /.](19|20)\\d\\d")) {
                    editduedate.setError("Please the due date in the format MM/DD/YYYY");
                    editduedate.requestFocus();
                }
                if (changepriority.getSelectedItem().toString().equals("Priority Level")) {
                    ((TextView) changepriority.getSelectedView()).setError("Please choose a priority level");
                    changepriority.requestFocus();
                }
                if (!taskName.getText().toString().isEmpty()
                        && (!editduedate.getText().toString().isEmpty() && editduedate.getText().toString().matches("(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])[- /.](19|20)\\d\\d"))
                        && !changepriority.getSelectedItem().toString().equals("Priority Level")) {

                    Toast.makeText(ViewEditTaskActivity.this, "You clicked the save a task button!", Toast.LENGTH_SHORT).show();

                    Task task = new Task();
                    task.setTaskName(taskName.getText().toString());
                    task.setCourse(editCoursename.getSelectedItem().toString());
                    task.setDueDate(editduedate.getText().toString());
                    task.setPriorityLevel(changepriority.getSelectedItem().toString());

                    if (editnotes.getText().toString().isEmpty()) {
                        task.setNotes("No Notes");
                    } else {
                        task.setNotes(editnotes.getText().toString());
                    }


                    databaseReference.child("priorityLevel").setValue(changepriority.getSelectedItem().toString());
                    databaseReference.child("notes").setValue(editnotes.getText().toString());
                    databaseReference.child("dueDate").setValue(editduedate.getText().toString());
                    databaseReference.child("course").setValue(editCoursename.getSelectedItem().toString());

                    if (completeCheck.isChecked()) {
                        databaseReference.child("completed").setValue("true");
                    } else {
                        databaseReference.child("completed").setValue("false");
                    }


                    finish();
                }
            }
        });

        //handle cancel button
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ViewEditTaskActivity.this, "You clicked the cancel button!", Toast.LENGTH_SHORT).show();


                finish();
            }
        });

        //handle delete button
        deleteBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ViewEditTaskActivity.this, "You clicked the delete button!", Toast.LENGTH_SHORT).show();
                databaseReference.removeValue();


                finish();
            }
        });


    }
}
