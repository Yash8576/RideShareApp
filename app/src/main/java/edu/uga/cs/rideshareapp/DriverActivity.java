package edu.uga.cs.rideshareapp;

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

public class DriverActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // show back arrow
            getSupportActionBar().setDisplayShowTitleEnabled(false); // hides title if you want
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation1);

        // Handle safe area (bottom padding for nav bar)
        ViewCompat.setOnApplyWindowInsetsListener(bottomNavigationView,
                new OnApplyWindowInsetsListener() {
                    @Override
                    public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                        int bottomInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;
                        v.setPadding(0, 0, 0, bottomInset);
                        return insets;
                    }
                });

        // Load the default fragment
        loadFragment(new RequestsFragment());

        // Set listener for bottom navigation
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;

                int itemId = item.getItemId();
                if (itemId == R.id.nav_requests) {
                    fragment = new RequestsFragment();
                } else if (itemId == R.id.nav_post_ride) {
                    fragment = new PostedRidesUserViewFragment();
                } else if (itemId == R.id.nav_profile) {
                    fragment = new ProfileFragment();
                }

                if (fragment != null) {
                    loadFragment(fragment);
                    return true;
                }

                return false;
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // handles back navigation
        return true;
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_bar_menu, menu);

        // Get the action view of the menu item
        MenuItem coinItem = menu.findItem(R.id.action_coins);
        View actionView = coinItem.getActionView();

        // Find the TextView inside the custom layout
        TextView coinCount = actionView.findViewById(R.id.coin_count);

        // Set default or dynamic value (you'll update this later)
        coinCount.setText("100");

        // Optional: handle click if needed
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Optionally show a toast or open coin history
            }
        });

        return true;
    }
}