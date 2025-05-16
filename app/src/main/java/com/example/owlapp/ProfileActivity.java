package com.example.owlapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private ImageView btnClose;
    private TextView btnLogout, btnSave;
    private EditText edtFullName, edtUsername, edtEmail, edtPhone, edtPassword;
    private UserManager userManager;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userManager = new UserManager(this);
        currentUser = userManager.getUser();

        btnClose = findViewById(R.id.btnClose);
        btnLogout = findViewById(R.id.btnLogout);
        btnSave = findViewById(R.id.btnSave);

        edtFullName = findViewById(R.id.edtFullName);
        edtUsername = findViewById(R.id.edtUsername);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtPassword);

        // Hiển thị thông tin người dùng
        if (currentUser != null) {
            edtFullName.setText(currentUser.getFullName());
            edtUsername.setText(currentUser.getUsername());
            edtEmail.setText(currentUser.getEmail());
            edtPhone.setText(currentUser.getPhone());
            edtPassword.setText(currentUser.getPassword());
        }

        btnClose.setOnClickListener(v -> finish());

        btnLogout.setOnClickListener(v -> {
            userManager.clearUser();
            Toast.makeText(this, "Bạn đã đăng xuất", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        btnSave.setOnClickListener(v -> {
            // Cập nhật thông tin người dùng
            currentUser.setFullName(edtFullName.getText().toString().trim());
            currentUser.setUsername(edtUsername.getText().toString().trim());
            currentUser.setEmail(edtEmail.getText().toString().trim());
            currentUser.setPhone(edtPhone.getText().toString().trim());
            currentUser.setPassword(edtPassword.getText().toString().trim());

            userManager.saveUser(currentUser);
            Toast.makeText(this, "Thông tin đã được cập nhật", Toast.LENGTH_SHORT).show();
        });
    }
}