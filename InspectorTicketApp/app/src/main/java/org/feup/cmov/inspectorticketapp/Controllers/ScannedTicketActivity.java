package org.feup.cmov.inspectorticketapp.Controllers;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.feup.cmov.inspectorticketapp.Models.SharedDataSingleton;
import org.feup.cmov.inspectorticketapp.Models.Ticket;
import org.feup.cmov.inspectorticketapp.R;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;

public class ScannedTicketActivity extends AppCompatActivity {
    private SharedDataSingleton sharedData = SharedDataSingleton.getInstance();
    private Ticket mTicket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_ticket);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTicket = sharedData.getScannedTicket();

        validateTicket(mTicket);

        TextView ticketTitleText = (TextView) findViewById(R.id.ticket_title);
        ticketTitleText.setText(String.format(
                ticketTitleText.getText().toString(),
                mTicket.getFromStation().getName(), mTicket.getToStation().getName()));

        String date = android.text.format.DateUtils.formatDateTime(this,
                mTicket.getDate(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR);

        TextView ticketDateText = (TextView) findViewById(R.id.ticket_date_hours);
        ticketDateText.setText(String.format(
                ticketDateText.getText().toString(),
                date, mTicket.getHoursStart(), mTicket.getMinutesStart()));

        TextView seatNumberTextView = (TextView) findViewById(R.id.seat_number_text);
        seatNumberTextView.setText(String.format(
                seatNumberTextView.getText().toString(),
                mTicket.getSeatNumber() + 1));
    }

    private PublicKey mPublicKey;

    public PublicKey getPublicKey() {
        if (mPublicKey == null) {
            String publicKeyString = getString(R.string.public_key);
            mPublicKey = getKey(publicKeyString);
        }
        return mPublicKey;
    }

    private void validateTicket(Ticket ticket) {
        try {
            PublicKey pub = getPublicKey();
            Signature sg = Signature.getInstance("SHA1WithRSA");
            sg.initVerify(pub);
            sg.update(ticket.toVerifiableJson().getBytes("utf-8"));

            byte[] signatureBytes = hexStringToByteArray(ticket.getSignature());

            boolean isValidTicket = sg.verify(signatureBytes);

            if (isValidTicket) {
                Toast.makeText(this, "Ticket is valid", Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(this, "Not a valid ticket!", Toast.LENGTH_SHORT)
                        .show();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }


    public static PublicKey getKey(String key){
        try{
            byte[] keyBytes = Base64.decode(key.getBytes("utf-8"), Base64.DEFAULT);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(spec);
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }
}
