package edu.uga.cs.rideshareapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PostedRequestsUserViewFragment extends Fragment {

    private List<Ride> requestList = new ArrayList<>();
    private PostedRequestAdapter adapter;
    private RecyclerView recyclerView;
    private View emptyStateLayout;

    public PostedRequestsUserViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_posted_requests_userview, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewPostedRequests);
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout);
        FloatingActionButton fab = view.findViewById(R.id.fabPostReq);

        adapter = new PostedRequestAdapter(requestList, position -> {
            requestList.remove(position);
            adapter.notifyItemRemoved(position);
            adapter.notifyItemRangeChanged(position, requestList.size());
            updateViewVisibility();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        updateViewVisibility();

        fab.setOnClickListener(v -> {
            View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_post_request, null);
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
            bottomSheetDialog.setContentView(bottomSheetView);
            bottomSheetDialog.show();

            EditText fromField = bottomSheetDialog.findViewById(R.id.fromLocation);
            EditText toField = bottomSheetDialog.findViewById(R.id.toLocation);
            EditText dateTimeField = bottomSheetDialog.findViewById(R.id.dateTime);
            EditText notesField = bottomSheetDialog.findViewById(R.id.notes);
            Button postButton = bottomSheetDialog.findViewById(R.id.buttonPostrequest);
            Button cancelButton = bottomSheetDialog.findViewById(R.id.buttonCancelrequest);

            // 🗓 Setup Date Picker on click
            if (dateTimeField != null) {
                dateTimeField.setFocusable(false);
                dateTimeField.setClickable(true);
                dateTimeField.setOnClickListener(v1 -> {
                    Calendar calendar = Calendar.getInstance();
                    DatePickerDialog datePickerDialog = new DatePickerDialog(
                            requireContext(),
                            (DatePicker view1, int year, int month, int dayOfMonth) -> {
                                String formattedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                                dateTimeField.setText(formattedDate);
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                    );
                    datePickerDialog.getDatePicker().setCalendarViewShown(false); // Use spinner style
                    datePickerDialog.show();
                });
            }

            postButton.setOnClickListener(btn -> {
                String from = fromField.getText().toString();
                String to = toField.getText().toString();
                String dateTime = dateTimeField.getText().toString();
                String notes = notesField.getText().toString();

                if (from.isEmpty() || to.isEmpty() || dateTime.isEmpty()) {
                    Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                Ride newRequest = new Ride("From: " + from + " → To: " + to, "Date: " + dateTime, "Notes: " + (notes.isEmpty() ? "None" : notes));
                requestList.add(newRequest);
                adapter.notifyItemInserted(requestList.size() - 1);
                bottomSheetDialog.dismiss();
                updateViewVisibility();
            });

            cancelButton.setOnClickListener(btn -> bottomSheetDialog.dismiss());
        });

        return view;
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