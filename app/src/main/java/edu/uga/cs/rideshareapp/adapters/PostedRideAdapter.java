package edu.uga.cs.rideshareapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uga.cs.rideshareapp.R;
import edu.uga.cs.rideshareapp.models.Ride;

public class PostedRideAdapter extends RecyclerView.Adapter<PostedRideAdapter.RideViewHolder> {

    public interface OnRideCancelListener {
        void onRideCancelled(int position);
    }

    private final List<Ride> rideList;
    private final OnRideCancelListener cancelListener;

    public PostedRideAdapter(List<Ride> rideList, OnRideCancelListener cancelListener) {
        this.rideList = rideList;
        this.cancelListener = cancelListener;
    }

    @NonNull
    @Override
    public RideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_posted_rides, parent, false);
        return new RideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RideViewHolder holder, int position) {
        Ride ride = rideList.get(position);
        holder.fromText.setText("From: " + ride.from);
        holder.toText.setText("To: " + ride.to);
        holder.dateText.setText("Date: " + ride.date);
        holder.timeText.setText("Time: " + ride.time);

        holder.cancelButton.setOnClickListener(v -> cancelListener.onRideCancelled(position));
    }

    @Override
    public int getItemCount() {
        return rideList.size();
    }

    public static class RideViewHolder extends RecyclerView.ViewHolder {
        TextView fromText, toText, dateText, timeText;
        Button cancelButton;

        public RideViewHolder(@NonNull View itemView) {
            super(itemView);
            fromText = itemView.findViewById(R.id.textViewFrom);
            toText = itemView.findViewById(R.id.textViewTo);
            dateText = itemView.findViewById(R.id.textViewDate);
            timeText = itemView.findViewById(R.id.textViewTime);
            cancelButton = itemView.findViewById(R.id.buttonCancel);
        }
    }
}