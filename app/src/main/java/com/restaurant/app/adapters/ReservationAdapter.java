package com.restaurant.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.restaurant.app.R;
import com.restaurant.app.models.Reservation;
import java.util.List;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder> {
    private List<Reservation> reservations;

    public ReservationAdapter(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    @NonNull
    @Override
    public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reservation, parent, false);
        return new ReservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationViewHolder holder, int position) {
        Reservation reservation = reservations.get(position);
        holder.bind(reservation);
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    static class ReservationViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewDateTime, textViewPartySize, textViewStatus;

        public ReservationViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDateTime = itemView.findViewById(R.id.textViewDateTime);
            textViewPartySize = itemView.findViewById(R.id.textViewPartySize);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
        }

        public void bind(Reservation reservation) {
            textViewDateTime.setText("Fecha: " + reservation.getReservationDateTime());
            textViewPartySize.setText("Personas: " + reservation.getPartySize());
            textViewStatus.setText("Estado: " + reservation.getStatus());
        }
    }
}