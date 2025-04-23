package edu.uga.cs.rideshareapp.fragments;

import android.app.AlertDialog;
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

        // Buttons
        Button btnMyRides = view.findViewById(R.id.button_my_rides);
        Button btnComplaint = view.findViewById(R.id.button_complaint);
        Button btnAbout = view.findViewById(R.id.button_about);
        Button btnResetPassword = view.findViewById(R.id.reset_password_button);
        Button btnLogout = view.findViewById(R.id.button_logout);
        Button btnRemoveAccount = view.findViewById(R.id.remove_acc_button);

        // 🔐 Show username without @uga.edu
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            if (email != null && email.contains("@")) {
                String username = email.substring(0, email.indexOf("@"));  // Get everything before '@'
                profileName.setText(username);
            } else {
                profileName.setText("Dwaag");
            }
        }

        // 👉 Navigate to My Rides
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

        // 🔁 Reset Password
        btnResetPassword.setOnClickListener(v -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String email = currentUser.getEmail();
                if (email != null && !email.isEmpty()) {
                    new AlertDialog.Builder(requireContext())
                            .setTitle("Reset Password")
                            .setMessage("We'll send a password reset link to " + email + ". Continue?")
                            .setPositiveButton("Send", (dialog, which) -> {
                                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getContext(), "Reset email sent to " + email, Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(getContext(), "Failed to send reset email. Try again later.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                } else {
                    Toast.makeText(getContext(), "No email found for this user.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "User not logged in.", Toast.LENGTH_SHORT).show();
            }
        });

        // 🚪 Logout
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // ❌ Delete Account
        btnRemoveAccount.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Remove Account")
                    .setMessage("Are you sure you want to permanently delete your account?")
                    .setPositiveButton("Yes, Delete", (dialog, which) -> {
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (currentUser != null) {
                            currentUser.delete()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getContext(), "Account deleted successfully", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(getContext(), "Failed to delete account. Please re-login and try again.", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        return view;
    }
}