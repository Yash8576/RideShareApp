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

    public RequestsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_requests, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewRequests);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Request> requests = new ArrayList<>();
        requests.add(new Request("From: UGA Campus → To: Downtown", "Date: April 14, 2025 – 3:00 PM", "Notes: Needs to arrive on time"));
        requests.add(new Request("From: Athens → To: Atlanta", "Date: April 15, 2025 – 5:00 PM", "Notes: Carry small bag"));
        requests.add(new Request("From: UGA Library → To: Airport", "Date: April 16, 2025 – 9:00 AM", "Notes: Urgent"));

        RequestAdapter adapter = new RequestAdapter(requests);
        recyclerView.setAdapter(adapter);

        return view;
    }
}