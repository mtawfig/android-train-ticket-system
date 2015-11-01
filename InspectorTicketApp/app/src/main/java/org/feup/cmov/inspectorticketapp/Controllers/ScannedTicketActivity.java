package org.feup.cmov.inspectorticketapp.Controllers;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;

import org.feup.cmov.inspectorticketapp.Models.SharedDataSingleton;
import org.feup.cmov.inspectorticketapp.Models.Ticket;
import org.feup.cmov.inspectorticketapp.Models.TicketDbHelper;
import org.feup.cmov.inspectorticketapp.Models.User;
import org.feup.cmov.inspectorticketapp.R;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;

public class ScannedTicketActivity extends AppCompatActivity {
    private SharedDataSingleton sharedData = SharedDataSingleton.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_ticket);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Ticket scannedTicket = sharedData.getScannedTicket();
        drawTicketValidation(scannedTicket);

        TicketDbHelper mDbHelper = new TicketDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Ticket storedTicket = TicketDbHelper.getTicketWithUUID(db, scannedTicket.getUuid());

        if (storedTicket == null) {
            storedTicket = scannedTicket;
            if (!scannedTicket.getUsed()) {
                scannedTicket.setUsed(true);
                TicketDbHelper.insertTicket(db, scannedTicket);
            }
        }
        else if (!storedTicket.getUsed()) {
            storedTicket.setUsed(true);
            TicketDbHelper.updateTicket(db, storedTicket);
        }

        drawTicketDetails(storedTicket);
    }

    private void drawTicketValidation(Ticket scannedTicket) {
        TextView ticketValidationText = (TextView) findViewById(R.id.ticket_validation_message);
        if (isValidTicket(scannedTicket)) {
            ticketValidationText.setText(getString(R.string.valid_ticket));
        }
        else {
            ticketValidationText.setText(getString(R.string.invalid_ticket));
        }
    }

    public void drawTicketDetails(Ticket storedTicket) {
        TextView ticketTitleText = (TextView) findViewById(R.id.ticket_title);
        if (storedTicket.getFromStation() != null && storedTicket.getToStation() != null) {
            ticketTitleText.setText(String.format(
                    ticketTitleText.getText().toString(),
                    storedTicket.getFromStation().getName(), storedTicket.getToStation().getName()));
        }
        else {
            ticketTitleText.setText(getString(R.string.ticket_no_details));
        }

        String date = android.text.format.DateUtils.formatDateTime(this,
                storedTicket.getDate(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR);

        TextView ticketDateText = (TextView) findViewById(R.id.ticket_date_hours);
        ticketDateText.setText(String.format(
                ticketDateText.getText().toString(),
                date, storedTicket.getHoursStart(), storedTicket.getMinutesStart()));

        TextView seatNumberTextView = (TextView) findViewById(R.id.seat_number_text);
        seatNumberTextView.setText(String.format(
                seatNumberTextView.getText().toString(),
                storedTicket.getSeatNumber() + 1));

        User user = storedTicket.getUser();
        TextView ticketUserNameText = (TextView) findViewById(R.id.user_name_text);
        TextView ticketUserEmailText = (TextView) findViewById(R.id.user_email_text);

        if (user != null) {
            ticketUserNameText.setText(String.format(
                    ticketUserNameText.getText().toString(),
                    user.getName()));
            ticketUserEmailText.setText(String.format(
                    ticketUserEmailText.getText().toString(),
                    user.getEmail()));
        }
        else {
            ticketUserNameText.setVisibility(View.GONE);
            ticketUserEmailText.setVisibility(View.GONE);
        }
    }

    private PublicKey mPublicKey;

    public PublicKey getPublicKey() {
        if (mPublicKey == null) {
            String publicKeyString = getString(R.string.public_key);
            mPublicKey = getKey(publicKeyString);
        }
        return mPublicKey;
    }

    private boolean isValidTicket(Ticket ticket) {
        try {
            PublicKey pub = getPublicKey();
            Signature sg = Signature.getInstance("SHA1WithRSA");
            sg.initVerify(pub);
            sg.update(ticket.toVerifiableJson().getBytes("utf-8"));

            byte[] signatureBytes = hexStringToByteArray(ticket.getSignature());

            return sg.verify(signatureBytes);
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
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
