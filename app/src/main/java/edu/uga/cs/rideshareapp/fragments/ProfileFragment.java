package edu.uga.cs.rideshareapp.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import edu.uga.cs.rideshareapp.R;
import edu.uga.cs.rideshareapp.activities.LoginActivity;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        Button btnMyRides = view.findViewById(R.id.button_my_rides);
        Button btnComplaint = view.findViewById(R.id.button_complaint);
        Button btnAbout = view.findViewById(R.id.button_about);
        Button btnResetPassword = view.findViewById(R.id.reset_password_button);
        Button btnLogout = view.findViewById(R.id.button_logout);
        Button btnRemoveAccount = view.findViewById(R.id.remove_acc_button);

        TextView profileName = view.findViewById(R.id.profile_name);
        ImageView profileLogo = view.findViewById(R.id.profile_logo);

        ScrollView scrollView = view.findViewById(R.id.scroll_area);

        // ✅ Proper dynamic bottom padding to avoid hiding last button
        ViewCompat.setOnApplyWindowInsetsListener(scrollView, (v, insets) -> {
            int bottomInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;
            v.setPadding(
                    v.getPaddingLeft(),
                    v.getPaddingTop(),
                    v.getPaddingRight(),
                    bottomInset + 110  // ➡️ little extra padding for good spacing
            );
            return insets;
        });

        // Set profile name
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getEmail() != null) {
            String email = user.getEmail();
            profileName.setText(email.split("@")[0]);
        }

        // Navigate to My Rides
        btnMyRides.setOnClickListener(v -> {
            hideBottomNavigation();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new MyRidesFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Navigate to Raise Complaint
        btnComplaint.setOnClickListener(v -> {
            hideBottomNavigation();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new RaiseAComplaintFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Navigate to About Us
        btnAbout.setOnClickListener(v -> {
            hideBottomNavigation();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new AboutUsFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Reset Password
        btnResetPassword.setOnClickListener(v -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null && currentUser.getEmail() != null) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Reset Password")
                        .setMessage("Send password reset email to " + currentUser.getEmail() + "?")
                        .setPositiveButton("Send", (dialog, which) -> {
                            FirebaseAuth.getInstance().sendPasswordResetEmail(currentUser.getEmail())
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getContext(), "Reset email sent", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getContext(), "Failed to send reset email", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        // Logout
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // Delete Account
        btnRemoveAccount.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete Account")
                    .setMessage("Are you sure you want to delete your account?")
                    .setPositiveButton("Yes, Delete", (dialog, which) -> {
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (currentUser != null) {
                            currentUser.delete()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getContext(), "Account deleted", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(getContext(), "Failed to delete account. Please re-login.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        showBottomNavigation();
    }

    private void hideBottomNavigation() {
        BottomNavigationView nav1 = requireActivity().findViewById(R.id.bottom_navigation1);
        BottomNavigationView nav2 = requireActivity().findViewById(R.id.bottom_navigation2);

        if (nav1 != null) {
            nav1.setVisibility(View.GONE);
        }
        if (nav2 != null) {
            nav2.setVisibility(View.GONE);
        }
    }

    private void showBottomNavigation() {
        BottomNavigationView nav1 = requireActivity().findViewById(R.id.bottom_navigation1);
        BottomNavigationView nav2 = requireActivity().findViewById(R.id.bottom_navigation2);

        if (nav1 != null) {
            nav1.setVisibility(View.VISIBLE);
        }
        if (nav2 != null) {
            nav2.setVisibility(View.VISIBLE);
        }
    }
}