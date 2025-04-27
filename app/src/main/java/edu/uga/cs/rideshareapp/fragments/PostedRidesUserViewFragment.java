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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edu.uga.cs.rideshareapp.R;
import edu.uga.cs.rideshareapp.adapters.PostedRideAdapter;
import edu.uga.cs.rideshareapp.models.Ride;

public class PostedRidesUserViewFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostedRideAdapter adapter;
    private final List<Ride> rideList = new ArrayList<>();
    private final List<String> rideKeys = new ArrayList<>();

    public PostedRidesUserViewFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_posted_rides_userview, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewPostedRides);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new PostedRideAdapter(rideList, rideKeys, position -> {
            String key = rideKeys.get(position);
            FirebaseDatabase.getInstance().getReference("ride_offers")
                    .child(key)
                    .removeValue();
            rideList.remove(position);
            rideKeys.remove(position);
            adapter.notifyItemRemoved(position);
        });

        recyclerView.setAdapter(adapter);

        // ✅ Add safe bottom padding (system bars + 110dp)
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

        FloatingActionButton fab = view.findViewById(R.id.fabPostRide);
        fab.setOnClickListener(v -> showBottomSheet());

        loadPostedRides();

        return view;
    }

    private void loadPostedRides() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }
        String currentUserEmail = user.getEmail();

        FirebaseDatabase.getInstance().getReference("ride_offers")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        rideList.clear();
                        rideKeys.clear();

                        for (DataSnapshot snap : snapshot.getChildren()) {
                            Ride ride = snap.getValue(Ride.class);
                            if (ride != null && currentUserEmail != null && ride.userEmail != null
                                    && ride.userEmail.equals(currentUserEmail)) {
                                rideList.add(ride);
                                rideKeys.add(snap.getKey());
                            }
                        }

                        adapter.notifyDataSetChanged();
                        updateEmptyState();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Failed to load rides", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateEmptyState() {
        if (getView() == null) return;
        View emptyState = getView().findViewById(R.id.emptyStateLayout);
        if (emptyState == null) return;

        if (rideList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
        }
    }

    private void showBottomSheet() {
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_post_ride, null);
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        dialog.setContentView(sheetView);
        dialog.show();

        EditText fromField = sheetView.findViewById(R.id.fromLocation);
        EditText toField = sheetView.findViewById(R.id.toLocation);
        EditText dateField = sheetView.findViewById(R.id.date);
        EditText timeField = sheetView.findViewById(R.id.time);
        Button postButton = sheetView.findViewById(R.id.buttonPostRide);
        Button cancelButton = sheetView.findViewById(R.id.buttonCancelRide);

        dateField.setFocusable(false);
        dateField.setClickable(true);
        dateField.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(requireContext(), (DatePicker view, int year, int month, int dayOfMonth) -> {
                dateField.setText(String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        timeField.setFocusable(false);
        timeField.setClickable(true);
        timeField.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new TimePickerDialog(requireContext(), (TimePicker view, int hourOfDay, int minute) -> {
                timeField.setText(String.format("%02d:%02d", hourOfDay, minute));
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
        });

        postButton.setOnClickListener(v -> {
            String from = fromField.getText().toString().trim();
            String to = toField.getText().toString().trim();
            String date = dateField.getText().toString().trim();
            String time = timeField.getText().toString().trim();

            if (from.isEmpty() || to.isEmpty() || date.isEmpty() || time.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            String userEmail = currentUser.getEmail();
            String userUid = currentUser.getUid();
            if (user == null) {
                Toast.makeText(getContext(), "Not logged in", Toast.LENGTH_SHORT).show();
                return;
            }

            Ride ride = new Ride(from, to, date, time, userEmail, userUid);

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

        cancelButton.setOnClickListener(v -> dialog.dismiss());
    }
}