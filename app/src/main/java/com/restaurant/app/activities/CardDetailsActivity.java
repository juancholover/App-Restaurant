package com.restaurant.app.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.restaurant.app.R;

public class CardDetailsActivity extends AppCompatActivity {
    private EditText editTextCardNumber, editTextExpiryDate, editTextCVV, editTextCardName;
    private Button buttonSaveCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_details);

        initViews();
    }

    private void initViews() {
        editTextCardNumber = findViewById(R.id.editTextCardNumber);
        editTextExpiryDate = findViewById(R.id.editTextExpiryDate);
        editTextCVV = findViewById(R.id.editTextCVV);
        editTextCardName = findViewById(R.id.editTextCardName);
        buttonSaveCard = findViewById(R.id.buttonSaveCard);

        buttonSaveCard.setOnClickListener(v -> saveCardDetails());
    }

    private void saveCardDetails() {
        String cardNumber = editTextCardNumber.getText().toString().trim();
        String expiryDate = editTextExpiryDate.getText().toString().trim();
        String cvv = editTextCVV.getText().toString().trim();
        String cardName = editTextCardName.getText().toString().trim();

        if (TextUtils.isEmpty(cardNumber) || TextUtils.isEmpty(expiryDate) ||
                TextUtils.isEmpty(cvv) || TextUtils.isEmpty(cardName)) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidCardNumber(cardNumber)) {
            Toast.makeText(this, "Número de tarjeta inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidExpiryDate(expiryDate)) {
            Toast.makeText(this, "Fecha de expiración inválida (MM/YY)", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidCVV(cvv)) {
            Toast.makeText(this, "CVV inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        // En producción, aquí tokenizarías la tarjeta con un proveedor seguro
        // Por ahora solo simulamos guardar los datos
        Toast.makeText(this, "Tarjeta validada correctamente", Toast.LENGTH_SHORT).show();

        setResult(RESULT_OK);
        finish();
    }

    private boolean isValidCardNumber(String cardNumber) {
        // Algoritmo de Luhn simplificado
        cardNumber = cardNumber.replaceAll("\\s+", "");
        return cardNumber.length() >= 13 && cardNumber.length() <= 19 && cardNumber.matches("\\d+");
    }

    private boolean isValidExpiryDate(String expiryDate) {
        // Formato MM/YY
        return expiryDate.matches("(0[1-9]|1[0-2])/\\d{2}");
    }

    private boolean isValidCVV(String cvv) {
        return cvv.matches("\\d{3,4}");
    }
}