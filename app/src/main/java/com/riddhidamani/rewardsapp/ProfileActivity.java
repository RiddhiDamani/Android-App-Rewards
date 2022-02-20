package com.riddhidamani.rewardsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.view.View;
import android.widget.Toast;

import com.riddhidamani.rewardsapp.databinding.ActivityProfileBinding;
import com.riddhidamani.rewardsapp.profile.Profile;
import com.riddhidamani.rewardsapp.reward.Reward;
import com.riddhidamani.rewardsapp.reward.RewardAdapter;
import com.riddhidamani.rewardsapp.volley.CreateProfileVolley;
import com.riddhidamani.rewardsapp.volley.DeleteProfileVolley;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ProfileActivity";
    private ActivityProfileBinding binding;
    public static Profile loggedInUserProfile;

    private final List<Reward> rewardList = new ArrayList<>();
    private RewardAdapter mAdaptor;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_profile);

        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        HomeNav.setupHomeIndicator(getSupportActionBar());
        setTitle("Your Profile");

        // get profile from CreateProfile
        Intent intent = getIntent();
        if(intent.hasExtra("NEW_PROFILE")) {
            Log.d(TAG, "onCreate: ProfileActivity ---- Inside PROFILE");
            loggedInUserProfile = (Profile) intent.getSerializableExtra("NEW_PROFILE");

        } else if (intent.hasExtra("LOGIN_PROFILE")) {
            loggedInUserProfile = (Profile) intent.getSerializableExtra("LOGIN_PROFILE");
        }

        if(loggedInUserProfile != null) {
            updateProfile(loggedInUserProfile);
        }

        // Recycler View for reward history
        recyclerView = findViewById(R.id.recycler_rewards_display);
        mAdaptor = new RewardAdapter(rewardList, this);
        recyclerView.setAdapter(mAdaptor);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void updateProfile(Profile profile) {
        String fullName = profile.getLastName() + ", " + profile.getFirstName();
        binding.lastFirstName.setText(fullName);
        binding.username.setText("("+profile.getUsername()+")");
        binding.departmentValue.setText(profile.getDepartment());
        binding.positionValue.setText(profile.getPosition());
        String storyTmp = profile.getStory();
        binding.storyValue.setText(profile.getStory());

        String pointStr = profile.getPoints();
        binding.pointsValue.setText(pointStr);
        binding.location.setText(profile.getLocation());
        binding.pointsToAwardValue.setText(profile.getPointsToAward());
        String imgStr = profile.getImageBytes();
        textToImage(profile.getImageBytes());

        binding.rewardHistoryTitle.setText("Reward History(" + profile.getListOfRewards().size() + "): ");

        // add rewards from profile to rewardList
        for(int i = 0; i < profile.getListOfRewards().size(); i++){
            Reward r = profile.getListOfRewards().get(i);
            rewardList.add(r);
            //mAdaptor.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case R.id.profile_edit_menu:
                //startEditProfileActivity();
                return true;
            case R.id.profile_leaderboard_menu:
                initiateLeaderBoardActivity();
                return true;
            case R.id.preference_menu:
                return true;
            case R.id.delete:
                deleteProfile();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    private void initiateLeaderBoardActivity() {
        Intent intent = new Intent(this, LeaderboardActivity.class);
        startActivity(intent);
    }

    private void deleteProfile() {
        //Toast.makeText(this, "Deleting Profile: ", Toast.LENGTH_LONG).show();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                initiateDeleteProfile();
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // just a OK button
            }
        });

        String content = "Delete Profile for " + loggedInUserProfile.getFirstName() + " " + loggedInUserProfile.getLastName()
                +"\n(The Rewards app will be closed upon deletion).";

        builder.setTitle("Delete Profile?");
        builder.setMessage(content);
        builder.setIcon(R.drawable.icon);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void initiateDeleteProfile() {
        DeleteProfileVolley.initiate(ProfileActivity.this, loggedInUserProfile.getUsername());
        finishAffinity();
    }

    public void textToImage(String imgStr64) {
        if (imgStr64 == null) return;

        byte[] imageBytes = Base64.decode(imgStr64, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        binding.imageDisplay.setImageBitmap(bitmap);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if(requestCode == EDIT_REQUEST){
//            if(resultCode == Activity.RESULT_OK){
//                if(data != null) {
//                    Profile p = (Profile)data.getSerializableExtra("EDIT_PROFILE");
//                    if(p != null){
//                        updateProfile(p);
//                    }
//                }
//            }
//        }
    }
}