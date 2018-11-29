package com.example.android.timeswap;

/*
    Written by Chris McLaughlin

    Allows for easy access of information of a particular users rating.
    Allows for easy setup of a users rating when an account is created.
 */
public class RatingInformation {
    public double rating;
    public int tasksCompleted;
    public String userID;

    public RatingInformation(){}

    /* Store values for this RatingInformation */
    public RatingInformation(double rating, int tasksCompleted, String userID) {
        this.rating = rating;
        this.tasksCompleted = tasksCompleted;
        this.userID = userID;
    }

    /* Return this users rating */
    public double getRating() { return rating; }

    /* Return the amount of completed tasks by this user */
    public int getTasksCompleted() { return tasksCompleted; }

    /* Return the user that this rating information is associated with */
    public String getUserID() { return userID; }
}
