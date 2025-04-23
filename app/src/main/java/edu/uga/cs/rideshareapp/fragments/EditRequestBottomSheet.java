package edu.uga.cs.rideshareapp.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import edu.uga.cs.rideshareapp.R;
import edu.uga.cs.rideshareapp.models.Request;

public class EditRequestBottomSheet extends BottomSheetDialogFragment {

    public interface OnRequestUpdatedListener {
        void onRequestUpdated(Request updatedRequest);
    }

    private Request request;
    private String firebaseKey;
    private OnRequestUpdatedListener listener;

    public EditRequestBottomSheet(Request request, String firebaseKey, OnRequestUpdatedListener listener) {
        this.request = request;
        this.firebaseKey = firebaseKey;
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_edit_request, container, false);

        EditText editFrom = view.findViewById(R.id.editFrom1);
        EditText editTo = view.findViewById(R.id.editTo1);
        EditText editDate = view.findViewById(R.id.editDate1);
        EditText editTime = view.findViewById(R.id.editTime1);
        Button buttonUpdate = view.findViewById(R.id.btnUpdateRequest1);
        Button btnCancelEdit = view.findViewById(R.id.btnCancelEdit1);

        // Pre-fill with existing data
        editFrom.setText(request.from);
        editTo.setText(request.to);
        editDate.setText(request.date);
        editTime.setText(request.time);

        editDate.setOnClickListener(v -> showDatePickerDialog(editDate));
        editTime.setOnClickListener(v -> showTimePickerDialog(editTime));
        btnCancelEdit.setOnClickListener(v -> dismiss());

        buttonUpdate.setOnClickListener(v -> {
            String newFrom = editFrom.getText().toString().trim();
            String newTo = editTo.getText().toString().trim();
            String newDate = editDate.getText().toString().trim();
            String newTime = editTime.getText().toString().trim();

            if (newFrom.isEmpty() || newTo.isEmpty() || newDate.isEmpty() || newTime.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update request object
            request.from = newFrom;
            request.to = newTo;
            request.date = newDate;
            request.time = newTime;

            // Ensure userEmail is always retained
            if (request.userEmail == null || request.userEmail.isEmpty()) {
                request.userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            }

            // Update in Firebase
            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference("ride_requests")
                    .child(firebaseKey);

            ref.setValue(request).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Request updated", Toast.LENGTH_SHORT).show();
                    if (listener != null) {
                        listener.onRequestUpdated(request);
                    }
                    dismiss();
                } else {
                    Toast.makeText(getContext(), "Failed to update request", Toast.LENGTH_SHORT).show();
                }
            });
        });

        return view;
    }

    private void showDatePickerDialog(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (DatePicker view, int year, int month, int dayOfMonth) -> {
                    String date = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    editText.setText(date);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void showTimePickerDialog(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                getContext(),
                (TimePicker view, int hourOfDay, int minute) -> {
                    String time = String.format("%02d:%02d", hourOfDay, minute);
                    editText.setText(time);
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
        );
        timePickerDialog.show();
    }
}