package org.feup.cmov.inspectorticketapp.Controllers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.zxing.Result;

import org.feup.cmov.inspectorticketapp.Models.SharedDataSingleton;
import org.feup.cmov.inspectorticketapp.Models.Ticket;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class TicketQRScannerActivity extends Activity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    private SharedDataSingleton mSharedDataSingleton = SharedDataSingleton.getInstance();

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {

        Intent intent = null;

        Ticket scannedTicket = null;
        try {
            scannedTicket = Ticket.fromTransmittableString(rawResult.getText());
        } catch(Exception e) {
            Toast.makeText(this, "QR code did not transmit ticket data", Toast.LENGTH_LONG)
                    .show();

            intent = new Intent(this, MainActivity.class);
        }

        if (scannedTicket != null) {
            mSharedDataSingleton.setScannedTicket(scannedTicket);
            intent = new Intent(this, ScannedTicketActivity.class);
        }

        finish();
        startActivity(intent);
    }
}
