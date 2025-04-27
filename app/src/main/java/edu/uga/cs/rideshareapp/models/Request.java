package edu.uga.cs.rideshareapp.models;

public class Request {
    public String from, to, date, time, userEmail, userUid;  // 🆕 Added userUid

    public Request() {} // Needed for Firebase

    public Request(String from, String to, String date, String time, String userEmail, String userUid) {
        this.from = from;
        this.to = to;
        this.date = date;
        this.time = time;
        this.userEmail = userEmail;
        this.userUid = userUid;  // 🆕 Save UID too
    }
}