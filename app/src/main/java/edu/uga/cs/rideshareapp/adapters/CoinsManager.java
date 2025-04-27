package edu.uga.cs.rideshareapp.adapters;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CoinsManager {

    private static final String USERS_PATH = "users";

    public static void fetchCoins(ValueEventListener listener) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference coinsRef = FirebaseDatabase.getInstance().getReference(USERS_PATH)
                    .child(currentUser.getUid())
                    .child("coins");
            coinsRef.addListenerForSingleValueEvent(listener);
        }
    }

    public static void listenForCoins(ValueEventListener listener) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference coinsRef = FirebaseDatabase.getInstance().getReference(USERS_PATH)
                    .child(currentUser.getUid())
                    .child("coins");
            coinsRef.addValueEventListener(listener);
        }
    }

    public static void updateCoins(int delta, String userId) {
        DatabaseReference coinsRef = FirebaseDatabase.getInstance().getReference(USERS_PATH)
                .child(userId)
                .child("coins");

        coinsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer coins = snapshot.getValue(Integer.class);
                if (coins != null) {
                    coinsRef.setValue(coins + delta);
                } else {
                    coinsRef.setValue(50 + delta);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public static void initializeCoinsIfNeeded(Context context) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference coinsRef = FirebaseDatabase.getInstance().getReference(USERS_PATH)
                    .child(currentUser.getUid())
                    .child("coins");

            coinsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        coinsRef.setValue(50);
                        Toast.makeText(context, "Account initialized with 50 coins", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, "Failed to initialize coins", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // 🆕 NEW: Universal function to update coins for both driver and rider when ride is fully confirmed
    public static void handleCoinsWhenRideConfirmed(String driverUid, String riderUid) {
        updateCoins(50, driverUid);   // driver earns +50
        updateCoins(-50, riderUid);   // rider loses -50
    }
}