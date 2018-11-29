package com.example.android.timeswap;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/*
    Written by Chris McLaughlin (Except for logout which written by Hao Zhou)

    This activity is the main page of the application when you are logged in.
    From this activity, you can go to the following activities:
        ListingActivity
        HistoryActivity
        ManageActivity
        TodoActivity
    You will also have the ability to logout of your account from this page
 */
public class HomeActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private Button Listings, History, Manage, Logout,Todo;
    private TextView welcometext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setupUIViews();

        FirebaseUser user = firebaseAuth.getCurrentUser();
        welcometext.setText("Welcome, "+user.getEmail());

        //When Listings is clicked
        Listings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Bring the user to the ListingsActivity
                Intent in = new Intent(HomeActivity.this, ListingsActivity.class);
                in.putExtra("key", "");
                startActivity(in);
            }
        });

        //When History is clicked
        History.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Bring the user to the HistoryActivity
                startActivity(new Intent(HomeActivity.this, HistoryActivity.class));
            }
        });

        //When Manage is clicked
        Manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Bring the user to the ManageActivity
                startActivity(new Intent(HomeActivity.this, ManageActivity.class));
            }
        });

        //When To do is clicked
        Todo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Bring the user to the TodoActivity
                startActivity(new Intent(HomeActivity.this, TodoActivity.class));
            }
        });

        //When logout is clicked
        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log the user out of their account
                firebaseAuth.signOut();
                finish();
                //Bring the user to the LoginActivity
                startActivity(new Intent(HomeActivity.this,LoginActivity.class));
            }
        });
    }

    //Link all of the XML Objects to Java
    private void setupUIViews(){
        //findViewById connects java variables to ids in the XML layout
        Listings = (Button)findViewById(R.id.btnListings);
        History = (Button)findViewById(R.id.btnHistory);
        Manage = (Button)findViewById(R.id.btnManage);
        Logout = (Button)findViewById(R.id.logoutBtn);
        Todo = (Button)findViewById(R.id.TodoBtn);
        welcometext = (TextView)findViewById(R.id.welcome);
        firebaseAuth = FirebaseAuth.getInstance();
    }
}
