package com.riddhidamani.rewardsapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import com.riddhidamani.rewardsapp.databinding.ActivityProfileBinding;
import com.riddhidamani.rewardsapp.profile.Profile;
import com.riddhidamani.rewardsapp.reward.Reward;
import com.riddhidamani.rewardsapp.reward.RewardAdapter;
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
    public static Reward reward;
    private ActivityResultLauncher<Intent> editProfileResultLauncher;

    // Shared Preferences
    private SharedPreferencesConfig myPrefs;
    public static String APIKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_profile);

        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        HomeNav.setupHomeIndicator(getSupportActionBar());
        setTitle(" Your Profile");

        myPrefs = new SharedPreferencesConfig(this);
        APIKey = myPrefs.getValue("APIKey");

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

        editProfileResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), this::displayEditProfileHandler);
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
        binding.location.setText(MainActivity.locText);
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
                initiateEditProfileActivity();
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

    private void initiateEditProfileActivity() {
        Intent intent = new Intent(this, EditProfileActivity.class);
        intent.putExtra("EDIT_PROFILE", loggedInUserProfile);
        editProfileResultLauncher.launch(intent);
    }

    private void initiateLeaderBoardActivity() {
        Intent intent = new Intent(this, LeaderboardActivity.class);
        intent.putExtra("ADD_REWARD", reward);
        editProfileResultLauncher.launch(intent);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.icon);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               finishAffinity();
            }
        });

        String message = "Profile for " + loggedInUserProfile.getFirstName() + " " + loggedInUserProfile.getLastName() + " Deleted";
        builder.setIcon(R.drawable.logo);
        builder.setTitle("Profile Deleted");
        builder.setMessage(message);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void textToImage(String imgStr64) {
        if (imgStr64 == null) return;

        byte[] imageBytes = Base64.decode(imgStr64, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        binding.imageDisplay.setImageBitmap(bitmap);
    }

    private void displayEditProfileHandler(ActivityResult activityResult) {
        Log.d(TAG, "On handleResult Method: EDIT");
        if(activityResult.getResultCode() == 1) {
            Intent data = activityResult.getData();
            if(data != null) {
                loggedInUserProfile = (Profile)data.getSerializableExtra("EDIT_PROFILE");
                if(loggedInUserProfile != null) {
                    updateProfile(loggedInUserProfile);
                }
            }
        }
        else if(activityResult.getResultCode() == 2) {
            Intent data = activityResult.getData();
            if(data != null) {
                reward = (Reward) data.getSerializableExtra("ADD_REWARD");
                if(reward != null) {
                    int updatedPointsToAward =
                            Integer.parseInt(loggedInUserProfile.getPointsToAward()) - Integer.parseInt(reward.getAmount());
                    loggedInUserProfile.setPointsToAward(String.valueOf(updatedPointsToAward));
                    updateProfile(loggedInUserProfile);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.icon);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        builder.setIcon(R.drawable.logo);
        builder.setTitle("Exit Rewards App?");
        AlertDialog dialog = builder.create();
        dialog.show();
        //super.onBackPressed();
    }
}