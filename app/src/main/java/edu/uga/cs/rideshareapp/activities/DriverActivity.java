package edu.uga.cs.rideshareapp.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import edu.uga.cs.rideshareapp.R;
import edu.uga.cs.rideshareapp.fragments.PostedRidesUserViewFragment;
import edu.uga.cs.rideshareapp.fragments.ProfileFragment;
import edu.uga.cs.rideshareapp.fragments.RequestsFragment;

public class DriverActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    private final Fragment requestsFragment = new RequestsFragment();
    private final Fragment ridesFragment = new PostedRidesUserViewFragment();
    private final Fragment profileFragment = new ProfileFragment();

    private Fragment currentFragment;

    private static final String SELECTED_TAB_KEY = "selected_tab_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation1);

        // Handle safe area padding
        ViewCompat.setOnApplyWindowInsetsListener(bottomNavigationView, (v, insets) -> {
            int bottomInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;
            v.setPadding(0, 0, 0, bottomInset);
            return insets;
        });

        // Add all fragments only once
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, profileFragment, "Profile").hide(profileFragment)
                    .add(R.id.fragment_container, ridesFragment, "Rides").hide(ridesFragment)
                    .add(R.id.fragment_container, requestsFragment, "Requests")
                    .commit();
            currentFragment = requestsFragment;
            bottomNavigationView.setSelectedItemId(R.id.nav_requests); // default tab
        } else {
            // After rotation, restore the correct current fragment
            String selectedTab = savedInstanceState.getString(SELECTED_TAB_KEY);
            if ("Profile".equals(selectedTab)) {
                currentFragment = profileFragment;
                bottomNavigationView.setSelectedItemId(R.id.nav_profile);
            } else if ("Rides".equals(selectedTab)) {
                currentFragment = ridesFragment;
                bottomNavigationView.setSelectedItemId(R.id.nav_post_ride);
            } else {
                currentFragment = requestsFragment;
                bottomNavigationView.setSelectedItemId(R.id.nav_requests);
            }
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            if (itemId == R.id.nav_requests) {
                selectedFragment = requestsFragment;
            } else if (itemId == R.id.nav_post_ride) {
                selectedFragment = ridesFragment;
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = profileFragment;
            }

            if (selectedFragment != null && selectedFragment != currentFragment) {
                getSupportFragmentManager().beginTransaction()
                        .hide(currentFragment)
                        .show(selectedFragment)
                        .commit();
                currentFragment = selectedFragment;
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save which fragment is active
        if (currentFragment == profileFragment) {
            outState.putString(SELECTED_TAB_KEY, "Profile");
        } else if (currentFragment == ridesFragment) {
            outState.putString(SELECTED_TAB_KEY, "Rides");
        } else {
            outState.putString(SELECTED_TAB_KEY, "Requests");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_bar_menu, menu);

        MenuItem coinItem = menu.findItem(R.id.action_coins);
        View actionView = coinItem.getActionView();
        TextView coinCount = actionView.findViewById(R.id.coin_count);

        coinCount.setText("100");

        actionView.setOnClickListener(v -> {
            // Optional: Show toast or navigate to coin history
        });

        return true;
    }
}