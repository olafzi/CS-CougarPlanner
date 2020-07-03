package com.example.myfirstapp;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class TaskViewHolder extends RecyclerView.ViewHolder {

    private View mView;


    public TaskViewHolder(View itemView) {
        super(itemView);

        mView = itemView;
    }

    public void setDetails(Task task, final Context context, String uid) {

        Log.d("TAG", "setDetails: " + task);

        TextView taskname = (TextView) mView.findViewById(R.id.TaskNameTextView);
        TextView taskcourse = (TextView) mView.findViewById(R.id.TaskCourseTextView);
        TextView tasknotes = (TextView) mView.findViewById(R.id.TaskNotesTextView);
        TextView taskduedate = (TextView) mView.findViewById(R.id.taskDueTextView);
        TextView taskpriority = (TextView) mView.findViewById(R.id.taskPriorityTextView);
        final TextView color = (TextView) mView.findViewById(R.id.colorTV);


        Log.d("TAG", "populateViewHolder: " + task.getTaskName()
                + task.getCourse() + task.getNotes() + task.getDueDate() + task.getPriorityLevel());

        Query query = FirebaseDatabase.getInstance().getReference("courses/" + uid + "/" + task.getCourse());
        Log.d("TAG", "setDetails:  have the query");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        if(d.getValue().equals("Pink")) {
                            color.setBackgroundColor(context.getResources().getColor(R.color.Pink));
                        }
                        else if(d.getValue().equals("Red")){
                            color.setBackgroundColor(context.getResources().getColor(R.color.Red));
                        }
                        else if(d.getValue().equals("Orange")){
                            color.setBackgroundColor(context.getResources().getColor(R.color.Orange));
                        }
                        else if(d.getValue().equals("Yellow")){
                            color.setBackgroundColor(context.getResources().getColor(R.color.Yellow));
                        }
                        else if(d.getValue().equals("Green")){
                            color.setBackgroundColor(context.getResources().getColor(R.color.Green));
                        }
                        else if(d.getValue().equals("Cyan")){
                            color.setBackgroundColor(context.getResources().getColor(R.color.Cyan));
                        }
                        else if(d.getValue().equals("Azure")){
                            color.setBackgroundColor(context.getResources().getColor(R.color.Azure));
                        }
                        else if(d.getValue().equals("Blue")){
                            color.setBackgroundColor(context.getResources().getColor(R.color.Blue));
                        }
                        else if(d.getValue().equals("Violet")){
                            color.setBackgroundColor(context.getResources().getColor(R.color.Violet));
                        }
                        else if(d.getValue().equals("Magenta")){
                            color.setBackgroundColor(context.getResources().getColor(R.color.Magenta));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        taskname.setText(task.getTaskName());
        taskcourse.setText(task.getCourse());
        tasknotes.setText(task.getNotes());
        taskduedate.setText(task.getDueDate());
        taskpriority.setText(task.getPriorityLevel());


        Log.d("TAG", "setDetails: " + taskname.getText());




    }


}