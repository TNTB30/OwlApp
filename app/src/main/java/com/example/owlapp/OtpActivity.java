package com.example.owlapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class OtpActivity extends AppCompatActivity {

    private EditText edtOtp;
    private Button btnConfirm;
    private ImageView imgBack;
    private UserManager userManager;
    private OTPManager otpManager;
    private String purpose; // "register" hoặc "login"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        userManager = new UserManager(this);
        otpManager = new OTPManager(this);

        // Nhận thông tin từ Intent
        purpose = getIntent().getStringExtra("purpose");

        // Initialize views
        edtOtp = findViewById(R.id.edtOtp);
        btnConfirm = findViewById(R.id.btnConfirm);
        imgBack = findViewById(R.id.imgBack);

        // Set click listeners
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = edtOtp.getText().toString().trim();

                if (otp.isEmpty() || otp.length() < 6) {
                    Toast.makeText(OtpActivity.this, "Vui lòng nhập đủ 6 số OTP", Toast.LENGTH_SHORT).show();
                } else if (otpManager.verifyOTP(otp)) {
                    Toast.makeText(OtpActivity.this, "Xác thực thành công", Toast.LENGTH_SHORT).show();

                    if ("register".equals(purpose)) {
                        // Nếu là đăng ký, chuyển đến HomeActivity
                        Intent intent = new Intent(OtpActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        // Nếu là đăng nhập, chuyển đến HomeActivity
                        Intent intent = new Intent(OtpActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(OtpActivity.this, "Mã OTP không đúng", Toast.LENGTH_SHORT).show();
                }
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}