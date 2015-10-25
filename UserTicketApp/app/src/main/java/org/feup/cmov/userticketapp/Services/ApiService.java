package org.feup.cmov.userticketapp.Services;

import android.content.ContentValues;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class ApiService {

    // private static String SERVER_ADDRESS = "http://10.62.201.202:8000";
    private static String SERVER_ADDRESS = "http://192.168.1.73:8000";
    //private static String SERVER_ADDRESS = "http://172.30.38.85:8000";
    // private static String SERVER_ADDRESS = "http://10.125.40.136:8000";

    private final static String CHARSET = "UTF-8";

    final static Gson gson = new Gson();

    public static String getHttpResponse(String endpoint) {
        try {
            // TODO implement cache mechanism

            URL url = new URL(SERVER_ADDRESS + endpoint);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setDoInput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setUseCaches(false);

            try {
                int responseCode = urlConnection.getResponseCode();
                // TODO HANDLE ERRORS
                if(responseCode == 200) {

                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    StringBuilder sb = new StringBuilder();
                    String inputLine;
                    while ((inputLine = br.readLine()) != null) {
                        sb.append(inputLine);
                    }
                    return sb.toString();
                }
            } finally {
                urlConnection.disconnect();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public static String getHttpPostResponse(String endpoint, ContentValues data) {
        try {
            URL url = new URL(SERVER_ADDRESS + endpoint);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Accept-Charset", CHARSET);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + CHARSET);
            urlConnection.setUseCaches(false);

            try {

                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, CHARSET));
                writer.write(getData(data));
                writer.flush();
                writer.close();
                os.close();

                int responseCode = urlConnection.getResponseCode();

                if (responseCode == 200) {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    StringBuilder sb = new StringBuilder();
                    String inputLine;
                    while ((inputLine = br.readLine()) != null) {
                        sb.append(inputLine);
                    }
                    return sb.toString();
                }
            } catch (Exception e) {
                return  null;
            } finally {
                urlConnection.disconnect();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    private static String getData(ContentValues data) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Map.Entry<String, Object> entry : data.valueSet()) {

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), CHARSET));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue().toString(), CHARSET));
        }

        return result.toString();
    }
}
