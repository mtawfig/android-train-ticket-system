package org.feup.cmov.inspectorticketapp.Controllers;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.app.Activity;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import org.feup.cmov.inspectorticketapp.Models.SharedDataSingleton;
import org.feup.cmov.inspectorticketapp.Models.Ticket;
import org.feup.cmov.inspectorticketapp.R;

public class TicketNFCScannerActivity extends AppCompatActivity {
    private SharedDataSingleton mSharedDataSingleton = SharedDataSingleton.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_nfcscanner);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);
    }

    void processIntent(Intent intent) {
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        String result = new String(msg.getRecords()[0].getPayload());
        handleResult(result);
    }

    void handleResult(String result) {
        Intent intent = null;

        Ticket scannedTicket = null;
        try {
            scannedTicket = Ticket.fromTransmittableString(result);
        } catch(Exception e) {
            Toast.makeText(this, "NFC tag did not transmit ticket data", Toast.LENGTH_LONG)
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
