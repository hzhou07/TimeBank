
package com.example.android.timeswap;
/*
  Written by Hao Zhou

  except distance related stuff is written by Xiang Ding)

  displaying tasks information in a listview for doers to sign up
*/
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


//import java.io.IOException;
//import javax.ws.rs.client.Client;
//import javax.ws.rs.client.WebTarget;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ListingsActivity extends AppCompatActivity {
    private static final String TAG = "ListingsActivity";
    private FirebaseAuth firebaseAuth;
    private Button Create, Search, Return;
    ListView listViewTasks;
    DatabaseReference databaseTasks;
    List<TaskInformation> taskList,displayList;
    List<Float> distanceList;


    //    private PlaceDetectionClient mPlaceDetectionClient; // for using google place
    private FusedLocationProviderClient mFusedLocationProviderClient; // for using android location
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    private int count = 0;
    private double currentLat, currentLng, taskLat, taskLng;
    private String placeID;
    String parsedDistance = "";
    String response = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listings);
        setupUIViews();


        Create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ListingsActivity.this, CreateListingActivity.class));
            }
        });

        /*Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ListingsActivity.this, HomeActivity.class));
            }
        });*/
        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ListingsActivity.this,SearchActivity.class));
                //showUpdateDialog();
                /*
                for(int i=0; i<taskList.size();i++){
                    TaskInformation task = taskList.get(i);
                    if(task.getTitle().toLowerCase().contains(keywords.toLowerCase()))
                        displayList.add(taskList.get(i));
                }*/
                //TaskList adapter = new TaskList(ListingsActivity.this,displayList);
                //listViewTasks.setAdapter(adapter);
            }
        });

        Return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ListingsActivity.this,HomeActivity.class));
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
                    Intent in = new Intent(ListingsActivity.this, VerifyJobActivity.class);
                    in.putExtra("task_ID", task.getTaskID());
                    startActivity(in);
                    //showUpdateDialog(task,task.getTitle());
                }
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        getCurrentAndroidLocation();
        databaseTasks.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                taskList.clear();

                for(DataSnapshot taskSnapshot : dataSnapshot.getChildren()){
                    TaskInformation task = taskSnapshot.getValue(TaskInformation.class);
                    String s = task.getDoer();
                    String keywords = getIntent().getExtras().getString("key").trim();
                    placeID = task.getPlace_id();
                    Log.d(TAG, "OnStart: place_id: " + placeID);
                    if(keywords.equals("")){
                        if(s.equals("TBD")) {
                            getTaskCoordinate(placeID);
                            //String dis = getDistance(currentLat, currentLng, placeID);
                            float dis = getCalculatedDistance(currentLat, currentLng, taskLat, taskLng);
                            Log.d(TAG, "OnStart: distance: " + dis);

                            taskList.add(task);
                            distanceList.add(dis);
                        }
                    }
                    else{
                        if(s.equals("TBD") &&
                                (task.getTitle().toLowerCase().contains(keywords.toLowerCase())||
                                        task.getDescription().toLowerCase().contains(keywords.toLowerCase()))
                                ) {
                            getTaskCoordinate(placeID);
                            //String dis = getDistance(currentLat, currentLng, placeID);
                            float dis = getCalculatedDistance(currentLat, currentLng, taskLat, taskLng);
                            Log.d(TAG, "On Start: distance: " + dis);

                            taskList.add(task);
                            distanceList.add(dis);

                        }
                    }


                }


                TaskList adapter = new TaskList(ListingsActivity.this,taskList, distanceList);

                //TaskList adapter = new TaskList(ListingsActivity.this,taskList);
                listViewTasks.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

//    private void getCurrentGoogleLocation() {
//        try {
//            mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);
//            Task<PlaceLikelihoodBufferResponse> placeResult = mPlaceDetectionClient.getCurrentPlace(null);
//            placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
//                @Override
//                public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
//                    PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
////                    for (PlaceLikelihood placeLikelihood : likelyPlaces) {
////                        Log.i(TAG, String.format("Place '%s' has likelihood: %g",
////                                placeLikelihood.getPlace().getName(),
////                                placeLikelihood.getLikelihood()));
////                    }
//                    currentLat = likelyPlaces.get(0).getPlace().getLatLng().latitude;
//                    currentLng = likelyPlaces.get(0).getPlace().getLatLng().longitude;
//                    Log.d(TAG, "getCurrentGoogleLocation: found current location: " + currentLat + ", " + currentLng);
//
//
//                    likelyPlaces.release();
//                }
//            });
//        }
//        catch (SecurityException e) {
//            Log.e(TAG, "getCurrentGoogleLocation: SecurityException: " + e.getMessage() );
//
//        }
//    }


    private void getCurrentAndroidLocation(){
        Log.d(TAG, "getCurrentAndroidLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            mFusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location currentLocation) {
                            // Got last known location. In some rare situations this can be null.
                            if (currentLocation != null) {
                                // Logic to handle location object
                                currentLat = currentLocation.getLatitude();
                                currentLng = currentLocation.getLongitude();
                                Log.d(TAG, "getCurrentAndroidLocation: found current location: " + currentLat + ", " + currentLng);


                            }else{
                                Log.d(TAG, "getCurrentAndroidLocation: current location is null");
                                Toast.makeText(ListingsActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }catch (SecurityException e){
            Log.e(TAG, "getCurrentAndroidLocation: SecurityException: " + e.getMessage() );
        }
    }

    private void getTaskCoordinate(String plid) {
        mGeoDataClient = Places.getGeoDataClient(this, null);
        mGeoDataClient.getPlaceById(plid).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                if (task.isSuccessful()) {
                    PlaceBufferResponse places = task.getResult();
                    Place myPlace = places.get(0);

                    count++;
                    taskLat = myPlace.getLatLng().latitude;
                    taskLng = myPlace.getLatLng().longitude;
                    Log.i(TAG, "getTaskCoordinate: Place found: " + myPlace.getName() +
                            " " + taskLat+ ", " + taskLng + "  count: " + count);
                    places.release();
                } else {
                    Log.e(TAG, "Place not found.");
                }
            }
        });
    }

//    public DistanceMatrix estimateRouteTime(DateTime time, Boolean isForCalculateArrivalTime, DirectionsApi.RouteRestriction routeRestriction, LatLng departure, LatLng... arrivals) {
//        try {
//            DistanceMatrixApiRequest req = DistanceMatrixApi.newRequest(context);
//            if (isForCalculateArrivalTime) {
//                req.departureTime(time);
//            } else {
//                req.arrivalTime(time);
//            }
//            if (routeRestriction == null) {
//                routeRestriction = DirectionsApi.RouteRestriction.TOLLS;
//            }
//            DistanceMatrix trix = req.origins(departure)
//                    .destinations(arrivals)
//                    .mode(TravelMode.DRIVING)
//                    .avoid(routeRestriction)
//                    .language("fr-FR")
//                    .await();
//            return trix;
//
//        } catch (ApiException e) {
//            System.out.println(e.getMessage());
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//        return null;
//    }

    public float getCalculatedDistance (double lat_a, double lng_a, double lat_b, double lng_b )
    {
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(lat_b-lat_a);
        double lngDiff = Math.toRadians(lng_b-lng_a);
        double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
                Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
                        Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        return new Float(distance * meterConversion).floatValue();
    }

    public String getDistance(final double currentLat, final double currentLng, final String plid){


        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://maps.googleapis.com/maps/api/distancematrix/" +
                            "json?origins=" + currentLat + "," + currentLng +
                            "&destinations=place_id:" + plid + "&mode=driving&language=en&" +
                            "key=AIzaSyAhJXh4WN6B7QK3dtFWFsZS2RKwVuPi0rs");
                    final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    response = org.apache.commons.io.IOUtils.toString(in, "UTF-8");

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray dist=(JSONArray)jsonObject.get("rows");
                    JSONObject obj2 = (JSONObject)dist.get(0);
                    JSONArray disting=(JSONArray)obj2.get("elements");
                    JSONObject obj3 = (JSONObject)disting.get(0);
                    JSONObject obj4=(JSONObject)obj3.get("distance");
                    JSONObject obj5=(JSONObject)obj3.get("duration");
                    //System.out.println(obj4.get("text"));
                    //System.out.println(obj5.get("text"));
                    parsedDistance=obj4.getString("text");
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getDistance: distance is " + parsedDistance);
        return parsedDistance;
    }








    /*
    private void showUpdateDialog(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.update_dialog,null);
        dialogBuilder.setView(dialogView);

        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.buttonSearch);
        final EditText TextKeyword = (EditText) findViewById(R.id.EditTextKey);
        dialogBuilder.setTitle("Search");

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String user_input = TextKeyword.getText().toString().trim();
                //Intent in = new Intent(getApplicationContext(),ListingsActivity.class);
                //in.putExtra("key",user_input);
                //getIntent().putExtra("key",user_input);




                alertDialog.dismiss();



            }
        });


    }
    private void showUpdateDialog(final TaskInformation task,String title){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.update_dialog,null);
        dialogBuilder.setView(dialogView);

        final Button buttonUpdate = (Button) findViewById(R.id.buttonSearch);

        dialogBuilder.setTitle("Sign up for task"+ title);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateTask(task);
                alertDialog.dismiss();
            }
        });
    }

    private void verifyJob(final TaskInformation task, String title){

    }*/

    private boolean updateTask(TaskInformation task){
        String taskID = task.getTaskID();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("task_list").child(taskID);
        FirebaseUser user = firebaseAuth.getCurrentUser();

        TaskInformation newTask = new TaskInformation(taskID,task.creator,task.title,task.description,task.location, task.place_id, task.payment,user.getUid());
        //TaskInformation newTask = new TaskInformation(taskID,task.creator,task.title,task.description,task.latitude, task.longitude, task.payment,user.getUid());

        databaseReference.setValue(newTask);
        Toast.makeText(this,"Sign up tasks successfully!",Toast.LENGTH_LONG).show();
        return true;

    }

    private void setupUIViews(){
        Create = (Button)findViewById(R.id.btnCreate);
        Search = (Button)findViewById(R.id.btnSearch);
        Return = (Button)findViewById(R.id.BtnReturn);
        listViewTasks = (ListView)findViewById(R.id.listViewTasks);
        databaseTasks = FirebaseDatabase.getInstance().getReference("task_list");
        firebaseAuth = FirebaseAuth.getInstance();
        taskList = new ArrayList<>();
        displayList = new ArrayList<>();
        distanceList = new ArrayList<>();
    }



}