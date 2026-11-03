package com.example.campusride;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class StudentDashboardActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;

    String sapId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        bottomNavigation = findViewById(R.id.bottomNavigation);

        sapId = getIntent().getStringExtra("sapId");

        loadFragment(new HomeFragment());

        bottomNavigation.setOnItemSelectedListener(item -> {

            Fragment fragment = null;

            if (item.getItemId() == R.id.nav_home) {
                fragment = new HomeFragment();
            }

            if (item.getItemId() == R.id.nav_schedule) {
                fragment = new ScheduleFragment();
            }

            if (item.getItemId() == R.id.nav_pass) {
                fragment = new MyPassFragment(sapId);
            }

            if (fragment != null) {
                loadFragment(fragment);
            }

            return true;
        });
    }

    private void loadFragment(Fragment fragment) {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .commit();
    }
}