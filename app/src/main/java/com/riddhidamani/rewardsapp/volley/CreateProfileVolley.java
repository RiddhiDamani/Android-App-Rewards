package com.riddhidamani.rewardsapp.volley;


import android.net.Uri;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.riddhidamani.rewardsapp.CreateProfileActivity;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;


public class CreateProfileVolley {

    private static final String TAG = "CreateProfileVolley";
    private static final String baseURL = "http://www.christopherhield.org/api/";
    private static final String endPoint = "Profile/CreateProfile";

    public static void createProfile(CreateProfileActivity createProfileActivity, String firstname, String lastname,
                                 String username, String password, String department, String position,
                                 String story, String remainingPointsToAward, String location,
                                 String imageString64) {

        RequestQueue queue = Volley.newRequestQueue(createProfileActivity);

        String urlToUse = makeUrl(firstname, lastname, username, password, department, position, story, remainingPointsToAward, location);

        Log.d(TAG, "run: Full URL: " + urlToUse);
        Log.d(TAG, "Main Activity API" + CreateProfileActivity.APIKey);

        Response.Listener<JSONObject> listener = response -> Log.d(TAG, "user profile created successfully" + response);

        Response.ErrorListener error = error1 -> {
            Log.d(TAG, "onErrorResponse: " + error1.getMessage());
            //String errorMsg = error.networkResponse == null ?
            //        error.getClass().getName() : new String(error.networkResponse.data);
            NetworkResponse response = error1.networkResponse;
            if(response != null) {
                switch (response.statusCode) {
                    case 400:
                        Toast.makeText(createProfileActivity, "Provided Values are null, empty, exceed the " +
                                "maximum field size, or violate special requirements", Toast.LENGTH_LONG).show();
                    case 409:
                        Toast.makeText(createProfileActivity, "User does not exist, or user attempts to change" +
                                "the username", Toast.LENGTH_LONG).show();
                }
            }
            //Toast.makeText(createProfileActivity, errorMsg, Toast.LENGTH_LONG).show();
        };

        // Request a string response from the provided URL.
        // Request Body has - imageBase64
        JsonRequest<JSONObject> jsonRequest = new JsonRequest<JSONObject>(
                Request.Method.POST, urlToUse, imageString64, listener, error) {

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                // This method is always the same!
                try {
                    String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));
                    return Response.success(new JSONObject(jsonString),
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (Exception e) {
                    return Response.error(new ParseError(e));
                }
            }

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=UTF-8");
                headers.put("Accept", "application/json");
                headers.put("ApiKey", CreateProfileActivity.APIKey);
                return headers;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(jsonRequest);
    }

    private static String makeUrl(String firstname, String lastname,
                                  String username, String password, String department, String position,
                                  String story, String remainingPointsToAward, String location) {


        String urlString = baseURL + endPoint;
        Log.d(TAG, "run: Initial URL: " + urlString);
        Uri.Builder buildURL = Uri.parse(urlString).buildUpon();
        buildURL.appendQueryParameter("firstName", firstname);
        buildURL.appendQueryParameter("lastName", lastname);
        buildURL.appendQueryParameter("userName", username);
        buildURL.appendQueryParameter("department", department);
        buildURL.appendQueryParameter("story", story);
        buildURL.appendQueryParameter("position", position);
        buildURL.appendQueryParameter("password", password);
        buildURL.appendQueryParameter("remainingPointsToAward", remainingPointsToAward);
        buildURL.appendQueryParameter("location", location);

        return buildURL.build().toString();
    }
}
