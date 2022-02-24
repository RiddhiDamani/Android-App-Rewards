package com.riddhidamani.rewardsapp;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.riddhidamani.rewardsapp.databinding.ActivityRewardBinding;
import com.riddhidamani.rewardsapp.profile.Profile;
import com.riddhidamani.rewardsapp.reward.Reward;
import com.riddhidamani.rewardsapp.volley.RewardsVolley;

public class RewardActivity extends AppCompatActivity {

    private static final String TAG = "RewardActivity";
    private ActivityRewardBinding binding;
    private Profile profileHolder;
    private String fullName;
    private Reward newRewardPoints = new Reward();
    private Profile loggedInUserProfile = ProfileActivity.loggedInUserProfile;

    // Shared Preferences
    private SharedPreferencesConfig myPrefs;
    public static String APIKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_reward);

        binding = ActivityRewardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        HomeNav.setupHomeIndicator(getSupportActionBar());

        myPrefs = new SharedPreferencesConfig(this);
        APIKey = myPrefs.getValue("APIKey");

        // get Profile from LeaderboardActivity
        Intent intent = getIntent();
        if(intent.hasExtra("ADD_REWARD")) {
            profileHolder = (Profile) intent.getSerializableExtra("ADD_REWARD");
            if(profileHolder != null) {
                String fullNameStr = profileHolder.getLastName() + ", " + profileHolder.getFirstName();
                binding.rewardFirstLastName.setText(fullNameStr);
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
        }

        fullName = profileHolder.getFirstName() + " " + profileHolder.getLastName();
        setTitle(fullName);
    }

    // reusing menu save item from edit profile menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editprofile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.editSave:
                saveRewardPointsDialog();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    public void saveRewardPointsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.icon);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveUserRewardPoints();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        String content = "Add rewards for " + fullName + "?";

        builder.setIcon(R.drawable.logo);
        builder.setTitle("Add Rewards Points?");
        builder.setMessage(content);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void saveUserRewardPoints() {
        String pointsToSend = binding.rewardPointsTosendValue.getText().toString();
        String comment = binding.rewardCommentValue.getText().toString();

        newRewardPoints.setAmount(pointsToSend);
        newRewardPoints.setNote(comment);
        newRewardPoints.setGiverUser(loggedInUserProfile.getUsername());
        String giverFullName = loggedInUserProfile.getFirstName() + " " + loggedInUserProfile.getLastName();
        newRewardPoints.setGiverName(giverFullName);
        newRewardPoints.setReceiverUser(profileHolder.getUsername());

        RewardsVolley.saveRewardPoints(this, newRewardPoints);

    }

    public Bitmap textToImage(String imgStr64) {
        if (imgStr64 == null) return null;
        byte[] imageBytes = Base64.decode(imgStr64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }

    public void getUpdatedRewards(String giverNameData, String amountData, String noteData, String awardDate) {
        Log.d(TAG, "getUpdatedRewards: I AM IN HERE!!!");
        newRewardPoints.setGiverName(giverNameData);
        newRewardPoints.setAmount(amountData);
        newRewardPoints.setNote(noteData);
        newRewardPoints.setAwardDate(awardDate);
        Intent intent = new Intent();
        intent.putExtra("ADD_REWARD", newRewardPoints);
        setResult(2, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("ADD_REWARD", newRewardPoints);
        setResult(2, intent);
        finish();
        super.onBackPressed();
    }
}