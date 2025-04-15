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
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import edu.uga.cs.rideshareapp.fragments.PostedRidesUserViewFragment;
import edu.uga.cs.rideshareapp.fragments.ProfileFragment;
import edu.uga.cs.rideshareapp.R;
import edu.uga.cs.rideshareapp.fragments.RequestsFragment;

public class DriverActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    // Fragments
    private final Fragment requestsFragment = new RequestsFragment();
    private final Fragment ridesFragment = new PostedRidesUserViewFragment();
    private final Fragment profileFragment = new ProfileFragment();

    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // show back arrow
            getSupportActionBar().setDisplayShowTitleEnabled(false); // hides title
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation1);

        // Handle safe area padding for gesture navigation
        ViewCompat.setOnApplyWindowInsetsListener(bottomNavigationView,
                new OnApplyWindowInsetsListener() {
                    @Override
                    public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                        int bottomInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;
                        v.setPadding(0, 0, 0, bottomInset);
                        return insets;
                    }
                });

        // Add fragments once and hide the others
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, profileFragment, "Profile").hide(profileFragment)
                .add(R.id.fragment_container, ridesFragment, "Rides").hide(ridesFragment)
                .add(R.id.fragment_container, requestsFragment, "Requests")
                .commit();

        currentFragment = requestsFragment;

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_bar_menu, menu);

        // Get the action view of the menu item
        MenuItem coinItem = menu.findItem(R.id.action_coins);
        View actionView = coinItem.getActionView();

        // Find the TextView inside the custom layout
        TextView coinCount = actionView.findViewById(R.id.coin_count);

        // Set default or dynamic value
        coinCount.setText("100");

        actionView.setOnClickListener(v -> {
            // Optional: Show toast or navigate to coin history
        });

        return true;
    }
}