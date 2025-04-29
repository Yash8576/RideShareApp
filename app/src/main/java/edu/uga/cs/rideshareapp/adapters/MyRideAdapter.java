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

import java.util.List;

import edu.uga.cs.rideshareapp.R;
import edu.uga.cs.rideshareapp.models.MyRide;

public class MyRideAdapter extends RecyclerView.Adapter<MyRideAdapter.RideViewHolder> {

    private final List<MyRide> myRideList;
    private final List<String> keyList;
    private final Context context;

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

                            boolean driverConfirmed = snapshot.child("confirmedByDriver").getValue(Boolean.class) != null
                                    ? snapshot.child("confirmedByDriver").getValue(Boolean.class) : false;
                            boolean riderConfirmed = snapshot.child("confirmedByRider").getValue(Boolean.class) != null
                                    ? snapshot.child("confirmedByRider").getValue(Boolean.class) : false;

                            if (currentUserEmail.equals(driverEmail)) {
                                driverConfirmed = true;
                                snapshot.getRef().child("confirmedByDriver").setValue(true);
                            } else if (currentUserEmail.equals(riderEmail)) {
                                riderConfirmed = true;
                                snapshot.getRef().child("confirmedByRider").setValue(true);
                            }

                            // 🛡️ After setting, check immediately if both confirmed
                            if (driverConfirmed && riderConfirmed) {
                                String driverUid = snapshot.child("driverUid").getValue(String.class);
                                String riderUid = snapshot.child("riderUid").getValue(String.class);

                                if (driverUid != null && riderUid != null) {
                                    CoinsManager.handleCoinsWhenRideConfirmed(driverUid, riderUid);

                                    // Remove the ride from database
                                    snapshot.getRef().removeValue();

                                    // Remove from adapter list
                                    int adapterPosition = holder.getAdapterPosition();
                                    if (adapterPosition != RecyclerView.NO_POSITION) {
                                        myRideList.remove(adapterPosition);
                                        keyList.remove(adapterPosition);
                                        notifyItemRemoved(adapterPosition);
                                    }

                                    Toast.makeText(context, "Ride confirmed. Coins transferred!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) { }
                    });
        });

        holder.cancel.setOnClickListener(v -> {
            FirebaseDatabase.getInstance().getReference("accepted_rides")
                    .child(key)
                    .removeValue();

            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                myRideList.remove(adapterPosition);
                keyList.remove(adapterPosition);
                notifyItemRemoved(adapterPosition);
            }
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