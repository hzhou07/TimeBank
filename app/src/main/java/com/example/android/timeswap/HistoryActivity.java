package com.example.android.timeswap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/*
    Written by Chris McLaughlin and Hao Zhou

    This activity allows the user to see all of the tasks that they created
    for other users to complete.
    Along with this, creators have the ability to state when a particular task is
    completed by clicking the task that is complete
 */
public class HistoryActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    ListView listViewTasks;
    DatabaseReference databaseTasks;
    List<TaskInformation> taskList;
    static TaskInformation globalTask;
    Button Return;

    /* Written by Chris McLaughlin */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        setupUIViews();

        //When a task is long clicked (held)
        listViewTasks.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Store the clicked task into TaskInformation
                    //This will allow easy access to various information about the task
                TaskInformation task = taskList.get(i);
                globalTask = task;

                //If the task is completed and reviewed
                if(task.getIscomplete() == 1){
                    //Display the follow message
                    Toast.makeText(HistoryActivity.this, "This Task Has Already Been Completed And Reviewed", Toast.LENGTH_SHORT).show();
                }
                else if(task.getDoer().equals("TBD")){
                    Toast.makeText(HistoryActivity.this, "Can not rate an incomplete task!", Toast.LENGTH_SHORT).show();
                }
                else{
                    //Mark the task as completed, and rate the doer
                    startActivity(new Intent(HistoryActivity.this, RatingActivity.class));
                }
                return false;
            }
        });

        //When the return button is clicked
        Return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Go back to the home page
                startActivity(new Intent(HistoryActivity.this, HomeActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseTasks.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                taskList.clear();

                for(DataSnapshot taskSnapshot : dataSnapshot.getChildren()){
                    TaskInformation task = taskSnapshot.getValue(TaskInformation.class);
                    String s1 = task.getCreator();
                    String s2 = firebaseAuth.getCurrentUser().getUid();
                    if(s1.equals(s2)) {
                        taskList.add(task);
                    }
                }

                TaskList adapter = new TaskList(HistoryActivity.this,taskList);
                listViewTasks.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //Link all of the XML Objects to Java
    private void setupUIViews(){
        listViewTasks = (ListView)findViewById(R.id.listVIewHistory);
        databaseTasks = FirebaseDatabase.getInstance().getReference("task_list");
        firebaseAuth = FirebaseAuth.getInstance();
        taskList = new ArrayList<>();
        Return = (Button)findViewById(R.id.btnReturn);
    }
}
