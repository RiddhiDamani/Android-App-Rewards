package com.riddhidamani.rewardsapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.riddhidamani.rewardsapp.databinding.ActivityLeaderboardBinding;
import com.riddhidamani.rewardsapp.databinding.ActivityRewardBinding;
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
    public static String fullnameStr;
//    private ActivityLeaderboardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

//        binding = ActivityLeaderboardBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());

        HomeNav.setupHomeIndicator(getSupportActionBar());
        setTitle("Leaderboard");


        recyclerView = findViewById(R.id.recyclerView);
        mAdaptor = new ProfileAdapter(profileList, this);
        recyclerView.setAdapter(mAdaptor);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getAllProfile();

//        fullnameStr = ProfileActivity.loggedInUserProfile.getFirstName();
//        TextView fullname = findViewById(R.id.fullname);
//        fullname.setTextColor(ContextCompat.getColor(this, R.color.dark_orange));

        displayRewardLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), this::displayRewardHandler);
    }

    private void displayRewardHandler(ActivityResult activityResult) {
        Log.d(TAG, "On handleResult Method: Leaderboard Activity");
        if(activityResult.getResultCode() == RESULT_OK) {
            Intent data = activityResult.getData();
            if(data != null) {
                Reward reward = (Reward) data.getSerializableExtra("ADD_REWARD");
                if(reward != null) {
                    //updateProfile(profile);
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        int position = recyclerView.getChildLayoutPosition(view);
        Profile profile = profileList.get(position);

        Intent intent = new Intent(this, RewardActivity.class);
        intent.putExtra("ADD_REWARD", profile);
        displayRewardLauncher.launch(intent);
    }

    private void getAllProfile() {
        GetAllProfileVolley.getAllUserProfiles(this, MainActivity.APIKey);
    }

    public void addProfile(Profile newProfile) {
        if(newProfile == null) Log.d(TAG, "addProfile: null new profile to add");

        profileList.add(newProfile);
        mAdaptor.notifyDataSetChanged();
        Collections.sort(profileList);
    }
}