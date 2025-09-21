package com.restaurant.app.models;

import java.math.BigDecimal;

public class OrderItem {
    private Long id;
    private Order order;
    private MenuItem menuItem;
    private Integer quantity;
    private BigDecimal price;
    private String specialInstructions;

    public OrderItem() {}

    public OrderItem(MenuItem menuItem, Integer quantity) {
        this.menuItem = menuItem;
        this.quantity = quantity;
        this.price = menuItem.getPrice();
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public MenuItem getMenuItem() { return menuItem; }
    public void setMenuItem(MenuItem menuItem) { this.menuItem = menuItem; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getSpecialInstructions() { return specialInstructions; }
    public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }

    public BigDecimal getSubtotal() {
        return price.multiply(new BigDecimal(quantity));
    }
}