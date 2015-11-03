package org.feup.cmov.userticketapp.Services;

import android.content.Context;
import android.os.AsyncTask;

import org.feup.cmov.userticketapp.Models.ErrorResponse;
import org.feup.cmov.userticketapp.Models.GetItineraryOptions;
import org.feup.cmov.userticketapp.Models.HttpResponse;
import org.feup.cmov.userticketapp.Models.Itinerary;
import org.feup.cmov.userticketapp.Models.Station;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class GetItinerary extends AsyncTask<GetItineraryOptions, Void, HttpResponse> {

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
    protected final HttpResponse doInBackground(GetItineraryOptions... optionsList) {
        if (optionsList.length != 1) {
            return null;
        }

        GetItineraryOptions options = optionsList[0];

        String url = "/stations/" +
                options.getFromStation().getStationId() +
                "/to/" + options.getToStation().getStationId();

        Date selectedDate = options.getSelectedDate();
        if (selectedDate != null) {
            String isoDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.UK)
                    .format(selectedDate);
            url += "?date=" + isoDate;
        }

        return ApiService.getHttpResponse(mContext, url);
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
