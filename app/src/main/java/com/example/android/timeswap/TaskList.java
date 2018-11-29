
package com.example.android.timeswap;
/*
  Written by Hao Zhou

  for display task in a listview
*/
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

public class TaskList extends ArrayAdapter<TaskInformation> {
    private static final String TAG = "TaskList";

    private Activity context;
    private List<TaskInformation> tasklist;
    private List<Float> distancelist;




    public TaskList(Activity context, List<TaskInformation> tasklist){
        super(context, R.layout.list_layout, tasklist);
        this.context=context;
        this.tasklist=tasklist;
    }

    public TaskList(Activity context, List<TaskInformation> tasklist, List<Float> distance){
        super(context, R.layout.list_layout, tasklist);
        this.context=context;
        this.tasklist=tasklist;
        this.distancelist=distance;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.list_layout,null,true);

        TextView textViewTitle=(TextView) listViewItem.findViewById(R.id.textViewTitle);
        TextView textViewDes = (TextView) listViewItem.findViewById(R.id.textViewDes);
        TextView textViewPay = (TextView) listViewItem.findViewById(R.id.textViewPay);
        TextView textViewLocation = (TextView) listViewItem.findViewById(R.id.textViewLoc);
        TextView textViewDoer = (TextView) listViewItem.findViewById(R.id.textViewDoer);
        TextView textViewCp = (TextView) listViewItem.findViewById(R.id.textViewIsC);

        TaskInformation task = tasklist.get(position);
        textViewTitle.setText("Title: "+task.getTitle());
        textViewDes.setText("Description: " + task.getDescription());
        textViewPay.setText("Price: "+task.getPayment());
        textViewDoer.setText("Doer: "+task.getDoer());
        textViewLocation.setText("Location: "+task.getLocation());
        if (distancelist!=null) {
        textViewDoer.setText("Distance: "+distancelist.get(position) + " mi");}


        if(task.getIscomplete()==0)
            textViewCp.setText("Is Complete : No");
        else
            textViewCp.setText("Is Complete : Yes");

        return listViewItem;
    }

    /**********************************************************************************
     code snippets for using google play services to get current location
     play services is recommended, yet it is not as accurate as using android location service
     below we will use android location instead
     ***********************************************************************************/
//        Task<PlaceLikelihoodBufferResponse> placeResult = mPlaceDetectionClient.getCurrentPlace(null);
//        placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
//            @Override
//            public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
//                PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
//                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
//                    Log.i(TAG, String.format("Place '%s' has likelihood: %g",
//                            placeLikelihood.getPlace().getName(),
//                            placeLikelihood.getLikelihood()));
//                }
//                likelyPlaces.release();
//            }
//        });


}
