package edu.uga.cs.rideshareapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Top section
        ImageView profileLogo = view.findViewById(R.id.profile_logo);
        TextView profileName = view.findViewById(R.id.profile_name);

        // Scrollable buttons
        Button btnMyRides = view.findViewById(R.id.button_my_rides);
        Button btnComplaint = view.findViewById(R.id.button_complaint);
        Button btnAbout = view.findViewById(R.id.button_about);
        Button btnLogout = view.findViewById(R.id.button_logout);

        // Set name or image dynamically if needed
        profileName.setText("John Doe");

        // Button click actions
        btnMyRides.setOnClickListener(v ->
                Toast.makeText(getActivity(), "My Rides clicked", Toast.LENGTH_SHORT).show());

        btnComplaint.setOnClickListener(v ->
                Toast.makeText(getActivity(), "Raise a Complaint clicked", Toast.LENGTH_SHORT).show());

        btnAbout.setOnClickListener(v -> {
            Fragment aboutUsFragment = new AboutUsFragment();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, aboutUsFragment)
                    .addToBackStack(null)
                    .commit();
        });

        btnLogout.setOnClickListener(v ->
                Toast.makeText(getActivity(), "Logout clicked", Toast.LENGTH_SHORT).show());

        return view;
    }
}