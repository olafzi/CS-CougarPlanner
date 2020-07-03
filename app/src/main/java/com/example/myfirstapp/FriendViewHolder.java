package com.example.myfirstapp;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FriendViewHolder extends RecyclerView.ViewHolder {

    View mView;

    public FriendViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
    }

    public void setDetails(String user, Context context) {

        Log.d("TAG", "setDetails: " + user);

        TextView friendName = (TextView) mView.findViewById(R.id.FriendNameTextView);


        Log.d("TAG", "populateViewHolder: " + user);


        friendName.setText(user);


        Log.d("TAG", "setDetails: " + friendName.getText());




    }
}
