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
    private String email, username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        // Nhận thông tin từ màn hình đăng ký
        if (getIntent().hasExtra("email")) {
            email = getIntent().getStringExtra("email");
            username = getIntent().getStringExtra("username");
        }

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
                } else {
                    // Simulate OTP verification
                    // In a real app, you would validate this against a server
                    Toast.makeText(OtpActivity.this, "Xác thực thành công", Toast.LENGTH_SHORT).show();

                    // Navigate to HomeActivity
                    Intent intent = new Intent(OtpActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
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
