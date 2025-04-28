package edu.uga.cs.rideshareapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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

        adapter = new MyRideAdapter(getContext(), myRides, rideKeys);
        recyclerView.setAdapter(adapter);

        // ✅ Safe padding for bottom navigation (system bars + 110dp)
        ViewCompat.setOnApplyWindowInsetsListener(recyclerView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(
                    v.getPaddingLeft(),
                    v.getPaddingTop(),
                    v.getPaddingRight(),
                    systemBars.bottom + 110
            );
            return insets;
        });

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

                            if (driverEmail == null || riderEmail == null) continue;

                            if (!(currentUserEmail.equals(driverEmail) || currentUserEmail.equals(riderEmail))) continue;

                            String from = snap.child("from").getValue(String.class);
                            String to = snap.child("to").getValue(String.class);
                            String date = snap.child("date").getValue(String.class);
                            String time = snap.child("time").getValue(String.class);
                            String status = snap.child("status").getValue(String.class);
                            Boolean confirmedByDriver = snap.child("confirmedByDriver").getValue(Boolean.class);
                            Boolean confirmedByRider = snap.child("confirmedByRider").getValue(Boolean.class);

                            if (confirmedByDriver == null) confirmedByDriver = false;
                            if (confirmedByRider == null) confirmedByRider = false;

                            int pointsValue = 50;
                            String coinsEarned;

                            if (confirmedByDriver && confirmedByRider) {
                                coinsEarned = (currentUserEmail.equals(driverEmail) ? "+" : "-") + pointsValue + " Coins";
                            } else {
                                coinsEarned = "Pending";
                            }

                            MyRide ride = new MyRide(
                                    from != null ? from : "Unknown",
                                    to != null ? to : "Unknown",
                                    date != null ? date : "Unknown",
                                    time != null ? time : "Unknown",
                                    status != null ? status : "Pending",
                                    coinsEarned,
                                    driverEmail,
                                    riderEmail,
                                    confirmedByDriver,
                                    confirmedByRider
                            );

                            myRides.add(ride);
                            rideKeys.add(snap.getKey());
                        }

                        // ✅ SORT accepted rides by (date + time)
                        myRides.sort((r1, r2) -> {
                            String datetime1 = r1.date + " " + r1.time;
                            String datetime2 = r2.date + " " + r2.time;
                            return datetime1.compareTo(datetime2);
                        });

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Failed to load accepted rides", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}