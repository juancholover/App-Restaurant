package com.restaurant.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.restaurant.app.R;
import com.restaurant.app.adapters.CartItemAdapter;
import com.restaurant.app.models.CartItem;
import com.restaurant.app.utils.CartManager;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity implements CartItemAdapter.OnCartItemChangeListener {
    private RecyclerView recyclerViewCart;
    private TextView textViewTotal, textViewEmptyCart;
    private Button buttonCheckout;

    private CartManager cartManager;
    private CartItemAdapter adapter;
    private List<CartItem> cartItems;
    private NumberFormat currencyFormat;
    private Long restaurantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        restaurantId = getIntent().getLongExtra("restaurant_id", -1);
        cartManager = CartManager.getInstance();
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "PE"));

        initViews();
        loadCartItems();
    }

    private void initViews() {
        recyclerViewCart = findViewById(R.id.recyclerViewCart);
        textViewTotal = findViewById(R.id.textViewTotal);
        textViewEmptyCart = findViewById(R.id.textViewEmptyCart);
        buttonCheckout = findViewById(R.id.buttonCheckout);

        recyclerViewCart.setLayoutManager(new LinearLayoutManager(this));

        buttonCheckout.setOnClickListener(v -> proceedToCheckout());
    }

    private void loadCartItems() {
        cartItems = cartManager.getCartItems();

        if (cartItems.isEmpty()) {
            showEmptyCart();
        } else {
            showCart();
        }

        adapter = new CartItemAdapter(cartItems, this);
        recyclerViewCart.setAdapter(adapter);
        updateTotal();
    }

    private void showEmptyCart() {
        recyclerViewCart.setVisibility(android.view.View.GONE);
        textViewEmptyCart.setVisibility(android.view.View.VISIBLE);
        buttonCheckout.setVisibility(android.view.View.GONE);
    }

    private void showCart() {
        recyclerViewCart.setVisibility(android.view.View.VISIBLE);
        textViewEmptyCart.setVisibility(android.view.View.GONE);
        buttonCheckout.setVisibility(android.view.View.VISIBLE);
    }

    private void updateTotal() {
        textViewTotal.setText("Total: " + currencyFormat.format(cartManager.getTotalAmount()));
    }

    @Override
    public void onQuantityChanged(CartItem item, int newQuantity) {
        cartManager.updateQuantity(item.getMenuItem(), newQuantity);
        item.setQuantity(newQuantity);
        adapter.notifyDataSetChanged();
        updateTotal();
    }

    @Override
    public void onItemRemoved(CartItem item) {
        cartManager.removeItem(item.getMenuItem());
        cartItems.remove(item);
        adapter.notifyDataSetChanged();
        updateTotal();

        if (cartItems.isEmpty()) {
            showEmptyCart();
        }
    }

    private void proceedToCheckout() {
        if (cartManager.isEmpty()) {
            Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, CheckoutActivity.class);
        intent.putExtra("restaurant_id", restaurantId);
        startActivity(intent);
    }
}