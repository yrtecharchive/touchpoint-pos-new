package com.android.touchpoint;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PaymentMethod extends AppCompatActivity {

    private ParkingBillResponse response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment_method);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get the data from the Intent
        response = (ParkingBillResponse) getIntent().getSerializableExtra("bill_response");

        // Display the data if needed
        if (response != null) {
            Toast.makeText(this, "Bill Amount: " + response.getBill(), Toast.LENGTH_LONG).show();
        }

        // Initialize radio buttons
        RadioGroup radioGroup = findViewById(R.id.radioGroupPaymentMethod);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton radioButton = findViewById(checkedId);
            String selectedPaymentMethod = radioButton.getText().toString();
            Toast.makeText(this, "Selected Payment Method: " + selectedPaymentMethod, Toast.LENGTH_SHORT).show();

            if ("Cash".equals(selectedPaymentMethod)) {
                Intent intent = new Intent(PaymentMethod.this, CashPayment.class);
                intent.putExtra("bill_response", response);
                startActivity(intent);
            } else if ("Complimentary".equals(selectedPaymentMethod)) {
                Intent intent = new Intent(PaymentMethod.this, ComplimetaryForm.class);
                intent.putExtra("bill_response", response);
                startActivity(intent);
            }
        });
    }
}
