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

public class PostRequestFragment extends Fragment {

    public PostRequestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_request, container, false);

        FloatingActionButton fabPostReq = view.findViewById(R.id.fabPostReq);
        View emptyStateLayout = view.findViewById(R.id.emptyStateLayout);

        fabPostReq.setOnClickListener(v -> {
            emptyStateLayout.setVisibility(View.GONE); // hide empty state for now

            View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_post_request, null);
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
            bottomSheetDialog.setContentView(bottomSheetView);
            bottomSheetDialog.show();

            // Post button logic
            Button postButton = bottomSheetView.findViewById(R.id.buttonPostrequest);
            postButton.setOnClickListener(post -> {
                String from = ((EditText) bottomSheetView.findViewById(R.id.fromLocation)).getText().toString();
                String to = ((EditText) bottomSheetView.findViewById(R.id.toLocation)).getText().toString();
                String dateTime = ((EditText) bottomSheetView.findViewById(R.id.dateTime)).getText().toString();
                String notes = ((EditText) bottomSheetView.findViewById(R.id.notes)).getText().toString();

                // Show confirmation
                Toast.makeText(getContext(),
                        "Request posted:\n" + from + " → " + to + "\n" + dateTime,
                        Toast.LENGTH_SHORT).show();

                bottomSheetDialog.dismiss();
            });

            // Cancel button logic
            Button cancelButton = bottomSheetView.findViewById(R.id.buttonCancelrequest);
            cancelButton.setOnClickListener(cancel -> {
                bottomSheetDialog.dismiss();
                emptyStateLayout.setVisibility(View.VISIBLE); // optionally restore empty state
            });

            bottomSheetDialog.setOnDismissListener(dialog -> {
                // Optionally restore empty state
                // emptyStateLayout.setVisibility(View.VISIBLE);
            });
        });

        return view;
    }
}