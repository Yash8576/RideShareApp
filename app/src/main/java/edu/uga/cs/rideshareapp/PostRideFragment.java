package edu.uga.cs.rideshareapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostRideFragment#} factory method to
 * create an instance of this fragment.
 */
public class PostRideFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_post_ride, container, false);

        FloatingActionButton fab = view.findViewById(R.id.fabPostRide);
        LinearLayout emptyState = view.findViewById(R.id.emptyStateLayout);

        fab.setOnClickListener(v -> {
            emptyState.setVisibility(View.GONE); // Hide empty state when posting

            View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_post_ride, null);
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
            bottomSheetDialog.setContentView(bottomSheetView);
            bottomSheetDialog.show();

            Button submitButton = bottomSheetView.findViewById(R.id.buttonPostRide);
            submitButton.setOnClickListener(btn -> {
                // You can collect data here
                String from = ((EditText) bottomSheetView.findViewById(R.id.fromLocation)).getText().toString();
                String to = ((EditText) bottomSheetView.findViewById(R.id.toLocation)).getText().toString();
                String dateTime = ((EditText) bottomSheetView.findViewById(R.id.dateTime)).getText().toString();

                Toast.makeText(getContext(), "Ride posted from " + from + " to " + to, Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            });

            // Optional: show empty state again if user dismisses without submitting
            bottomSheetDialog.setOnDismissListener(dialog -> {
                // If no rides were added, show empty state again
                // emptyState.setVisibility(View.VISIBLE);
            });
        });

        return view;
    }
}