package com.example.android.timeswap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.auth.FirebaseAuth;

/*
    Written by Chris McLaughlin

    Is a simple activity that displays the terms of the application to the user

 */
public class TermsActivity extends AppCompatActivity {
    private TextView Terms;
    private Button Return;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        setupUIViews();

        //When return is clicked
        Return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Bring the user to the HomeActivity
                startActivity(new Intent(TermsActivity.this, HomeActivity.class));
            }
        });
    }

    //Link all of the XML Objects to Java
    private void setupUIViews(){
        Terms = (TextView)findViewById(R.id.tvTerms);
        Return = (Button)findViewById(R.id.btnReturn);
    }
}
