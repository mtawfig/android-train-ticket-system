package org.feup.cmov.inspectorticketapp.Services;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.reflect.TypeToken;

import org.feup.cmov.inspectorticketapp.Models.ErrorResponse;
import org.feup.cmov.inspectorticketapp.Models.HttpResponse;
import org.feup.cmov.inspectorticketapp.Models.Ticket;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GetTickets extends AsyncTask<Void, Void, HttpResponse> {

    public interface OnGetTicketsTaskCompleted {
        void onTaskCompleted(List<Ticket> tickets);
        void onTaskError(ErrorResponse error);
    }

    private static Type ticketListType = new TypeToken<List<Ticket>>() {}.getType();
    private OnGetTicketsTaskCompleted listener;
    private Context mContext;

    public GetTickets(Context context, OnGetTicketsTaskCompleted listener){
        this.listener = listener;
        mContext = context;
    }

    @Override
    protected final HttpResponse doInBackground(Void... params) {

        return ApiService.getHttpResponse(mContext, "/tickets/all");
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
