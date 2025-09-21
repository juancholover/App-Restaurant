package com.restaurant.app.models;

import java.math.BigDecimal;

public class CartItem {
    private MenuItem menuItem;
    private int quantity;
    private String specialInstructions;

    public CartItem(MenuItem menuItem, int quantity) {
        this.menuItem = menuItem;
        this.quantity = quantity;
    }

    public MenuItem getMenuItem() { return menuItem; }
    public void setMenuItem(MenuItem menuItem) { this.menuItem = menuItem; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getSpecialInstructions() { return specialInstructions; }
    public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }

    public BigDecimal getSubtotal() {
        return menuItem.getPrice().multiply(new BigDecimal(quantity));
    }
}