package com.example.campusride;

import android.app.ProgressDialog;
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

public class StudentLoginActivity extends AppCompatActivity {

    EditText studentId, studentPassword;
    Button studentLoginButton;

    FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);

        studentId = findViewById(R.id.studentId);
        studentPassword = findViewById(R.id.studentPassword);
        studentLoginButton = findViewById(R.id.studentLoginButton);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        dialog = new ProgressDialog(this);
        dialog.setMessage("Logging in...");
        dialog.setCancelable(false);

        studentLoginButton.setOnClickListener(v -> {

            String sapId = studentId.getText().toString().trim();
            String password = studentPassword.getText().toString().trim();

            if (sapId.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            dialog.show();

            String email = sapId + "@campusride.com";

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

                                                dialog.dismiss();

                                                if (snapshot.exists()) {

                                                    String role = snapshot.child("role").getValue(String.class);

                                                    if ("student".equals(role)) {

                                                        StudentSession.sapId = sapId;

                                                        Toast.makeText(StudentLoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                                                        Intent intent = new Intent(StudentLoginActivity.this, StudentDashboardActivity.class);
                                                        intent.putExtra("sapId", sapId);
                                                        startActivity(intent);
                                                        finish();

                                                    } else {
                                                        Toast.makeText(StudentLoginActivity.this, "Access Denied", Toast.LENGTH_SHORT).show();
                                                    }

                                                } else {
                                                    Toast.makeText(StudentLoginActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                                dialog.dismiss();
                                                Toast.makeText(StudentLoginActivity.this, "Database Error", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }

                        } else {

                            dialog.dismiss();
                            Toast.makeText(StudentLoginActivity.this, "Wrong SAP ID or Password", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

}
