package edu.uga.cs.rideshareapp.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import edu.uga.cs.rideshareapp.R;
import edu.uga.cs.rideshareapp.adapters.CoinsManager;
import edu.uga.cs.rideshareapp.fragments.PostedRidesUserViewFragment;
import edu.uga.cs.rideshareapp.fragments.ProfileFragment;
import edu.uga.cs.rideshareapp.fragments.RequestsFragment;

public class DriverActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private TextView coinCount;

    private Fragment requestsFragment;
    private Fragment ridesFragment;
    private Fragment profileFragment;

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

        // Fetch existing fragments if they exist
        requestsFragment = getSupportFragmentManager().findFragmentByTag("Requests");
        ridesFragment = getSupportFragmentManager().findFragmentByTag("Rides");
        profileFragment = getSupportFragmentManager().findFragmentByTag("Profile");

        if (requestsFragment == null) {
            requestsFragment = new RequestsFragment();
        }
        if (ridesFragment == null) {
            ridesFragment = new PostedRidesUserViewFragment();
        }
        if (profileFragment == null) {
            profileFragment = new ProfileFragment();
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, profileFragment, "Profile").hide(profileFragment)
                    .add(R.id.fragment_container, ridesFragment, "Rides").hide(ridesFragment)
                    .add(R.id.fragment_container, requestsFragment, "Requests")
                    .commit();
            currentFragment = requestsFragment;
            bottomNavigationView.setSelectedItemId(R.id.nav_requests);
        } else {
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

        updateCoinsDisplay();
    }

    private void updateCoinsDisplay() {
        CoinsManager.listenForCoins(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer coins = snapshot.getValue(Integer.class);
                if (coinCount != null) {
                    if (coins == null || coins == 0) {
                        coinCount.setText("no coins");
                    } else {
                        coinCount.setText(String.valueOf(coins));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Optional error handling
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_bar_menu, menu);

        MenuItem coinItem = menu.findItem(R.id.action_coins);
        View actionView = coinItem.getActionView();
        coinCount = actionView.findViewById(R.id.coin_count);

        updateCoinsDisplay();

        actionView.setOnClickListener(v -> updateCoinsDisplay());

        return true;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
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
}