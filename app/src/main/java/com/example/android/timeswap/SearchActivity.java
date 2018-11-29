
package com.example.android.timeswap;
/*
  Written by Hao Zhou

  allows user to search tasks using key words regarding title and description
*/
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    private Button search,clear;
    EditText input;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setupUIViews();

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user_input = input.getText().toString().trim();
                if(user_input.trim().isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Please enter the keywords", Toast.LENGTH_LONG).show();
                }
                else{
                    Intent in = new Intent(SearchActivity.this, ListingsActivity.class);
                    in.putExtra("key", user_input);
                    startActivity(in);
                }

            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(SearchActivity.this, ListingsActivity.class);
                in.putExtra("key", "");
                startActivity(in);
            }
        });
    }

    private void setupUIViews(){

        search = (Button)findViewById(R.id.SearchBtn);
        clear = (Button)findViewById(R.id.clearBtn);
        input = (EditText) findViewById(R.id.editTextSearchKey);
    }

}
