package org.feup.cmov.userticketapp.Services;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.reflect.TypeToken;

import org.feup.cmov.userticketapp.Models.ErrorResponse;
import org.feup.cmov.userticketapp.Models.HttpResponse;
import org.feup.cmov.userticketapp.Models.Station;
import org.feup.cmov.userticketapp.Models.Timetable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GetTimetables extends AsyncTask<Station, Void, HttpResponse> {

    private static Type timetableListType = new TypeToken<List<Timetable>>() {}.getType();

    public interface OnGetTimetableTaskCompleted {
        void onTaskCompleted(ArrayList<Timetable> timetable);
        void onTaskError(ErrorResponse error);
    }

    private OnGetTimetableTaskCompleted listener;
    private Context mContext;

    public GetTimetables(Context context, OnGetTimetableTaskCompleted listener){
        this.listener = listener;
        mContext = context;
    }

    @Override
    protected final HttpResponse doInBackground(Station... stations) {
        if (stations.length != 1) {
            return null;
        }

        Station selectedStation = stations[0];

        return ApiService.getHttpResponse(mContext, "/stations/" +
                selectedStation.getStationId() + "/timetable");
    }

    @Override
    protected void onPostExecute(HttpResponse response){
        if (response.isError()) {
            ErrorResponse error = ApiService.gson.fromJson(response.getContent(), ErrorResponse.class);
            listener.onTaskError(error);
        } else {
            ArrayList<Timetable> timetable = ApiService.gson.fromJson(response.getContent(), timetableListType);
            listener.onTaskCompleted(timetable);
        }
    }
}
