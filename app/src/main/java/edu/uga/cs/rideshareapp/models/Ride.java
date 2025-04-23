package edu.uga.cs.rideshareapp.models;

public class Ride {
    public String from;
    public String to;
    public String date;
    public String time;
    public String userEmail;

    public Ride() {
        // Firebase needs empty constructor
    }

    public Ride(String from, String to, String date, String time, String userEmail) {
        this.from = from;
        this.to = to;
        this.date = date;
        this.time = time;
        this.userEmail = userEmail;
    }
}