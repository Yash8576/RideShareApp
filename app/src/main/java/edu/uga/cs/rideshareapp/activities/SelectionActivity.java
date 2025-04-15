package edu.uga.cs.rideshareapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import edu.uga.cs.rideshareapp.R;

public class SelectionActivity extends AppCompatActivity {

    FrameLayout driverSection, riderSection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        // Link to the FrameLayouts that act as buttons
        driverSection = findViewById(R.id.driverSection);
        riderSection = findViewById(R.id.riderSection);

        // Set click listeners to navigate
        driverSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectionActivity.this, DriverActivity.class);
                startActivity(intent);
            }
        });

        riderSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectionActivity.this, RiderActivity.class);
                startActivity(intent);
            }
        });
    }
}