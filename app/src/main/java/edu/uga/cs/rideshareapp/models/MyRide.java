package edu.uga.cs.rideshareapp.models;

public class MyRide {
    public String from;
    public String to;
    public String date;
    public String time;
    public String status;
    public String coinsEarned;
    public String driverEmail;
    public String riderEmail;
    public boolean confirmedByDriver;
    public boolean confirmedByRider;

    public MyRide() {
        // Required empty constructor for Firebase
    }

    public MyRide(String from, String to, String date, String time, String status, String coinsEarned,
                  String driverEmail, String riderEmail, boolean confirmedByDriver, boolean confirmedByRider) {
        this.from = from;
        this.to = to;
        this.date = date;
        this.time = time;
        this.status = status;
        this.coinsEarned = coinsEarned;
        this.driverEmail = driverEmail;
        this.riderEmail = riderEmail;
        this.confirmedByDriver = confirmedByDriver;
        this.confirmedByRider = confirmedByRider;
    }
}