package com.riddhidamani.rewardsapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.riddhidamani.rewardsapp.profile.Profile;
import com.riddhidamani.rewardsapp.profile.ProfileAdapter;
import com.riddhidamani.rewardsapp.reward.Reward;
import com.riddhidamani.rewardsapp.volley.GetAllProfileVolley;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LeaderboardActivity";

    private List<Profile> profileList = new ArrayList<>();
    private ProfileAdapter mAdaptor;
    private RecyclerView recyclerView;
    private static int ADD_REWARD_REQUEST = 2;
    private ActivityResultLauncher<Intent> displayRewardLauncher;
    public static Reward reward;
    private int position;

    // Shared Preferences
    private SharedPreferencesConfig myPrefs;
    public static String APIKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        HomeNav.setupHomeIndicator(getSupportActionBar());
        setTitle(" Leaderboard");

        myPrefs = new SharedPreferencesConfig(this);
        APIKey = myPrefs.getValue("APIKey");

        recyclerView = findViewById(R.id.recyclerView);
        mAdaptor = new ProfileAdapter(profileList, this);
        recyclerView.setAdapter(mAdaptor);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getAllProfile();

        displayRewardLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), this::displayRewardHandler);

    }

    private void displayRewardHandler(ActivityResult activityResult) {
        Log.d(TAG, "On handleResult Method: Leaderboard Activity");
        if(activityResult.getResultCode() == 2) {
            Intent data = activityResult.getData();
            if(data != null) {
                reward = (Reward) data.getSerializableExtra("ADD_REWARD");
                if(reward != null) {
                    int totalPoints = Integer.parseInt(profileList.get(position).getPoints()) + Integer.parseInt(reward.getAmount());
                    profileList.get(position).setPoints(String.valueOf(totalPoints));
                    Collections.sort(profileList);
                    mAdaptor.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        position = recyclerView.getChildLayoutPosition(view);
        Profile profile = profileList.get(position);
        Intent intent = new Intent(this, RewardActivity.class);
        intent.putExtra("ADD_REWARD", profile);
        displayRewardLauncher.launch(intent);
    }

    private void getAllProfile() {
        GetAllProfileVolley.getAllUserProfiles(this, APIKey);
    }

    public void addProfile(Profile newProfile) {
        if(newProfile == null) Log.d(TAG, "addProfile: null new profile to add");
        profileList.add(newProfile);
        mAdaptor.notifyDataSetChanged();
        Collections.sort(profileList);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("ADD_REWARD", reward);
        setResult(2, intent);
        finish();
        super.onBackPressed();
    }
}