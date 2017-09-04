package com.citparkingsystem.lib;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Dave Tolentin on 7/16/2017.
 */

public class SessionManager extends ServerAddress {

    private int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "CIT_PARKING_SYSTEM";
    private static final String KEY_IS_LOGGED_IN = "keyIsLoggedIn";
    private static final String KEY_IS_SUCCESS_CONNECTING = "keyIsSuccessConnecting";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public void setLoginCredentials(Boolean isLoggedIn, String username,
                                    String firstName, String lastName, String userProfile) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.putString("keyUsername", username);
        editor.putString("keyFirstName", firstName);
        editor.putString("keyLastName", lastName);
        // editor.putString("keyUserProfile", "http://"+IP+""+PORT+"/"+PACKAGE+userProfile);
        editor.apply();
    }

    public void setSuccessConnecting(boolean isSuccess) {
        editor.putBoolean(KEY_IS_SUCCESS_CONNECTING, isSuccess);
        editor.apply();
    }

    public void parkingAreaAvailableSlotsAcademic(String slots) {
        editor.putString("keySlotsAcademic", slots);
        editor.apply();
    }

    public void parkingArea(String what) {
        editor.putString("keyParkingArea", what);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }
    public boolean isConnected() {
        return sharedPreferences.getBoolean(KEY_IS_SUCCESS_CONNECTING, false);
    }

    public void clearUserData() {
        SharedPreferences.Editor sharedEditor = sharedPreferences.edit();
        sharedEditor.clear();
        sharedEditor.apply();
    }
}
