package com.restaurant.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.restaurant.app.R;
import com.restaurant.app.models.Payment;
import com.restaurant.app.services.PaymentService;
import com.restaurant.app.utils.CartManager;
import com.restaurant.app.utils.Constants;
import java.text.NumberFormat;
import java.util.Locale;

public class PaymentActivity extends AppCompatActivity {
    private TextView textViewTotal;
    private RadioGroup radioGroupPaymentMethod;
    private RadioButton radioCard, radioCash, radioDigitalWallet, radioMercadoPago;
    private Button buttonPay, buttonCardDetails;

    private PaymentService paymentService;
    private Long orderId;
    private Long restaurantId;
    private NumberFormat currencyFormat;
    private CartManager cartManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        orderId = getIntent().getLongExtra("order_id", -1);
        restaurantId = getIntent().getLongExtra("restaurant_id", -1);

        if (orderId == -1) {
            finish();
            return;
        }

        paymentService = new PaymentService(this);
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "PE"));
        cartManager = CartManager.getInstance();

        initViews();
        updateTotal();
    }

    private void initViews() {
        textViewTotal = findViewById(R.id.textViewTotal);
        radioGroupPaymentMethod = findViewById(R.id.radioGroupPaymentMethod);
        radioCard = findViewById(R.id.radioCard);
        radioCash = findViewById(R.id.radioCash);
        radioDigitalWallet = findViewById(R.id.radioDigitalWallet);
        radioMercadoPago = findViewById(R.id.radioMercadoPago); // NUEVO
        buttonPay = findViewById(R.id.buttonPay);
        buttonCardDetails = findViewById(R.id.buttonCardDetails);

        radioGroupPaymentMethod.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioCard) {
                buttonCardDetails.setVisibility(android.view.View.VISIBLE);
                buttonPay.setText("Pagar con Tarjeta");
            } else if (checkedId == R.id.radioMercadoPago) {
                buttonCardDetails.setVisibility(android.view.View.GONE);
                buttonPay.setText("Pagar con MercadoPago");
            } else {
                buttonCardDetails.setVisibility(android.view.View.GONE);
                buttonPay.setText("Confirmar Pago");
            }
        });

        buttonCardDetails.setOnClickListener(v -> openCardDetailsActivity());
        buttonPay.setOnClickListener(v -> processPayment());
    }

    private void updateTotal() {
        textViewTotal.setText("Total: " + currencyFormat.format(cartManager.getTotalAmount()));
    }

    private void processPayment() {
        int checkedId = radioGroupPaymentMethod.getCheckedRadioButtonId();
        if (checkedId == -1) {
            Toast.makeText(this, "Selecciona un método de pago", Toast.LENGTH_SHORT).show();
            return;
        }

        // NUEVO: Si seleccionó MercadoPago, redirigir a MercadoPagoPaymentActivity
        if (checkedId == R.id.radioMercadoPago) {
            Intent intent = new Intent(this, MercadoPagoPaymentActivity.class);
            intent.putExtra("order_id", orderId);
            intent.putExtra("restaurant_id", restaurantId);
            startActivity(intent);
            finish();
            return;
        }

        // Para otros métodos de pago, continuar con el flujo normal
        String paymentMethod = getPaymentMethodFromRadio(checkedId);

        buttonPay.setEnabled(false);
        buttonPay.setText("Procesando...");

        paymentService.processPayment(orderId, paymentMethod, new PaymentService.PaymentCallback() {
            @Override
            public void onSuccess(Payment payment) {
                if ("COMPLETED".equals(payment.getStatus())) {
                    cartManager.clearCart();
                    showPaymentSuccess(payment);
                } else if ("FAILED".equals(payment.getStatus())) {
                    showPaymentError(payment.getFailureReason());
                }
                buttonPay.setEnabled(true);
                buttonPay.setText("Pagar");
            }

            @Override
            public void onError(String error) {
                showPaymentError(error);
                buttonPay.setEnabled(true);
                buttonPay.setText("Pagar");
            }
        });
    }

    private String getPaymentMethodFromRadio(int checkedId) {
        if (checkedId == R.id.radioCard) {
            return Constants.PAYMENT_CREDIT_CARD;
        } else if (checkedId == R.id.radioCash) {
            return Constants.PAYMENT_CASH;
        } else if (checkedId == R.id.radioDigitalWallet) {
            return Constants.PAYMENT_DIGITAL_WALLET;
        }
        return Constants.PAYMENT_CREDIT_CARD;
    }

    private void showPaymentSuccess(Payment payment) {
        Intent intent = new Intent(this, PaymentSuccessActivity.class);
        intent.putExtra("transaction_id", payment.getTransactionId());
        intent.putExtra("amount", payment.getAmount().toString());
        intent.putExtra("payment_method", payment.getMethod());
        startActivity(intent);
        finish();
    }

    private void showPaymentError(String error) {
        Toast.makeText(this, "Error en el pago: " + error, Toast.LENGTH_LONG).show();
    }

    private void openCardDetailsActivity() {
        Intent intent = new Intent(this, CardDetailsActivity.class);
        startActivityForResult(intent, Constants.REQUEST_CARD_DETAILS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CARD_DETAILS && resultCode == RESULT_OK) {
            Toast.makeText(this, "Tarjeta guardada", Toast.LENGTH_SHORT).show();
        }
    }
}