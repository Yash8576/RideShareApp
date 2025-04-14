package edu.uga.cs.rideshareapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PostedRideAdapter extends RecyclerView.Adapter<PostedRideAdapter.PostedRideViewHolder> {

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
    public PostedRideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_posted_rides, parent, false);
        return new PostedRideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostedRideViewHolder holder, int position) {
        Ride ride = rideList.get(position);
        holder.textFromTo.setText(ride.fromTo);
        holder.textDateTime.setText(ride.dateTime);
        holder.textNotes.setText(ride.notes);

        holder.cancelButton.setOnClickListener(v -> cancelListener.onRideCancelled(position));
    }

    @Override
    public int getItemCount() {
        return rideList.size();
    }

    public static class PostedRideViewHolder extends RecyclerView.ViewHolder {
        TextView textFromTo, textDateTime, textNotes;
        Button cancelButton;

        public PostedRideViewHolder(@NonNull View itemView) {
            super(itemView);
            textFromTo = itemView.findViewById(R.id.textViewFromTo);
            textDateTime = itemView.findViewById(R.id.textViewDateTime);
            textNotes = itemView.findViewById(R.id.textViewNotes);
            cancelButton = itemView.findViewById(R.id.buttonCancel);
        }
    }
}