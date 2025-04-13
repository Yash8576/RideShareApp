package edu.uga.cs.rideshareapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MyRideAdapter extends RecyclerView.Adapter<MyRideAdapter.RideViewHolder> {

    private final List<MyRide> myRideList;

    public MyRideAdapter(List<MyRide> myRideList) {
        this.myRideList = myRideList;
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
        MyRide myRide = myRideList.get(position);
        holder.from.setText("From: " + myRide.from);
        holder.to.setText("To: " + myRide.to);
        holder.date.setText("Date: " + myRide.date);
        holder.time.setText("Time: " + myRide.time);
        holder.status.setText("Status: " + myRide.status);
        holder.coins.setText(myRide.coinsEarned);

        holder.confirm.setOnClickListener(v -> {
            myRide.status = "Confirmed";
            myRide.coinsEarned = "+50 Coins";
            notifyItemChanged(position);
        });

        holder.cancel.setOnClickListener(v -> {
            myRide.status = "Cancelled";
            myRide.coinsEarned = "+0 Coins";
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return myRideList.size();
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