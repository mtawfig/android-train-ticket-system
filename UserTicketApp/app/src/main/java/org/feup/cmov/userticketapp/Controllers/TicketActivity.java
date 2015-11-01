package org.feup.cmov.userticketapp.Controllers;

import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.onbarcode.barcode.android.AndroidColor;
import com.onbarcode.barcode.android.IBarcode;
import com.onbarcode.barcode.android.QRCode;

import org.feup.cmov.userticketapp.Models.SharedDataSingleton;
import org.feup.cmov.userticketapp.Models.Ticket;
import org.feup.cmov.userticketapp.R;

public class TicketActivity extends AppCompatActivity {
    private SharedDataSingleton sharedData = SharedDataSingleton.getInstance();
    private Ticket mTicket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTicket = sharedData.getSelectedTicket();

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

        TextView ticketUsedTextView = (TextView) findViewById(R.id.ticket_used_text);
        if (mTicket.getUsed()) {
            ticketUsedTextView.setText(getString(R.string.ticket_used));
        }
        else {
            ticketUsedTextView.setText(getString(R.string.ticket_not_used));
        }

        SurfaceView surface = (SurfaceView) findViewById(R.id.surface);
        surface.getHolder().addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                // Do some drawing when surface is ready
                Canvas canvas = holder.lockCanvas();
                drawQRCode(canvas);
                holder.unlockCanvasAndPost(canvas);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }
        });
    }

    private void drawQRCode(Canvas canvas) {
        QRCode barcode = new QRCode();

        barcode.setData(mTicket.toTransmittableString());
        barcode.setDataMode(QRCode.M_AUTO);
        barcode.setVersion(1);
        barcode.setEcl(QRCode.ECL_L);

        barcode.setFnc1Mode(IBarcode.FNC1_NONE);
        barcode.setProcessTilde(false);
        barcode.setUom(IBarcode.UOM_PIXEL);
        barcode.setX(canvas.getWidth() * 21 / 1020);
        barcode.setResolution(72);
        RectF bounds = new RectF(0, 0, 0, 0);

        try {
            TypedArray array = getTheme().obtainStyledAttributes(new int[] {
                    android.R.attr.colorBackground,
                    android.R.attr.textColorPrimary,
            });
            int backgroundColor = array.getColor(0, 0xFF00FF);
            canvas.drawColor(backgroundColor);
            barcode.drawBarcode(canvas, bounds);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
