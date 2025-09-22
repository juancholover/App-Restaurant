package com.restaurant.app.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.restaurant.app.R;
import com.restaurant.app.models.Reservation;
import com.restaurant.app.services.ReservationService;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ReservationActivity extends AppCompatActivity {
    private TextView textViewRestaurantName, textViewSelectedDateTime;
    private EditText editTextPartySize, editTextSpecialRequests;
    private Button buttonSelectDateTime, buttonConfirmReservation;

    private ReservationService reservationService;
    private Long restaurantId;
    private String restaurantName;
    private Calendar selectedDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        restaurantId = getIntent().getLongExtra("restaurant_id", -1);
        restaurantName = getIntent().getStringExtra("restaurant_name");

        reservationService = new ReservationService(this);

        initViews();
    }

    private void initViews() {
        textViewRestaurantName = findViewById(R.id.textViewRestaurantName);
        textViewSelectedDateTime = findViewById(R.id.textViewSelectedDateTime);
        editTextPartySize = findViewById(R.id.editTextPartySize);
        editTextSpecialRequests = findViewById(R.id.editTextSpecialRequests);
        buttonSelectDateTime = findViewById(R.id.buttonSelectDateTime);
        buttonConfirmReservation = findViewById(R.id.buttonConfirmReservation);

        textViewRestaurantName.setText(restaurantName);

        buttonSelectDateTime.setOnClickListener(v -> showDateTimePicker());
        buttonConfirmReservation.setOnClickListener(v -> confirmReservation());
    }

    private void showDateTimePicker() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            this,
                            (timeView, hourOfDay, minute) -> {
                                selectedDateTime = Calendar.getInstance();
                                selectedDateTime.set(year, month, dayOfMonth, hourOfDay, minute, 0);
                                updateDateTimeDisplay();
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                    );
                    timePickerDialog.show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void updateDateTimeDisplay() {
        if (selectedDateTime != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            textViewSelectedDateTime.setText(formatter.format(selectedDateTime.getTime()));
            textViewSelectedDateTime.setVisibility(android.view.View.VISIBLE);
        }
    }

    private void confirmReservation() {
        if (selectedDateTime == null) {
            Toast.makeText(this, "Selecciona fecha y hora", Toast.LENGTH_SHORT).show();
            return;
        }

        String partySizeStr = editTextPartySize.getText().toString().trim();
        if (TextUtils.isEmpty(partySizeStr)) {
            Toast.makeText(this, "Ingresa el número de personas", Toast.LENGTH_SHORT).show();
            return;
        }

        int partySize;
        try {
            partySize = Integer.parseInt(partySizeStr);
            if (partySize <= 0) {
                Toast.makeText(this, "Número de personas debe ser mayor a 0", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Número de personas inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        String specialRequests = editTextSpecialRequests.getText().toString().trim();

        buttonConfirmReservation.setEnabled(false);
        buttonConfirmReservation.setText("Procesando...");

        // Formato ISO 8601 (ejemplo: "2025-09-22T12:34:56")
        SimpleDateFormat isoFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        String dateTimeStr = isoFormatter.format(selectedDateTime.getTime());

        reservationService.createReservation(restaurantId, dateTimeStr, partySize, specialRequests,
                new ReservationService.ReservationCallback() {
                    @Override
                    public void onSuccess(Reservation reservation) {
                        Toast.makeText(ReservationActivity.this, "Reserva confirmada", Toast.LENGTH_LONG).show();
                        finish();
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(ReservationActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                        buttonConfirmReservation.setEnabled(true);
                        buttonConfirmReservation.setText("Confirmar Reserva");
                    }
                });
    }
}