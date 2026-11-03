package com.example.campusride;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    LinearLayout adminCard, studentCard, securityCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adminCard = findViewById(R.id.adminCard);
        studentCard = findViewById(R.id.studentCard);
        securityCard = findViewById(R.id.securityCard);

        // Admin Login
        adminCard.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AdminLoginActivity.class);
            startActivity(intent);
        });

        // Student Login
        studentCard.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, StudentLoginActivity.class);
            startActivity(intent);
        });

        // Security Login
        securityCard.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SecurityLoginActivity.class);
            startActivity(intent);
        });
    }
}