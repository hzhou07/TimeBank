
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
/*
  Written by Hao Zhou

  for doer to sign up tasks
*/
public class VerifyJobActivity extends AppCompatActivity {
    private TextView areYouSure, ToS; //, Term1, Term2;
    private Button Confirm, Deny;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_job);
        setupUIViews();

        Bundle extra = getIntent().getExtras();
        final String taskID = extra.getString("task_ID");

        Confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Transfer the credits
                //Place this activity in your to do

                updatedoer(taskID);
                startActivity(new Intent(VerifyJobActivity.this, HomeActivity.class));

            }
        });

        Deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(VerifyJobActivity.this, ListingsActivity.class);
                i.putExtra("key", "");
                startActivity(i);
            }
        });

        ToS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(VerifyJobActivity.this, TermsActivity.class));
            }
        });
    }

    private void updatedoer(String taskid){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("task_list");
        //String userid = firebaseAuth.getCurrentUser().getUid();
        ref.child(taskid).child("doer").setValue(firebaseAuth.getCurrentUser().getUid());

    }

    private void setupUIViews(){
        areYouSure = (TextView)findViewById(R.id.tvPrompt);
        ToS = (TextView)findViewById(R.id.tvTerms);
        //Term1 = (TextView)findViewById(R.id.tvTerm1);
        //Term2 = (TextView)findViewById(R.id.tvTerm2);
        Confirm = (Button)findViewById(R.id.btnConfirm);
        Deny = (Button)findViewById(R.id.btnDeny);
        firebaseAuth = firebaseAuth = FirebaseAuth.getInstance();
    }
}