package edu.uga.cs.rideshareapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.uga.cs.rideshareapp.models.MyRide;
import edu.uga.cs.rideshareapp.adapters.MyRideAdapter;
import edu.uga.cs.rideshareapp.R;

public class MyRidesFragment extends Fragment {

    public MyRidesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_rides, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_my_rides);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<MyRide> myRides = new ArrayList<>();


        MyRideAdapter adapter = new MyRideAdapter(myRides);
        recyclerView.setAdapter(adapter);

        return view;
    }
}