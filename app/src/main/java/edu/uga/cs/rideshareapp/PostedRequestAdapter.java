package edu.uga.cs.rideshareapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PostedRequestAdapter extends RecyclerView.Adapter<PostedRequestAdapter.RequestViewHolder> {

    public interface OnRequestCancelListener {
        void onRequestCancelled(int position);
    }

    private final List<Ride> requestList;
    private final OnRequestCancelListener cancelListener;

    public PostedRequestAdapter(List<Ride> requestList, OnRequestCancelListener cancelListener) {
        this.requestList = requestList;
        this.cancelListener = cancelListener;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_posted_requested, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        Ride request = requestList.get(position);
        holder.textFromTo.setText(request.fromTo);
        holder.textDateTime.setText(request.dateTime);
        holder.textNotes.setText(request.notes);

        holder.cancelButton.setOnClickListener(v -> cancelListener.onRequestCancelled(position));
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView textFromTo, textDateTime, textNotes;
        Button cancelButton;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            textFromTo = itemView.findViewById(R.id.textViewFromTo);
            textDateTime = itemView.findViewById(R.id.textViewDateTime);
            textNotes = itemView.findViewById(R.id.textViewNotes);
            cancelButton = itemView.findViewById(R.id.buttonCancel);
        }
    }
}