package com.example.myfirstapp;


public class Course {
    private String CourseName;
    private String MeetingDays;
    private String Instructor;
    private String Color;

    public Course(){

    }

    public Course(String courseName, String meetingDays, String instructor, String color) {
        CourseName = courseName;
        MeetingDays = meetingDays;
        Instructor = instructor;
        Color = color;
    }

    public String getCourseName() {
        return CourseName;
    }

    public void setCourseName(String courseName) {
        CourseName = courseName;
    }

    public String getMeetingDays() {
        return MeetingDays;
    }

    public void setMeetingDays(String meetingDays) {
        MeetingDays = meetingDays;
    }

    public String getInstructor() {
        return Instructor;
    }

    public void setInstructor(String instructor) {
        Instructor = instructor;
    }

    public String getColor() {
        return Color;
    }

    public void setColor(String color) {
        Color = color;
    }
}