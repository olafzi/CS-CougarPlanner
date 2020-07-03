package com.example.myfirstapp;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class NotificationViewHolder extends RecyclerView.ViewHolder {
    private View mView;
    private String SenderName;
    private String RecieverName;

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private String uid = firebaseAuth.getUid();

    public NotificationViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
    }

    public void setDetails(final FriendRequests friendRequests, final Context context) {

        Log.d("TAG", "setDetails: " + friendRequests.getSenderID());

        final TextView sender = (TextView) mView.findViewById(R.id.nameOfUserTV);


        //query database to get sender name using id
        Query query = FirebaseDatabase.getInstance().getReference("users/" + friendRequests.getSenderID() + "/name");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("TAG", "onDataChange: " + dataSnapshot.getValue().toString());
                SenderName = dataSnapshot.getValue().toString();
                sender.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //query database to get reciever name using id
        Query query2 = FirebaseDatabase.getInstance().getReference("users/" + uid + "/name");

        query2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("TAG", "onDataChange: " + dataSnapshot.getValue().toString());
                RecieverName = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Log.d("TAG", "populateViewHolder: " + friendRequests.getSenderID());



        sender.setText(SenderName);

        Log.d("TAG", "setDetails: " + sender.getText());

        Button declineButton = mView.findViewById(R.id.declineButton);
        Button acceptButton = mView.findViewById(R.id.acceptButton);

        //make friends on both sides
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "You clicked the Button." , Toast.LENGTH_SHORT).show();


                DatabaseReference databaseReferenceSender = FirebaseDatabase.getInstance().getReference("friends/" + uid);
                databaseReferenceSender.child(friendRequests.getSenderID()).setValue(SenderName);
                DatabaseReference databaseReferenceReciever = FirebaseDatabase.getInstance().getReference("friends/" + friendRequests.getSenderID());
                databaseReferenceReciever.child(uid).setValue(RecieverName);

                //delete request from database
                DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference("requests/" + uid + "/sender");
                Query query = requestRef.orderByChild("senderID").equalTo(friendRequests.getSenderID());
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot d : dataSnapshot.getChildren()) {
                                d.getRef().removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                requestRef.removeValue();
            }
        });

        //handle decline button (delete request from database nothing else)
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                String uid = firebaseAuth.getUid();
                DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference("requests/" + uid + "/sender");
                Query query = requestRef.orderByChild("senderID").equalTo(friendRequests.getSenderID());
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot d : dataSnapshot.getChildren()) {
                                d.getRef().removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                requestRef.removeValue();
            }
        });






    }
}
