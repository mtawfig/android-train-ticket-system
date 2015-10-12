package org.feup.cmov.userticketapp.Services;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
    private static String SERVER_ADDRESS = "http://10.62.201.202:8000";

    final static Gson gson = new Gson();

    private static Type stationListType = new TypeToken<List<Station>>() {}.getType();

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
            URL url = null;
            try {
                url = new URL(SERVER_ADDRESS + "/stations");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    return gson.fromJson(reader, stationListType);
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                return new ArrayList<>();
            }
        }

        @Override
        protected void onPostExecute(List<Station> stations){
            listener.onTaskCompleted(stations);
        }
    }

}
