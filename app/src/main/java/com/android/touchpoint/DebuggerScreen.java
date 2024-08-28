package com.android.touchpoint;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class DebuggerScreen extends AppCompatActivity {
    private LinearLayout linearprint, linearscan;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private BluetoothSocket bluetoothSocket;

    private static final int REQUEST_ENABLE_BT = 1;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_debugger_screen);

        getSplashWidget();
        initiateWidget();
    }

    private void getSplashWidget() {
        linearprint = findViewById(R.id.linearPrint);
        linearscan = findViewById(R.id.linearQr);
    }

    private void initiateWidget() {
        linearprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the Bluetooth printing process
                startBluetoothPrinting();
            }
        });
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
            String receiptData = generateReceiptData(); // Generate the receipt data
            outputStream.write(receiptData.getBytes());
            outputStream.close();
            bluetoothSocket.close();

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

    private String generateReceiptData() {
        return "PICC\n" +
                "--------------------------------\n" +
                "Philippine International             Date: 07-22-2024\n" +
                "Convention Center                    Time: 08:54:53 AM\n" +
                "\n" +
                "PICC, Complex 1307 Pasay City,\n" +
                "Metro Manila, Philippines\n" +
                "(+63)936994578\n" +
                "--------------------------------\n" +
                "VAT REG TIN: 000-000-000-00000\n" +
                "MIN: 1234567891\n" +
                "Plate Number: ABC124\n" +
                "Vehicle: SUV\n" +
                "--------------------------------\n" +
                "Cashier: cashiername\n" +
                "--------------------------------\n" +
                "Gate In: 2024-07-22 08:20:21\n" +
                "Bill Time: 2024-07-22 08:20:21\n" +
                "Parking Time: 000:32\n" +
                "Amount Due: PHP 1500.00\n" +
                "Vat(12%): PHP 1500.00\n" +
                "Total Amount Due: PHP 1500.00\n" +
                "--------------------------------\n" +
                "Vatable Sales: PHP 1500.00\n" +
                "Vat-Exempt: PHP 0.00\n" +
                "Discount: PHP 0.00\n" +
                "Payment Mode: Credit\n" +
                "--------------------------------\n" +
                "NTEKSYSTEMS Incorporation\n" +
                "ACCREDITATION: 000000000000000\n" +
                "Date Issued: 12/12/2020\n" +
                "Valid Until: 12/12/2023\n" +
                "BIR PTU NO: AB1234567-12345678\n" +
                "PTU DATE ISSUED: 11/24/2020\n" +
                "THIS SERVES AS AN OFFICIAL RECEIPT\n" +
                "\n\n"; // Adding spaces at the bottom
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
