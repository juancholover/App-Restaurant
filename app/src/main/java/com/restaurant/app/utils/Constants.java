package com.restaurant.app.utils;

public class Constants {
    public static final String BASE_URL = "http://10.0.2.2:9091/api/v1/";

    // Endpoints
    public static final String AUTH_LOGIN = "auth/login";
    public static final String AUTH_REGISTER = "auth/register";
    public static final String RESTAURANTS = "restaurants";
    public static final String MENU_ITEMS = "menu-items";
    public static final String RESERVATIONS = "reservations";
    public static final String ORDERS = "orders";
    public static final String PAYMENTS = "payments";

    // SharedPreferences keys
    public static final String PREF_NAME = "restaurant_prefs";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USER_EMAIL = "user_email";
    public static final String KEY_USER_NAME = "user_name";
    public static final String KEY_IS_LOGGED_IN = "is_logged_in";

    // Request codes
    public static final int REQUEST_LOGIN = 1001;
    public static final int REQUEST_REGISTER = 1002;
    public static final int REQUEST_CARD_DETAILS = 1003;

    // Order types
    public static final String ORDER_TYPE_DELIVERY = "DELIVERY";
    public static final String ORDER_TYPE_PICKUP = "PICKUP";

    // Status codes
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_CONFIRMED = "CONFIRMED";
    public static final String STATUS_CANCELLED = "CANCELLED";
    public static final String STATUS_COMPLETED = "COMPLETED";

    // Payment methods
    public static final String PAYMENT_CREDIT_CARD = "CREDIT_CARD";
    public static final String PAYMENT_DEBIT_CARD = "DEBIT_CARD";
    public static final String PAYMENT_CASH = "CASH";
    public static final String PAYMENT_DIGITAL_WALLET = "DIGITAL_WALLET";
    public static final String PAYMENT_PAYPAL = "PAYPAL";
    public static final String PAYMENT_YAPE = "YAPE";
    public static final String PAYMENT_PLIN = "PLIN";

    // Payment status
    public static final String PAYMENT_STATUS_PENDING = "PENDING";
    public static final String PAYMENT_STATUS_PROCESSING = "PROCESSING";
    public static final String PAYMENT_STATUS_COMPLETED = "COMPLETED";
    public static final String PAYMENT_STATUS_FAILED = "FAILED";
    public static final String PAYMENT_STATUS_CANCELLED = "CANCELLED";
    public static final String PAYMENT_STATUS_REFUNDED = "REFUNDED";
}