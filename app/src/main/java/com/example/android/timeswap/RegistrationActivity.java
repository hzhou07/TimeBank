package com.example.android.timeswap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/*
    Written by Chris McLaughlin

    Allows the user to register for an account

    When the user creates an account, they automatically start with 10 in their bank, and a 0 rating
 */
public class RegistrationActivity extends AppCompatActivity {
    private EditText Username, Password, Email;
    private Button Register;
    private TextView Login;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private DatabaseReference databaserefBank, databaserefRatings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        setupUIViews();

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        //When register is clicked
        Register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //validate returns true if everything is good with login credentials
                if(validate(Username.getText().toString(), Password.getText().toString(), Email.getText().toString())){
                    //upload to database
                    String user_email = Email.getText().toString().trim();
                    String user_password = Password.getText().toString().trim();

                    //Create the account with the information that was provided from the user
                    firebaseAuth.createUserWithEmailAndPassword(user_email, user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //If the account was successfully created
                            if(task.isSuccessful()){
                                //Display the message below
                                Toast.makeText(RegistrationActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                                //Sign in the user
                                signIn(Email.getText().toString(), Password.getText().toString());

                                //The User will now be credited with 10 hours to start and 0 rating
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                BankInformation bankInfo = new BankInformation(10, user.getUid());
                                RatingInformation ratingInfo = new RatingInformation(0, 0, user.getUid());
                                databaserefBank.child(user.getUid()).setValue(bankInfo);
                                databaserefRatings.child(user.getUid()).setValue(ratingInfo);
                            }
                            else{
                                //Display the message below
                                Toast.makeText(RegistrationActivity.this, "Registration failed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        //When Login is clicked
        Login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //Bring the user to the LoginActivity
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    //Link all of the XML Objects to Java and Get Database references
    private void setupUIViews(){
        //findViewById connects java variables to ids in the XML layout
        Username = (EditText)findViewById(R.id.etEmail);
        Password = (EditText)findViewById(R.id.etPassword);
        Email = (EditText)findViewById(R.id.etEmail);
        Register = (Button)findViewById(R.id.btnRegister);
        Login = (TextView)findViewById(R.id.tvSignIn);
        databaserefBank = FirebaseDatabase.getInstance().getReference("bank");
        databaserefRatings = FirebaseDatabase.getInstance().getReference("ratings");
    }

    /* Validates the the information provided by the user is valid */
    private Boolean validate(String user, String pass, String email){
        progressDialog.setMessage("Validating the information provided");
        progressDialog.show();
        //If a field is empty
        if(user.isEmpty() || pass.isEmpty() || email.isEmpty()){
            progressDialog.dismiss();
            //Display the message below
            Toast.makeText(this, "Please fill out all of the fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        else{
            //Return true, as the information is valid
            progressDialog.dismiss();
            return true;
        }
    }

    /* Signs the user into their account once the account is registered */
    private void signIn(String email, String password){
        //Sign the user in with the information they provided
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //If they signed in successfully
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    //Display the message and bring them to the HomeActivity
                    Toast.makeText(RegistrationActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    MainActivity.currentLogin = Email.getText().toString();
                    startActivity(new Intent(RegistrationActivity.this, HomeActivity.class));
                } else {
                    //Display the message below
                    progressDialog.dismiss();
                    Toast.makeText(RegistrationActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
