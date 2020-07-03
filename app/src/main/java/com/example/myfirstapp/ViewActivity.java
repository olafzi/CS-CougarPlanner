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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewActivity extends AppCompatActivity {

    private Button backButton;
    private ActionBar actionBar;
    private TextView noTasks;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private String uid = firebaseAuth.getUid();
    private ArrayList<Task> tasks = new ArrayList<>();
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        //getting the date from the clicked date from previous activity
        Intent incoming = getIntent();
        String date = incoming.getStringExtra("date");

        //setting up action bar
        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2196f3")));
        actionBar.setTitle("Tasks Due on " + date);

        //getting references
        backButton = findViewById(R.id.backButton);
        noTasks = findViewById(R.id.tv_no_data);
        this.firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("tasks/" + uid);

        recyclerView = (RecyclerView) findViewById(R.id.taskViewRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ViewActivity.this));
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));

        displayTasks(date);


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ViewActivity.this, "You clicked the back button!", Toast.LENGTH_SHORT).show();
                //Intent intToDaily = new Intent(ViewActivity.this, ScheduleActivity.class);
                //startActivity(intToDaily);
                finish();
            }
        });

    }

    private void displayTasks(final String day) {
        Toast.makeText(ViewActivity.this, "Finding Today's Tasks", Toast.LENGTH_LONG).show();
        Query firebaseSearchQuery = FirebaseDatabase.getInstance().getReference("tasks/" + uid).orderByChild("dueDate").equalTo(day);

        Log.d("TAG", "displayTasks: " + firebaseSearchQuery.toString());

        firebaseSearchQuery.addValueEventListener(new ValueEventListener() {
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
                }
                else {
                    noTasks.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);

                }
            }//onDataChange

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        FirebaseRecyclerAdapter<Task, TaskViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Task, TaskViewHolder>(Task.class,R.layout.tasks_layout, TaskViewHolder.class, firebaseSearchQuery) {

            @Override
            protected void populateViewHolder(TaskViewHolder taskViewHolder, Task task, final int i) {



                Log.d("TAG", "populateViewHolder: " + tasks);
                taskViewHolder.setDetails(tasks.get(i), getBaseContext(), uid);

                taskViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String taskId = getRef(i).getKey();

                        Intent IntToTask = new Intent(ViewActivity.this, ViewEditTaskActivity.class);
                        IntToTask.putExtra("taskId", taskId);
                        startActivity(IntToTask);
                    }
                });
            }
        };



        recyclerView.setAdapter(firebaseRecyclerAdapter);
        Log.d("TAG", "displayTasks: setting adapter");


    }
}