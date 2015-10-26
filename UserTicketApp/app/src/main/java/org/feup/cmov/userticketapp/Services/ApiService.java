package org.feup.cmov.userticketapp.Services;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;

import com.google.gson.Gson;

import org.feup.cmov.userticketapp.Models.SharedPreferencesFactory;
import org.feup.cmov.userticketapp.R;

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

    // private static String SERVER_ADDRESS = "http://localhost:8000";
    private static String SERVER_ADDRESS = "http://192.168.1.73:8000";
    //private static String SERVER_ADDRESS = "http://172.30.38.85:8000";
    // private static String SERVER_ADDRESS = "http://10.125.40.136:8000";

    private final static String CHARSET = "UTF-8";

    final static Gson gson = new Gson();

    public static String getHttpResponse(Context context, String endpoint) {
        try {
            // TODO implement cache mechanism

            URL url = new URL(SERVER_ADDRESS + endpoint);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setDoInput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setUseCaches(false);

            String token = getAuthorizationToken(context);
            if (token != null) {
                urlConnection.setRequestProperty ("Authorization", token);
            }

            try {
                int responseCode = urlConnection.getResponseCode();
                // TODO HANDLE ERRORS

                InputStream in;
                if (responseCode == 200) {
                    in = new BufferedInputStream(urlConnection.getInputStream());
                } else {
                    in = new BufferedInputStream(urlConnection.getErrorStream());
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                StringBuilder sb = new StringBuilder();
                String inputLine;
                while ((inputLine = br.readLine()) != null) {
                    sb.append(inputLine);
                }

                if (responseCode != 200) {
                    Log.e("Error", sb.toString());
                    return null;
                }

                return sb.toString();

            } finally {
                urlConnection.disconnect();
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static String getAuthorizationToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.shared_preferences_identifier),
                Context.MODE_PRIVATE);

        boolean isUserSignedIn = SharedPreferencesFactory.getBooleanValueFromPreferences(
                context.getString(R.string.shared_preferences_user_sign_in),
                sharedPreferences);

        if (isUserSignedIn) {
            return SharedPreferencesFactory.getStringValueFromPreferences(
                    context.getString(R.string.shared_preferences_token_key),
                    sharedPreferences);
        }
        return null;
    }

    public static String getHttpPostResponse(Context context, String endpoint, ContentValues data) {
        try {
            URL url = new URL(SERVER_ADDRESS + endpoint);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Accept-Charset", CHARSET);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + CHARSET);
            urlConnection.setUseCaches(false);

            String token = getAuthorizationToken(context);
            if (token != null) {
                urlConnection.setRequestProperty ("Authorization", token);
            }

            try {

                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, CHARSET));
                writer.write(getData(data));
                writer.flush();
                writer.close();
                os.close();

                int responseCode = urlConnection.getResponseCode();

                InputStream in;
                if (responseCode == 200) {
                    in = new BufferedInputStream(urlConnection.getInputStream());
                } else {
                    in = new BufferedInputStream(urlConnection.getErrorStream());
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                StringBuilder sb = new StringBuilder();
                String inputLine;
                while ((inputLine = br.readLine()) != null) {
                    sb.append(inputLine);
                }

                if (responseCode != 200) {
                    Log.e("Error", sb.toString());
                    return null;
                }

                return sb.toString();

            } catch (Exception e) {
                return  null;
            } finally {
                urlConnection.disconnect();
            }
        } catch (Exception e) {
            return null;
        }
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
