package com.riddhidamani.rewardsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.riddhidamani.rewardsapp.profile.Profile;
import com.riddhidamani.rewardsapp.profile.ProfileAdapter;

import java.util.ArrayList;
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

        if (getActionBar() != null) {
            // Comment out the below line to show the default home indicator
            getActionBar().setHomeAsUpIndicator(R.drawable.logo);
            getActionBar().setHomeButtonEnabled(true);
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

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

    }
}