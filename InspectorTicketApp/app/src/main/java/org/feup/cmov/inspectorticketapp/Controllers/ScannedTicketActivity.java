package org.feup.cmov.inspectorticketapp.Controllers;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.widget.TextView;

import org.feup.cmov.inspectorticketapp.Models.SharedDataSingleton;
import org.feup.cmov.inspectorticketapp.Models.Ticket;
import org.feup.cmov.inspectorticketapp.R;

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
}
