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
    private final List<Ride> rideList = new ArrayList<>();

    public RidesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rides, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewRides);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new RideAdapter(getContext(), rideList);
        recyclerView.setAdapter(adapter);

        // ✅ Add system padding + 110dp to RecyclerView for bottom navigation bar
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

                        // ✅ SORT by (date + time) ascending (soonest first)
                        rideList.sort((r1, r2) -> {
                            String datetime1 = r1.date + " " + r1.time;
                            String datetime2 = r2.date + " " + r2.time;
                            return datetime1.compareTo(datetime2); // earliest first
                        });

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Failed to load ride offers", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}