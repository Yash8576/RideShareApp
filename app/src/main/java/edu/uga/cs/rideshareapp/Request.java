package edu.uga.cs.rideshareapp;

public class Request {
    public String fromTo;
    public String dateTime;
    public String notes;

    public Request(String fromTo, String dateTime, String notes) {
        this.fromTo = fromTo;
        this.dateTime = dateTime;
        this.notes = notes;
    }
}