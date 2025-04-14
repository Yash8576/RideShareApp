package edu.uga.cs.rideshareapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RequestsFragment extends Fragment {

    private PostedRequestAdapter adapter;

    public RequestsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_requests, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewRequests);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Ride> requestList = new ArrayList<>();
        requestList.add(new Ride("From: UGA Campus → To: Downtown", "Date: April 14, 2025 – 3:00 PM", "Notes: Be on time"));
        requestList.add(new Ride("From: Bus Stop → To: Main Library", "Date: April 15, 2025 – 9:15 AM", "Notes: Quiet ride preferred"));
        requestList.add(new Ride("From: Science Hall → To: Ramsey", "Date: April 16, 2025 – 5:45 PM", "Notes: Will bring pet"));

        adapter = new PostedRequestAdapter(requestList, position -> {
            requestList.remove(position);
            adapter.notifyItemRemoved(position);
            adapter.notifyItemRangeChanged(position, requestList.size());
        });

        recyclerView.setAdapter(adapter);

        return view;
    }
}