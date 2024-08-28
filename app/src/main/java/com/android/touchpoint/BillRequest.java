package com.android.touchpoint;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BillRequest extends AsyncTask<String, Void, Object> {

    private static final String TAG = "BillRequest";
    private final Callback callback;

    public interface Callback {
        void onResult(Object result);
    }

    public BillRequest(Callback callback) {
        this.callback = callback;
    }

    @Override
    protected Object doInBackground(String... params) {
        String urlString = params[0]; // URL to call
        StringBuilder result = new StringBuilder();

        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
            } finally {
                urlConnection.disconnect();
            }

            // Parse the JSON response
            JSONObject jsonResponse = new JSONObject(result.toString());
            String status = jsonResponse.getString("status");
            if (status.equals("success")) {
                ParkingBillResponse response = new ParkingBillResponse();
                response.setStatus(jsonResponse.getString("status"));
                response.setId(jsonResponse.getString("id"));
                response.setCode(jsonResponse.getString("code"));
                response.setGate(jsonResponse.getString("gate"));
                response.setVclass(jsonResponse.getString("vclass"));
                response.setEntryTime(jsonResponse.getString("entry_time"));
                response.setPayTime(jsonResponse.getString("pay_time"));
                response.setPtime(jsonResponse.getString("Ptime"));
                response.setBill(jsonResponse.getInt("bill"));
                response.setAccessType(jsonResponse.getString("accesstype"));
                return response;
            } else {
                return result.toString(); // Return the raw response in case of failure
            }
        } catch (Exception e) {
            Log.e(TAG, "Error during HTTP request", e);
            return "Error: " + e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(Object result) {
        callback.onResult(result);
    }
}
