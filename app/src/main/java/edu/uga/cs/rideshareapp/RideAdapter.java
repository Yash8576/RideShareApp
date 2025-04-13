package edu.uga.cs.rideshareapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RideAdapter extends RecyclerView.Adapter<RideAdapter.RideViewHolder> {

    private final List<Ride> rideList;

    public RideAdapter(List<Ride> rideList) {
        this.rideList = rideList;
    }

    @NonNull
    @Override
    public RideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ride_item, parent, false);
        return new RideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RideViewHolder holder, int position) {
        Ride ride = rideList.get(position);
        holder.from.setText("From: " + ride.from);
        holder.to.setText("To: " + ride.to);
        holder.date.setText("Date: " + ride.date);
        holder.time.setText("Time: " + ride.time);
        holder.status.setText("Status: " + ride.status);
        holder.coins.setText(ride.coinsEarned);

        holder.confirm.setOnClickListener(v -> {
            ride.status = "Confirmed";
            ride.coinsEarned = "+50 Coins";
            notifyItemChanged(position);
        });

        holder.cancel.setOnClickListener(v -> {
            ride.status = "Cancelled";
            ride.coinsEarned = "+0 Coins";
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return rideList.size();
    }

    static class RideViewHolder extends RecyclerView.ViewHolder {
        TextView from, to, date, time, status, coins;
        Button confirm, cancel;

        public RideViewHolder(@NonNull View itemView) {
            super(itemView);
            from = itemView.findViewById(R.id.text_from);
            to = itemView.findViewById(R.id.text_to);
            date = itemView.findViewById(R.id.text_date);
            time = itemView.findViewById(R.id.text_time);
            status = itemView.findViewById(R.id.text_status);
            coins = itemView.findViewById(R.id.text_coins);
            confirm = itemView.findViewById(R.id.button_confirm);
            cancel = itemView.findViewById(R.id.button_cancel);
        }
    }
}