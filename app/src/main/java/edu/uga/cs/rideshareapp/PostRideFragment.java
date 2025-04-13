package edu.uga.cs.rideshareapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostRideFragment#} factory method to
 * create an instance of this fragment.
 */
public class PostRideFragment extends Fragment {

    public PostRideFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout (now using ConstraintLayout as root)
        View view = inflater.inflate(R.layout.fragment_post_ride, container, false);

        // Reference FAB and container layout
        FloatingActionButton fab = view.findViewById(R.id.fabPostRide);
        View emptyStateLayout = view.findViewById(R.id.emptyStateLayout);

        fab.setOnClickListener(v -> {
            emptyStateLayout.setVisibility(View.GONE); // Hide empty state when FAB is clicked

            // Inflate bottom sheet view
            View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_post_ride, null);
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
            bottomSheetDialog.setContentView(bottomSheetView);
            bottomSheetDialog.show();

            // Submit button in bottom sheet
            Button submitButton = bottomSheetView.findViewById(R.id.buttonPostRide);
            submitButton.setOnClickListener(btn -> {
                // Get form inputs
                String from = ((EditText) bottomSheetView.findViewById(R.id.fromLocation)).getText().toString();
                String to = ((EditText) bottomSheetView.findViewById(R.id.toLocation)).getText().toString();
                String dateTime = ((EditText) bottomSheetView.findViewById(R.id.dateTime)).getText().toString();

                Toast.makeText(getContext(), "MyRide posted from " + from + " to " + to, Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            });

            // Optional: Show empty state again if user cancels without posting
            bottomSheetDialog.setOnDismissListener(dialog -> {
                // emptyStateLayout.setVisibility(View.VISIBLE);
            });
        });

        return view;
    }
}