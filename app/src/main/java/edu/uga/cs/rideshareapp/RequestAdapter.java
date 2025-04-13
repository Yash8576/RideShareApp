package edu.uga.cs.rideshareapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {

    private final List<Request> requestList;

    public RequestAdapter(List<Request> requestList) {
        this.requestList = requestList;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        Request request = requestList.get(position);

        holder.textFromTo.setText(request.fromTo);
        holder.textDateTime.setText(request.dateTime);
        holder.textNotes.setText(request.notes);

        holder.buttonAccept.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Request Accepted", Toast.LENGTH_SHORT).show();
        });

        holder.buttonDecline.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Request Declined", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView textFromTo, textDateTime, textNotes;
        Button buttonAccept, buttonDecline;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            textFromTo = itemView.findViewById(R.id.textViewFromTo);
            textDateTime = itemView.findViewById(R.id.textViewDateTime);
            textNotes = itemView.findViewById(R.id.textViewNotes);
            buttonAccept = itemView.findViewById(R.id.buttonAccept);
            buttonDecline = itemView.findViewById(R.id.buttonDecline);
        }
    }
}