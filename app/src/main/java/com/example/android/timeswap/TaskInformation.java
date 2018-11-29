
package com.example.android.timeswap;

import com.google.android.gms.maps.model.LatLng;
/*
  Written by Hao Zhou

  location information modified by Xiang Ding

  for user to create new task
*/
public class TaskInformation {
    public String taskID;
    public String creator;
    public String title;
    public String description;
    public String location;
    public String place_id;
    public double latitude;
    public double longitude;
    public double payment;
    public int iscomplete;
    public String doer;
    public LatLng latLng;

    public TaskInformation(){}
    public TaskInformation(String taskID, String creator,String title, String description, String location,String place_id, double payment,String doer) {
        this.taskID = taskID;
        this.creator = creator;
        this.title = title;
        this.description = description;
        this.location = location;
        this.place_id = place_id;
        this.payment = payment;
        this.doer = doer;
        this.iscomplete = 0;
    }

//    public TaskInformation(String taskID, String creator,String title, String description, double lat, double lng, double payment,String doer) {
//        this.taskID = taskID;
//        this.creator = creator;
//        this.title = title;
//        this.description = description;
//        this.payment = payment;
//        this.doer = doer;
//        this.iscomplete = 0;
//        this.latitude = lat;
//        this.longitude = lng;
//
//    }


    public String getTaskID() {
        return taskID;
    }

    public String getCreator() {
        return creator;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getPlace_id() {
        return place_id;
    }

    public double getPayment() {
        return payment;
    }

    public int getIscomplete() {
        return iscomplete;
    }

    public String getDoer() {
        return doer;
    }

    public void setIscomplete(int iscomplete) {
        this.iscomplete = iscomplete;
    }

    public void setDoer(String doer) {
        this.doer = doer;
    }
}
