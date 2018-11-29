package com.example.android.timeswap;

import android.app.Activity;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/*
    Written by Chris McLaughlin and Eric Liu

    This is the activity that allows the user to log into the application
 */
public class LoginActivity extends AppCompatActivity {
    private EditText Email, Password;
    private Button Login;
    private TextView Register;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    /* Written by Chris McLaughlin */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setupUIViews();
        isGooglePlayServicesAvailable(this);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        //When Login is clicked
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Store the email and password that was supplied by the user
                String email = Email.getText().toString().trim();
                String password = Password.getText().toString().trim();
                //Attempt to validate that information
                validate(email, password);
            }
        });

        //When Register is clicked
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Bring the user to the RegistrationActivity
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            }
        });
    }

    //Link all of the XML Objects to Java
    private void setupUIViews(){
        //findViewById connects java variables to ids in the XML layout
        Email = (EditText)findViewById(R.id.etEmail);
        Password = (EditText)findViewById(R.id.etPassword);
        Login = (Button)findViewById(R.id.btnLogin);
        Register = (TextView) findViewById(R.id.tvRegister);
    }

    /* Written by Chris McLaughlin */
    /* Validates the information that was supplied by the user */
    private void validate(String pUsername, String pPassword){
        //If both if the username(email) and password field are not empty
        if(!pUsername.isEmpty() && !pPassword.isEmpty()) {
            progressDialog.setMessage("Validating the information provided");
            progressDialog.show();
            //Attempt to validate the information
            firebaseAuth.signInWithEmailAndPassword(pUsername, pPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    //If it is validated
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        //Display a successful message to the user
                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        MainActivity.currentLogin = Email.getText().toString();
                        //Bring the user to the HomeActivity
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    } else {
                        progressDialog.dismiss();
                        //Display a failed message to the user
                        Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else{
            //Display  message to fill out both fields
            Toast.makeText(LoginActivity.this, "Please Fill Out Your Email And Password", Toast.LENGTH_SHORT).show();
        }
    }


    //Written by Eric Liu
    public boolean isGooglePlayServicesAvailable(Activity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if(status != ConnectionResult.SUCCESS) {
            if(googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(activity, status, 2404).show();
            }
            return false;
        }
        return true;
    }
}
