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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import edu.uga.cs.rideshareapp.R;
import edu.uga.cs.rideshareapp.adapters.CoinsManager;
import edu.uga.cs.rideshareapp.fragments.PostedRequestsUserViewFragment;
import edu.uga.cs.rideshareapp.fragments.ProfileFragment;
import edu.uga.cs.rideshareapp.fragments.RidesFragment;

public class RiderActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private TextView coinCount;

    private Fragment ridesFragment;
    private Fragment requestsFragment;
    private Fragment profileFragment;

    private Fragment currentFragment;
    private static final String SELECTED_TAB_KEY = "selected_tab_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation2);


        ViewCompat.setOnApplyWindowInsetsListener(bottomNavigationView, (v, insets) -> {
            int bottomInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;
            v.setPadding(0, 0, 0, bottomInset);
            return insets;
        });

        // Fetch existing fragments if exist
        ridesFragment = getSupportFragmentManager().findFragmentByTag("Rides");
        requestsFragment = getSupportFragmentManager().findFragmentByTag("Requests");
        profileFragment = getSupportFragmentManager().findFragmentByTag("Profile");

        if (ridesFragment == null) {
            ridesFragment = new RidesFragment();
        }
        if (requestsFragment == null) {
            requestsFragment = new PostedRequestsUserViewFragment();
        }
        if (profileFragment == null) {
            profileFragment = new ProfileFragment();
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, profileFragment, "Profile").hide(profileFragment)
                    .add(R.id.fragment_container, requestsFragment, "Requests").hide(requestsFragment)
                    .add(R.id.fragment_container, ridesFragment, "Rides")
                    .commit();
            currentFragment = ridesFragment;
            bottomNavigationView.setSelectedItemId(R.id.nav_rides);
        } else {
            String selectedTab = savedInstanceState.getString(SELECTED_TAB_KEY);
            if ("Profile".equals(selectedTab)) {
                currentFragment = profileFragment;
                bottomNavigationView.setSelectedItemId(R.id.nav_profile);
            } else if ("Requests".equals(selectedTab)) {
                currentFragment = requestsFragment;
                bottomNavigationView.setSelectedItemId(R.id.nav_post_request);
            } else {
                currentFragment = ridesFragment;
                bottomNavigationView.setSelectedItemId(R.id.nav_rides);
            }
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            if (itemId == R.id.nav_rides) {
                selectedFragment = ridesFragment;
            } else if (itemId == R.id.nav_post_request) {
                selectedFragment = requestsFragment;
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

        updateCoinsDisplay(); // Fetch initial coins
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

        updateCoinsDisplay(); // fetch coins when menu created

        actionView.setOnClickListener(v -> updateCoinsDisplay());

        return true;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (currentFragment == profileFragment) {
            outState.putString(SELECTED_TAB_KEY, "Profile");
        } else if (currentFragment == requestsFragment) {
            outState.putString(SELECTED_TAB_KEY, "Requests");
        } else {
            outState.putString(SELECTED_TAB_KEY, "Rides");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    protected void onResume() {
        super.onResume();
        updateCoinsDisplay();
    }
}