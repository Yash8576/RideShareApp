package edu.uga.cs.rideshareapp.adapters;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class CoinsManager {

    private static final String USERS_PATH = "users";

    private static DatabaseReference getCoinsRef(String uid) {
        return FirebaseDatabase.getInstance().getReference(USERS_PATH).child(uid).child("coins");
    }

    private static FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    // Fetch current user's coins
    public static void fetchCoins(ValueEventListener listener) {
        FirebaseUser user = getCurrentUser();
        if (user != null) {
            getCoinsRef(user.getUid()).addListenerForSingleValueEvent(listener);
        }
    }

    // Listen continuously for coins updates
    public static void listenForCoins(ValueEventListener listener) {
        FirebaseUser user = getCurrentUser();
        if (user != null) {
            getCoinsRef(user.getUid()).addValueEventListener(listener);
        }
    }

    // Update coins by delta (+ or -)
    public static void updateCoins(int delta, String userId) {
        getCoinsRef(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer currentCoins = snapshot.getValue(Integer.class);
                int updatedCoins = (currentCoins != null ? currentCoins : 50) + delta;
                getCoinsRef(userId).setValue(updatedCoins);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    // Initialize user with 50 coins if not present
    public static void initializeCoinsIfNeeded(Context context) {
        FirebaseUser user = getCurrentUser();
        if (user != null) {
            getCoinsRef(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        getCoinsRef(user.getUid()).setValue(50);
                        Toast.makeText(context, "Account initialized with 50 coins!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, "Failed to initialize coins.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Handle coins transfer: +50 to driver, -50 from rider only when ride confirmed
    public static void handleCoinsWhenRideConfirmed(String driverUid, String riderUid) {
        updateCoins(50, driverUid);   // driver earns 50
        updateCoins(-50, riderUid);   // rider loses 50
    }
}