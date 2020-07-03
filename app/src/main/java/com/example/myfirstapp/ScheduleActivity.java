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
import android.widget.CalendarView;
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

public class ScheduleActivity extends AppCompatActivity {

    private ActionBar actionBar;
    private Button backButton;
    private CalendarView calendarView;
    private Button addTaskButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        //setting up action bar
        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2196f3")));
        actionBar.setTitle("Schedule");

        //getting references
        backButton = findViewById(R.id.backButton);
        addTaskButton = findViewById(R.id.addTaskButton);
        calendarView = findViewById(R.id.calendarView);




        //handle back button
        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(ScheduleActivity.this, "You clicked the back button!", Toast.LENGTH_SHORT).show();

                finish();
            }
        });

        //handle add task button
        addTaskButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(ScheduleActivity.this, "You clicked the add task button!", Toast.LENGTH_SHORT).show();
                Intent intToTask = new Intent(ScheduleActivity.this,AddTaskActivity.class);
                startActivity(intToTask);
            }
        });

        //getting the calendar set up
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                int digits = Integer.toString(dayOfMonth).trim().length();
                String date;
                if(digits == 1){
                    date = (month + 1) + "/0" + dayOfMonth + "/" + year;
                }
                else{
                    date = (month + 1) + "/" + dayOfMonth + "/" + year;
                }

                Intent intent = new Intent(ScheduleActivity.this, ViewActivity.class);
                intent.putExtra("date", date);
                startActivity(intent);
            }
        });

    }


}
