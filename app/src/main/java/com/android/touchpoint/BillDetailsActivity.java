package com.android.touchpoint;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class BillDetailsActivity extends AppCompatActivity {

    private EditText etGate, etAccessType, etCode, etEntryTime, etPayTime, etVehicleClass, etBillAmount, etTotalTime;
    private Button btnProceed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_details);
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

        // Initialize your views
        etGate = findViewById(R.id.etGate);
        etAccessType = findViewById(R.id.etAccessType);
        etCode = findViewById(R.id.etCode);
        etEntryTime = findViewById(R.id.etEntryTime);
        etPayTime = findViewById(R.id.etPayTime);
        etVehicleClass = findViewById(R.id.etVehicleClass);
        etBillAmount = findViewById(R.id.etBillAmount);
        btnProceed = findViewById(R.id.btnProceedinBillDetails);
        etTotalTime = findViewById(R.id.etParkingDuration);

        // Get the data from the Intent
        ParkingBillResponse response = (ParkingBillResponse) getIntent().getSerializableExtra("bill_response");

        if (response != null) {
            // Display the data
            etGate.setText(response.getGate());
            etAccessType.setText(response.getAccessType());
            etCode.setText(response.getCode());
            etEntryTime.setText(response.getEntryTime());
            etPayTime.setText(response.getPayTime());
            etVehicleClass.setText(response.getVclass());
            etBillAmount.setText(String.valueOf(response.getBill()));
            etTotalTime.setText(response.getPtime());
        }

        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BillDetailsActivity.this, PaymentMethod.class);
                intent.putExtra("bill_response", response);
                startActivity(intent);
            }
        });

    }
}
