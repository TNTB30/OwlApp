package com.example.owlapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername, edtPassword;
    private Button btnLogin, btnRegister;
    private UserManager userManager;
    private OTPManager otpManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userManager = new UserManager(this);
        otpManager = new OTPManager(this);

        // Khởi tạo view
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        // Xử lý khi nhấn nút Đăng nhập
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edtUsername.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                } else if (userManager.authenticate(username, password)) {
                    // Lấy thông tin người dùng
                    User user = userManager.getUser();

                    otpManager.sendOTP(user.getEmail(), new OTPManager.OTPCallback() {
                        @Override
                        public void onOTPSent(String otp) {
                            // Lưu OTP vào UserManager
                            userManager.saveTemporaryOTP(otp);

                            Toast.makeText(LoginActivity.this, "Vui lòng nhập mã OTP", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, OtpActivity.class);
                            intent.putExtra("purpose", "login");
                            startActivity(intent);
                        }
                    });
                } else {
                    Toast.makeText(LoginActivity.this, "Tên đăng nhập hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Xử lý khi nhấn nút Đăng ký
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}