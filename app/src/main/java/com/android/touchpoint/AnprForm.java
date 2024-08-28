package com.android.touchpoint;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class AnprForm extends AppCompatActivity {
    private Button btncancel, btnproceed;
    private EditText etfield;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anpr_form);
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

        getAnprFormmWidgets();
        initializedAnprFormWidgets();
    }

    private void getAnprFormmWidgets() {
        etfield = findViewById(R.id.fieldCode);
        btncancel = findViewById(R.id.btnCancel);
        btnproceed = findViewById(R.id.btnProceed);
    }

    private void initializedAnprFormWidgets() {
        etfield.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No action needed
            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();
                if (!input.equals(input.toUpperCase())) {
                    etfield.removeTextChangedListener(this);
                    etfield.setText(input.toUpperCase());
                    etfield.setSelection(etfield.getText().length());
                    etfield.addTextChangedListener(this);
                }
            }
        });

        btnproceed.setOnClickListener(v -> {
            String plateNumber = etfield.getText().toString();
            String url = "http://192.168.1.100/parkingci/handheld/terminalBillRequest?access=Plate&code=" + plateNumber;

            // Send data using BillRequest
            new BillRequest(result -> {
                if (result instanceof String) {
                    Toast.makeText(AnprForm.this, "Response: " + result, Toast.LENGTH_LONG).show();
                } else if (result instanceof ParkingBillResponse) {
                    ParkingBillResponse response = (ParkingBillResponse) result;
                    // Start BillDetailsActivity and pass the data
                    Intent intent = new Intent(AnprForm.this, BillDetailsActivity.class);
                    intent.putExtra("bill_response", response);
                    startActivity(intent);
                }
            }).execute(url, ""); // The second parameter is empty as we're not sending additional data in POST
        });
    }
}
