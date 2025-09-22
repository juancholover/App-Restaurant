// activities/PaymentResultActivity.java
package com.restaurant.app.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.restaurant.app.utils.CartManager;

public class PaymentResultActivity extends AppCompatActivity {
    private static final String TAG = "PaymentResultActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Manejar deep link de MercadoPago
        handleDeepLink();
    }

    private void handleDeepLink() {
        Intent intent = getIntent();
        Uri data = intent.getData();

        if (data != null) {
            Log.d(TAG, "Deep link received: " + data.toString());

            String status = data.getQueryParameter("collection_status");
            String paymentId = data.getQueryParameter("collection_id");
            String externalReference = data.getQueryParameter("external_reference");

            Log.d(TAG, "Status: " + status);
            Log.d(TAG, "Payment ID: " + paymentId);
            Log.d(TAG, "External Reference: " + externalReference);

            // Manejar según el estado
            if ("approved".equals(status)) {
                handleApprovedPayment(paymentId, externalReference);
            } else if ("pending".equals(status)) {
                handlePendingPayment(paymentId, externalReference);
            } else if ("rejected".equals(status)) {
                handleRejectedPayment(paymentId, externalReference);
            } else {
                // Estado desconocido, ir a MainActivity
                goToMainActivity();
            }
        } else {
            // No hay data, ir a MainActivity
            goToMainActivity();
        }
    }

    private void handleApprovedPayment(String paymentId, String externalReference) {
        Log.d(TAG, "Payment approved: " + paymentId);

        // Limpiar carrito
        CartManager.getInstance().clearCart();

        // Ir a pantalla de éxito
        Intent intent = new Intent(this, PaymentSuccessActivity.class);
        intent.putExtra("transaction_id", paymentId);
        intent.putExtra("payment_method", "MERCADOPAGO");
        intent.putExtra("external_reference", externalReference);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void handlePendingPayment(String paymentId, String externalReference) {
        Log.d(TAG, "Payment pending: " + paymentId);

        // Ir a pantalla de pendiente
        Intent intent = new Intent(this, PaymentPendingActivity.class);
        intent.putExtra("payment_id", paymentId);
        intent.putExtra("external_reference", externalReference);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void handleRejectedPayment(String paymentId, String externalReference) {
        Log.d(TAG, "Payment rejected: " + paymentId);

        // Ir a MainActivity con mensaje de error
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("payment_error", "Pago rechazado");
        intent.putExtra("payment_id", paymentId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}