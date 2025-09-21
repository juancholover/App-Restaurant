package com.restaurant.app.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.restaurant.app.R;
import com.restaurant.app.models.User;
import com.restaurant.app.services.AuthService;

public class RegisterActivity extends AppCompatActivity {
    private EditText editTextFullName, editTextEmail, editTextPassword, editTextPhone, editTextAddress;
    private Button buttonRegister;
    private TextView textViewLogin;
    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authService = new AuthService(this);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        editTextFullName = findViewById(R.id.editTextFullName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextAddress = findViewById(R.id.editTextAddress);
        buttonRegister = findViewById(R.id.buttonRegister);
        textViewLogin = findViewById(R.id.textViewLogin);
    }

    private void setupClickListeners() {
        buttonRegister.setOnClickListener(v -> performRegister());
        textViewLogin.setOnClickListener(v -> finish());
    }

    private void performRegister() {
        String fullName = editTextFullName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();

        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Por favor completa los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        buttonRegister.setEnabled(false);
        buttonRegister.setText("Registrando...");

        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(password);
        user.setPhone(phone);
        user.setAddress(address);

        authService.register(user, new AuthService.AuthCallback() {
            @Override
            public void onSuccess(User registeredUser) {
                Toast.makeText(RegisterActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(RegisterActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                buttonRegister.setEnabled(true);
                buttonRegister.setText("Registrarse");
            }
        });
    }
}