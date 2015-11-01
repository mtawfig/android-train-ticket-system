package org.feup.cmov.inspectorticketapp.Services;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.reflect.TypeToken;

import org.feup.cmov.inspectorticketapp.Models.ErrorResponse;
import org.feup.cmov.inspectorticketapp.Models.HttpResponse;
import org.feup.cmov.inspectorticketapp.Models.Ticket;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PutTickets extends AsyncTask<Ticket, Void, HttpResponse> {

    public interface OnPutTicketsTaskCompleted {
        void onTaskCompleted(String message);
        void onTaskError(ErrorResponse error);
    }

    private static Type ticketListType = new TypeToken<List<Ticket>>() {}.getType();
    private OnPutTicketsTaskCompleted listener;
    private Context mContext;

    public PutTickets(Context context, OnPutTicketsTaskCompleted listener){
        this.listener = listener;
        mContext = context;
    }

    @Override
    protected final HttpResponse doInBackground(Ticket... tickets) {

        ContentValues contentValues = new ContentValues();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tickets.length; i++) {
            if (i == 0) {
                sb.append('[');
            } else {
                sb.append(',');
            }
            Ticket ticket = tickets[i];
            sb.append("{\"uuid\":\"")
                    .append(ticket.getUuid())
                    .append("\",\"used\":")
                    .append(ticket.getUsed())
                    .append("}");
        }
        sb.append(']');

        contentValues.put("tickets", sb.toString());

        return ApiService.getHttpPutResponse(mContext,
                "/tickets", contentValues);
    }

    private static Type responseType = new TypeToken<Map<String, String>>() {}.getType();

    @Override
    protected void onPostExecute(HttpResponse response){
        if (response.isError()) {
            ErrorResponse error = ApiService.gson.fromJson(response.getContent(), ErrorResponse.class);
            listener.onTaskError(error);
        } else {
            Map<String, String> parsedResponse = ApiService.gson.fromJson(response.getContent(), responseType);
            listener.onTaskCompleted(parsedResponse.get("message"));
        }
    }
}
