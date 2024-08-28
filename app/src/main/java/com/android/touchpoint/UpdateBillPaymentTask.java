package com.android.touchpoint;

import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONObject;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateBillPaymentTask extends AsyncTask<String, Void, String> {

    private static final String TAG = "UpdateBillPaymentTask";
    private final CashPayment activity;
    private final UpdateBillPaymentHandler handler;

    public UpdateBillPaymentTask(CashPayment activity) {
        this.activity = activity;
        this.handler = new UpdateBillPaymentHandler(activity);
    }

    @Override
    protected String doInBackground(String... params) {
        String urlString = params[0]; // URL to call
        String bill = params[1];
        String id = params[2];
        String access = params[3];
        String code = params[4];
        String gate = params[5];
        String entryTime = params[6];
        String vehicle = params[7];
        String ptime = params[8];
        String payTime = params[9];
        String payMode = params[10];

        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // Prepare POST data
            String postData = "bill=" + bill +
                    "&id=" + id +
                    "&access=" + access +
                    "&code=" + code +
                    "&gate=" + gate +
                    "&etime=" + entryTime +
                    "&vehicle=" + vehicle +
                    "&ptime=" + ptime +
                    "&paytime=" + payTime +
                    "&paymode=" + payMode;

            // Send POST data
            OutputStream os = urlConnection.getOutputStream();
            os.write(postData.getBytes());
            os.flush();
            os.close();

            // Get the response
            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();

            return result.toString();

        } catch (Exception e) {
            Log.e(TAG, "Error during HTTP request", e);
            return "Error: " + e.getMessage();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    @Override
    protected void onPostExecute(String result) {
        // Use the UpdateBillPaymentHandler to process the result
        handler.handleResponse(result);
    }
}
