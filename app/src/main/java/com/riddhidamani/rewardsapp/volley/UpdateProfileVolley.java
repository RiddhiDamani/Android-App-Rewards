package com.riddhidamani.rewardsapp.volley;


import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.riddhidamani.rewardsapp.EditProfileActivity;
import com.riddhidamani.rewardsapp.MainActivity;
import com.riddhidamani.rewardsapp.profile.Profile;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UpdateProfileVolley {

    private static final String TAG = "UpdateProfileVolley";
    private static final String baseURL = "http://www.christopherhield.org/api/";
    private static final String endPoint = "Profile/UpdateProfile";
    private static String firstname, lastname, username, department, story, position, password, location, imageString64;


    public static void updateProfileData(EditProfileActivity editProfileActivity, Profile profileHolder) {
        RequestQueue queue = Volley.newRequestQueue(editProfileActivity);
        Profile profile = profileHolder;
        firstname = profile.getFirstName();
        lastname = profile.getLastName();
        username = profile.getUsername();
        password = profile.getPassword();
        department = profile.getDepartment();
        position = profile.getPosition();
        location = profile.getLocation();
        story = profile.getStory();
        imageString64 = profile.getImageBytes();

        String urlToUse = makeUrl(firstname, lastname, username, department, story, position, password, location);
        Log.d(TAG, "updateProfileData: Full URL: " + urlToUse);

        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String points = response.getString("remainingPointsToAward");
                    editProfileActivity.runOnUiThread(() -> {
                        editProfileActivity.getUpdatedUserProfile(password, firstname, lastname, department, position, story);
                    });
                } catch (JSONException exception) {
                    exception.printStackTrace();
                }
                Log.d(TAG, "run: created successfully" + response);
            }
        };

        Response.ErrorListener error = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error.getMessage());
                String errorMsg = error.networkResponse == null ?
                        error.getClass().getName() : new String(error.networkResponse.data);
                Toast.makeText(editProfileActivity, errorMsg, Toast.LENGTH_LONG).show();
            }
        };


        // Request a string response from the provided URL.
        // Request Body has - imageBase64
        JsonRequest<JSONObject> jsonRequest = new JsonRequest<JSONObject>(
                Request.Method.PUT, urlToUse, imageString64, listener, error) {

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
                headers.put("ApiKey", editProfileActivity.APIKey);
                return headers;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(jsonRequest);

    }

    private static String makeUrl(String firstname, String lastname,
                                  String username, String department, String story,
                                  String position, String password, String location) {

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
        buildURL.appendQueryParameter("location", location);
        return buildURL.build().toString();
    }
}
