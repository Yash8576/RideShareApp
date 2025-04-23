package edu.uga.cs.rideshareapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.uga.cs.rideshareapp.R;
import edu.uga.cs.rideshareapp.adapters.MyRideAdapter;
import edu.uga.cs.rideshareapp.models.MyRide;

public class MyRidesFragment extends Fragment {

    private RecyclerView recyclerView;
    private MyRideAdapter adapter;
    private final List<MyRide> myRides = new ArrayList<>();
    private final List<String> rideKeys = new ArrayList<>();

    public MyRidesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_rides, container, false);

        recyclerView = view.findViewById(R.id.recycler_my_rides);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MyRideAdapter(myRides, rideKeys);
        recyclerView.setAdapter(adapter);

        fetchAcceptedRides();

        return view;
    }

    private void fetchAcceptedRides() {
        String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getEmail()
                : null;

        if (currentUserEmail == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseDatabase.getInstance().getReference("accepted_rides")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        myRides.clear();
                        rideKeys.clear();

                        for (DataSnapshot snap : snapshot.getChildren()) {
                            String driverEmail = snap.child("driverEmail").getValue(String.class);
                            String riderEmail = snap.child("riderEmail").getValue(String.class);
                            String from = snap.child("from").getValue(String.class);
                            String to = snap.child("to").getValue(String.class);
                            String date = snap.child("date").getValue(String.class);
                            String time = snap.child("time").getValue(String.class);
                            String status = snap.child("status").getValue(String.class);

                            String pointsStr;
                            Object pointsObj = snap.child("points").getValue();
                            if (pointsObj instanceof Long) {
                                pointsStr = String.valueOf((Long) pointsObj);
                            } else {
                                pointsStr = pointsObj != null ? pointsObj.toString() : "0";
                            }

                            int pointsValue;
                            try {
                                pointsValue = Integer.parseInt(pointsStr);
                            } catch (NumberFormatException e) {
                                pointsValue = 0;
                            }

                            if ((driverEmail != null || riderEmail != null) &&
                                    (currentUserEmail.equals(driverEmail) || currentUserEmail.equals(riderEmail))) {

                                String coinLabel = currentUserEmail.equals(driverEmail) ? "+" : "-";
                                String coinsEarned = coinLabel + pointsValue + " Coins";

                                MyRide ride = new MyRide(from, to, date, time, status, coinsEarned);
                                myRides.add(ride);
                                rideKeys.add(snap.getKey());
                            }
                        }

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Failed to load accepted rides", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}