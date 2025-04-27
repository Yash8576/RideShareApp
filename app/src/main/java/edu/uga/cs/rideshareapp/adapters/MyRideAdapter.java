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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.uga.cs.rideshareapp.R;
import edu.uga.cs.rideshareapp.models.MyRide;

public class MyRideAdapter extends RecyclerView.Adapter<MyRideAdapter.RideViewHolder> {

    private final List<MyRide> myRideList;
    private final List<String> keyList;
    private final Context context;
    private final Set<String> updatedCoinsForKeys = new HashSet<>(); // track which rides already updated coins

    public MyRideAdapter(Context context, List<MyRide> myRideList, List<String> keyList) {
        this.context = context;
        this.myRideList = myRideList;
        this.keyList = keyList;
    }

    @NonNull
    @Override
    public RideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_myride, parent, false);
        return new RideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RideViewHolder holder, int position) {
        MyRide myRide = myRideList.get(position);

        holder.from.setText("From: " + myRide.from);
        holder.to.setText("To: " + myRide.to);
        holder.date.setText("Date: " + myRide.date);
        holder.time.setText("Time: " + myRide.time);
        holder.coins.setText(myRide.coinsEarned);

        holder.driverConfirmed.setText("Driver Confirmation: " + (myRide.confirmedByDriver ? "Confirmed" : "Pending"));
        holder.riderConfirmed.setText("Rider Confirmation: " + (myRide.confirmedByRider ? "Confirmed" : "Pending"));

        String key = keyList.get(position);

        // 🔥 Attach live listener to this ride
        FirebaseDatabase.getInstance().getReference("accepted_rides")
                .child(key)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) return;

                        Boolean driverConfirmed = snapshot.child("confirmedByDriver").getValue(Boolean.class);
                        Boolean riderConfirmed = snapshot.child("confirmedByRider").getValue(Boolean.class);

                        if (driverConfirmed != null && riderConfirmed != null && driverConfirmed && riderConfirmed) {
                            // ✅ Both confirmed

                            if (!updatedCoinsForKeys.contains(key)) {
                                updatedCoinsForKeys.add(key); // avoid double updates

                                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                String currentUserEmail = currentUser != null ? currentUser.getEmail() : "";

                                String driverEmail = snapshot.child("driverEmail").getValue(String.class);
                                String riderEmail = snapshot.child("riderEmail").getValue(String.class);

                                if (currentUserEmail.equals(driverEmail)) {
                                    CoinsManager.updateCoins(50, context); // Driver earns 50 coins
                                    Toast.makeText(context, "🎉 You earned 50 coins!", Toast.LENGTH_SHORT).show();
                                } else if (currentUserEmail.equals(riderEmail)) {
                                    CoinsManager.updateCoins(-50, context); // Rider spends 50 coins
                                    Toast.makeText(context, "💸 You spent 50 coins!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });

        // ✅ Confirm Button Logic (only mark confirm)
        holder.confirm.setOnClickListener(v -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            String currentUserEmail = currentUser != null ? currentUser.getEmail() : "";

            FirebaseDatabase.getInstance().getReference("accepted_rides")
                    .child(key)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()) return;

                            String driverEmail = snapshot.child("driverEmail").getValue(String.class);
                            String riderEmail = snapshot.child("riderEmail").getValue(String.class);

                            if (currentUserEmail.equals(driverEmail)) {
                                snapshot.getRef().child("confirmedByDriver").setValue(true);
                            } else if (currentUserEmail.equals(riderEmail)) {
                                snapshot.getRef().child("confirmedByRider").setValue(true);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
        });

        holder.cancel.setOnClickListener(v -> {
            FirebaseDatabase.getInstance().getReference("accepted_rides")
                    .child(key)
                    .removeValue();
        });
    }

    @Override
    public int getItemCount() {
        return myRideList.size();
    }

    static class RideViewHolder extends RecyclerView.ViewHolder {
        TextView from, to, date, time, coins, driverConfirmed, riderConfirmed;
        Button confirm, cancel;

        public RideViewHolder(@NonNull View itemView) {
            super(itemView);
            from = itemView.findViewById(R.id.text_from);
            to = itemView.findViewById(R.id.text_to);
            date = itemView.findViewById(R.id.text_date);
            time = itemView.findViewById(R.id.text_time);
            coins = itemView.findViewById(R.id.text_coins);
            driverConfirmed = itemView.findViewById(R.id.text_driver_confirmed);
            riderConfirmed = itemView.findViewById(R.id.text_rider_confirmed);
            confirm = itemView.findViewById(R.id.button_confirm);
            cancel = itemView.findViewById(R.id.button_cancel);
        }
    }
}