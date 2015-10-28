package org.feup.cmov.userticketapp.Controllers;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

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

      /*
         QRCode Valid data char set:
              numeric data (digits 0 - 9);
              alphanumeric data (digits 0 - 9; upper case letters A -Z; nine other characters: space, $ % * + - . / : );
              byte data (default: ISO/IEC 8859-1);
              Kanji characters
      */
        // some data
        // for example: bizcard format
        // barcode.setData("BIZCARD:N:Kelly;X:Goto;T:Design Ethnographer;C:gotomedia LLC;A:2169 Folsom Street M302;B:4158647007;F:4158647004;M:4159907005;E:kelly@gotomedia.com;;");


        // a small QRcode with 68 characters
        barcode.setData(mTicket.getSignature());
        barcode.setDataMode(QRCode.M_AUTO);
        barcode.setVersion(1);
        barcode.setEcl(QRCode.ECL_L);

        //  if you want to encode GS1 compatible QR Code, you need set FNC1 mode to IBarcode.FNC1_ENABLE
        barcode.setFnc1Mode(IBarcode.FNC1_NONE);

        //  Set the processTilde property to true, if you want use the tilde character "~" to specify special characters in the input data. Default is false.
        //  1-byte character: ~ddd (character value from 0 ~ 255)
        //  ASCII (with EXT): from ~000 to ~255
        //  2-byte character: ~6ddddd (character value from 0 ~ 65535)
        //  Unicode: from ~600000 to ~665535
        //  ECI: from ~7000000 to ~7999999
        //  SJIS: from ~9ddddd (Shift JIS 0x8140 ~ 0x9FFC and 0xE040 ~ 0xEBBF)
        barcode.setProcessTilde(false);

        // unit of measure for X, Y, LeftMargin, RightMargin, TopMargin, BottomMargin
        barcode.setUom(IBarcode.UOM_PIXEL);
        // barcode module width in pixel
        barcode.setX(canvas.getWidth() * 27 / 1020);

        // barcode.setLeftMargin(31f);
        // barcode.setRightMargin(31f);
        // barcode.setTopMargin(35f);
        // barcode.setBottomMargin(35f);
        // barcode image resolution in dpi
        barcode.setResolution(72);

        // barcode bar color and background color in Android device
        barcode.setForeColor(AndroidColor.black);
        barcode.setBackColor(AndroidColor.white);

        RectF bounds = new RectF(0, 0, canvas.getWidth(), canvas.getHeight());

        try {
            barcode.drawBarcode(canvas, bounds);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
