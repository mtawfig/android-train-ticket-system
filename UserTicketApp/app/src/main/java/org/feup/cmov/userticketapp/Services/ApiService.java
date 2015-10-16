package org.feup.cmov.userticketapp.Services;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.feup.cmov.userticketapp.Models.Itinerary;
import org.feup.cmov.userticketapp.Models.Station;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class ApiService {
    // private static String SERVER_ADDRESS = "http://10.62.201.202:8000";
    private static String SERVER_ADDRESS = "http://192.168.1.73:8000";

    final static Gson gson = new Gson();

    private static Type stationListType = new TypeToken<List<Station>>() {}.getType();

    private static String getHttpResponse(String address) {
        try {
            URL url = new URL(address);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                StringBuilder sb = new StringBuilder();
                String inputLine;
                while ((inputLine = br.readLine()) != null) {
                    sb.append(inputLine);
                }
                return sb.toString();
            } finally {
                urlConnection.disconnect();
            }
        } catch (Exception e) {
            return null;
        }
    }

    public interface OnGetStationsTaskCompleted {
        void onTaskCompleted(List<Station> stations);
    }

    public static class GetStations extends AsyncTask<Void, Void, List<Station>> {

        private OnGetStationsTaskCompleted listener;

        public GetStations(OnGetStationsTaskCompleted listener){
            this.listener = listener;
        }

        @Override
        protected List<Station> doInBackground(Void... params) {
            // TODO implement cache mechanism
            String response = getHttpResponse(SERVER_ADDRESS + "/stations");
            if (response == null) {
                return new ArrayList<>();
            }
            return gson.fromJson(response, stationListType);
        }

        @Override
        protected void onPostExecute(List<Station> stations){
            listener.onTaskCompleted(stations);
        }
    }

    public interface OnGetItineraryTaskCompleted {
        void onTaskCompleted(Itinerary itinerary);
    }

    public static class GetItinerary extends AsyncTask<Station, Void, Itinerary> {

        private OnGetItineraryTaskCompleted listener;

        public GetItinerary(OnGetItineraryTaskCompleted listener){
            this.listener = listener;
        }

        @Override
        protected final Itinerary doInBackground(Station... stations) {
            // TODO implement cache mechanism

            if (stations.length != 2) {
                return null;
            }

            Station fromStation = stations[0];
            Station toStation = stations[1];

            String response = getHttpResponse(SERVER_ADDRESS + "/stations/" +
                    fromStation.getStationId() + "/to/" + toStation.getStationId());
            if (response == null) {
                return null;
            }
            return gson.fromJson(response, stationListType);
        }

        @Override
        protected void onPostExecute(Itinerary itinerary){
            listener.onTaskCompleted(itinerary);
        }
    }

}
