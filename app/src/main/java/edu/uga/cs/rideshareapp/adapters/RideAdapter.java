package edu.uga.cs.rideshareapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uga.cs.rideshareapp.R;
import edu.uga.cs.rideshareapp.models.Ride;

public class RideAdapter extends RecyclerView.Adapter<RideAdapter.RideViewHolder> {

    private final Context context;
    private final List<Ride> rideList;

    public RideAdapter(Context context, List<Ride> rideList) {
        this.context = context;
        this.rideList = rideList;
    }

    @NonNull
    @Override
    public RideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ride, parent, false);
        return new RideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RideViewHolder holder, int position) {
        Ride ride = rideList.get(position);

        holder.fromText.setText("From: " + ride.from);
        holder.toText.setText("To: " + ride.to);
        holder.dateText.setText("Date: " + ride.date);
        holder.timeText.setText("Time: " + ride.time);

        holder.joinButton.setOnClickListener(v -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser == null) {
                Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show();
                return;
            }

            String riderEmail = currentUser.getEmail();
            String riderUid = currentUser.getUid(); // 🆕 get rider UID

            if (riderEmail.equals(ride.userEmail)) {
                Toast.makeText(context, "You cannot join your own ride!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Step 1: Check coin balance
            DatabaseReference coinsRef = FirebaseDatabase.getInstance().getReference("users")
                    .child(riderUid)
                    .child("coins");

            coinsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Integer coins = snapshot.getValue(Integer.class);

                    if (coins == null) {
                        Toast.makeText(context, "No coins found for your account", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (coins >= 50) {
                        // Step 2: Move ride to accepted_rides
                        Map<String, Object> acceptedRide = new HashMap<>();
                        acceptedRide.put("from", ride.from);
                        acceptedRide.put("to", ride.to);
                        acceptedRide.put("date", ride.date);
                        acceptedRide.put("time", ride.time);
                        acceptedRide.put("driverEmail", ride.userEmail);  // Poster
                        acceptedRide.put("riderEmail", riderEmail);       // Joiner
                        acceptedRide.put("status", "Pending");
                        acceptedRide.put("points", 50);

                        // 🆕 Save UIDs also
                        acceptedRide.put("driverUid", ride.userUid);
                        acceptedRide.put("riderUid", riderUid);

                        FirebaseDatabase.getInstance().getReference("accepted_rides")
                                .push()
                                .setValue(acceptedRide)
                                .addOnSuccessListener(unused -> {
                                    // Step 3: Remove from ride_offers
                                    FirebaseDatabase.getInstance().getReference("ride_offers")
                                            .orderByChild("from").equalTo(ride.from)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for (DataSnapshot child : snapshot.getChildren()) {
                                                        Ride match = child.getValue(Ride.class);
                                                        if (match != null &&
                                                                match.from.equals(ride.from) &&
                                                                match.to.equals(ride.to) &&
                                                                match.date.equals(ride.date) &&
                                                                match.time.equals(ride.time)) {
                                                            child.getRef().removeValue();
                                                            break;
                                                        }
                                                    }
                                                    Toast.makeText(context, "Ride moved to My Rides ✅", Toast.LENGTH_SHORT).show();
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    Toast.makeText(context, "Failed to remove ride from offers", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Failed to accept ride: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        // Not enough coins
                        Toast.makeText(context, "You cannot join the ride, balance less than 50 ❌", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, "Failed to check coin balance", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return rideList.size();
    }

    public static class RideViewHolder extends RecyclerView.ViewHolder {
        TextView fromText, toText, dateText, timeText;
        Button joinButton;

        public RideViewHolder(@NonNull View itemView) {
            super(itemView);
            fromText = itemView.findViewById(R.id.textViewFrom);
            toText = itemView.findViewById(R.id.textViewTo);
            dateText = itemView.findViewById(R.id.textViewDate);
            timeText = itemView.findViewById(R.id.textViewTime);
            joinButton = itemView.findViewById(R.id.buttonJoinRide);
        }
    }
}