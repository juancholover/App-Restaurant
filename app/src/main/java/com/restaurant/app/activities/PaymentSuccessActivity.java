package com.restaurant.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.restaurant.app.R;
import java.text.NumberFormat;
import java.util.Locale;

public class PaymentSuccessActivity extends AppCompatActivity {
    private TextView textViewSuccessMessage, textViewTransactionId, textViewAmount, textViewPaymentMethod;
    private Button buttonContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);

        initViews();
        displayPaymentInfo();
    }

    private void initViews() {
        textViewSuccessMessage = findViewById(R.id.textViewSuccessMessage);
        textViewTransactionId = findViewById(R.id.textViewTransactionId);
        textViewAmount = findViewById(R.id.textViewAmount);
        textViewPaymentMethod = findViewById(R.id.textViewPaymentMethod);
        buttonContinue = findViewById(R.id.buttonContinue);

        buttonContinue.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void displayPaymentInfo() {
        String transactionId = getIntent().getStringExtra("transaction_id");
        String amount = getIntent().getStringExtra("amount");
        String paymentMethod = getIntent().getStringExtra("payment_method");

        textViewTransactionId.setText("ID de transacción: " + transactionId);

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "PE"));
        textViewAmount.setText("Monto pagado: " + currencyFormat.format(Double.parseDouble(amount)));

        String methodText = getPaymentMethodText(paymentMethod);
        textViewPaymentMethod.setText("Método de pago: " + methodText);
    }

    private String getPaymentMethodText(String method) {
        switch (method) {
            case "CREDIT_CARD":
                return "Tarjeta de Crédito";
            case "DEBIT_CARD":
                return "Tarjeta de Débito";
            case "CASH":
                return "Efectivo";
            case "DIGITAL_WALLET":
                return "Billetera Digital";
            case "PAYPAL":
                return "PayPal";
            case "YAPE":
                return "Yape";
            case "PLIN":
                return "Plin";
            default:
                return method;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}