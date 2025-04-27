package edu.uga.cs.rideshareapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uga.cs.rideshareapp.R;
import edu.uga.cs.rideshareapp.fragments.EditRideBottomSheet;
import edu.uga.cs.rideshareapp.models.Ride;

public class PostedRideAdapter extends RecyclerView.Adapter<PostedRideAdapter.RideViewHolder> {

    public interface OnRideCancelListener {
        void onRideCancelled(int position);
    }

    private final List<Ride> rideList;
    private final List<String> keyList;
    private final OnRideCancelListener cancelListener;

    public PostedRideAdapter(List<Ride> rideList, List<String> keyList, OnRideCancelListener cancelListener) {
        this.rideList = rideList;
        this.keyList = keyList;
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
        Ride currentRide = rideList.get(position);
        String firebaseKey = keyList.get(position);

        holder.fromText.setText("From: " + currentRide.from);
        holder.toText.setText("To: " + currentRide.to);
        holder.dateText.setText("Date: " + currentRide.date);
        holder.timeText.setText("Time: " + currentRide.time);

        holder.cancelButton.setOnClickListener(v -> cancelListener.onRideCancelled(position));

        holder.buttonEdit.setOnClickListener(v -> {
            EditRideBottomSheet bottomSheet = new EditRideBottomSheet(currentRide, firebaseKey, updatedRide -> {
                rideList.set(position, updatedRide);
                notifyItemChanged(position);
            });

            Context context = holder.itemView.getContext();
            if (context instanceof FragmentActivity) {
                ((FragmentActivity) context).getSupportFragmentManager()
                        .beginTransaction()
                        .add(bottomSheet, "EditRideBottomSheet")
                        .commit();
            } else {
                Toast.makeText(context, "Unable to open editor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return rideList.size();
    }

    public static class RideViewHolder extends RecyclerView.ViewHolder {
        TextView fromText, toText, dateText, timeText;
        Button cancelButton, buttonEdit;

        public RideViewHolder(@NonNull View itemView) {
            super(itemView);
            fromText = itemView.findViewById(R.id.textViewFrom);
            toText = itemView.findViewById(R.id.textViewTo);
            dateText = itemView.findViewById(R.id.textViewDate);
            timeText = itemView.findViewById(R.id.textViewTime);
            cancelButton = itemView.findViewById(R.id.buttonCancel);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
        }
    }
}