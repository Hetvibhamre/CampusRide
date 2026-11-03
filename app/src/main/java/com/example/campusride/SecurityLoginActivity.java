package com.example.campusride;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class SecurityLoginActivity extends AppCompatActivity {

    EditText securityId, securityPassword;
    Button securityLoginButton;

    FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_login);

        securityId = findViewById(R.id.securityId);
        securityPassword = findViewById(R.id.securityPassword);
        securityLoginButton = findViewById(R.id.securityLoginButton);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        securityLoginButton.setOnClickListener(v -> {

            String id = securityId.getText().toString().trim();
            String password = securityPassword.getText().toString().trim();

            if (id.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // 🔥 SAME AS STUDENT → ID → EMAIL CONVERSION
            String email = id + "@campusride.com";

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {

                            FirebaseUser user = mAuth.getCurrentUser();

                            if (user != null) {

                                String uid = user.getUid();

                                databaseReference.child(uid)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {

                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                if (snapshot.exists()) {

                                                    String role = snapshot.child("role").getValue(String.class);

                                                    if ("security".equals(role)) {

                                                        Toast.makeText(SecurityLoginActivity.this, "Security Login Successful", Toast.LENGTH_SHORT).show();

                                                        Intent intent = new Intent(SecurityLoginActivity.this, SecurityScanActivity.class);
                                                        startActivity(intent);
                                                        finish();

                                                    } else {

                                                        Toast.makeText(SecurityLoginActivity.this, "Access Denied", Toast.LENGTH_SHORT).show();
                                                    }

                                                } else {

                                                    Toast.makeText(SecurityLoginActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                                Toast.makeText(SecurityLoginActivity.this, "Database Error", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }

                        } else {

                            Toast.makeText(SecurityLoginActivity.this, "Invalid Security Credentials", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }


}
