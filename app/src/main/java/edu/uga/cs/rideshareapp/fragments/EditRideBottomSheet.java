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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import edu.uga.cs.rideshareapp.R;
import edu.uga.cs.rideshareapp.models.Ride;

public class EditRideBottomSheet extends BottomSheetDialogFragment {

    public interface OnRideUpdatedListener {
        void onRideUpdated(Ride updatedRide);
    }

    private Ride ride;
    private OnRideUpdatedListener listener;
    private String firebaseKey;

    public EditRideBottomSheet(Ride ride, OnRideUpdatedListener listener) {
        this.ride = ride;
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_edit_ride, container, false);

        EditText editFrom = view.findViewById(R.id.editFrom);
        EditText editTo = view.findViewById(R.id.editTo);
        EditText editDate = view.findViewById(R.id.editDate);
        EditText editTime = view.findViewById(R.id.editTime);
        Button buttonUpdate = view.findViewById(R.id.btnUpdateRide);
        Button btnCancelEdit = view.findViewById(R.id.btnCancelEdit);

        editFrom.setText(ride.from);
        editTo.setText(ride.to);
        editDate.setText(ride.date);
        editTime.setText(ride.time);

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

            ride.from = newFrom;
            ride.to = newTo;
            ride.date = newDate;
            ride.time = newTime;

            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference("ride_offers")
                    .child(firebaseKey);

            ref.setValue(ride).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Ride updated", Toast.LENGTH_SHORT).show();
                    if (listener != null) {
                        listener.onRideUpdated(ride);
                    }
                    dismiss();
                } else {
                    Toast.makeText(getContext(), "Failed to update ride", Toast.LENGTH_SHORT).show();
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
                    String date = dayOfMonth + "/" + (month + 1) + "/" + year;
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