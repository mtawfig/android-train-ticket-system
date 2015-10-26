package org.feup.cmov.userticketapp.Services;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.reflect.TypeToken;

import org.feup.cmov.userticketapp.Models.BuyTicketOptions;
import org.feup.cmov.userticketapp.Models.ErrorResponse;
import org.feup.cmov.userticketapp.Models.HttpResponse;
import org.feup.cmov.userticketapp.Models.Station;
import org.feup.cmov.userticketapp.Models.Ticket;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BuyTickets extends AsyncTask<BuyTicketOptions, Void, HttpResponse> {

    public interface OnBuyTicketsTaskCompleted {
        void onTaskCompleted(ArrayList<Ticket> tickets);
        void onTaskError(ErrorResponse error);
    }

    private OnBuyTicketsTaskCompleted listener;
    private Context mContext;

    public BuyTickets(Context context, OnBuyTicketsTaskCompleted listener){
        this.listener = listener;
        mContext = context;
    }

    private static Type ticketListType = new TypeToken<List<Ticket>>() {}.getType();

    @Override
    protected final HttpResponse doInBackground(BuyTicketOptions... optionsList) {
        if (optionsList == null || optionsList[0] == null) {
            return null;
        }

        BuyTicketOptions options = optionsList[0];

        ContentValues contentValues = new ContentValues();
        contentValues.put("cardNumber", options.getCreditCardNumber().replaceAll("\\D", ""));
        contentValues.put("monthExpiration", options.getCreditCardMonth());
        contentValues.put("yearExpiration", options.getCreditCardYear());
        contentValues.put("cardSecurityCode", options.getCreditCardCode());

        if (options.getDate() != null) {
            String dateString = new SimpleDateFormat("yyyy-MM-dd", Locale.UK).format(options.getDate());
            contentValues.put("date", dateString);
        }

        ArrayList<Integer> arraySeatNumber = options.getArraySeatNumber();
        if (arraySeatNumber != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < arraySeatNumber.size(); i++) {
                if (i == 0) {
                    sb.append('[').append(arraySeatNumber.get(i).toString());
                } else {
                    sb.append(',').append(arraySeatNumber.get(i).toString());
                }
            }
            sb.append(']');
            contentValues.put("arraySeatNumber", sb.toString());
        }

        return ApiService.getHttpPostResponse(mContext,
                "/tickets/" + options.getStartStation().getStationId()
                        + "/to/" + options.getEndStation().getStationId(), contentValues);
    }

    @Override
    protected void onPostExecute(HttpResponse response){
        if (response.isError()) {
            ErrorResponse error = ApiService.gson.fromJson(response.getContent(), ErrorResponse.class);
            listener.onTaskError(error);
        } else {
            ArrayList<Ticket> tickets = ApiService.gson.fromJson(response.getContent(), ticketListType);
            listener.onTaskCompleted(tickets);
        }
    }
}
