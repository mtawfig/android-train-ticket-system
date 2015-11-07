package org.feup.cmov.inspectorticketapp.Services;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.reflect.TypeToken;

import org.feup.cmov.inspectorticketapp.Models.ErrorResponse;
import org.feup.cmov.inspectorticketapp.Models.HttpResponse;
import org.feup.cmov.inspectorticketapp.Models.Station;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GetStations extends AsyncTask<Void, Void, HttpResponse> {

    public interface OnGetStationsTaskCompleted {
        void onTaskCompleted(List<Station> stations);
        void onTaskError(ErrorResponse error);
    }

    private OnGetStationsTaskCompleted listener;
    private Context mContext;

    private static Type stationListType = new TypeToken<List<Station>>() {}.getType();


    public GetStations(Context context, OnGetStationsTaskCompleted listener){
        this.listener = listener;
        mContext = context;
    }

    @Override
    protected HttpResponse doInBackground(Void... params) {
        return ApiService.getHttpResponse(mContext, "/stations");
    }

    @Override
    protected void onPostExecute(HttpResponse response){
        if (response.isError()) {
            ErrorResponse error = ApiService.gson.fromJson(response.getContent(), ErrorResponse.class);
            listener.onTaskError(error);
        } else {
            ArrayList<Station> stations = ApiService.gson.fromJson(response.getContent(), stationListType);
            listener.onTaskCompleted(stations);
        }
    }
}