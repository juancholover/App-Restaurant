package com.restaurant.app.services;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.restaurant.app.models.Order;
import com.restaurant.app.models.CartItem;
import com.restaurant.app.network.ApiClient;
import com.restaurant.app.utils.Constants;
import com.restaurant.app.utils.SessionManager;
import org.json.JSONArray;
import org.json.JSONObject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderService {
    private ApiClient apiClient;
    private SessionManager sessionManager;
    private Handler mainHandler;

    public OrderService(Context context) {
        apiClient = ApiClient.getInstance(context);
        sessionManager = new SessionManager(context);
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public interface OrderListCallback {
        void onSuccess(List<Order> orders);
        void onError(String error);
    }

    public interface OrderCallback {
        void onSuccess(Order order);
        void onError(String error);
    }

    public void getUserOrders(OrderListCallback callback) {
        long userId = sessionManager.getUserId();
        apiClient.get(Constants.ORDERS + "/user/" + userId, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    List<Order> orders = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject orderJson = jsonArray.getJSONObject(i);
                        Order order = parseOrder(orderJson);
                        orders.add(order);
                    }

                    mainHandler.post(() -> callback.onSuccess(orders));
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

    public void createOrder(Long restaurantId, List<CartItem> cartItems, String orderType, String deliveryAddress, OrderCallback callback) {
        try {
            BigDecimal total = BigDecimal.ZERO;
            JSONArray itemsArray = new JSONArray();

            for (CartItem cartItem : cartItems) {
                JSONObject itemJson = new JSONObject();
                itemJson.put("menuItemId", cartItem.getMenuItem().getId());
                itemJson.put("quantity", cartItem.getQuantity());
                itemJson.put("price", cartItem.getMenuItem().getPrice().toString());
                itemJson.put("specialInstructions", cartItem.getSpecialInstructions() != null ? cartItem.getSpecialInstructions() : "");
                itemsArray.put(itemJson);

                total = total.add(cartItem.getSubtotal());
            }

            JSONObject requestBody = new JSONObject();
            requestBody.put("userId", sessionManager.getUserId());
            requestBody.put("restaurantId", restaurantId);
            requestBody.put("totalAmount", total.toString());
            requestBody.put("orderType", orderType);
            requestBody.put("deliveryAddress", deliveryAddress != null ? deliveryAddress : "");
            requestBody.put("items", itemsArray);

            apiClient.post(Constants.ORDERS, requestBody, new ApiClient.ApiCallback() {
                @Override
                public void onSuccess(String response) {
                    try {
                        JSONObject orderJson = new JSONObject(response);
                        Order order = parseOrder(orderJson);
                        mainHandler.post(() -> callback.onSuccess(order));
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

    private Order parseOrder(JSONObject json) throws Exception {
        Order order = new Order();
        order.setId(json.getLong("id"));
        order.setTotalAmount(new BigDecimal(json.getString("totalAmount")));
        order.setDeliveryAddress(json.optString("deliveryAddress"));
        order.setStatus(json.optString("status"));
        order.setOrderType(json.optString("orderType"));
        order.setCreatedAt(json.optString("createdAt"));
        order.setDeliveryTime(json.optString("deliveryTime"));
        return order;
    }
}