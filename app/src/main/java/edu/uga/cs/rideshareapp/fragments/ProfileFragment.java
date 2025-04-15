package edu.uga.cs.rideshareapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import edu.uga.cs.rideshareapp.R;
import edu.uga.cs.rideshareapp.activities.LoginActivity;

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

        // 🔐 Set username from Firebase (email without @uga.edu)
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String email = user.getEmail(); // e.g., alex@uga.edu
            if (email != null && email.endsWith("@uga.edu")) {
                String username = email.replace("@uga.edu", ""); // e.g., alex
                profileName.setText(username);
            } else {
                profileName.setText("User");
            }
        }

        // 👉 My Rides
        btnMyRides.setOnClickListener(v -> {
            Fragment myRidesFragment = new MyRidesFragment();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, myRidesFragment)
                    .addToBackStack(null)
                    .commit();
        });

        // 👉 Raise A Complaint
        btnComplaint.setOnClickListener(v -> {
            Fragment complaintFragment = new RaiseAComplaintFragment();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, complaintFragment)
                    .addToBackStack(null)
                    .commit();
        });

        // 👉 About Us
        btnAbout.setOnClickListener(v -> {
            Fragment aboutUsFragment = new AboutUsFragment();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, aboutUsFragment)
                    .addToBackStack(null)
                    .commit();
        });

        // 🚪 Logout
        btnLogout.setOnClickListener(v -> {

            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }
}