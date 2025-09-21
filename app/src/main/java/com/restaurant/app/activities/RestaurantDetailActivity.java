package com.restaurant.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.restaurant.app.R;
import com.restaurant.app.adapters.MenuItemAdapter;
import com.restaurant.app.models.MenuItem;
import com.restaurant.app.models.Restaurant;
import com.restaurant.app.services.MenuService;
import com.restaurant.app.services.RestaurantService;
import com.restaurant.app.utils.CartManager;
import java.util.ArrayList;
import java.util.List;

public class RestaurantDetailActivity extends AppCompatActivity {
    private TextView textViewName, textViewDescription, textViewAddress, textViewHours;
    private ImageView imageViewRestaurant;
    private RecyclerView recyclerViewMenu;
    private Button buttonReservation, buttonViewCart;

    private Long restaurantId;
    private Restaurant restaurant;
    private RestaurantService restaurantService;
    private MenuService menuService;
    private MenuItemAdapter menuAdapter;
    private List<MenuItem> menuItems;
    private CartManager cartManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        restaurantId = getIntent().getLongExtra("restaurant_id", -1);
        if (restaurantId == -1) {
            finish();
            return;
        }

        initServices();
        initViews();
        loadRestaurantDetails();
        loadMenuItems();
    }

    private void initServices() {
        restaurantService = new RestaurantService(this);
        menuService = new MenuService(this);
        cartManager = CartManager.getInstance();
    }

    private void initViews() {
        textViewName = findViewById(R.id.textViewRestaurantName);
        textViewDescription = findViewById(R.id.textViewDescription);
        textViewAddress = findViewById(R.id.textViewAddress);
        textViewHours = findViewById(R.id.textViewHours);
        imageViewRestaurant = findViewById(R.id.imageViewRestaurant);
        recyclerViewMenu = findViewById(R.id.recyclerViewMenu);
        buttonReservation = findViewById(R.id.buttonReservation);
        buttonViewCart = findViewById(R.id.buttonViewCart);

        menuItems = new ArrayList<>();
        menuAdapter = new MenuItemAdapter(menuItems, this::onMenuItemClick);

        recyclerViewMenu.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMenu.setAdapter(menuAdapter);

        buttonReservation.setOnClickListener(v -> openReservationActivity());
        buttonViewCart.setOnClickListener(v -> openCartActivity());

        updateCartButton();
    }

    private void loadRestaurantDetails() {
        restaurantService.getRestaurantById(restaurantId, new RestaurantService.RestaurantCallback() {
            @Override
            public void onSuccess(Restaurant rest) {
                restaurant = rest;
                updateUI();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(RestaurantDetailActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMenuItems() {
        menuService.getMenuItemsByRestaurant(restaurantId, new MenuService.MenuItemListCallback() {
            @Override
            public void onSuccess(List<MenuItem> items) {
                menuItems.clear();
                menuItems.addAll(items);
                menuAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(RestaurantDetailActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI() {
        if (restaurant != null) {
            textViewName.setText(restaurant.getName());
            textViewDescription.setText(restaurant.getDescription());
            textViewAddress.setText(restaurant.getAddress());
            textViewHours.setText(restaurant.getOpenTime() + " - " + restaurant.getCloseTime());
            // Aquí podrías cargar la imagen usando Picasso o Glide
        }
    }

    private void onMenuItemClick(MenuItem menuItem) {
        cartManager.addItem(menuItem, 1);
        updateCartButton();
        Toast.makeText(this, menuItem.getName() + " agregado al carrito", Toast.LENGTH_SHORT).show();
    }

    private void updateCartButton() {
        int itemCount = cartManager.getItemCount();
        if (itemCount > 0) {
            buttonViewCart.setText("Ver Carrito (" + itemCount + ")");
            buttonViewCart.setVisibility(android.view.View.VISIBLE);
        } else {
            buttonViewCart.setVisibility(android.view.View.GONE);
        }
    }

    private void openReservationActivity() {
        Intent intent = new Intent(this, ReservationActivity.class);
        intent.putExtra("restaurant_id", restaurantId);
        intent.putExtra("restaurant_name", restaurant != null ? restaurant.getName() : "");
        startActivity(intent);
    }

    private void openCartActivity() {
        Intent intent = new Intent(this, CartActivity.class);
        intent.putExtra("restaurant_id", restaurantId);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartButton();
    }
}