package com.example.myfirstapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    //ask user for their email, name, username, password, date of birth, university
    // name, current year at the university, and expected graduation date. Add info to their profile database
    private EditText emailId, password;
    private Button btnSignUp;
    private TextView tvSignIn;
    private FirebaseAuth mFirebaseAuth;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setting up action bar
        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2196f3")));
        actionBar.setTitle("Cougar Planner - Create an Account");

        //get the fire base authentication instance
        mFirebaseAuth = FirebaseAuth.getInstance();

        //R.id used to locate and connect to resource
        emailId = findViewById(R.id.emailET);
        password = findViewById(R.id.passwordET);
        btnSignUp = findViewById(R.id.button);
        tvSignIn = findViewById(R.id.textView);

        //when a person clicks sign up: handle as follows
        btnSignUp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v){
                //get the email and password entered
                String email = emailId.getText().toString();
                String pwd = password.getText().toString();

                //if no email is entered, display message
                if(email.isEmpty()){
                    emailId.setError("Please enter email id");
                    emailId.requestFocus();
                }
                else if(pwd.isEmpty()){ //if no password is entered, display message
                    password.setError("Please enter your password");
                    password.requestFocus();
                }
                else if(email.isEmpty() && pwd.isEmpty()){ //if no email and no password is entered, display message
                    Toast.makeText(MainActivity.this,"Fields are Empty!", Toast.LENGTH_SHORT).show();
                }
                else if(!(email.isEmpty() && pwd.isEmpty())){ //if email and password is entered


                    //create a new user with the inputted email and password
                    mFirebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {

                        //after creating the account then:
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //if not successful in creating account, notify user
                            if(!task.isSuccessful()){
                                Toast.makeText(MainActivity.this,"SignUp Unsuccessful, please try again!", Toast.LENGTH_SHORT).show();
                                Toast.makeText(MainActivity.this,"Maybe you already have an account?", Toast.LENGTH_SHORT).show();


                            }
                            else{

                                //if creating account is successful, then send them to the verify email screen
                                startActivity(new Intent(MainActivity.this, VerifyEmailActivity.class));

                            }
                        }
                    });
                }
                else{
                    Toast.makeText(MainActivity.this,"Error Occurred!", Toast.LENGTH_SHORT).show();

                }
            }
        });

        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(i);
            }
        });
    }
}
