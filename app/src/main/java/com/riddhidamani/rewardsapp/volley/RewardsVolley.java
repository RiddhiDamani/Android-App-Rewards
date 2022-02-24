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
import com.riddhidamani.rewardsapp.MainActivity;
import com.riddhidamani.rewardsapp.RewardActivity;
import com.riddhidamani.rewardsapp.reward.Reward;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RewardsVolley {
    private static final String TAG = "RewardsVolley";
    private static final String baseURL = "http://www.christopherhield.org/api/";
    private static final String endPoint = "Rewards/AddRewardRecord";
    private static String receiverUser, giverUser, giverName, amount, note;


    public static void saveRewardPoints(RewardActivity rewardActivity, Reward newRewardPoints) {

        RequestQueue queue = Volley.newRequestQueue(rewardActivity);
        Reward newReward = newRewardPoints;
        receiverUser = newReward.getReceiverUser();
        giverUser = newReward.getGiverUser();
        giverName = newReward.getGiverName();
        amount = newReward.getAmount();
        note = newReward.getNote();

        String urlToUse = makeUrl(receiverUser, giverUser, giverName, amount, note);
        Log.d(TAG, "updateProfileData: Full URL: " + urlToUse);

        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String giverNameData = response.getString("giverName");
                    String amountData = response.getString("amount");
                    String noteData = response.getString("note");
                    String awardDate = response.getString("awardDate");
                    rewardActivity.runOnUiThread(() -> {
                        rewardActivity.getUpdatedRewards(giverNameData, amountData, noteData, awardDate);
                    });
                } catch (JSONException exception) {
                    exception.printStackTrace();
                }
                Log.d(TAG, "Reward created successfully" + response);
            }
        };

        Response.ErrorListener error = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error.getMessage());
                String errorMsg = error.networkResponse == null ?
                        error.getClass().getName() : new String(error.networkResponse.data);
                Toast.makeText(rewardActivity, errorMsg, Toast.LENGTH_LONG).show();
            }
        };

        // Request a string response from the provided URL.
        JsonRequest<JSONObject> jsonRequest = new JsonRequest<JSONObject>(
                Request.Method.POST, urlToUse, null, listener, error) {

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
                headers.put("ApiKey", rewardActivity.APIKey);
                return headers;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(jsonRequest);

    }


    private static String makeUrl(String receiverUser, String giverUser,
                                  String giverName, String amount, String note) {

        String urlString = baseURL + endPoint;
        Log.d(TAG, "run: Initial URL: " + urlString);
        Uri.Builder buildURL = Uri.parse(urlString).buildUpon();
        buildURL.appendQueryParameter("receiverUser", receiverUser);
        buildURL.appendQueryParameter("giverUser", giverUser);
        buildURL.appendQueryParameter("giverName", giverName);
        buildURL.appendQueryParameter("amount", amount);
        buildURL.appendQueryParameter("note", note);
        return buildURL.build().toString();
    }
}
