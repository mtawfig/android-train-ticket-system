package org.feup.cmov.userticketapp.Services;

import android.os.AsyncTask;

import com.google.gson.reflect.TypeToken;

import org.feup.cmov.userticketapp.Models.Station;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GetStations extends AsyncTask<Void, Void, List<Station>> {

    public interface OnGetStationsTaskCompleted {
        void onTaskCompleted(List<Station> stations);
    }

    private OnGetStationsTaskCompleted listener;
    private static Type stationListType = new TypeToken<List<Station>>() {}.getType();


    public GetStations(OnGetStationsTaskCompleted listener){
        this.listener = listener;
    }

    @Override
    protected List<Station> doInBackground(Void... params) {
        String response = ApiService.getHttpResponse("/stations");
        if (response == null) {
            return new ArrayList<>();
        }
        return ApiService.gson.fromJson(response, stationListType);
    }

    @Override
    protected void onPostExecute(List<Station> stations){
        listener.onTaskCompleted(stations);
    }
}