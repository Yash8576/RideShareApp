package edu.uga.cs.rideshareapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.uga.cs.rideshareapp.R;
import edu.uga.cs.rideshareapp.adapters.RequestAdapter;
import edu.uga.cs.rideshareapp.models.Request;

public class RequestsFragment extends Fragment {

    private RecyclerView recyclerView;
    private RequestAdapter adapter;
    private final List<Request> requestList = new ArrayList<>();

    public RequestsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_requests, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recyclerViewRequests);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new RequestAdapter(requestList);
        recyclerView.setAdapter(adapter);

        // ✅ Add safe padding + 110dp bottom so nothing is hidden by bottom nav
        ViewCompat.setOnApplyWindowInsetsListener(recyclerView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(
                    v.getPaddingLeft(),
                    v.getPaddingTop(),
                    v.getPaddingRight(),
                    systemBars.bottom + 110
            );
            return insets;
        });

        loadRequestsFromFirebase();
    }

    private void loadRequestsFromFirebase() {
        FirebaseDatabase.getInstance().getReference("ride_requests")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        requestList.clear();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            Request request = child.getValue(Request.class);
                            if (request != null) {
                                requestList.add(request);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Failed to load ride requests", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}