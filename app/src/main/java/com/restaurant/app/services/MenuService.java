package com.restaurant.app.services;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.restaurant.app.models.MenuItem;
import com.restaurant.app.network.ApiClient;
import com.restaurant.app.utils.Constants;
import org.json.JSONArray;
import org.json.JSONObject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MenuService {
    private ApiClient apiClient;
    private Handler mainHandler;

    public MenuService(Context context) {
        apiClient = ApiClient.getInstance(context);
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public interface MenuItemListCallback {
        void onSuccess(List<MenuItem> menuItems);

        void onError(String error);
    }

    public void getMenuItemsByRestaurant(Long restaurantId, MenuItemListCallback callback) {
        apiClient.get(Constants.MENU_ITEMS + "/restaurant/" + restaurantId, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    List<MenuItem> menuItems = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject menuItemJson = jsonArray.getJSONObject(i);
                        MenuItem menuItem = parseMenuItem(menuItemJson);
                        menuItems.add(menuItem);
                    }

                    mainHandler.post(() -> callback.onSuccess(menuItems));
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

    private MenuItem parseMenuItem(JSONObject json) throws Exception {
        MenuItem menuItem = new MenuItem();
        menuItem.setId(json.getLong("id"));
        menuItem.setName(json.getString("name"));
        menuItem.setDescription(json.optString("description"));
        menuItem.setPrice(new BigDecimal(json.getString("price")));
        menuItem.setImageUrl(json.optString("imageUrl"));
        menuItem.setCategory(json.optString("category"));
        menuItem.setAvailable(json.optBoolean("available", true));
        return menuItem;
    }
}