package com.riddhidamani.rewardsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    }

    @Override
    public void onClick(View v) {

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