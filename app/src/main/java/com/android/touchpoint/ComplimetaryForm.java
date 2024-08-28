package com.android.touchpoint;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class ComplimetaryForm extends AppCompatActivity {
    private Button btnCancel, btnProceed;
    private EditText etFieldCodeQr;
    private ImageView imgbtn;

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted){
                    showCamera();
                }else{
//                    Show my user need
                }
            });

    private ActivityResultLauncher<ScanOptions> qrCodeLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() == null){
            Toast.makeText(this, "Scan Cancelled", Toast.LENGTH_SHORT).show();
        }else{
            setResult(result.getContents());
        }
    });

    private void setResult(String contents){
        etFieldCodeQr.setText(contents);

        String qrCode = etFieldCodeQr.getText().toString();
        if (qrCode.isEmpty()) {
            Toast.makeText(ComplimetaryForm.this, "Please enter a QR code", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://192.168.1.100/parkingci/handheld/terminalBillRequest?access=QR&code=" + qrCode;




    }

    private void showCamera(){
        ScanOptions options= new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setPrompt("Place the QR Code in view finder to continue payment!");
        options.setCameraId(0);
        options.setBeepEnabled(false);
        options.setBarcodeImageEnabled(true);
        options.setOrientationLocked(false);
        qrCodeLauncher.launch(options);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_complimetary_form);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeWidgets();

        setUpListeners();
    }

    private void initializeWidgets() {
        etFieldCodeQr = findViewById(R.id.fieldCodeQr);
        btnCancel = findViewById(R.id.btnCancel);
        btnProceed = findViewById(R.id.btnProceed);
        imgbtn = findViewById(R.id.imgScanner);
    }


    private void setUpListeners() {

        imgbtn.setOnClickListener(view ->{
            checkSelfPermissionAndShowActivity(this);
        });
        etFieldCodeQr.addTextChangedListener(new TextWatcher() {
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
                    etFieldCodeQr.removeTextChangedListener(this);
                    etFieldCodeQr.setText(input.toUpperCase());
                    etFieldCodeQr.setSelection(etFieldCodeQr.getText().length());
                    etFieldCodeQr.addTextChangedListener(this);
                }
            }
        });

        btnProceed.setOnClickListener(v -> {
            String qrCode = etFieldCodeQr.getText().toString();
            if (qrCode.isEmpty()) {
                Toast.makeText(ComplimetaryForm.this, "Please enter a QR code", Toast.LENGTH_SHORT).show();
                return;
            }

            String url = "http://192.168.1.100/parkingci/handheld/terminalBillRequest?access=QR&code=" + qrCode;


        });

        btnCancel.setOnClickListener(v -> {
            // Handle cancel action
            finish(); // Close the current activity and return to the previous one
        });
    }

    private void  checkSelfPermissionAndShowActivity(Context context){
        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.CAMERA
        )== PackageManager.PERMISSION_GRANTED){
            showCamera();
        } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
            Toast.makeText(context, "Camera permission required!", Toast.LENGTH_SHORT).show();

        }else{
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }
}