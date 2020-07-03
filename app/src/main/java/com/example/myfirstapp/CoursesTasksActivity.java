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
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CoursesTasksActivity extends AppCompatActivity {

    private ActionBar actionBar;
    private String selectedCourseID;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private String uid;// = firebaseAuth.getUid();
    private String showDetailsFlag;
    private ArrayList<Task> tasks = new ArrayList<>();
    private TextView noTasks;
    private RecyclerView recyclerView;
    private Button backButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses_tasks);

        selectedCourseID = getIntent().getExtras().get("courseId").toString();
        uid = getIntent().getExtras().get("userId").toString();
        showDetailsFlag = getIntent().getExtras().get("showDetailsFlag").toString();

        //setting up action bar
        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2196f3")));
        actionBar.setTitle("View Course " + selectedCourseID + "'s Tasks");

        //getting references
        noTasks = findViewById(R.id.tv_no_data);
        recyclerView = findViewById(R.id.CoursesTasksRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(CoursesTasksActivity.this));
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CoursesTasksActivity.this, "You clicked the cancel button!", Toast.LENGTH_SHORT).show();
                //Intent intToHome = new Intent(CoursesTasksActivity.this, ViewEditCourseActivity.class);
                //intToHome.putExtra("courseId" , selectedCourseID);
                //startActivity(intToHome);
                finish();
            }
        });

        displayTasks();
    }

    private void displayTasks() {
        Toast.makeText(CoursesTasksActivity.this, "Finding Your Courses Tasks", Toast.LENGTH_SHORT).show();
        Query query = FirebaseDatabase.getInstance().getReference("tasks/" + uid).orderByChild("course").equalTo(selectedCourseID);

        Log.d("TAG", "displayCourses: " + query.toString());


        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        tasks.add(d.getValue(Task.class));
                        Log.d("TAG", "onDataChange: " + d.getValue(Task.class).getTaskName());

                    }

                    //printing tasks to log for debugging purposes
                    for (int i = 0; i < tasks.size(); i++) {
                        Log.d("TAG", "displayTasks (loop): " + tasks.get(i).getTaskName());
                    }
                } else {
                    noTasks.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);
                }
            }//onDataChange

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        FirebaseRecyclerAdapter<Task, TaskViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Task, TaskViewHolder>(Task.class, R.layout.tasks_layout, TaskViewHolder.class, query) {

            @Override
            protected void populateViewHolder(TaskViewHolder taskViewHolder, Task task, final int i) {

                Log.d("TAG", "populateViewHolder CoursesTasksActivity: " + tasks + " i " + i);
                taskViewHolder.setDetails(tasks.get(i), getBaseContext(), uid);

                Log.d("TAG", "populateViewHolder: i " + i);

                //show task details only if it's a task under ur actual profile not friends.
                if(showDetailsFlag.equals("true")){
                    taskViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String taskId = getRef(i).getKey();

                            Intent IntToTask = new Intent(CoursesTasksActivity.this, ViewEditTaskActivity.class);
                            IntToTask.putExtra("taskId", taskId);
                            startActivity(IntToTask);
                        }
                    });
                }



            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        Log.d("TAG", "displayTasks: setting adapter");


    }
}