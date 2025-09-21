package com.restaurant.app.services;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.restaurant.app.models.Restaurant;
import com.restaurant.app.network.ApiClient;
import com.restaurant.app.utils.Constants;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class RestaurantService {
    private ApiClient apiClient;
    private Handler mainHandler;

    public RestaurantService(Context context) {
        apiClient = ApiClient.getInstance(context);
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public interface RestaurantListCallback {
        void onSuccess(List<Restaurant> restaurants);
        void onError(String error);
    }

    public interface RestaurantCallback {
        void onSuccess(Restaurant restaurant);
        void onError(String error);
    }

    public void getAllRestaurants(RestaurantListCallback callback) {
        apiClient.get(Constants.RESTAURANTS, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    List<Restaurant> restaurants = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject restaurantJson = jsonArray.getJSONObject(i);
                        Restaurant restaurant = parseRestaurant(restaurantJson);
                        restaurants.add(restaurant);
                    }

                    mainHandler.post(() -> callback.onSuccess(restaurants));
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

    public void getRestaurantById(Long id, RestaurantCallback callback) {
        apiClient.get(Constants.RESTAURANTS + "/" + id, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject restaurantJson = new JSONObject(response);
                    Restaurant restaurant = parseRestaurant(restaurantJson);
                    mainHandler.post(() -> callback.onSuccess(restaurant));
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

    public void searchRestaurants(String query, RestaurantListCallback callback) {
        apiClient.get(Constants.RESTAURANTS + "/search?query=" + query, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    List<Restaurant> restaurants = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject restaurantJson = jsonArray.getJSONObject(i);
                        Restaurant restaurant = parseRestaurant(restaurantJson);
                        restaurants.add(restaurant);
                    }

                    mainHandler.post(() -> callback.onSuccess(restaurants));
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

    private Restaurant parseRestaurant(JSONObject json) throws Exception {
        Restaurant restaurant = new Restaurant();
        restaurant.setId(json.getLong("id"));
        restaurant.setName(json.getString("name"));
        restaurant.setDescription(json.optString("description"));
        restaurant.setAddress(json.optString("address"));
        restaurant.setPhone(json.optString("phone"));
        restaurant.setImageUrl(json.optString("imageUrl"));
        restaurant.setCapacity(json.optInt("capacity"));
        restaurant.setOpenTime(json.optString("openTime"));
        restaurant.setCloseTime(json.optString("closeTime"));
        return restaurant;
    }
}