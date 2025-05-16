package com.example.owlapp;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;


public class UserManager {
    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_USER = "current_user";
    private static final String KEY_OTP = "otp_code";

    private static final String KEY_TEMP_OTP = "temp_otp";


    private SharedPreferences sharedPreferences;
    private Gson gson;

    public UserManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void saveUser(User user) {
        String userJson = gson.toJson(user);
        sharedPreferences.edit().putString(KEY_USER, userJson).apply();
    }

    public User getUser() {
        String userJson = sharedPreferences.getString(KEY_USER, null);
        if (userJson != null) {
            return gson.fromJson(userJson, User.class);
        }
        return null;
    }

    public void generateAndSaveOTP() {
        // Generate random 6-digit OTP
        int otp = (int) (Math.random() * 900000) + 100000;
        sharedPreferences.edit().putString(KEY_OTP, String.valueOf(otp)).apply();
    }


    public boolean verifyOTP(String inputOtp) {
        String savedOtp = sharedPreferences.getString(KEY_TEMP_OTP, null);
        return savedOtp != null && savedOtp.equals(inputOtp);
    }

    public boolean authenticate(String username, String password) {
        User user = getUser();
        return user != null && user.getUsername().equals(username) && user.getPassword().equals(password);
    }

    public void clearUser() {
        sharedPreferences.edit().remove(KEY_USER).apply();
    }

    // Thêm phương thức để lưu OTP tạm thời
    public void saveTemporaryOTP(String otp) {
        sharedPreferences.edit().putString(KEY_TEMP_OTP, otp).apply();
    }

    public String getTemporaryOTP() {
        return sharedPreferences.getString(KEY_TEMP_OTP, null);
    }

    public void clearTemporaryOTP() {
        sharedPreferences.edit().remove(KEY_TEMP_OTP).apply();
    }
}