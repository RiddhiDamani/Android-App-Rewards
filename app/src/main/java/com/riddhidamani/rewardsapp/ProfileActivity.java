package com.riddhidamani.rewardsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.riddhidamani.rewardsapp.databinding.ActivityCreateProfileBinding;
import com.riddhidamani.rewardsapp.databinding.ActivityProfileBinding;
import com.riddhidamani.rewardsapp.profile.Profile;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    private ActivityProfileBinding binding;
    public static Profile loggedInUserProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_profile);

        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getActionBar() != null) {
            // Comment out the below line to show the default home indicator
            getActionBar().setHomeAsUpIndicator(R.drawable.logo);
            getActionBar().setHomeButtonEnabled(true);
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setTitle("Your Profile");

        // get profile from CreateProfile
        Intent intent = getIntent();
        if(intent.hasExtra("NEW_PROFILE")) {
            loggedInUserProfile = (Profile) intent.getSerializableExtra("NEW_PROFILE");

        } else if (intent.hasExtra("LOGIN_PROFILE")) {
            loggedInUserProfile = (Profile) intent.getSerializableExtra("LOGIN_PROFILE");
        }

        if(loggedInUserProfile != null) {
            //updateProfile(loggedInUserProfile);
        }
    }
}