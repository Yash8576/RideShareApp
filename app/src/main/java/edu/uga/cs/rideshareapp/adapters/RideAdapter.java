package edu.uga.cs.rideshareapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import edu.uga.cs.rideshareapp.R;
import edu.uga.cs.rideshareapp.models.Ride;
import java.util.HashMap;
import java.util.Map;

public class RideAdapter extends RecyclerView.Adapter<RideAdapter.RideViewHolder> {

    private final List<Ride> rideList;

    public RideAdapter(List<Ride> rideList) {
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
            String riderEmail = FirebaseAuth.getInstance().getCurrentUser() != null ?
                    FirebaseAuth.getInstance().getCurrentUser().getEmail() : "unknown";

            if (riderEmail.equals(ride.userEmail)) {
                Toast.makeText(v.getContext(), "You cannot join your own ride!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create accepted ride entry
            Map<String, Object> acceptedRide = new HashMap<>();
            acceptedRide.put("from", ride.from);
            acceptedRide.put("to", ride.to);
            acceptedRide.put("date", ride.date);
            acceptedRide.put("time", ride.time);
            acceptedRide.put("driverEmail", ride.userEmail); // posted by
            acceptedRide.put("riderEmail", riderEmail);      // current user
            acceptedRide.put("status", "Pending");
            acceptedRide.put("points", 50);

            // Add to accepted_rides
            FirebaseDatabase.getInstance().getReference("accepted_rides")
                    .push()
                    .setValue(acceptedRide)
                    .addOnSuccessListener(unused -> {
                        // Remove from ride_offers
                        FirebaseDatabase.getInstance().getReference("ride_offers")
                                .orderByChild("from").equalTo(ride.from)
                                .addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {
                                        for (com.google.firebase.database.DataSnapshot child : snapshot.getChildren()) {
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
                                        Toast.makeText(v.getContext(), "Ride joined successfully!", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull com.google.firebase.database.DatabaseError error) {
                                        Toast.makeText(v.getContext(), "Failed to join ride", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(v.getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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