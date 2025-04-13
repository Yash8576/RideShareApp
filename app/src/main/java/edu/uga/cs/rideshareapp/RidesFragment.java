package edu.uga.cs.rideshareapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RidesFragment extends Fragment {

    public RidesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rides, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewRides);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Ride> rideList = new ArrayList<>();
        rideList.add(new Ride("From: East Campus → To: Athens Downtown", "Date: April 14, 2025 – 5:00 PM", "Notes: 3 seats available, no pets please"));
        rideList.add(new Ride("From: UGA → To: Airport", "Date: April 15, 2025 – 8:30 AM", "Notes: Will wait max 10 mins"));
        rideList.add(new Ride("From: Main Library → To: Five Points", "Date: April 16, 2025 – 2:00 PM", "Notes: Shared ride with 2 more"));

        RideAdapter adapter = new RideAdapter(rideList);
        recyclerView.setAdapter(adapter);

        return view;
    }
}