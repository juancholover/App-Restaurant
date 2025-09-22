package com.restaurant.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.restaurant.app.R;
import com.restaurant.app.models.Order;
import com.restaurant.app.services.OrderService;
import com.restaurant.app.utils.CartManager;
import com.restaurant.app.utils.Constants;
import java.text.NumberFormat;
import java.util.Locale;

public class CheckoutActivity extends AppCompatActivity {
    private TextView textViewTotal;
    private RadioGroup radioGroupOrderType;
    private RadioButton radioDelivery, radioPickup;
    private EditText editTextDeliveryAddress;
    private Button buttonPlaceOrder;

    private CartManager cartManager;
    private OrderService orderService;
    private Long restaurantId;
    private NumberFormat currencyFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        restaurantId = getIntent().getLongExtra("restaurant_id", -1);
        cartManager = CartManager.getInstance();
        orderService = new OrderService(this);
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "PE"));

        initViews();
        updateTotal();
    }

    private void initViews() {
        textViewTotal = findViewById(R.id.textViewTotal);
        radioGroupOrderType = findViewById(R.id.radioGroupOrderType);
        radioDelivery = findViewById(R.id.radioDelivery);
        radioPickup = findViewById(R.id.radioPickup);
        editTextDeliveryAddress = findViewById(R.id.editTextDeliveryAddress);
        buttonPlaceOrder = findViewById(R.id.buttonPlaceOrder);

        radioGroupOrderType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioDelivery) {
                editTextDeliveryAddress.setVisibility(android.view.View.VISIBLE);
            } else {
                editTextDeliveryAddress.setVisibility(android.view.View.GONE);
            }
        });

        // CAMBIO: Ahora procede al pago en lugar de completar la orden
        buttonPlaceOrder.setOnClickListener(v -> proceedToPayment());
    }

    private void updateTotal() {
        textViewTotal.setText("Total: " + currencyFormat.format(cartManager.getTotalAmount()));
    }

    private void proceedToPayment() {
        if (cartManager.isEmpty()) {
            Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show();
            return;
        }

        int checkedId = radioGroupOrderType.getCheckedRadioButtonId();
        if (checkedId == -1) {
            Toast.makeText(this, "Selecciona el tipo de pedido", Toast.LENGTH_SHORT).show();
            return;
        }

        String orderType = checkedId == R.id.radioDelivery ?
                Constants.ORDER_TYPE_DELIVERY : Constants.ORDER_TYPE_PICKUP;

        String deliveryAddress = null;
        if (orderType.equals(Constants.ORDER_TYPE_DELIVERY)) {
            deliveryAddress = editTextDeliveryAddress.getText().toString().trim();
            if (TextUtils.isEmpty(deliveryAddress)) {
                Toast.makeText(this, "Ingresa la dirección de entrega", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        buttonPlaceOrder.setEnabled(false);
        buttonPlaceOrder.setText("Creando pedido...");

        // Primero crear la orden
        orderService.createOrder(restaurantId, cartManager.getCartItems(),
                orderType, deliveryAddress, new OrderService.OrderCallback() {
                    @Override
                    public void onSuccess(Order order) {
                        // Luego proceder al pago
                        Intent intent = new Intent(CheckoutActivity.this, PaymentActivity.class);
                        intent.putExtra("order_id", order.getId());
                        intent.putExtra("restaurant_id", restaurantId);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(CheckoutActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                        buttonPlaceOrder.setEnabled(true);
                        buttonPlaceOrder.setText("Continuar al Pago");
                    }
                });
    }
}