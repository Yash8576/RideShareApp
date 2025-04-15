package edu.uga.cs.rideshareapp.models;

public class Ride {
    public String fromTo;
    public String dateTime;
    public String notes;

    public Ride(String fromTo, String dateTime, String notes) {
        this.fromTo = fromTo;
        this.dateTime = dateTime;
        this.notes = notes;
    }
}