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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import edu.uga.cs.rideshareapp.R;
import edu.uga.cs.rideshareapp.models.Request;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {

    private final List<Request> requestList;

    public RequestAdapter(List<Request> requestList) {
        this.requestList = requestList;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        Request request = requestList.get(position);

        holder.fromText.setText("From: " + request.from);
        holder.toText.setText("To: " + request.to);
        holder.dateText.setText("Date: " + request.date);
        holder.timeText.setText("Time: " + request.time);

        holder.acceptButton.setOnClickListener(v -> {
            String driverEmail = FirebaseAuth.getInstance().getCurrentUser() != null
                    ? FirebaseAuth.getInstance().getCurrentUser().getEmail()
                    : "unknown";
            String driverUid = FirebaseAuth.getInstance().getCurrentUser() != null
                    ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                    : "unknown";

            if (driverEmail.equals(request.userEmail)) {
                Toast.makeText(v.getContext(), "You cannot accept your own request!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create accepted ride entry
            Map<String, Object> acceptedRide = new HashMap<>();
            acceptedRide.put("from", request.from);
            acceptedRide.put("to", request.to);
            acceptedRide.put("date", request.date);
            acceptedRide.put("time", request.time);
            acceptedRide.put("driverEmail", driverEmail);         // Accepting user
            acceptedRide.put("driverUid", driverUid);              // Accepting user's UID ✅
            acceptedRide.put("riderEmail", request.userEmail);     // Request poster
            acceptedRide.put("riderUid", request.userUid);         // Request poster's UID ✅
            acceptedRide.put("status", "Pending");
            acceptedRide.put("points", 50);
            acceptedRide.put("confirmedByDriver", false);
            acceptedRide.put("confirmedByRider", false);

            FirebaseDatabase.getInstance()
                    .getReference("accepted_rides")
                    .push()
                    .setValue(acceptedRide)
                    .addOnSuccessListener(unused -> {
                        FirebaseDatabase.getInstance()
                                .getReference("ride_requests")
                                .orderByChild("from").equalTo(request.from)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot child : snapshot.getChildren()) {
                                            Request r = child.getValue(Request.class);
                                            if (r != null &&
                                                    r.from.equals(request.from) &&
                                                    r.to.equals(request.to) &&
                                                    r.date.equals(request.date) &&
                                                    r.time.equals(request.time)) {
                                                child.getRef().removeValue();
                                                break;
                                            }
                                        }
                                        Toast.makeText(v.getContext(), "Request accepted successfully!", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(v.getContext(), "Failed to remove request", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(v.getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView fromText, toText, dateText, timeText;
        Button acceptButton;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            fromText = itemView.findViewById(R.id.textViewFrom);
            toText = itemView.findViewById(R.id.textViewTo);
            dateText = itemView.findViewById(R.id.textViewDate);
            timeText = itemView.findViewById(R.id.textViewTime);
            acceptButton = itemView.findViewById(R.id.buttonAccept);
        }
    }
}