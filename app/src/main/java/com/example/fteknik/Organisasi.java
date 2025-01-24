package com.example.fteknik;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Organisasi extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_organisasi);

        // Initialize button and set onClickListener for the back button
        findViewById(R.id.btnKeluar).setOnClickListener(this::goToMainPage);
    }

    // Method to navigate back to the main page
    public void goToMainPage(View view) {
        Intent intent = new Intent(this, MainActivity.class);  // Replace with your main activity
        startActivity(intent);
        finish();  // Close current activity to prevent returning to it when pressing back
    }
}
