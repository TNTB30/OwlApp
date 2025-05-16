package com.example.owlapp;

import android.content.Context;
import android.widget.Toast;

public class OTPManager {
    private Context context;
    private String generatedOTP;

    public OTPManager(Context context) {
        this.context = context;
    }

    public void sendOTP(String emailOrPhone, OTPCallback callback) {
        // Generate random 6-digit OTP
        generatedOTP = String.valueOf((int) (Math.random() * 900000) + 100000);

        // Hiển thị OTP trong log để debugmaiphong
        System.out.println("OTP sent to " + emailOrPhone + ": " + generatedOTP);

        // Hiển thị thông báo cho người dùng (chỉ để demo)
        Toast.makeText(context, "Mã OTP đã được gửi: " + generatedOTP, Toast.LENGTH_LONG).show();

        // Gọi callback khi hoàn thành
        callback.onOTPSent(generatedOTP);
    }

    public boolean verifyOTP(String inputOTP) {
        // Thay vì dùng biến generatedOTP, sử dụng UserManager để kiểm tra
        UserManager userManager = new UserManager(context);
        return userManager.verifyOTP(inputOTP);
    }

    public interface OTPCallback {
        void onOTPSent(String otp);
    }
}