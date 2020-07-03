package com.example.myfirstapp;

public class Task {
    private String taskName;
    private String course;
    private String dueDate;
    private String priorityLevel;
    private String notes;
    private String completed;

    public Task() {
    }

    public Task(String taskName, String course, String dueDate, String priorityLevel, String notes) {
        this.taskName = taskName;
        this.course = course;
        this.dueDate = dueDate;
        this.priorityLevel = priorityLevel;
        this.notes = notes;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(String priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCompleted() {
        return completed;
    }

    public void setCompleted(String completed) {
        this.completed = completed;
    }
}
