package com.android.touchpoint;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class CashPayment extends AppCompatActivity {

    private ParkingBillResponse response;
    private TextView tvBillAmount;
    private EditText etCashAmount;
    private String orNumber;
    private String cashierName;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private BluetoothSocket bluetoothSocket;

    private static final int REQUEST_ENABLE_BT = 1;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cash_payment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvBillAmount = findViewById(R.id.tvBillAmount);
        etCashAmount = findViewById(R.id.etCashAmountInput);

        // Get the data from the Intent
        response = (ParkingBillResponse) getIntent().getSerializableExtra("bill_response");

        if (response != null) {
            tvBillAmount.setText(String.valueOf(response.getBill()));
        }

        setupNumpadButtons();
    }

    private void setupNumpadButtons() {
        int[] buttonIds = {
                R.id.button1, R.id.button2, R.id.button3, R.id.buttonCancel,
                R.id.button4, R.id.button5, R.id.button6, R.id.buttonDelete,
                R.id.button7, R.id.button8, R.id.button9, R.id.buttonProceed,
                R.id.button000, R.id.button00, R.id.button0
        };

        for (int id : buttonIds) {
            Button button = findViewById(id);
            button.setOnClickListener(this::onNumpadButtonClick);
        }
    }

    private void onNumpadButtonClick(View view) {
        Button button = (Button) view;
        String currentText = etCashAmount.getText().toString();
        String buttonText = button.getText().toString();

        switch (buttonText) {
            case "Cancel":
                showCancelWarningDialog();
                break;
            case "Clear":
                etCashAmount.setText("");
                break;
            case "Delete":
                if (currentText.length() > 0) {
                    etCashAmount.setText(currentText.substring(0, currentText.length() - 1));
                }
                break;
            case "Proceed":
                handleProceedAction();
                break;
            default:
                etCashAmount.setText(currentText + buttonText);
                break;
        }
    }

    private void handleProceedAction() {
        String inputAmountStr = etCashAmount.getText().toString();
        double billAmount = Double.parseDouble(tvBillAmount.getText().toString());
        double inputAmount;

        try {
            inputAmount = Double.parseDouble(inputAmountStr);
        } catch (NumberFormatException e) {
            inputAmount = 0;
        }

        if (inputAmount < billAmount) {
            showInvalidInputDialog();
        } else if (inputAmount == billAmount) {
            // Call the async task to update the bill payment
            String url = "http://192.168.1.100/parkingci/handheld/terminalBillPaid";
            new UpdateBillPaymentTask(this).execute(url,
                    String.valueOf(billAmount),
                    response.getId(),
                    response.getAccessType(),
                    response.getCode(),
                    response.getGate(),
                    response.getEntryTime(),
                    response.getVclass(),
                    response.getPtime(),
                    response.getPayTime(),
                    "Cash" // Assuming this is the paymode, adjust as needed
            );
        } else {
            // Handle cases where the input amount is greater than the bill amount
            Toast.makeText(this, "Amount exceeds bill amount", Toast.LENGTH_SHORT).show();
        }
    }

    private void showInvalidInputDialog() {
        // Inflate the custom layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_invalid_input, null);

        // Create and configure the custom dialog
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        // Find the button in the dialog layout
        Button dialogOkButton = dialogView.findViewById(R.id.dialogOkButton);
        dialogOkButton.setOnClickListener(v -> dialog.dismiss());

        // Show the dialog
        dialog.show();
    }

    private void showCancelWarningDialog() {
        // Inflate the custom layout for the warning dialog
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_warning_notification, null);

        // Create and configure the warning dialog
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        // Find and set up the buttons in the dialog layout
        Button dialogOkButton = dialogView.findViewById(R.id.dialogOkButton);
        dialogOkButton.setOnClickListener(v -> {
            // Handle the cancel action
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            finish();
            dialog.dismiss();
        });

        // Show the dialog
        dialog.show();
    }

    private void showSuccessDialog() {
        // Inflate the custom layout for the success dialog
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_success, null);

        // Create and configure the success dialog
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        // Find and set up the button in the dialog layout
        Button dialogOkButton = dialogView.findViewById(R.id.dialogOkButton);
        dialogOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();
            }
        });
        // Show the dialog
        dialog.show();
    }

    public void updateReceiptWithORNumber(String orNumber) {
        this.orNumber = orNumber;
    }

    public void updateCashierName(String cashierName) {
        this.cashierName = cashierName;
    }

    private void generateReceipt() {
        if (orNumber != null && cashierName != null && response != null) {
            String receiptData = generateReceiptData(response);
            Log.d("CashPayment", "Receipt Data: " + receiptData);
        }
    }

    public void printReceipt() {
        if (orNumber != null && cashierName != null && response != null) {
            startBluetoothPrinting();
        }
    }

    private String generateReceiptData(ParkingBillResponse response) {
        double billAmount = response.getBill(); // Assuming this is the total amount

        // VAT calculation
        double totalVatSales = billAmount / 1.12;
        double totalVat = (billAmount / 1.12) * 0.12;
        String vatTotal = String.format("%.2f", totalVat);
        String vatSalesTotal = String.format("%.2f", totalVatSales);

        return "                PICC\n" +
                "--------------------------------\n" +
                "   Philippine International\n" +
                "      Convention Center\n" +
                "   Date: 07-22-2024\n" +
                "   Time: 08:54:53 AM\n" +
                "--------------------------------\n" +
                "PICC, Complex 1307 Pasay City,\n" +
                "   Metro Manila, Philippines\n" +
                "       (+63)936994578\n" +
                "--------------------------------\n" +
                "VAT REG TIN: 000-000-000-00000\n" +
                "    MIN: 1234567891\n" +
                "   OR Number: " + (orNumber != null ? orNumber : "N/A") + "\n" +
                "Plate Number: " + response.getCode() + "\n" +
                "    Vehicle: " + response.getVclass() + "\n" +
                "--------------------------------\n" +
                "   Cashier: " + (cashierName != null ? cashierName : "Unknown") + "\n" +
                "--------------------------------\n" +
                "Gate In: " + response.getEntryTime()+"\n" +
                "Bill Time: " + response.getPayTime() + "\n" +
                "Parking Time: " + response.getPtime() + "\n" +
                "Amount Due: PHP " + String.format("%.2f", billAmount) + "\n" +
                "Vat(12%): PHP " + vatTotal + "\n" +
                "Total Amount Due: PHP " + String.format("%.2f", billAmount) + "\n" +
                "--------------------------------\n" +
                "Vatable Sales: PHP " + vatSalesTotal + "\n" +
                "Vat-Exempt: PHP 0.00\n" +
                "Discount: PHP 0.00\n" +
                "Payment Mode: Cash\n" +
                "--------------------------------\n" +
                "NTEKSYSTEMS Incorporation\n" +
                "   ACCREDITATION: 000000000000000\n" +
                "   Date Issued: 12/12/2020\n" +
                "   Valid Until: 12/12/2023\n" +
                "   BIR PTU NO: AB1234567-12345678\n" +
                "   PTU DATE ISSUED: 11/24/2020\n" +
                "THIS SERVES AS AN OFFICIAL RECEIPT\n" +
                "\n\n"; // Adding spaces at the bottom
    }

    private void startBluetoothPrinting() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            connectToPrinter();
        }
    }

    private void connectToPrinter() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals("IposPrinter")) { // Replace with your printer's name
                    bluetoothDevice = device;
                    break;
                }
            }
        }

        try {
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);
            bluetoothSocket.connect();

            OutputStream outputStream = bluetoothSocket.getOutputStream();
            String receiptData = generateReceiptData(response); // Pass the response object
            outputStream.write(receiptData.getBytes());
            outputStream.close();
            bluetoothSocket.close();

            // Show the success dialog after successful printing
            showSuccessDialog();

        } catch (IOException e) {
            e.printStackTrace();
            try {
                if (bluetoothSocket != null) {
                    bluetoothSocket.close();
                }
            } catch (IOException closeException) {
                closeException.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                connectToPrinter();
            } else {
                Toast.makeText(this, "Bluetooth not enabled", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
