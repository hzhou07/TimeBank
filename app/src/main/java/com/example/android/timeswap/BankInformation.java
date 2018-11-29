package com.example.android.timeswap;

/*
    Written by Chris McLaughlin

    Allows for easy access of information of a particular users bank.
    Allows for easy setup of a users bank when an account is created.
 */
public class BankInformation {
    public double bankAmount;
    public String userID;

    public BankInformation(){}

    /* Store values for this BankInformation */
    public BankInformation(double bankAmount, String userID) {
        this.bankAmount = bankAmount;
        this.userID = userID;
    }

    /* Return the amount in the users bank */
    public double getBankAmount() { return bankAmount; }

    /* Return the user id of this bank */
    public String getUserID() { return userID; }
}
