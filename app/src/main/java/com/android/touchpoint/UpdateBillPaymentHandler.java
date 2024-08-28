package com.android.touchpoint;

import android.app.Activity;
import android.widget.Toast;
import org.json.JSONObject;

public class UpdateBillPaymentHandler {

    private final Activity activity;

    public UpdateBillPaymentHandler(Activity activity) {
        this.activity = activity;
    }

    public void handleResponse(String response) {
        try {
            // Parse the JSON response
            JSONObject jsonResponse = new JSONObject(response);
            String ornumber = jsonResponse.getString("OR");
            String status = jsonResponse.getString("Status");
            String cashier = jsonResponse.getString("Cashier"); // Adjust according to your response

            // Check if the status is success
            if ("success".equalsIgnoreCase(status)) {
                if (activity instanceof CashPayment) {
                    CashPayment cashPayment = (CashPayment) activity;
                    cashPayment.updateReceiptWithORNumber(ornumber);
                    cashPayment.updateCashierName(cashier);
                    cashPayment.printReceipt(); // Call method to print receipt
                }
            } else {
                Toast.makeText(activity, "Payment update failed: " + status, Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            // Handle JSON parsing or any other errors
            Toast.makeText(activity, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
