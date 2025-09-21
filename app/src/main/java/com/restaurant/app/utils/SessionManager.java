package com.restaurant.app.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.restaurant.app.models.User;

public class SessionManager {
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void createSession(String token, User user) {
        editor.putString(Constants.KEY_TOKEN, token);
        editor.putLong(Constants.KEY_USER_ID, user.getId());
        editor.putString(Constants.KEY_USER_EMAIL, user.getEmail());
        editor.putString(Constants.KEY_USER_NAME, user.getFullName());
        editor.putBoolean(Constants.KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(Constants.KEY_IS_LOGGED_IN, false);
    }

    public String getToken() {
        return prefs.getString(Constants.KEY_TOKEN, null);
    }

    public long getUserId() {
        return prefs.getLong(Constants.KEY_USER_ID, -1);
    }

    public String getUserEmail() {
        return prefs.getString(Constants.KEY_USER_EMAIL, null);
    }

    public String getUserName() {
        return prefs.getString(Constants.KEY_USER_NAME, null);
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}