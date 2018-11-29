package com.example.android.timeswap;

import android.content.Intent;
import android.media.Rating;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/*
    Written by Chris McLaughlin and Hao Zhou

    Provides a way for the user to see information about their account
    Along with this, the user has the ability to update their email and password
 */
public class ManageActivity extends AppCompatActivity {
    private TextView balance, rating, tvPassword, tvEmail;
    private EditText passwordOld, passwordNew, passwordEmail, emailNew;
    private Button passwordUpdate, emailUpdate;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference bankref,rref;
    private double amount,rat;

    /* Written by Chris McLaughlin */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);
        setupUIViews();

        //When the user clicks Update in the password section
        passwordUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check the ensure that both fields are not empty
                if(!passwordOld.getText().toString().isEmpty() && !passwordNew.getText().toString().isEmpty()){
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    //Validate that the current password provided is correct
                    firebaseAuth.signInWithEmailAndPassword(user.getEmail(), passwordOld.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //If the password is correct
                            if (task.isSuccessful()) {
                                //Update the users password to the new one
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                user.updatePassword(passwordNew.getText().toString());
                                Toast.makeText(ManageActivity.this, "Your Password Has Been Updated", Toast.LENGTH_SHORT).show();
                            } else {
                                //If the passsword is not correct
                                //Display the message below
                                Toast.makeText(ManageActivity.this, "Your Provided Password Does Not Match The One Registered", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{
                    //Display the message below
                    Toast.makeText(ManageActivity.this, "Please Fill Out Both Fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //When the update button is clicked in the email section
        emailUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check to see that both fields are filled out
                if(!emailNew.getText().toString().isEmpty() && passwordEmail.getText().toString().isEmpty()){
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    //attempt to sign in with the information provided
                    firebaseAuth.signInWithEmailAndPassword(user.getEmail(), passwordEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //If the password was correct
                            if (task.isSuccessful()) {
                                //Update the users email
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                user.updateEmail(emailNew.getText().toString());
                                //Display the message below
                                Toast.makeText(ManageActivity.this, "Your Email and Login Has Been Updated", Toast.LENGTH_SHORT).show();
                            } else {
                                //Display the message below
                                Toast.makeText(ManageActivity.this, "Your Provided Password Does Not Match The One Registered", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{
                    //Display the message below
                    Toast.makeText(ManageActivity.this, "Please Fill Out Both Fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /* Written by Chris McLaughlin and Hao Zhou */
    @Override
    protected void onStart() {
        super.onStart();
        //Get the ID of the user thats currently logged in
        final String userID = firebaseAuth.getCurrentUser().getUid();
        //Using the reference to the bank database
        bankref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Iterate through the list of banks
                for(DataSnapshot taskSnapshot : dataSnapshot.getChildren()){
                    //Store this bank info into BankInformation
                    BankInformation bank = taskSnapshot.getValue(BankInformation.class);
                    //get the userID to this bank
                    String s = bank.getUserID();
                    //If the userID of this bank is the same as the logged in user
                    if(s.equals(userID)) {
                        //Store the follow values from the bank
                        amount = bank.getBankAmount();
                        balance.setText("Balance: "+amount);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Using the reference to the rating database
        rref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Iterate through the list of ratings
                for(DataSnapshot taskSnapshot : dataSnapshot.getChildren()){
                    //Store this rating into RatingInformation
                    RatingInformation rate1 = taskSnapshot.getValue(RatingInformation.class);
                    //If the userID of this RatingInformation matches the currently logged in user
                    if(userID.equals(rate1.getUserID())){
                        //Store the following value from the rating
                        rat = rate1.getRating();
                        rating.setText("Rating : "+rat);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //Link all of the XML Objects to Java and Get Database references
    private void setupUIViews(){
        balance = (TextView)findViewById(R.id.tvBalance);
        rating = (TextView)findViewById(R.id.tvRating);
        tvPassword = (TextView)findViewById(R.id.tvPassword);
        tvEmail = (TextView)findViewById(R.id.tvEmail);
        passwordOld = (EditText)findViewById(R.id.etCurrentPassword);
        passwordNew = (EditText)findViewById(R.id.etNewPassword);
        passwordEmail = (EditText)findViewById(R.id.etPasswordEmail);
        emailNew = (EditText)findViewById(R.id.etEmail);
        passwordUpdate = (Button)findViewById(R.id.btnPassword);
        emailUpdate = (Button)findViewById(R.id.btnEmail);
        firebaseAuth = FirebaseAuth.getInstance();
        bankref = FirebaseDatabase.getInstance().getReference("bank");
        rref = FirebaseDatabase.getInstance().getReference("ratings");
    }
}
