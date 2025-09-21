package com.restaurant.app.utils;

import com.restaurant.app.models.CartItem;
import com.restaurant.app.models.MenuItem;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private List<CartItem> cartItems;

    private CartManager() {
        cartItems = new ArrayList<>();
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addItem(MenuItem menuItem, int quantity) {
        // Buscar si el item ya está en el carrito
        for (CartItem cartItem : cartItems) {
            if (cartItem.getMenuItem().getId().equals(menuItem.getId())) {
                cartItem.setQuantity(cartItem.getQuantity() + quantity);
                return;
            }
        }

        // Si no está, agregar nuevo item
        cartItems.add(new CartItem(menuItem, quantity));
    }

    public void updateQuantity(MenuItem menuItem, int quantity) {
        for (CartItem cartItem : cartItems) {
            if (cartItem.getMenuItem().getId().equals(menuItem.getId())) {
                cartItem.setQuantity(quantity);
                break;
            }
        }
    }

    public void removeItem(MenuItem menuItem) {
        cartItems.removeIf(cartItem ->
                cartItem.getMenuItem().getId().equals(menuItem.getId()));
    }

    public List<CartItem> getCartItems() {
        return new ArrayList<>(cartItems);
    }

    public int getItemCount() {
        int count = 0;
        for (CartItem item : cartItems) {
            count += item.getQuantity();
        }
        return count;
    }

    public BigDecimal getTotalAmount() {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            total = total.add(item.getSubtotal());
        }
        return total;
    }

    public void clearCart() {
        cartItems.clear();
    }

    public boolean isEmpty() {
        return cartItems.isEmpty();
    }
}