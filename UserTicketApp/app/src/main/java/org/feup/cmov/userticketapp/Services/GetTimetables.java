package org.feup.cmov.userticketapp.Services;

import android.os.AsyncTask;

import com.google.gson.reflect.TypeToken;

import org.feup.cmov.userticketapp.Models.Station;
import org.feup.cmov.userticketapp.Models.Timetable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GetTimetables extends AsyncTask<Station, Void, ArrayList<Timetable>> {

    private static Type timetableListType = new TypeToken<List<Timetable>>() {}.getType();

    public interface OnGetTimetableTaskCompleted {
        void onTaskCompleted(ArrayList<Timetable> timetable);
    }

    private OnGetTimetableTaskCompleted listener;

    public GetTimetables(OnGetTimetableTaskCompleted listener){
        this.listener = listener;
    }

    @Override
    protected final  ArrayList<Timetable> doInBackground(Station... stations) {
        if (stations.length != 1) {
            return null;
        }

        Station selectedStation = stations[0];

        String response = ApiService.getHttpResponse("/stations/" +
                selectedStation.getStationId() + "/timetable");
        if (response == null) {
            return null;
        }
        return ApiService.gson.fromJson(response, timetableListType);
    }

    @Override
    protected void onPostExecute(ArrayList<Timetable> timetable){
        listener.onTaskCompleted(timetable);
    }
}
