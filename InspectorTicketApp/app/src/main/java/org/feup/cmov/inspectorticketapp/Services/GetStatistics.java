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

public class GetStatistics extends AsyncTask<Void, Void, HttpResponse> {

    public interface OnGetStatisticsTaskCompleted {
        void onTaskCompleted(String json);
        void onTaskError(ErrorResponse error);
    }

    private OnGetStatisticsTaskCompleted listener;
    private Context mContext;

    public GetStatistics(Context context, OnGetStatisticsTaskCompleted listener){
        this.listener = listener;
        mContext = context;
    }

    @Override
    protected final HttpResponse doInBackground(Void... params) {

        return ApiService.getHttpResponse(mContext, "/tickets/stats");
    }

    @Override
    protected void onPostExecute(HttpResponse response){
        if (response.isError()) {
            ErrorResponse error = ApiService.gson.fromJson(response.getContent(), ErrorResponse.class);
            listener.onTaskError(error);
        } else {
            listener.onTaskCompleted(response.getContent());
        }
    }
}
