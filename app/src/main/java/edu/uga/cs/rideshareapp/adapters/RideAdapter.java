package edu.uga.cs.rideshareapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uga.cs.rideshareapp.R;
import edu.uga.cs.rideshareapp.models.Ride;

public class RideAdapter extends RecyclerView.Adapter<RideAdapter.RideViewHolder> {

    private final List<Ride> rideList;

    public RideAdapter(List<Ride> rideList) {
        this.rideList = rideList;
    }

    @NonNull
    @Override
    public RideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ride, parent, false);
        return new RideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RideViewHolder holder, int position) {
        Ride ride = rideList.get(position);
        holder.textFromTo.setText(ride.fromTo);
        holder.textDateTime.setText(ride.dateTime);
        holder.textNotes.setText(ride.notes);

        holder.joinButton.setOnClickListener(v ->
                Toast.makeText(v.getContext(), "Joined Ride", Toast.LENGTH_SHORT).show());

        holder.closeButton.setOnClickListener(v -> {
            rideList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, rideList.size());
        });
    }

    @Override
    public int getItemCount() {
        return rideList.size();
    }

    public static class RideViewHolder extends RecyclerView.ViewHolder {
        TextView textFromTo, textDateTime, textNotes;
        Button joinButton;
        ImageButton closeButton;

        public RideViewHolder(@NonNull View itemView) {
            super(itemView);
            textFromTo = itemView.findViewById(R.id.textViewFromToRide);
            textDateTime = itemView.findViewById(R.id.textViewDateTimeRide);
            textNotes = itemView.findViewById(R.id.textViewNotesRide);
            joinButton = itemView.findViewById(R.id.buttonJoinRide);
            closeButton = itemView.findViewById(R.id.buttonClose);
        }
    }
}