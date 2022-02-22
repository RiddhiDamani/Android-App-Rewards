package com.riddhidamani.rewardsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.riddhidamani.rewardsapp.databinding.ActivityRewardBinding;
import com.riddhidamani.rewardsapp.profile.Profile;

public class RewardActivity extends AppCompatActivity {

    private static final String TAG = "RewardActivity";
    private ActivityRewardBinding binding;
    private Profile profileHolder;
    private Profile loggedInUserProfile = ProfileActivity.loggedInUserProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_reward);

        binding = ActivityRewardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        HomeNav.setupHomeIndicator(getSupportActionBar());

        // get Profile from LeaderboardActivity
        Intent intent = getIntent();
        if(intent.hasExtra("ADD_REWARD")) {
            profileHolder = (Profile) intent.getSerializableExtra("ADD_REWARD");
        }

        if(profileHolder != null) {
            String fullName = profileHolder.getLastName() + ", " + profileHolder.getFirstName();
            binding.rewardFirstLastName.setText(fullName);
            binding.rewardPointsValue.setText(profileHolder.getPoints());
            binding.rewardDeptValue.setText(profileHolder.getDepartment());
            binding.rewardPositionValue.setText(profileHolder.getPosition());
            binding.rewardStoryValue.setText(profileHolder.getStory());
            textToImage(profileHolder.getImageBytes());

            Bitmap bitmap = textToImage(profileHolder.getImageBytes());
            binding.rewardProfilePic.setImageBitmap(bitmap);
        }
        else {
            Log.d(TAG, "onCreate: profile from Leaderboard is null" );
        }

        String fullName = profileHolder.getFirstName() + " " + profileHolder.getLastName();
        setTitle(fullName);

    }

    public Bitmap textToImage(String imgStr64) {
        if (imgStr64 == null) return null;
        byte[] imageBytes = Base64.decode(imgStr64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }
}