// services/MercadoPagoService.java - CORREGIDO
package com.restaurant.app.services;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

// Imports correctos de MercadoPago
import com.mercadopago.android.px.core.MercadoPagoCheckout;
import com.mercadopago.android.px.model.BusinessPayment;
import com.mercadopago.android.px.model.GenericPayment;
import com.mercadopago.android.px.model.IPaymentDescriptor;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.PaymentResult;

// Imports de tu app
import com.restaurant.app.network.ApiClient;
import com.restaurant.app.utils.SessionManager;
import org.json.JSONObject;

public class MercadoPagoService {
    private static final String TAG = "MercadoPagoService";
    public static final int REQUEST_CODE_PAYMENT = 1001;

    private ApiClient apiClient;
    private SessionManager sessionManager;
    private Handler mainHandler;
    private Context context;

    public MercadoPagoService(Context context) {
        this.context = context;
        apiClient = ApiClient.getInstance(context);
        sessionManager = new SessionManager(context);
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public interface MercadoPagoCallback {
        void onSuccess(PaymentResult paymentResult);
        void onError(String error);
        void onCancel();
    }

    public interface PreferenceCallback {
        void onSuccess(String preferenceId, String publicKey);
        void onError(String error);
    }

    public void createPreference(Long orderId, PreferenceCallback callback) {
        try {
            Log.d(TAG, "Creating preference for order: " + orderId);

            JSONObject requestBody = new JSONObject();
            requestBody.put("orderId", orderId);
            requestBody.put("userId", sessionManager.getUserId());

            apiClient.post("mercadopago/create-preference", requestBody, new ApiClient.ApiCallback() {
                @Override
                public void onSuccess(String response) {
                    try {
                        Log.d(TAG, "Preference response: " + response);
                        JSONObject jsonResponse = new JSONObject(response);
                        String preferenceId = jsonResponse.getString("preferenceId");
                        String publicKey = jsonResponse.getString("publicKey");

                        mainHandler.post(() -> callback.onSuccess(preferenceId, publicKey));
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing preference response", e);
                        mainHandler.post(() -> callback.onError("Error processing server response"));
                    }
                }

                @Override
                public void onError(String error) {
                    Log.e(TAG, "Error creating preference: " + error);
                    mainHandler.post(() -> callback.onError(error));
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error creating preference request", e);
            callback.onError("Error creating request: " + e.getMessage());
        }
    }

    public void startMercadoPagoFlow(Activity activity, String preferenceId, String publicKey) {
        try {
            Log.d(TAG, "Starting MercadoPago flow with preference: " + preferenceId);

            // Configurar y lanzar MercadoPago Checkout
            MercadoPagoCheckout checkout = new MercadoPagoCheckout.Builder(publicKey, preferenceId)
                    .build();

            checkout.startPayment(activity, REQUEST_CODE_PAYMENT);

        } catch (Exception e) {
            Log.e(TAG, "Error starting MercadoPago flow", e);
        }
    }

    public PaymentResultInfo processPaymentResult(PaymentResult paymentResult) {
        PaymentResultInfo resultInfo = new PaymentResultInfo();

        try {
            if (paymentResult != null) {
                Log.d(TAG, "Processing payment result");

                resultInfo.status = paymentResult.getPaymentStatus();
                resultInfo.statusDetail = paymentResult.getPaymentStatusDetail();

                // Ojo: en PX no recibes el paymentId directamente, eso viene desde backend
                resultInfo.paymentResultStatus = paymentResult.getPaymentStatus();

                if (paymentResult.getPaymentData() != null) {
                    resultInfo.paymentMethodId = paymentResult.getPaymentData().getPaymentMethod().getId();
                    resultInfo.paymentTypeId = paymentResult.getPaymentData().getPaymentMethod().getPaymentTypeId();
                }

                Log.d(TAG, "Payment processed - Status: " + resultInfo.status +
                        ", Detail: " + resultInfo.statusDetail +
                        ", Method: " + resultInfo.paymentMethodId);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error processing payment result", e);
        }

        return resultInfo;
    }

    public static class PaymentResultInfo {
        public Long paymentId;
        public String status;
        public String statusDetail;
        public String paymentMethodId;
        public String paymentTypeId;
        public String paymentResultStatus;

        public boolean isApproved() {
            return "approved".equals(status);
        }

        public boolean isPending() {
            return "pending".equals(status) || "in_process".equals(status);
        }

        public boolean isRejected() {
            return "rejected".equals(status);
        }

        public boolean isCancelled() {
            return "cancelled".equals(status);
        }

        @Override
        public String toString() {
            return "PaymentResultInfo{" +
                    "paymentId=" + paymentId +
                    ", status='" + status + '\'' +
                    ", statusDetail='" + statusDetail + '\'' +
                    ", paymentMethodId='" + paymentMethodId + '\'' +
                    ", paymentTypeId='" + paymentTypeId + '\'' +
                    ", paymentResultStatus='" + paymentResultStatus + '\'' +
                    '}';
        }
    }
}