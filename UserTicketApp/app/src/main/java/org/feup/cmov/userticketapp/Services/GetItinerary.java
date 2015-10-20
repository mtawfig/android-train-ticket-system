package org.feup.cmov.userticketapp.Services;

import android.os.AsyncTask;

import org.feup.cmov.userticketapp.Models.Itinerary;
import org.feup.cmov.userticketapp.Models.Station;

public class GetItinerary extends AsyncTask<Station, Void, Itinerary> {

    public interface OnGetItineraryTaskCompleted {
        void onTaskCompleted(Itinerary itinerary);
    }

    private OnGetItineraryTaskCompleted listener;

    public GetItinerary(OnGetItineraryTaskCompleted listener){
        this.listener = listener;
    }

    @Override
    protected final Itinerary doInBackground(Station... stations) {
        if (stations.length != 2) {
            return null;
        }

        Station fromStation = stations[0];
        Station toStation = stations[1];

        String response = ApiService.getHttpResponse("/stations/" +
                fromStation.getStationId() + "/to/" + toStation.getStationId());
        if (response == null) {
            return null;
        }
        return ApiService.gson.fromJson(response, Itinerary.class);
    }

    @Override
    protected void onPostExecute(Itinerary itinerary){
        listener.onTaskCompleted(itinerary);
    }
}
