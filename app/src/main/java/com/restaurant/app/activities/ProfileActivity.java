package com.restaurant.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.restaurant.app.R;
import com.restaurant.app.adapters.ReservationAdapter;
import com.restaurant.app.models.Reservation;
import com.restaurant.app.services.AuthService;
import com.restaurant.app.services.ReservationService;
import com.restaurant.app.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    private TextView textViewUserName, textViewUserEmail;
    private Button buttonMyReservations, buttonMyOrders, buttonLogout;
    private RecyclerView recyclerViewReservations;

    private SessionManager sessionManager;
    private AuthService authService;
    private ReservationService reservationService;
    private ReservationAdapter reservationAdapter;
    private List<Reservation> reservations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sessionManager = new SessionManager(this);
        authService = new AuthService(this);
        reservationService = new ReservationService(this);

        initViews();
        loadUserInfo();
        loadReservations();
    }

    private void initViews() {
        textViewUserName = findViewById(R.id.textViewUserName);
        textViewUserEmail = findViewById(R.id.textViewUserEmail);
        buttonMyReservations = findViewById(R.id.buttonMyReservations);
        buttonMyOrders = findViewById(R.id.buttonMyOrders);
        buttonLogout = findViewById(R.id.buttonLogout);
        recyclerViewReservations = findViewById(R.id.recyclerViewReservations);

        reservations = new ArrayList<>();
        reservationAdapter = new ReservationAdapter(reservations);

        recyclerViewReservations.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewReservations.setAdapter(reservationAdapter);

        buttonMyReservations.setOnClickListener(v -> {
            // Toggle visibility de reservas
            if (recyclerViewReservations.getVisibility() == android.view.View.VISIBLE) {
                recyclerViewReservations.setVisibility(android.view.View.GONE);
            } else {
                recyclerViewReservations.setVisibility(android.view.View.VISIBLE);
                loadReservations();
            }
        });

        buttonMyOrders.setOnClickListener(v -> {
            startActivity(new Intent(this, OrderHistoryActivity.class));
        });

        buttonLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void loadUserInfo() {
        textViewUserName.setText(sessionManager.getUserName());
        textViewUserEmail.setText(sessionManager.getUserEmail());
    }

    private void loadReservations() {
        reservationService.getUserReservations(new ReservationService.ReservationListCallback() {
            @Override
            public void onSuccess(List<Reservation> reservationList) {
                reservations.clear();
                reservations.addAll(reservationList);
                reservationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ProfileActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Cerrar Sesión")
                .setMessage("¿Estás seguro de que quieres cerrar sesión?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    authService.logout();
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }
}