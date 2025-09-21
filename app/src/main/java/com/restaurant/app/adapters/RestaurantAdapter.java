package com.restaurant.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.restaurant.app.R;
import com.restaurant.app.models.Restaurant;
import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder> {
    private List<Restaurant> restaurants;
    private OnRestaurantClickListener listener;

    public interface OnRestaurantClickListener {
        void onRestaurantClick(Restaurant restaurant);
    }

    public RestaurantAdapter(List<Restaurant> restaurants, OnRestaurantClickListener listener) {
        this.restaurants = restaurants;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_restaurant, parent, false);
        return new RestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        Restaurant restaurant = restaurants.get(position);
        holder.bind(restaurant);
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    class RestaurantViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewName, textViewDescription, textViewAddress;
        private ImageView imageViewRestaurant;

        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewRestaurantName);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewAddress = itemView.findViewById(R.id.textViewAddress);
            imageViewRestaurant = itemView.findViewById(R.id.imageViewRestaurant);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onRestaurantClick(restaurants.get(position));
                    }
                }
            });
        }

        public void bind(Restaurant restaurant) {
            textViewName.setText(restaurant.getName());
            textViewDescription.setText(restaurant.getDescription());
            textViewAddress.setText(restaurant.getAddress());
            // Aquí podrías cargar la imagen usando Picasso o Glide
            // Picasso.get().load(restaurant.getImageUrl()).into(imageViewRestaurant);
        }
    }
}