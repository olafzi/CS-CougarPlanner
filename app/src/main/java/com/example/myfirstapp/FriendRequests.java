package com.example.myfirstapp;

public class FriendRequests{
    String senderID;
    String state;

    public FriendRequests() {
    }

    public FriendRequests(String UID) {
        this.senderID = UID;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

}
