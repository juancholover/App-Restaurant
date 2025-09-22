package com.restaurant.app.services;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.restaurant.app.models.Payment;
import com.restaurant.app.network.ApiClient;
import com.restaurant.app.utils.Constants;
import org.json.JSONObject;

public class PaymentService {
    private ApiClient apiClient;
    private Handler mainHandler;

    public PaymentService(Context context) {
        apiClient = ApiClient.getInstance(context);
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public interface PaymentCallback {
        void onSuccess(Payment payment);
        void onError(String error);
    }

    public void processPayment(Long orderId, String paymentMethod, PaymentCallback callback) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("orderId", orderId);
            requestBody.put("method", paymentMethod);

            apiClient.post("payments/process", requestBody, new ApiClient.ApiCallback() {
                @Override
                public void onSuccess(String response) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        Payment payment = parsePayment(jsonResponse);
                        mainHandler.post(() -> callback.onSuccess(payment));
                    } catch (Exception e) {
                        mainHandler.post(() -> callback.onError("Error al procesar respuesta"));
                    }
                }

                @Override
                public void onError(String error) {
                    mainHandler.post(() -> callback.onError(error));
                }
            });
        } catch (Exception e) {
            callback.onError("Error al crear petición: " + e.getMessage());
        }
    }

    public void getPaymentByOrderId(Long orderId, PaymentCallback callback) {
        apiClient.get("payments/order/" + orderId, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    Payment payment = parsePayment(jsonResponse);
                    mainHandler.post(() -> callback.onSuccess(payment));
                } catch (Exception e) {
                    mainHandler.post(() -> callback.onError("Error al procesar respuesta"));
                }
            }

            @Override
            public void onError(String error) {
                mainHandler.post(() -> callback.onError(error));
            }
        });
    }

    private Payment parsePayment(JSONObject json) throws Exception {
        Payment payment = new Payment();
        payment.setId(json.getLong("id"));
        payment.setOrderId(json.getLong("orderId"));
        payment.setAmount(new java.math.BigDecimal(json.getString("amount")));
        payment.setMethod(json.getString("method"));
        payment.setStatus(json.getString("status"));
        payment.setTransactionId(json.optString("transactionId"));
        payment.setPaymentProvider(json.optString("paymentProvider"));
        payment.setCreatedAt(json.optString("createdAt"));
        payment.setPaidAt(json.optString("paidAt"));
        payment.setFailureReason(json.optString("failureReason"));
        payment.setCardLast4(json.optString("cardLast4"));
        payment.setCardBrand(json.optString("cardBrand"));
        return payment;
    }
}