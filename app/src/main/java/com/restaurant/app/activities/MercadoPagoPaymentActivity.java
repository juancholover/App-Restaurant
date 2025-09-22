package com.restaurant.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.mercadopago.android.px.model.PaymentResult;
import com.restaurant.app.R;
import com.restaurant.app.services.MercadoPagoService;
import com.restaurant.app.utils.CartManager;
import java.text.NumberFormat;
import java.util.Locale;

public class MercadoPagoPaymentActivity extends AppCompatActivity {
    private TextView textViewTotal, textViewOrderInfo;
    private Button buttonPayWithMercadoPago;

    private MercadoPagoService mercadoPagoService;
    private Long orderId;
    private Long restaurantId;
    private CartManager cartManager;
    private NumberFormat currencyFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mercadopago_payment);

        orderId = getIntent().getLongExtra("order_id", -1);
        restaurantId = getIntent().getLongExtra("restaurant_id", -1);

        if (orderId == -1) {
            finish();
            return;
        }

        mercadoPagoService = new MercadoPagoService(this);
        cartManager = CartManager.getInstance();
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "PE"));

        initViews();
        updateUI();
    }

    private void initViews() {
        textViewTotal = findViewById(R.id.textViewTotal);
        textViewOrderInfo = findViewById(R.id.textViewOrderInfo);
        buttonPayWithMercadoPago = findViewById(R.id.buttonPayWithMercadoPago);

        buttonPayWithMercadoPago.setOnClickListener(v -> startMercadoPagoFlow());
    }

    private void updateUI() {
        textViewTotal.setText("Total: " + currencyFormat.format(cartManager.getTotalAmount()));
        textViewOrderInfo.setText("Pedido #" + orderId);
    }

    private void startMercadoPagoFlow() {
        buttonPayWithMercadoPago.setEnabled(false);
        buttonPayWithMercadoPago.setText("Creando preferencia...");

        mercadoPagoService.createPreference(orderId, new MercadoPagoService.PreferenceCallback() {
            @Override
            public void onSuccess(String preferenceId, String publicKey) {
                mercadoPagoService.startMercadoPagoFlow(
                        MercadoPagoPaymentActivity.this,
                        preferenceId,
                        publicKey
                );

                buttonPayWithMercadoPago.setEnabled(true);
                buttonPayWithMercadoPago.setText("Pagar con MercadoPago");
            }

            @Override
            public void onError(String error) {
                Toast.makeText(MercadoPagoPaymentActivity.this,
                        "Error: " + error, Toast.LENGTH_LONG).show();

                buttonPayWithMercadoPago.setEnabled(true);
                buttonPayWithMercadoPago.setText("Pagar con MercadoPago");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MercadoPagoService.REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                // Pago exitoso
                PaymentResult paymentResult = (PaymentResult) data.getSerializableExtra("payment_result");
                handlePaymentResult(paymentResult);

            } else if (resultCode == Activity.RESULT_CANCELED) {
                // Pago cancelado
                Toast.makeText(this, "Pago cancelado", Toast.LENGTH_SHORT).show();

            } else {
                // Error en el pago
                Toast.makeText(this, "Error en el pago", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handlePaymentResult(PaymentResult paymentResult) {
        MercadoPagoService.PaymentResultInfo resultInfo =
                mercadoPagoService.processPaymentResult(paymentResult);

        if (resultInfo.isApproved()) {
            // Pago aprobado
            cartManager.clearCart();
            showPaymentSuccess(resultInfo);

        } else if (resultInfo.isPending()) {
            // Pago pendiente
            showPaymentPending(resultInfo);

        } else if (resultInfo.isRejected()) {
            // Pago rechazado
            showPaymentRejected(resultInfo);
        }
    }

    private void showPaymentSuccess(MercadoPagoService.PaymentResultInfo resultInfo) {
        Intent intent = new Intent(this, PaymentSuccessActivity.class);
        intent.putExtra("transaction_id", resultInfo.paymentId.toString());
        intent.putExtra("amount", cartManager.getTotalAmount().toString());
        intent.putExtra("payment_method", resultInfo.paymentMethodId);
        intent.putExtra("payment_provider", "MERCADOPAGO");
        startActivity(intent);
        finish();
    }

    private void showPaymentPending(MercadoPagoService.PaymentResultInfo resultInfo) {
        Intent intent = new Intent(this, PaymentPendingActivity.class);
        intent.putExtra("payment_id", resultInfo.paymentId.toString());
        intent.putExtra("status_detail", resultInfo.statusDetail);
        startActivity(intent);
        finish();
    }

    private void showPaymentRejected(MercadoPagoService.PaymentResultInfo resultInfo) {
        Toast.makeText(this,
                "Pago rechazado: " + resultInfo.statusDetail,
                Toast.LENGTH_LONG).show();
    }
}