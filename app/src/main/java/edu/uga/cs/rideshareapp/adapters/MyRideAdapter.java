package edu.uga.cs.rideshareapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import edu.uga.cs.rideshareapp.R;
import edu.uga.cs.rideshareapp.models.MyRide;

public class MyRideAdapter extends RecyclerView.Adapter<MyRideAdapter.RideViewHolder> {

    private final List<MyRide> myRideList;
    private final List<String> keyList;  // Firebase keys for accepted rides

    public MyRideAdapter(List<MyRide> myRideList, List<String> keyList) {
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
        holder.status.setText("Status: " + myRide.status);
        holder.coins.setText(myRide.coinsEarned);

        String key = keyList.get(position);

        holder.confirm.setOnClickListener(v -> {
            myRide.status = "Confirmed";
            myRide.coinsEarned = "+50 Coins";
            notifyItemChanged(position);

            FirebaseDatabase.getInstance().getReference("accepted_rides").child(key)
                    .child("status").setValue("Confirmed");
            FirebaseDatabase.getInstance().getReference("accepted_rides").child(key)
                    .child("points").setValue("50");
        });

        holder.cancel.setOnClickListener(v -> {
            myRide.status = "Cancelled";
            myRide.coinsEarned = "+0 Coins";
            notifyItemChanged(position);

            FirebaseDatabase.getInstance().getReference("accepted_rides").child(key)
                    .child("status").setValue("Cancelled");
            FirebaseDatabase.getInstance().getReference("accepted_rides").child(key)
                    .child("points").setValue("0");
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