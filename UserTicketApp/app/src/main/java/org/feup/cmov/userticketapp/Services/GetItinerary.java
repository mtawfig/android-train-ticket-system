package org.feup.cmov.userticketapp.Services;

import android.content.Context;
import android.os.AsyncTask;

import org.feup.cmov.userticketapp.Models.ErrorResponse;
import org.feup.cmov.userticketapp.Models.HttpResponse;
import org.feup.cmov.userticketapp.Models.Itinerary;
import org.feup.cmov.userticketapp.Models.Station;

public class GetItinerary extends AsyncTask<Station, Void, HttpResponse> {

    public interface OnGetItineraryTaskCompleted {
        void onTaskCompleted(Itinerary itinerary);
        void onTaskError(ErrorResponse error);
    }

    private OnGetItineraryTaskCompleted listener;
    private Context mContext;

    public GetItinerary(Context context, OnGetItineraryTaskCompleted listener){
        this.listener = listener;
        mContext = context;
    }

    @Override
    protected final HttpResponse doInBackground(Station... stations) {
        if (stations.length != 2) {
            return null;
        }

        Station fromStation = stations[0];
        Station toStation = stations[1];

        return ApiService.getHttpResponse(mContext, "/stations/" +
                fromStation.getStationId() + "/to/" + toStation.getStationId());
    }

    @Override
    protected void onPostExecute(HttpResponse response){
        if (response.isError()) {
            ErrorResponse error = ApiService.gson.fromJson(response.getContent(), ErrorResponse.class);
            listener.onTaskError(error);
        } else {
            Itinerary itinerary = ApiService.gson.fromJson(response.getContent(), Itinerary.class);
            listener.onTaskCompleted(itinerary);
        }
    }
}
