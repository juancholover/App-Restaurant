package com.restaurant.app.activities;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.restaurant.app.R;
import com.restaurant.app.models.Order;
import com.restaurant.app.services.OrderService;
import java.util.ArrayList;
import java.util.List;

public class OrderHistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerViewOrders;
    private OrderService orderService;
    private List<Order> orders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        orderService = new OrderService(this);
        orders = new ArrayList<>();

        initViews();
        loadOrders();
    }

    private void initViews() {
        recyclerViewOrders = findViewById(R.id.recyclerViewOrders);
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));
        // Aquí podrías crear un OrderAdapter similar a ReservationAdapter
    }

    private void loadOrders() {
        orderService.getUserOrders(new OrderService.OrderListCallback() {
            @Override
            public void onSuccess(List<Order> orderList) {
                orders.clear();
                orders.addAll(orderList);
                // adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(OrderHistoryActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}