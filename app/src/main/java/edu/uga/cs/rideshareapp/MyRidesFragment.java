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

public class MyRidesFragment extends Fragment {

    public MyRidesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_rides, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_my_rides);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Ride> rides = new ArrayList<>();
        rides.add(new Ride("Athens", "Atlanta", "2025-04-12", "10:30 AM", "Pending", "+50 Coins"));
        rides.add(new Ride("UGA", "Airport", "2025-04-13", "08:00 AM", "Confirmed", "+50 Coins"));
        rides.add(new Ride("Downtown", "Campus", "2025-04-14", "01:15 PM", "Pending", "+50 Coins"));

        RideAdapter adapter = new RideAdapter(rides);
        recyclerView.setAdapter(adapter);

        return view;
    }
}