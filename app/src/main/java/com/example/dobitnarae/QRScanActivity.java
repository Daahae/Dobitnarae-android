package com.example.dobitnarae;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class QRScanActivity extends AppCompatActivity {
    /* QR code scanner 객체 */
    private IntentIntegrator qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* QR code Scanner Setting */
        qrScan = new IntentIntegrator(this);
        qrScan.setPrompt("아래 띄울 문구");
        qrScan.setOrientationLocked(true);
        qrScan.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            if (result.getContents() == null) {
                Log.v("qrcode :::::::::::", "no contents");
            } else { //QR코드, 내용 존재
                try {
                    /* QR 코드 내용*/
                    String temp = result.getContents();


                    Log.v("qrcode Contents :::::::", temp);
                    Toast.makeText(getApplicationContext(), result.getContents(), Toast.LENGTH_LONG).show();

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.v("Exception :::::::::::::", "QR code fail");
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
        finishAffinity();
    }
}
