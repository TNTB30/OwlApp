package com.example.owlapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private ImageView btnClose;
    private TextView btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        btnClose = findViewById(R.id.btnClose);
        btnLogout = findViewById(R.id.btnLogout);

        btnClose.setOnClickListener(v -> finish());

        btnLogout.setOnClickListener(v -> {
            Toast.makeText(this, "Bạn đã đăng xuất", Toast.LENGTH_SHORT).show();
            // Chuyển về màn hình đăng nhập nếu muốn:
            // startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
