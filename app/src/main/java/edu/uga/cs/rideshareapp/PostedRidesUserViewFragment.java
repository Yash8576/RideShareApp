package edu.uga.cs.rideshareapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PostedRidesUserViewFragment extends Fragment {

    private List<Ride> rideList = new ArrayList<>();
    private PostedRideAdapter adapter;
    private RecyclerView recyclerView;
    private View emptyStateLayout;

    public PostedRidesUserViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_posted_rides_userview, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewPostedRides);
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout);
        FloatingActionButton fab = view.findViewById(R.id.fabPostRide);

        adapter = new PostedRideAdapter(rideList, position -> {
            rideList.remove(position);
            adapter.notifyItemRemoved(position);
            adapter.notifyItemRangeChanged(position, rideList.size());
            updateViewVisibility();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        updateViewVisibility();

        fab.setOnClickListener(v -> {
            View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_post_ride, null);
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
            bottomSheetDialog.setContentView(bottomSheetView);
            bottomSheetDialog.show();

            EditText fromField = bottomSheetDialog.findViewById(R.id.fromLocation);
            EditText toField = bottomSheetDialog.findViewById(R.id.toLocation);
            EditText dateTimeField = bottomSheetDialog.findViewById(R.id.dateTime);
            Button postButton = bottomSheetDialog.findViewById(R.id.buttonPostRide);
            Button cancelButton = bottomSheetDialog.findViewById(R.id.buttonCancelRide);

            // 🗓 Setup Date Picker on click
            if (dateTimeField != null) {
                dateTimeField.setFocusable(false);
                dateTimeField.setClickable(true);
                dateTimeField.setOnClickListener(v1 -> {
                    Calendar calendar = Calendar.getInstance();
                    DatePickerDialog datePickerDialog = new DatePickerDialog(
                            requireContext(),
                            (DatePicker view1, int year, int month, int dayOfMonth) -> {
                                String formattedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                                dateTimeField.setText(formattedDate);
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                    );
                    datePickerDialog.getDatePicker().setCalendarViewShown(false); // Spinner style
                    datePickerDialog.show();
                });
            }

            postButton.setOnClickListener(btn -> {
                String from = fromField.getText().toString();
                String to = toField.getText().toString();
                String dateTime = dateTimeField.getText().toString();

                if (from.isEmpty() || to.isEmpty() || dateTime.isEmpty()) {
                    Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                Ride newRide = new Ride("From: " + from + " → To: " + to, "Date: " + dateTime, "Notes: None");
                rideList.add(newRide);
                adapter.notifyItemInserted(rideList.size() - 1);

                bottomSheetDialog.dismiss();
                updateViewVisibility();
            });

            cancelButton.setOnClickListener(cancel -> bottomSheetDialog.dismiss());
        });

        return view;
    }

    private void updateViewVisibility() {
        if (rideList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);
        }
    }
}