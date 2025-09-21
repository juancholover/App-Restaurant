package com.restaurant.app.services;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.restaurant.app.models.User;
import com.restaurant.app.network.ApiClient;
import com.restaurant.app.utils.Constants;
import com.restaurant.app.utils.SessionManager;
import org.json.JSONObject;

public class AuthService {
    private ApiClient apiClient;
    private SessionManager sessionManager;
    private Handler mainHandler;

    public AuthService(Context context) {
        apiClient = ApiClient.getInstance(context);
        sessionManager = new SessionManager(context);
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public interface AuthCallback {
        void onSuccess(User user);
        void onError(String error);
    }

    public void login(String email, String password, AuthCallback callback) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("email", email);
            requestBody.put("password", password);

            apiClient.post(Constants.AUTH_LOGIN, requestBody, new ApiClient.ApiCallback() {
                @Override
                public void onSuccess(String response) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String token = jsonResponse.getString("token");
                        JSONObject userJson = jsonResponse.getJSONObject("user");

                        User user = new User();
                        user.setId(userJson.getLong("id"));
                        user.setEmail(userJson.getString("email"));
                        user.setFullName(userJson.getString("fullName"));
                        user.setRole(userJson.getString("role"));

                        sessionManager.createSession(token, user);
                        mainHandler.post(() -> callback.onSuccess(user));
                    } catch (Exception e) {
                        mainHandler.post(() -> callback.onError("Error al procesar respuesta"));
                    }
                }

                @Override
                public void onError(String error) {
                    try {
                        // Mejorar el manejo de errores JSON
                        if (error.contains("{")) {
                            JSONObject errorJson = new JSONObject(error.substring(error.indexOf("{")));
                            String message = errorJson.optString("message", "Error desconocido");
                            mainHandler.post(() -> callback.onError(message));
                        } else {
                            mainHandler.post(() -> callback.onError("Error de conexión"));
                        }
                    } catch (Exception e) {
                        mainHandler.post(() -> callback.onError("Error de conexión"));
                    }
                }
            });
        } catch (Exception e) {
            callback.onError("Error al crear petición: " + e.getMessage());
        }
    }

    public void register(User user, AuthCallback callback) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("fullName", user.getFullName());
            requestBody.put("email", user.getEmail());
            requestBody.put("password", user.getPassword()); // En producción, esto debe ser manejado de forma segura
            requestBody.put("phone", user.getPhone() != null ? user.getPhone() : "");
            requestBody.put("address", user.getAddress() != null ? user.getAddress() : "");

            apiClient.post(Constants.AUTH_REGISTER, requestBody, new ApiClient.ApiCallback() {
                @Override
                public void onSuccess(String response) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONObject userJson = jsonResponse.getJSONObject("user");

                        User registeredUser = new User();
                        registeredUser.setId(userJson.getLong("id"));
                        registeredUser.setEmail(userJson.getString("email"));
                        registeredUser.setFullName(userJson.getString("fullName"));
                        registeredUser.setRole(userJson.getString("role"));

                        mainHandler.post(() -> callback.onSuccess(registeredUser));
                    } catch (Exception e) {
                        mainHandler.post(() -> callback.onError("Error al procesar respuesta"));
                    }
                }

                @Override
                public void onError(String error) {
                    try {
                        if (error.contains("{")) {
                            JSONObject errorJson = new JSONObject(error.substring(error.indexOf("{")));
                            String message = errorJson.optString("error", "Error desconocido");
                            mainHandler.post(() -> callback.onError(message));
                        } else {
                            mainHandler.post(() -> callback.onError("Error de conexión"));
                        }
                    } catch (Exception e) {
                        mainHandler.post(() -> callback.onError("Error de conexión"));
                    }
                }
            });
        } catch (Exception e) {
            callback.onError("Error al crear petición: " + e.getMessage());
        }
    }

    public void logout() {
        sessionManager.logout();
    }

    public boolean isLoggedIn() {
        return sessionManager.isLoggedIn();
    }
}