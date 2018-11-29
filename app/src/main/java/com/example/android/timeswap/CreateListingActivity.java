
package com.example.android.timeswap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
/*
  Written by Hao Zhou

  allows user to create new task
*/
public class CreateListingActivity extends AppCompatActivity {
    double amount = 0;
    private FirebaseAuth firebaseAuth;
    private EditText Title, Description, Payment;
    private TextView ViewLocation;
    private Button Create, Return, SetLocation;
    private DatabaseReference databaseref,bankref;
    private String location, place_id;
    private double lat, lng;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_listing);
        setupUIViews();

        Return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CreateListingActivity.this, HomeActivity.class));
            }
        });

        SetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //requesting location data from MapActivity
                Intent i = new Intent(CreateListingActivity.this, MapActivity.class);
                startActivityForResult(i, 1);
            }
        });

        ViewLocation.setText("Location");



        Create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = Title.getText().toString().trim();
                String description = Description.getText().toString().trim();
                String payment_string = Payment.getText().toString().trim();
                //get_balance();
                //Toast.makeText(getApplicationContext(), ""+amount, Toast.LENGTH_LONG).show();
                if(title.isEmpty() || description.isEmpty() || location.isEmpty() || payment_string.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please provide enough information", Toast.LENGTH_LONG).show();
                }
                else {
                    Double payment = Double.parseDouble(payment_string);
                    if (payment == 0) {
                        Toast.makeText(getApplicationContext(), "Payment should not be zero", Toast.LENGTH_LONG).show();
                    } else if (amount < payment) {
                        Toast.makeText(getApplicationContext(), "Not enough balance", Toast.LENGTH_LONG).show();
                    } else {
                        createTask(title, description, location, place_id, payment_string);
                        //createTask(title,description,lat,lng,payment_string);
                        modify_balance(Double.parseDouble(payment_string));
                        //startActivity(new Intent(getApplicationContext(), ListingsActivity.class));
                        Intent i = new Intent(getApplicationContext(), ListingsActivity.class);
                        i.putExtra("key", "");
                        startActivity(i);

                    }
                }
            }
        });
    }

    // getting location data from MapActivity
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                location = data.getStringExtra("EXTRA_PLACE_NAME");
                place_id = data.getStringExtra("EXTRA_PLACE_ID");
                //lat = data.getDoubleExtra("EXTRA_PLACE_LAT", 0);
                //lng = data.getDoubleExtra("EXTRA_PLACE_LNG", 0);

                ViewLocation.setText(location);

            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        final String userID = firebaseAuth.getCurrentUser().getUid();
        bankref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {



                for(DataSnapshot taskSnapshot : dataSnapshot.getChildren()){
                    BankInformation task = taskSnapshot.getValue(BankInformation.class);
                    String s = task.getUserID();
                    if(s.equals(userID)) {
                        amount = task.getBankAmount();
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void modify_balance(double payment){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("bank");
        String userid = firebaseAuth.getCurrentUser().getUid();
        BankInformation new_info = new BankInformation(amount-payment,userid);
        ref.child(userid).setValue(new_info);
    }

    private void get_balance(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("bank").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for(DataSnapshot child : children){
                    BankInformation bankInfo = child.getValue(BankInformation.class);

                    if(bankInfo.getUserID().equals(firebaseAuth.getCurrentUser().getUid())){
                        amount = bankInfo.getBankAmount();
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void createTask(String title,String description,String location, String place_id, String payment_string){
        double payment = Double.parseDouble(payment_string);
        FirebaseUser user= firebaseAuth.getCurrentUser();

        String taskID = databaseref.push().getKey();

        TaskInformation taskInfo = new TaskInformation(taskID,user.getUid(),title,description,location,place_id, payment,"TBD");


        databaseref.child(taskID).setValue(taskInfo);

        Toast.makeText(this,"Task is created!",Toast.LENGTH_LONG).show();
    }

    private void setupUIViews(){
        Title = (EditText)findViewById(R.id.etTitle);
        Description = (EditText)findViewById(R.id.etDescription);
        Payment = (EditText)findViewById(R.id.etPayment);
        Create = (Button)findViewById(R.id.btnCreate);
        databaseref = FirebaseDatabase.getInstance().getReference("task_list");
        bankref =FirebaseDatabase.getInstance().getReference("bank");
        firebaseAuth = FirebaseAuth.getInstance();
        Return = (Button)findViewById(R.id.btnReturn);
        SetLocation = (Button)findViewById(R.id.btnLocation);
        ViewLocation = (TextView)findViewById(R.id.tvLocation);
    }

}