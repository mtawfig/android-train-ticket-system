package org.feup.cmov.userticketapp.Services;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.reflect.TypeToken;

import org.feup.cmov.userticketapp.Models.Ticket;

import java.lang.reflect.Type;
import java.util.List;

public class GetTickets extends AsyncTask<Void, Void, List<Ticket>> {

    public interface OnGetTicketsTaskCompleted {
        void onTaskCompleted(List<Ticket> tickets);
    }

    private static Type ticketListType = new TypeToken<List<Ticket>>() {}.getType();
    private OnGetTicketsTaskCompleted listener;
    private Context mContext;

    public GetTickets(Context context, OnGetTicketsTaskCompleted listener){
        this.listener = listener;
        mContext = context;
    }

    @Override
    protected final List<Ticket> doInBackground(Void... params) {

        String response = ApiService.getHttpResponse(mContext, "/tickets");
        if (response == null) {
            return null;
        }
        return ApiService.gson.fromJson(response, ticketListType);
    }

    @Override
    protected void onPostExecute(List<Ticket> tickets){
        listener.onTaskCompleted(tickets);
    }
}
