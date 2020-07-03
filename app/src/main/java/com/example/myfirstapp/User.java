package com.example.myfirstapp;

public class User {
    private String name;
    private String uniname;
    private String email;
    private String dob;
    private String gradyr;
    private String currYear;
    private String imageURL;

    public User() {
    }

    public User(String name, String uniname, String email, String dob, String gradyr, String currYear) {
        this.name = name;
        this.uniname = uniname;
        this.email = email;
        this.dob = dob;
        this.gradyr = gradyr;
        this.currYear = currYear;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUniname() {
        return uniname;
    }

    public void setUniname(String uniname) {
        this.uniname = uniname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGradyr() {
        return gradyr;
    }

    public void setGradyr(String gradyr) {
        this.gradyr = gradyr;
    }

    public String getCurrYear() {
        return currYear;
    }

    public void setCurrYear(String currYear) {
        this.currYear = currYear;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}