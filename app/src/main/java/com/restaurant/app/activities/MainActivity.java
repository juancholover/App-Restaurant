package com.restaurant.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.restaurant.app.R;
import com.restaurant.app.adapters.RestaurantAdapter;
import com.restaurant.app.models.Restaurant;
import com.restaurant.app.services.AuthService;
import com.restaurant.app.services.RestaurantService;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RestaurantAdapter adapter;
    private RestaurantService restaurantService;
    private AuthService authService;
    private List<Restaurant> restaurants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initServices();
        checkAuthAndRedirect();
        initViews();
        loadRestaurants();
    }

    private void initServices() {
        restaurantService = new RestaurantService(this);
        authService = new AuthService(this);
    }

    private void checkAuthAndRedirect() {
        if (!authService.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewRestaurants);
        FloatingActionButton fabProfile = findViewById(R.id.fabProfile);

        restaurants = new ArrayList<>();
        adapter = new RestaurantAdapter(restaurants, this::onRestaurantClick);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        fabProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
        });
    }

    private void loadRestaurants() {
        restaurantService.getAllRestaurants(new RestaurantService.RestaurantListCallback() {
            @Override
            public void onSuccess(List<Restaurant> restaurantList) {
                restaurants.clear();
                restaurants.addAll(restaurantList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(MainActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onRestaurantClick(Restaurant restaurant) {
        Intent intent = new Intent(this, RestaurantDetailActivity.class);
        intent.putExtra("restaurant_id", restaurant.getId());
        intent.putExtra("restaurant_name", restaurant.getName());
        startActivity(intent);
    }
}