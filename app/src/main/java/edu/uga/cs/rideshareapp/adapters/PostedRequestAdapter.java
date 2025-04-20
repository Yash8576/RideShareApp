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
import edu.uga.cs.rideshareapp.models.Request;

public class PostedRequestAdapter extends RecyclerView.Adapter<PostedRequestAdapter.RequestViewHolder> {

    public interface OnRequestCancelListener {
        void onRequestCancelled(int position);
    }

    private final List<Request> requestList;
    private final OnRequestCancelListener cancelListener;

    public PostedRequestAdapter(List<Request> requestList, OnRequestCancelListener cancelListener) {
        this.requestList = requestList;
        this.cancelListener = cancelListener;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_posted_requested, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        Request request = requestList.get(position);
        holder.fromText.setText("From: " + request.from);
        holder.toText.setText("To: " + request.to);
        holder.dateText.setText("Date: " + request.date);
        holder.timeText.setText("Time: " + request.time);

        holder.cancelBtn.setOnClickListener(v -> cancelListener.onRequestCancelled(position));
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView fromText, toText, dateText, timeText;
        Button cancelBtn;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            fromText = itemView.findViewById(R.id.textViewFrom);
            toText = itemView.findViewById(R.id.textViewTo);
            dateText = itemView.findViewById(R.id.textViewDate);
            timeText = itemView.findViewById(R.id.textViewTime);
            cancelBtn = itemView.findViewById(R.id.buttonCancel);
        }
    }
}