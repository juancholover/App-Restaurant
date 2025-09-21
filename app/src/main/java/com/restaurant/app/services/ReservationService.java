package com.restaurant.app.services;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.restaurant.app.models.Reservation;
import com.restaurant.app.network.ApiClient;
import com.restaurant.app.utils.Constants;
import com.restaurant.app.utils.SessionManager;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ReservationService {
    private ApiClient apiClient;
    private SessionManager sessionManager;
    private Handler mainHandler;

    public ReservationService(Context context) {
        apiClient = ApiClient.getInstance(context);
        sessionManager = new SessionManager(context);
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public interface ReservationListCallback {
        void onSuccess(List<Reservation> reservations);
        void onError(String error);
    }

    public interface ReservationCallback {
        void onSuccess(Reservation reservation);
        void onError(String error);
    }

    public void getUserReservations(ReservationListCallback callback) {
        long userId = sessionManager.getUserId();
        apiClient.get(Constants.RESERVATIONS + "/user/" + userId, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    List<Reservation> reservations = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject reservationJson = jsonArray.getJSONObject(i);
                        Reservation reservation = parseReservation(reservationJson);
                        reservations.add(reservation);
                    }

                    mainHandler.post(() -> callback.onSuccess(reservations));
                } catch (Exception e) {
                    mainHandler.post(() -> callback.onError("Error parsing response"));
                }
            }

            @Override
            public void onError(String error) {
                mainHandler.post(() -> callback.onError(error));
            }
        });
    }

    public void createReservation(Long restaurantId, String dateTime, int partySize, String specialRequests, ReservationCallback callback) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("userId", sessionManager.getUserId());
            requestBody.put("restaurantId", restaurantId);
            requestBody.put("reservationDateTime", dateTime);
            requestBody.put("partySize", partySize);
            requestBody.put("specialRequests", specialRequests != null ? specialRequests : "");

            apiClient.post(Constants.RESERVATIONS, requestBody, new ApiClient.ApiCallback() {
                @Override
                public void onSuccess(String response) {
                    try {
                        JSONObject reservationJson = new JSONObject(response);
                        Reservation reservation = parseReservation(reservationJson);
                        mainHandler.post(() -> callback.onSuccess(reservation));
                    } catch (Exception e) {
                        mainHandler.post(() -> callback.onError("Error parsing response"));
                    }
                }

                @Override
                public void onError(String error) {
                    mainHandler.post(() -> callback.onError(error));
                }
            });
        } catch (Exception e) {
            callback.onError("Error creating request: " + e.getMessage());
        }
    }

    private Reservation parseReservation(JSONObject json) throws Exception {
        Reservation reservation = new Reservation();
        reservation.setId(json.getLong("id"));
        reservation.setReservationDateTime(json.optString("reservationDateTime"));
        reservation.setPartySize(json.optInt("partySize"));
        reservation.setSpecialRequests(json.optString("specialRequests"));
        reservation.setStatus(json.optString("status"));
        reservation.setCreatedAt(json.optString("createdAt"));
        return reservation;
    }
}