package com.riddhidamani.rewardsapp;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        HomeNav.setupHomeIndicator(getSupportActionBar());
        setTitle("Leaderboard");

        recyclerView = findViewById(R.id.recyclerView);
        mAdaptor = new ProfileAdapter(profileList, this);
        recyclerView.setAdapter(mAdaptor);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getAllProfile();

        displayRewardLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), this::displayRewardHandler);
    }

    private <O> void displayRewardHandler(O o) {
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