package com.example.android.timeswap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/*
    Written by Chris McLaughlin

    This activity is where the creator of a task goes to rate the person who completed the task
    that they had listed. It is here in which the user inputs the rating, and the rating for the
    person who completed the task is updated
 */
public class RatingActivity extends AppCompatActivity {
    private TextView ratingStatement;
    private RatingBar ratingBar;
    private Button Submit;
    private DatabaseReference databaserefRatings, databaserefTasks, databaserefBank;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    private RatingInformation holder;
    private Integer i;
    private double bankBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        setupUIViews();

        //When the submit button is clicked
        Submit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //Set the task as completed
                mDatabase.child("task_list").child(HistoryActivity.globalTask.getTaskID()).child("iscomplete").setValue(1);
                // account for the new task that the user just completed
                i = holder.getTasksCompleted() + 1;
                //Calculate the users new rating
                double ratingUpdate = ((holder.getRating() * holder.getTasksCompleted()) + ratingBar.getRating()) /i;
                //Update the ratings database of the doer, with the new amount of completed tasks
                mDatabase.child("ratings").child(holder.getUserID()).child("tasksCompleted").setValue(i);
                //Update the ratings database of the doer, with the new adjusted rating
                mDatabase.child("ratings").child(holder.getUserID()).child("rating").setValue(ratingUpdate);
                //Update the bank database of the doer with their new balance
                mDatabase.child("bank").child(holder.getUserID()).child("bankAmount").setValue(HistoryActivity.globalTask.getPayment() + bankBalance);
                startActivity(new Intent(getApplicationContext(), HistoryActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Get the completed task ID
        final String taskID = HistoryActivity.globalTask.getTaskID();

        //Using the reference to the bank database
        databaserefBank.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //For each bank in the database
                for(DataSnapshot taskSnapshot : dataSnapshot.getChildren()){
                    //Store this bank into the BankInformation class
                    BankInformation bankInfo = taskSnapshot.getValue(BankInformation.class);
                    //If the bank belongs to the person who completed the task
                    if(HistoryActivity.globalTask.getDoer().equals(bankInfo.getUserID())){
                        //Store their current balance
                        bankBalance = bankInfo.getBankAmount();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Using the reference to the task database
        databaserefTasks.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //For each task in the database
                for(DataSnapshot taskSnapshot : dataSnapshot.getChildren()){
                    //Store this task into the TaskInformation class
                    TaskInformation task = taskSnapshot.getValue(TaskInformation.class);
                    //get the taskID
                    String tasksID = task.getTaskID();
                    //If this task matches the task completed
                    if(tasksID == taskID){
                        //If no one has done this task yet
                        if(task.getDoer() == "TBD"){
                            //Display the message below
                            Toast.makeText(RatingActivity.this, "This Task Has Not Been Assigned Yet", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            //Get the id of the doer
                            final String doer = task.getDoer();
                            //Using the reference to the ratings database
                            databaserefRatings.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    //For each rating child in the database
                                    for(DataSnapshot taskSnapshot : dataSnapshot.getChildren()){
                                        //Store this rating into the RatingInformation class
                                        RatingInformation task = taskSnapshot.getValue(RatingInformation.class);
                                        //If the rating information belongs to this user
                                        if(doer.equals(task.getUserID())){
                                            //Store the RatingInformation into holder
                                            holder = task;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                        }
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
        ratingStatement = (TextView)findViewById(R.id.tvRating);
        ratingBar = (RatingBar)findViewById(R.id.etRating);
        Submit = (Button)findViewById(R.id.btnSubmit);
        databaserefRatings = FirebaseDatabase.getInstance().getReference("ratings");
        databaserefTasks = FirebaseDatabase.getInstance().getReference("task_list");
        databaserefBank = FirebaseDatabase.getInstance().getReference("bank");
    }

}
