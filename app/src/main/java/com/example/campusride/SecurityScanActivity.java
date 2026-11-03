package com.example.campusride;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class SecurityScanActivity extends AppCompatActivity {

    Button btnScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_scan);

        btnScan = findViewById(R.id.btnScan);

        btnScan.setOnClickListener(v -> startQRScanner());
    }

    private void startQRScanner() {

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("Scan Student Bus Pass QR");
        integrator.setBeepEnabled(true);
        integrator.setOrientationLocked(true);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {

            if (result.getContents() != null) {

                Intent intent = new Intent(this, SecurityResultActivity.class);
                intent.putExtra("sapId", result.getContents());
                startActivity(intent);

            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}