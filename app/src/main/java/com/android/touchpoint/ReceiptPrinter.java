package com.android.touchpoint;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class ReceiptPrinter extends AppCompatActivity {
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private BluetoothSocket bluetoothSocket;

    private static final int REQUEST_ENABLE_BT = 1;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_printer);

        // Get the receipt data from the Intent
        Intent intent = getIntent();
        String receiptData = intent.getStringExtra("receipt_data");

        // Start the Bluetooth printing process
        startBluetoothPrinting(receiptData);
    }

    private void startBluetoothPrinting(String receiptData) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            connectToPrinter(receiptData);
        }
    }

    private void connectToPrinter(String receiptData) {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals("IposPrinter")) { // Replace with your printer's name
                    bluetoothDevice = device;
                    break;
                }
            }

            if (bluetoothDevice != null) {
                try {
                    bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);
                    bluetoothSocket.connect();

                    OutputStream outputStream = bluetoothSocket.getOutputStream();
                    outputStream.write(receiptData.getBytes());
                    outputStream.flush();
                    outputStream.close();
                    bluetoothSocket.close();

                    Toast.makeText(this, "Receipt printed successfully", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to print receipt", Toast.LENGTH_SHORT).show();
                    closeSocket();
                }
            } else {
                Toast.makeText(this, "Printer not found", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No paired devices found", Toast.LENGTH_SHORT).show();
        }
    }

    private void closeSocket() {
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                String receiptData = getIntent().getStringExtra("receipt_data");
                connectToPrinter(receiptData);
            } else {
                Toast.makeText(this, "Bluetooth not enabled", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
