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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.uga.cs.rideshareapp.R;
import edu.uga.cs.rideshareapp.adapters.RideAdapter;
import edu.uga.cs.rideshareapp.models.Ride;

public class RidesFragment extends Fragment {

    private RecyclerView recyclerView;
    private RideAdapter adapter;
    private List<Ride> rideList = new ArrayList<>();

    public RidesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rides, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewRides);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new RideAdapter(getContext(), rideList);
        recyclerView.setAdapter(adapter);

        loadRideOffersFromFirebase();

        return view;
    }

    private void loadRideOffersFromFirebase() {
        FirebaseDatabase.getInstance().getReference("ride_offers")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        rideList.clear();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            Ride ride = snap.getValue(Ride.class);
                            if (ride != null) {
                                rideList.add(ride);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Failed to load ride offers", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}