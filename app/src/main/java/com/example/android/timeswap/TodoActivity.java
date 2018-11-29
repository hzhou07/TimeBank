
package com.example.android.timeswap;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.net.Uri;


import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
/*
  Written by Hao Zhou
  for user to view todo task

  Xiang Ding add navigation functionality

*/
public class TodoActivity extends AppCompatActivity {
    final String TAG = "TodoActivity";
    FirebaseAuth firebaseAuth;
    ListView listViewTasks;
    DatabaseReference databaseTasks;
    List<TaskInformation> taskList;

    GeoDataClient mGeoDataClient;
    double taskLat, taskLng;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);
        setupUIViews();
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
                    String s1 = task.getDoer();
                    String s2 = firebaseAuth.getCurrentUser().getUid();
                    if(s1.equals(s2)) {
                        taskList.add(task);
                    }
                }

                TaskList adapter = new TaskList(TodoActivity.this,taskList);
                listViewTasks.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        listViewTasks.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                TaskInformation task = taskList.get(i);
                String userID = firebaseAuth.getCurrentUser().getUid();
                String creatorID = task.getCreator();

                if (creatorID == userID) { //check to see if the user is trying to select their own job
                    Toast.makeText(getApplicationContext(),"You cannot select your own task!", Toast.LENGTH_LONG).show();
                }

                else {

                    getTaskCoordinate(task.getPlace_id());
                    // jump to google map navigation
                    Uri gmmIntentUri = Uri.parse("google.navigation:q="+taskLat+","+taskLng+"&mode=d");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                }
                return false;
            }
        });
    }

    private void getTaskCoordinate(String plid) {
        mGeoDataClient = Places.getGeoDataClient(this, null);
        mGeoDataClient.getPlaceById(plid).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                if (task.isSuccessful()) {
                    PlaceBufferResponse places = task.getResult();
                    Place myPlace = places.get(0);
                    taskLat = myPlace.getLatLng().latitude;
                    taskLng = myPlace.getLatLng().longitude;
                    Log.i(TAG, "getTaskCoordinate: Place found: " + myPlace.getName() +
                            " " + taskLat+ ", " + taskLng );
                    places.release();
                } else {
                    Log.e(TAG, "Place not found.");
                }
            }
        });
    }

    private void setupUIViews(){
        listViewTasks = (ListView)findViewById(R.id.ListViewTodo);
        databaseTasks = FirebaseDatabase.getInstance().getReference("task_list");
        firebaseAuth = FirebaseAuth.getInstance();
        taskList = new ArrayList<>();
    }
}
