package com.riddhidamani.rewardsapp.volley;

import android.graphics.Color;
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
import com.riddhidamani.rewardsapp.LeaderboardActivity;
import com.riddhidamani.rewardsapp.MainActivity;
import com.riddhidamani.rewardsapp.ProfileActivity;
import com.riddhidamani.rewardsapp.profile.Profile;
import com.riddhidamani.rewardsapp.reward.Reward;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GetAllProfileVolley {

    private static final String TAG = "GetAllProfileVolley";
    private static final String baseURL = "http://www.christopherhield.org/api/";
    private static final String endPoint = "Profile/GetAllProfiles";
    private static final Profile loggedInUserDetails = ProfileActivity.loggedInUserProfile;

    public static void getAllUserProfiles(LeaderboardActivity leaderboardActivity, String APIKey) {

        RequestQueue queue = Volley.newRequestQueue(leaderboardActivity);
        String urlString = baseURL + endPoint;
        Uri.Builder buildURL = Uri.parse(urlString).buildUpon();
        String urlToUse = buildURL.build().toString();
        List<Profile> profileList = new ArrayList<>();

        Response.Listener<JSONArray> listener = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Profile newProfile;
                for(int i = 0; i < response.length(); i++){
                   try {
                       JSONObject profile = (JSONObject) response.get(i);
                       String firstName = profile.getString("firstName");
                       String lastName = profile.getString("lastName");
                       String userName = profile.getString("userName");
                       String department = profile.getString("department");
                       String story = profile.getString("story");
                       String position = profile.getString("position");
                       String imageBytes = profile.getString("imageBytes");

                       newProfile = new Profile(userName);
                       newProfile.setFirstName(firstName);
                       newProfile.setLastName(lastName);
                       newProfile.setDepartment(department);
                       newProfile.setPosition(position);
                       newProfile.setStory(story);
                       newProfile.setImageBytes(imageBytes);


                       JSONArray reviewArray = profile.getJSONArray("rewardRecordViews");

                       List<Reward> rewardList = new ArrayList<Reward>();

                       int totalPoints = 0;

                       for(int j = 0; j < reviewArray.length(); j++) {
                           JSONObject review = (JSONObject)reviewArray.get(j);
                           String giverName = review.getString("giverName");
                           String amount = review.getString("amount");
                           totalPoints += Integer.parseInt(amount);
                           String note = review.getString("note");
                           String awardDate = review.getString("awardDate");

                           Reward newReward = new Reward();
                           newReward.setAmount(amount);
                           newReward.setGiverName(giverName);
                           newReward.setNote(note);
                           newReward.setAwardDate(awardDate);

                           rewardList.add(newReward);
                       }
                       newProfile.setPoints(String.valueOf(totalPoints));
                       newProfile.setListOfRewards(rewardList);

                       profileList.add(newProfile);


                       final Profile finalNewProfile = newProfile;
                       leaderboardActivity.runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               leaderboardActivity.addProfile(finalNewProfile);
                           }
                       });
                   }
                    catch (Exception e) {

                    }
                }
            }
        };

        Response.ErrorListener error = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error.getMessage());
                String errorMsg = error.networkResponse == null ?
                        error.getClass().getName() : new String(error.networkResponse.data);
                //mainActivity.runOnUiThread(() -> mainActivity.handleError(errorMsg));
            }
        };

        // Request a json response from the provided URL.
        JsonRequest<JSONArray> jsonRequest = new JsonRequest<JSONArray>(
                Request.Method.GET, urlToUse, null, listener, error) {

            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
                // This method is always the same!
                try {
                    String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));
                    return Response.success(new JSONArray(jsonString),
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
                headers.put("ApiKey", APIKey);
                return headers;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(jsonRequest);
    }
}
