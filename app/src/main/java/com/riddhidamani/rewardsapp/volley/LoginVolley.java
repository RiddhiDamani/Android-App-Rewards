package com.riddhidamani.rewardsapp.volley;

import android.net.Uri;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.riddhidamani.rewardsapp.MainActivity;
import com.riddhidamani.rewardsapp.R;
import com.riddhidamani.rewardsapp.profile.Profile;
import com.riddhidamani.rewardsapp.reward.Reward;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginVolley {

    private static final String TAG = "LoginVolley";
    private static final String baseURL = "http://www.christopherhield.org/api/";
    private static final String endPoint = "Profile/Login";

    public static void getLoginDetails(MainActivity mainActivity, String usernameStr, String passwordStr) {
        RequestQueue queue = Volley.newRequestQueue(mainActivity);
        String urlToUse = makeUrl(mainActivity, usernameStr, passwordStr);

        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String firstNameStr = response.getString("firstName");
                    String lastNameStr = response.getString("lastName");
                    String usernameStr = response.getString("userName");
                    String dpStr = response.getString("department");
                    String storyStr = response.getString("story");
                    String posStr = response.getString("position");
                    String passwordStr = response.getString("password");
                    String remainingPointsToAward = response.getString("remainingPointsToAward");
                    String locationStr = response.getString("location");
                    String imageBytes = response.getString("imageBytes");

                    Profile newProfile = new Profile(usernameStr);
                    newProfile.setFirstName(firstNameStr);
                    newProfile.setLastName(lastNameStr);
                    newProfile.setDepartment(dpStr);
                    newProfile.setStory(storyStr);
                    newProfile.setPosition(posStr);
                    newProfile.setPassword(passwordStr);
                    newProfile.setPointsToAward(remainingPointsToAward);
                    newProfile.setLocation(locationStr);
                    newProfile.setImageBytes(imageBytes);

                    List<Reward> rewardList = new ArrayList<Reward>();
                    JSONArray reviewArray = response.getJSONArray("rewardRecordViews");
                    int totalPoints = 0;

                    for(int i = 0; i < reviewArray.length(); i++) {
                        JSONObject oneReview = (JSONObject) reviewArray.get(i);
                        String giverName = oneReview.getString("giverName");
                        String amount = oneReview.getString("amount");
                        totalPoints += Integer.parseInt(amount);

                        String note = oneReview.getString("note");
                        String awardDate = oneReview.getString("awardDate");

                        Reward newReward = new Reward();
                        newReward.setAmount(amount);
                        newReward.setGiverName(giverName);
                        newReward.setNote(note);
                        newReward.setAwardDate(awardDate);

                        rewardList.add(newReward);
                    }

                    newProfile.setPoints(String.valueOf(totalPoints));
                    newProfile.setListOfRewards(rewardList);
                    final Profile p = newProfile;

                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mainActivity.displayLoginProfile(p);
                        }
                    });

                } catch (JSONException exception) {
                    exception.printStackTrace();
                }
            }
        };

        Response.ErrorListener error = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error.getMessage());
                String errorMsg = error.networkResponse == null ?
                        error.getClass().getName() : new String(error.networkResponse.data);
                mainActivity.runOnUiThread(() -> mainActivity.handleError(errorMsg));
            }
        };

        // Request a json response from the provided URL.
        JsonRequest<JSONObject> jsonRequest = new JsonRequest<JSONObject>(
                Request.Method.GET, urlToUse, null, listener, error) {

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
                headers.put("ApiKey", MainActivity.APIKey);
                return headers;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(jsonRequest);
    }

    private static String makeUrl(MainActivity activity, String username, String password) {

        String urlString = baseURL + endPoint;
        Uri.Builder buildURL = Uri.parse(urlString).buildUpon();
        buildURL.appendQueryParameter("userName", username);
        buildURL.appendQueryParameter("password", password);

        return buildURL.build().toString();
    }
}
