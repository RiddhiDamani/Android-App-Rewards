package com.riddhidamani.rewardsapp.volley;

import android.net.Uri;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.riddhidamani.rewardsapp.MainActivity;
import com.riddhidamani.rewardsapp.ProfileActivity;
import com.riddhidamani.rewardsapp.R;
import java.util.HashMap;
import java.util.Map;

public class DeleteProfileVolley {
    private static final String TAG = "DeleteProfileVolley";
    private static final String baseURL = "http://www.christopherhield.org/api/";
    private static final String endPoint = "Profile/DeleteProfile";


    public static void initiate(ProfileActivity profileActivity, String userName) {

        RequestQueue queue = Volley.newRequestQueue(profileActivity);

        String urlToUse = makeUrl(profileActivity, userName);
        Log.d(TAG, "run: Full URL: " + urlToUse);

        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String responseString = response;
                Log.d(TAG, "DeleteProfileVolley: " + responseString);
            }
        };

        Response.ErrorListener error = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error.getMessage());
                String errorMsg = error.networkResponse == null ?
                        error.getClass().getName() : new String(error.networkResponse.data);
                //activity.runOnUiThread(() -> activity.handleError(errorMsg));
            }
        };

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, urlToUse,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                }
            }){
                @Override
                public Map<String, String> getHeaders()  {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json; charset=UTF-8");
                    headers.put("Accept", "application/json");
                    headers.put("ApiKey", MainActivity.APIKey);
                    return headers;
                }
            };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    private static String makeUrl(ProfileActivity profileActivity, String userName) {
        String urlString = baseURL + endPoint;
        Uri.Builder buildURL = Uri.parse(urlString).buildUpon();
        buildURL.appendQueryParameter("userName", userName);

        return buildURL.build().toString();
    }
}
