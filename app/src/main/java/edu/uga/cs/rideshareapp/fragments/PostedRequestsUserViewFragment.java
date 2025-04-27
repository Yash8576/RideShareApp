package edu.uga.cs.rideshareapp.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edu.uga.cs.rideshareapp.R;
import edu.uga.cs.rideshareapp.adapters.PostedRequestAdapter;
import edu.uga.cs.rideshareapp.models.Request;

public class PostedRequestsUserViewFragment extends Fragment {

    private PostedRequestAdapter adapter;
    private final List<Request> requestList = new ArrayList<>();
    private final List<String> keyList = new ArrayList<>();
    private RecyclerView recyclerView;
    private View emptyStateLayout;

    public PostedRequestsUserViewFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_posted_requests_userview, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewPostedRequests);
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout);
        FloatingActionButton fab = view.findViewById(R.id.fabPostReq);

        adapter = new PostedRequestAdapter(requestList, position -> {
            String key = keyList.get(position);
            FirebaseDatabase.getInstance()
                    .getReference("ride_requests")
                    .child(key)
                    .removeValue();

            requestList.remove(position);
            keyList.remove(position);
            adapter.notifyItemRemoved(position);
            updateViewVisibility();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // ✅ Add safe bottom padding (system bars + 110dp)
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

        fab.setOnClickListener(v -> showBottomSheet());

        fetchRequestsFromFirebase();

        return view;
    }

    private void fetchRequestsFromFirebase() {
        final String currentEmail;

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        } else {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseDatabase.getInstance()
                .getReference("ride_requests")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        requestList.clear();
                        keyList.clear();

                        for (DataSnapshot data : snapshot.getChildren()) {
                            Request request = data.getValue(Request.class);
                            if (request != null && currentEmail.equals(request.userEmail)) {
                                requestList.add(request);
                                keyList.add(data.getKey());
                            }
                        }

                        adapter.updateKeys(keyList);
                        adapter.notifyDataSetChanged();
                        updateViewVisibility();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Failed to load requests", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showBottomSheet() {
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_post_request, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

        EditText fromField = bottomSheetView.findViewById(R.id.fromLocation);
        EditText toField = bottomSheetView.findViewById(R.id.toLocation);
        EditText dateField = bottomSheetView.findViewById(R.id.date);
        EditText timeField = bottomSheetView.findViewById(R.id.time);
        Button postButton = bottomSheetView.findViewById(R.id.buttonPostrequest);
        Button cancelButton = bottomSheetView.findViewById(R.id.buttonCancelrequest);

        dateField.setFocusable(false);
        dateField.setClickable(true);
        dateField.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(requireContext(),
                    (DatePicker view, int year, int month, int day) -> {
                        String formattedDate = String.format("%04d-%02d-%02d", year, month + 1, day);
                        dateField.setText(formattedDate);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        timeField.setFocusable(false);
        timeField.setClickable(true);
        timeField.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new TimePickerDialog(requireContext(),
                    (TimePicker view, int hour, int minute) -> {
                        String formattedTime = String.format("%02d:%02d", hour, minute);
                        timeField.setText(formattedTime);
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    false
            ).show();
        });

        postButton.setOnClickListener(btn -> {
            String from = fromField.getText().toString().trim();
            String to = toField.getText().toString().trim();
            String date = dateField.getText().toString().trim();
            String time = timeField.getText().toString().trim();
            String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

            if (from.isEmpty() || to.isEmpty() || date.isEmpty() || time.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Request request = new Request(from, to, date, time, userEmail);

            FirebaseDatabase.getInstance()
                    .getReference("ride_requests")
                    .push()
                    .setValue(request)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(getContext(), "Request posted successfully", Toast.LENGTH_SHORT).show();
                        bottomSheetDialog.dismiss();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to post: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        cancelButton.setOnClickListener(cancel -> bottomSheetDialog.dismiss());
    }

    private void updateViewVisibility() {
        if (requestList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);
        }
    }
}