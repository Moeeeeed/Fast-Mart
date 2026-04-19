package com.example.a2.activities; // Update this to your package name!

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.a2.R;

public class OnBoardActivity extends AppCompatActivity {

    Button btnGetStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activityonboard);

        btnGetStarted = findViewById(R.id.btnGetStarted);

        btnGetStarted.setOnClickListener(v -> {
            // Update SharedPreferences so Onboarding doesn't show again
            SharedPreferences prefs = getSharedPreferences("FastMartPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isFirstTime", false);
            editor.apply();

            // Navigate to AuthActivity
            startActivity(new Intent(OnBoardActivity.this, AuthActivity.class));
            finish(); // Close onboarding
        });
    }
}
