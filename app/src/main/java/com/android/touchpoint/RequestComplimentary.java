package com.android.touchpoint;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RequestComplimentary extends AsyncTask<String, Void, String> {
    private final Callback callback;

    public RequestComplimentary(Callback callback) {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(String... params) {
        String urlString = params[0]; // URL to call
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            int responseCode = urlConnection.getResponseCode();

            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    result.append(inputLine);
                }
                in.close();
            } else {
                result.append("Error: ").append(responseCode);
            }
        } catch (Exception e) {
            result.append("Exception: ").append(e.getMessage());
        }
        return result.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        callback.onResponse(result);
    }

    public interface Callback {
        void onResponse(String result);
    }
}
