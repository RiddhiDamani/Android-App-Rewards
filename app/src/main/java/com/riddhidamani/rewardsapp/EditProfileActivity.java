package com.riddhidamani.rewardsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.riddhidamani.rewardsapp.databinding.ActivityEditProfileBinding;
import com.riddhidamani.rewardsapp.databinding.ActivityProfileBinding;
import com.riddhidamani.rewardsapp.profile.Profile;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "EditProfileActivity";
    private ActivityEditProfileBinding binding;
    private Profile profileHolder;

    // location
    private FusedLocationProviderClient mFusedLocationClient;
    private static final int LOCATION_REQUEST = 111;
    public static String locationData = "Unspecified Location";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_edit_profile);

        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        HomeNav.setupHomeIndicator(getSupportActionBar());
        setTitle("Edit Profile");

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Intent intent = getIntent();
        if (intent.hasExtra("EDIT_PROFILE")) {
            profileHolder = (Profile)intent.getSerializableExtra("EDIT_PROFILE");
            if (profileHolder != null) {
                loadProfileData(profileHolder);
            }
        }

        locationData = MainActivity.locText;

        //setupEditText();

    }

    private void loadProfileData(Profile profile) {

        binding.epUsername.setEnabled(false);
        binding.epUsername.setText(profile.getUsername());
        binding.epPassword.setText(profile.getPassword());
        binding.epFirstname.setText(profile.getFirstName());
        binding.epLastname.setText(profile.getLastName());
        binding.epDepartment.setText(profile.getDepartment());
        binding.epPosition.setText(profile.getPosition());
        binding.epUserStory.setText(profile.getStory());
        textToImage(profile.getImageBytes());
    }

    public void textToImage(String imgStr64) {
        if (imgStr64 == null) return;

        byte[] imageBytes = Base64.decode(imgStr64, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        binding.epProfilePic.setImageBitmap(bitmap);
    }

}