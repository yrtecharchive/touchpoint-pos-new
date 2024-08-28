package com.android.touchpoint;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class MainActivity extends AppCompatActivity {
    private Button btnplate, btnticket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enable edge-to-edge display
        EdgeToEdge.enable(this);

        // Hide the system bars to make the activity full screen
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            WindowInsetsControllerCompat windowInsetsController = ViewCompat.getWindowInsetsController(v);
            if (windowInsetsController != null) {
                windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
                windowInsetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
            return WindowInsetsCompat.CONSUMED;
        });

        getMainWidgets();
        initializedMainWidgets();
    }

    private void getMainWidgets() {
        // Your code to get widgets
        btnplate = findViewById(R.id.btnPlateNumber);
        btnticket = findViewById(R.id.btnQrticket);
    }

    private void initializedMainWidgets() {
        // Your code to initialize widgets

        btnplate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AnprForm.class);
                startActivity(i);
                finish();
            }
        });

        btnticket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), TicketForm.class);
                startActivity(i);
                finish();
            }
        });
    }
}
