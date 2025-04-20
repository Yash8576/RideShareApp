package edu.uga.cs.rideshareapp.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edu.uga.cs.rideshareapp.R;
import edu.uga.cs.rideshareapp.adapters.PostedRideAdapter;
import edu.uga.cs.rideshareapp.models.Ride;

public class PostedRidesUserViewFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostedRideAdapter adapter;
    private List<Ride> rideList = new ArrayList<>();

    public PostedRidesUserViewFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_posted_rides_userview, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewPostedRides);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new PostedRideAdapter(rideList, position -> {
            // Cancel functionality (optional)
        });

        recyclerView.setAdapter(adapter);
        loadPostedRides();

        FloatingActionButton fab = view.findViewById(R.id.fabPostRide);
        fab.setOnClickListener(v -> showBottomSheet());

        return view;
    }

    private void loadPostedRides() {
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

                        // ✅ SHOW/HIDE EMPTY STATE
                        if (rideList.isEmpty()) {
                            recyclerView.setVisibility(View.GONE);
                            if (getView() != null) {
                                View emptyState = getView().findViewById(R.id.emptyStateLayout);
                                if (emptyState != null) emptyState.setVisibility(View.VISIBLE);
                            }
                        } else {
                            recyclerView.setVisibility(View.VISIBLE);
                            if (getView() != null) {
                                View emptyState = getView().findViewById(R.id.emptyStateLayout);
                                if (emptyState != null) emptyState.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Failed to load rides", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showBottomSheet() {
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_post_ride, null);
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        dialog.setContentView(sheetView);
        dialog.show();

        EditText from = sheetView.findViewById(R.id.fromLocation);
        EditText to = sheetView.findViewById(R.id.toLocation);
        EditText date = sheetView.findViewById(R.id.date);
        EditText time = sheetView.findViewById(R.id.time);
        Button post = sheetView.findViewById(R.id.buttonPostRide);
        Button cancel = sheetView.findViewById(R.id.buttonCancelRide);

        // Date Picker
        date.setFocusable(false);
        date.setClickable(true);
        date.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(requireContext(), (DatePicker view, int y, int m, int d) -> {
                date.setText(String.format("%04d-%02d-%02d", y, m + 1, d));
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Time Picker
        time.setFocusable(false);
        time.setClickable(true);
        time.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new TimePickerDialog(requireContext(), (TimePicker view, int h, int m) -> {
                time.setText(String.format("%02d:%02d", h, m));
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false).show();
        });

        // Submit to Firebase
        post.setOnClickListener(v -> {
            String f = from.getText().toString().trim();
            String t = to.getText().toString().trim();
            String d = date.getText().toString().trim();
            String ti = time.getText().toString().trim();

            if (f.isEmpty() || t.isEmpty() || d.isEmpty() || ti.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Ride ride = new Ride(f, t, d, ti);

            FirebaseDatabase.getInstance().getReference("ride_offers")
                    .push()
                    .setValue(ride)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(getContext(), "Ride offer posted!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        cancel.setOnClickListener(v -> dialog.dismiss());
    }
}