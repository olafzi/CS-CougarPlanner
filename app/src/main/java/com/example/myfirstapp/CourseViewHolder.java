package com.example.myfirstapp;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class CourseViewHolder extends RecyclerView.ViewHolder {
    View mView;


    public CourseViewHolder(View itemView) {
        super(itemView);

        mView = itemView;
    }

    public void setDetails(Course course, Context context) {

        Log.d("TAG", "setDetails: " + course);

        TextView coursename = (TextView) mView.findViewById(R.id.CourseNameTextView);
        TextView coursemeeting = (TextView) mView.findViewById(R.id.MeetingDaysTextView);
        TextView courseinstructor = (TextView) mView.findViewById(R.id.InstructorNameTextView);
        TextView coursecolor = (TextView) mView.findViewById(R.id.ColorTextView);
        TextView color = (TextView) mView.findViewById(R.id.colorTV);


        Log.d("TAG", "populateViewHolder: " + course.getCourseName() + course.getMeetingDays()
                        + course.getInstructor() + course.getColor());


        String colorStr = course.getColor();

        if(colorStr.equals("Pink")) {
            color.setBackgroundColor(context.getResources().getColor(R.color.Pink));
        }
        else if(colorStr.equals("Red")){
            color.setBackgroundColor(context.getResources().getColor(R.color.Red));
        }
        else if(colorStr.equals("Orange")){
            color.setBackgroundColor(context.getResources().getColor(R.color.Orange));
        }
        else if(colorStr.equals("Yellow")){
            color.setBackgroundColor(context.getResources().getColor(R.color.Yellow));
        }
        else if(colorStr.equals("Green")){
            color.setBackgroundColor(context.getResources().getColor(R.color.Green));
        }
        else if(colorStr.equals("Cyan")){
            color.setBackgroundColor(context.getResources().getColor(R.color.Cyan));
        }
        else if(colorStr.equals("Azure")){
            color.setBackgroundColor(context.getResources().getColor(R.color.Azure));
        }
        else if(colorStr.equals("Blue")){
            color.setBackgroundColor(context.getResources().getColor(R.color.Blue));
        }
        else if(colorStr.equals("Violet")){
            color.setBackgroundColor(context.getResources().getColor(R.color.Violet));
        }
        else if(colorStr.equals("Magenta")){
            color.setBackgroundColor(context.getResources().getColor(R.color.Magenta));
        }
        coursename.setText(course.getCourseName());
        coursemeeting.setText(course.getMeetingDays());
        courseinstructor.setText(course.getInstructor());
        coursecolor.setText(course.getColor());


        Log.d("TAG", "setDetails: " + coursename.getText());




    }


}

