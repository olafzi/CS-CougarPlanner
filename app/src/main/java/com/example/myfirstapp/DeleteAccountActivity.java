package com.example.myfirstapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.drm.DrmStore;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DeleteAccountActivity extends AppCompatActivity {

    private ActionBar actionBar;
    private EditText email;
    private EditText password;
    private Button cancel;
    private Button delete;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);

        //setting up the action bar
        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2196f3")));
        actionBar.setTitle("Verify to Delete Your Account");

        email = findViewById(R.id.emailEdit);
        password = findViewById(R.id.passwordEdit);
        delete = findViewById(R.id.deleteAccount);
        cancel = findViewById(R.id.buttoncancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // add error handling: Make sure all fields are inputted, else ask user to finish filling out form.
                if (email.getText().toString().isEmpty()) {
                    email.setError("Please enter your email");
                    email.requestFocus();
                }
                if (password.getText().toString().isEmpty()) {
                    password.setError("Please your password");
                    password.requestFocus();
                }
                if (!email.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
                    // Get auth credentials from the user for re-authentication.
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(email.getText().toString(), password.getText().toString());

                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                                    Log.d("TAG", "User re-authenticated.");

                                }
                            });

                    DatabaseReference usersCourses = FirebaseDatabase.getInstance().getReference("courses/" + user.getUid());
                    DatabaseReference usersTasks = FirebaseDatabase.getInstance().getReference("tasks/" + user.getUid());
                    DatabaseReference usersProfile = FirebaseDatabase.getInstance().getReference("users/" + user.getUid());

                    if (usersCourses != null) {
                        usersCourses.removeValue();
                    }
                    if (usersTasks != null) {
                        usersTasks.removeValue();
                    }
                    if (usersProfile != null) {
                        usersProfile.removeValue();
                    }

                    user.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("TAG", "User account deleted.");
                                        Toast.makeText(DeleteAccountActivity.this, "Your account has been deleted", Toast.LENGTH_SHORT).show();
                                        Intent intToMain = new Intent(DeleteAccountActivity.this, MainActivity.class);
                                        startActivity(intToMain);
                                    }
                                }
                            });

                }
            }
        });


    }


}
