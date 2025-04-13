package edu.uga.cs.rideshareapp;

public class Ride {
    public String from;
    public String to;
    public String date;
    public String time;
    public String status;
    public String coinsEarned;

    public Ride(String from, String to, String date, String time, String status, String coinsEarned) {
        this.from = from;
        this.to = to;
        this.date = date;
        this.time = time;
        this.status = status;
        this.coinsEarned = coinsEarned;
    }
}