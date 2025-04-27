package edu.uga.cs.rideshareapp.models;
public class Ride {
    public String from, to, date, time, userEmail, userUid;  // 🆕 added userUid

    public Ride() {}

    public Ride(String from, String to, String date, String time, String userEmail, String userUid) {
        this.from = from;
        this.to = to;
        this.date = date;
        this.time = time;
        this.userEmail = userEmail;
        this.userUid = userUid;
    }
}