package edu.uga.cs.rideshareapp.models;

public class Request {
    public String from;
    public String to;
    public String date;
    public String time;
    public String userEmail;

    public Request() {} // Firebase needs this

    public Request(String from, String to, String date, String time, String userEmail) {
        this.from = from;
        this.to = to;
        this.date = date;
        this.time = time;
        this.userEmail = userEmail;

    }
}