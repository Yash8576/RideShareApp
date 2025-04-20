package edu.uga.cs.rideshareapp.models;

public class Ride {
    public String from;
    public String to;
    public String date;
    public String time;

    public Ride() {
        // Needed for Firebase
    }

    public Ride(String from, String to, String date, String time) {
        this.from = from;
        this.to = to;
        this.date = date;
        this.time = time;
    }
}