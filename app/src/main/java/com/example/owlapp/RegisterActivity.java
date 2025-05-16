package com.example.owlapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtEmail, edtFullName, edtUsername, edtPassword, edtConfirmPassword, edtPhone;
    private Button btnRegister;
    private UserManager userManager;
    private OTPManager otpManager;
    private String currentOTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userManager = new UserManager(this);
        otpManager = new OTPManager(this);

        // Initialize views
        edtEmail = findViewById(R.id.edtEmail);
        edtFullName = findViewById(R.id.edtFullName);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        edtPhone = findViewById(R.id.edtPhone); // Thêm trường số điện thoại
        btnRegister = findViewById(R.id.btnRegister);

        // Set click listener
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString().trim();
                String fullName = edtFullName.getText().toString().trim();
                String username = edtUsername.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                String confirmPassword = edtConfirmPassword.getText().toString().trim();
                String phone = edtPhone.getText().toString().trim();

                if (email.isEmpty() || fullName.isEmpty() || username.isEmpty() ||
                        password.isEmpty() || confirmPassword.isEmpty() || phone.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(confirmPassword)) {
                    Toast.makeText(RegisterActivity.this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                } else {
                    // Gửi OTP đến email hoặc số điện thoại
                    otpManager.sendOTP(email, new OTPManager.OTPCallback() {
                        @Override
                        public void onOTPSent(String otp) {
                            // Lưu OTP vào UserManager thay vì biến tạm
                            userManager.saveTemporaryOTP(otp);

                            // Lưu thông tin người dùng tạm thời
                            User newUser = new User(email, fullName, username, password, phone);
                            userManager.saveUser(newUser);

                            // Chuyển sang màn hình xác nhận OTP
                            Intent intent = new Intent(RegisterActivity.this, OtpActivity.class);
                            intent.putExtra("purpose", "register");
                            startActivity(intent);
                        }
                    });
                }
            }
        });
    }
}