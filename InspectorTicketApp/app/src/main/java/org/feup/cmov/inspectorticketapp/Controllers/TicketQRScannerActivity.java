package org.feup.cmov.inspectorticketapp.Controllers;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;

import com.google.zxing.Result;

import org.feup.cmov.inspectorticketapp.Models.SharedDataSingleton;
import org.feup.cmov.inspectorticketapp.Models.Ticket;
import org.feup.cmov.inspectorticketapp.Models.TicketContract;
import org.feup.cmov.inspectorticketapp.Models.TicketDbHelper;
import org.feup.cmov.inspectorticketapp.Models.TicketEntry;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class TicketQRScannerActivity extends Activity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    private SharedDataSingleton mSharedDataSingleton = SharedDataSingleton.getInstance();
    private TicketDbHelper mDbHelper;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mDbHelper = new TicketDbHelper(this);
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

        Intent intent;

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Ticket matchedTicket = TicketDbHelper.getTicketWithSignature(db, rawResult.getText());

        if (matchedTicket == null) {
            Toast.makeText(this, "Ticket not found in local database. Please download tickets from server.", Toast.LENGTH_SHORT)
                    .show();

            intent = new Intent(this, MainActivity.class);
        }

        else {
            mSharedDataSingleton.setScannedTicket(matchedTicket);
            intent = new Intent(this, ScannedTicketActivity.class);
        }

        finish();
        startActivity(intent);
    }
}
