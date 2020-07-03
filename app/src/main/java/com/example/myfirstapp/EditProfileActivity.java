package com.example.myfirstapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfileActivity extends AppCompatActivity {

    private Button cancelButton;
    private Button saveButton;
    private TextView tvDeleteAcct;
    private ActionBar actionBar;
    private TextView nameET;
    private EditText dobET;
    private EditText uniET;
    private Spinner CurrentYearSpinner;
    private EditText gradYearET;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private String uid = firebaseAuth.getUid();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


        //setting up the action bar
        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2196f3")));
        actionBar.setTitle("Edit Profile");

        //getting references
        cancelButton = findViewById(R.id.cancelButton);
        saveButton = findViewById(R.id.saveProfileButton);
        nameET = findViewById(R.id.editName);
        dobET = findViewById(R.id.editDateOfBirth);
        uniET = findViewById(R.id.editUniversity);
        CurrentYearSpinner = findViewById(R.id.currentYearSpinner);
        gradYearET = findViewById(R.id.editExpectedGraduationYear);
        tvDeleteAcct = findViewById(R.id.deleteAccountTV);

        //populate data from database to edit text and spinner fields
        this.firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users/" + uid);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue().toString();
                    String dob = dataSnapshot.child("dob").getValue().toString();
                    String currYear = dataSnapshot.child("currYear").getValue().toString();
                    String gradYear = dataSnapshot.child("gradyr").getValue().toString();
                    String uniname = dataSnapshot.child("uniname").getValue().toString();

                    nameET.setText(name);
                    dobET.setText(dob);
                    gradYearET.setText(gradYear);
                    uniET.setText(uniname);

                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(EditProfileActivity.this, R.array.CurrentYear, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    CurrentYearSpinner.setAdapter(adapter);
                    if (currYear != null) {
                        int spinnerPosition = adapter.getPosition(currYear);
                        CurrentYearSpinner.setSelection(spinnerPosition);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //handle back button
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EditProfileActivity.this, "You clicked the cancel button!", Toast.LENGTH_SHORT).show();

                finish();
            }
        });

        //handle save button
        //update the users information in the database upon save
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // add error handling: Make sure all fields are inputted, else ask user to finish filling out form.
                if (dobET.getText().toString().isEmpty() || !dobET.getText().toString().matches("(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])[- /.](19|20)\\d\\d")) {
                    dobET.setError("Please enter your date of birth in the format MM/DD/YYYY");
                    dobET.requestFocus();
                }
                if (gradYearET.getText().toString().isEmpty()) {
                    gradYearET.setError("Please enter your expected graduation year");
                    gradYearET.requestFocus();
                }
                if (CurrentYearSpinner.getSelectedItem().toString().equals("Current Year")) {
                    ((TextView) CurrentYearSpinner.getSelectedView()).setError("Please choose a college level");
                    CurrentYearSpinner.requestFocus();
                }
                if (uniET.getText().toString().isEmpty()) {
                    uniET.setError("Please enter your university");
                    uniET.requestFocus();
                }
                if (!nameET.getText().toString().isEmpty()
                        && (!dobET.getText().toString().isEmpty() && dobET.getText().toString().matches("(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])[- /.](19|20)\\d\\d"))
                        && !gradYearET.getText().toString().isEmpty()
                        && !CurrentYearSpinner.getSelectedItem().toString().equals("Current Year") && !uniET.getText().toString().isEmpty()) {

                    Toast.makeText(EditProfileActivity.this, "You clicked the save button!", Toast.LENGTH_SHORT).show();

                    User user = new User();
                    user.setCurrYear(CurrentYearSpinner.getSelectedItem().toString());
                    user.setDob(dobET.getText().toString());
                    user.setGradyr(gradYearET.getText().toString());
                    user.setUniname(uniET.getText().toString());


                    databaseReference.child("currYear").setValue(CurrentYearSpinner.getSelectedItem().toString());
                    databaseReference.child("dob").setValue(dobET.getText().toString());
                    databaseReference.child("gradyr").setValue(gradYearET.getText().toString());
                    databaseReference.child("uniname").setValue(uniET.getText().toString());

                    finish();
                }
            }
        });

        //Delete account TextView
        tvDeleteAcct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intToDelete = new Intent(EditProfileActivity.this, DeleteAccountActivity.class);
                startActivity(intToDelete);
            }
        });
    }
}
