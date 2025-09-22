package com.restaurant.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.restaurant.app.R;

public class PaymentPendingActivity extends AppCompatActivity {
    private TextView textViewPaymentId, textViewStatusDetail;
    private Button buttonContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_pending);

        initViews();
        displayPaymentInfo();
    }

    private void initViews() {
        textViewPaymentId = findViewById(R.id.textViewPaymentId);
        textViewStatusDetail = findViewById(R.id.textViewStatusDetail);
        buttonContinue = findViewById(R.id.buttonContinue);

        buttonContinue.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void displayPaymentInfo() {
        String paymentId = getIntent().getStringExtra("payment_id");
        String statusDetail = getIntent().getStringExtra("status_detail");

        textViewPaymentId.setText("ID de pago: " + paymentId);

        String statusMessage = getStatusMessage(statusDetail);
        textViewStatusDetail.setText(statusMessage);
    }

    private String getStatusMessage(String statusDetail) {
        if (statusDetail == null) return "Tu pago está siendo procesado.";

        switch (statusDetail) {
            case "pending_waiting_payment":
                return "Esperando el pago del usuario.";
            case "pending_waiting_transfer":
                return "Esperando transferencia bancaria.";
            case "pending_review_manual":
                return "Tu pago está en revisión manual.";
            default:
                return "Tu pago está siendo procesado. Te notificaremos cuando se confirme.";
        }
    }
}