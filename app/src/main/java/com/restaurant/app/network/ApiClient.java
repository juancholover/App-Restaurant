package com.restaurant.app.network;

import android.content.Context;
import android.util.Log;
import com.restaurant.app.utils.Constants;
import com.restaurant.app.utils.SessionManager;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ApiClient {
    private static final String TAG = "ApiClient";
    private static ApiClient instance;
    private Executor executor;
    private SessionManager sessionManager;

    private ApiClient(Context context) {
        executor = Executors.newFixedThreadPool(4);
        sessionManager = new SessionManager(context);
    }

    public static synchronized ApiClient getInstance(Context context) {
        if (instance == null) {
            instance = new ApiClient(context.getApplicationContext());
        }
        return instance;
    }

    public interface ApiCallback {
        void onSuccess(String response);
        void onError(String error);
    }

    public void get(String endpoint, ApiCallback callback) {
        executor.execute(() -> {
            try {
                URL url = new URL(Constants.BASE_URL + endpoint);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                String token = sessionManager.getToken();
                if (token != null && !token.isEmpty()) {
                    connection.setRequestProperty("Authorization", "Bearer " + token);
                }

                int responseCode = connection.getResponseCode();
                String response = readResponse(connection);

                Log.d(TAG, "GET " + endpoint + " -> " + responseCode);

                if (responseCode >= 200 && responseCode < 300) {
                    callback.onSuccess(response);
                } else {
                    callback.onError("Error " + responseCode + ": " + response);
                }

                connection.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "GET request failed: " + endpoint, e);
                callback.onError("Error de conexión: " + e.getMessage());
            }
        });
    }

    public void post(String endpoint, JSONObject requestBody, ApiCallback callback) {
        executor.execute(() -> {
            try {
                URL url = new URL(Constants.BASE_URL + endpoint);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.setDoOutput(true);

                String token = sessionManager.getToken();
                if (token != null && !token.isEmpty()) {
                    connection.setRequestProperty("Authorization", "Bearer " + token);
                }

                // Log de la petición (solo en debug)
                if (requestBody != null) {
                    Log.d(TAG, "POST " + endpoint + " Body: " + requestBody.toString());
                    try (OutputStream os = connection.getOutputStream()) {
                        byte[] input = requestBody.toString().getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }
                }

                int responseCode = connection.getResponseCode();
                String response = readResponse(connection);

                Log.d(TAG, "POST " + endpoint + " -> " + responseCode);

                if (responseCode >= 200 && responseCode < 300) {
                    callback.onSuccess(response);
                } else {
                    callback.onError(response);
                }

                connection.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "POST request failed: " + endpoint, e);
                callback.onError("Error de conexión: " + e.getMessage());
            }
        });
    }

    private String readResponse(HttpURLConnection connection) throws IOException {
        BufferedReader reader;
        if (connection.getResponseCode() >= 200 && connection.getResponseCode() < 300) {
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } else {
            reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
        }

        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        return response.toString();
    }
}