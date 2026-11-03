package com.example.campusride;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SecurityResultActivity extends AppCompatActivity {

    TextView tvResult, tvDetails;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_result);

        tvResult = findViewById(R.id.tvResult);
        tvDetails = findViewById(R.id.tvDetails);

        databaseHelper = new DatabaseHelper(this);

        String sapId = getIntent().getStringExtra("sapId");

        StudentModel student = databaseHelper.getPassBySapId(sapId);

        if(student == null){

            tvResult.setText("INVALID PASS");
            tvResult.setTextColor(Color.RED);
            tvDetails.setText("No pass found in system");

            return;
        }

        String validity = student.getValidity();

        try{

            SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy", Locale.getDefault());

            Date today = new Date();
            Date expiry = sdf.parse(validity);

            if(expiry.before(today)){

                tvResult.setText("PASS EXPIRED");
                tvResult.setTextColor(Color.RED);

            }else{

                tvResult.setText("PASS VALID");
                tvResult.setTextColor(Color.parseColor("#2E7D32"));

            }

        }catch(Exception e){
            e.printStackTrace();
        }

        String details =
                "Name : " + student.getName() +
                        "\nSAP ID : " + student.getSapId() +
                        "\nProgramme : " + student.getProgramme() +
                        "\nBranch : " + student.getBranch() +
                        "\nYear : " + student.getYear() +
                        "\nValid Till : " + student.getValidity();

        tvDetails.setText(details);
    }
}