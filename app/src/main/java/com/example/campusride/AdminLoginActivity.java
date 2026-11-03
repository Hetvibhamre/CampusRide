package com.example.campusride;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public class AdminLoginActivity extends AppCompatActivity {

    EditText adminId, adminPassword;
    Button loginButton;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(this, AdminActivity.class));
            finish();
        }

        adminId = findViewById(R.id.adminId);
        adminPassword = findViewById(R.id.adminPassword);
        loginButton = findViewById(R.id.loginButton);

        mAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(v -> {

            String id = adminId.getText().toString().trim();
            String password = adminPassword.getText().toString().trim();

            if (id.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            } else {

                mAuth.signInWithEmailAndPassword(id, password)
                        .addOnCompleteListener(task -> {

                            if (task.isSuccessful()) {

                                String uid = mAuth.getCurrentUser().getUid();

                                DatabaseReference ref = FirebaseDatabase
                                        .getInstance()
                                        .getReference("Users")
                                        .child(uid);

                                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {

                                        if (snapshot.exists()) {

                                            String role = snapshot.child("role").getValue(String.class);

                                            if ("admin".equals(role)) {

                                                Toast.makeText(AdminLoginActivity.this, "Admin Login Successful", Toast.LENGTH_SHORT).show();

                                                Intent intent = new Intent(AdminLoginActivity.this, AdminActivity.class);
                                                startActivity(intent);
                                                finish();

                                            } else {

                                                Toast.makeText(AdminLoginActivity.this, "Access Denied! Not an Admin", Toast.LENGTH_SHORT).show();
                                                mAuth.signOut();
                                            }

                                        } else {

                                            Toast.makeText(AdminLoginActivity.this, "User data not found in database", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError error) {

                                        Toast.makeText(AdminLoginActivity.this, "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            } else {

                                Toast.makeText(AdminLoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();

                            }
                        });
            }
        });
    }
}