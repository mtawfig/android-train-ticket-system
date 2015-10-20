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
    // private static String SERVER_ADDRESS = "http://172.30.44.210:8000";

    final static Gson gson = new Gson();

    public static String getHttpResponse(String endpoint) {
        try {
            // TODO implement cache mechanism

            URL url = new URL(SERVER_ADDRESS + endpoint);
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
}
