package com.riddhidamani.rewardsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.riddhidamani.rewardsapp.databinding.ActivityProfileBinding;
import com.riddhidamani.rewardsapp.profile.Profile;
import com.riddhidamani.rewardsapp.reward.Reward;
import com.riddhidamani.rewardsapp.reward.RewardAdapter;

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
            updateProfile(loggedInUserProfile);
        }

        // Recycler View for reward history
        recyclerView = findViewById(R.id.recycler_rewards_display);
        mAdaptor = new RewardAdapter(rewardList, this);
        recyclerView.setAdapter(mAdaptor);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }



    private void updateProfile(Profile profile){
        String fullName = profile.getLastName() + ", " + profile.getFirstName();
        binding.lastFirstName.setText(fullName);
        binding.username.setText("("+profile.getUsername()+")");
        binding.department.setText(profile.getDepartment());
        binding.position.setText(profile.getPosition());
        String storyTmp = profile.getStory();
        binding.storyValue.setText(profile.getStory());

        String pointStr = profile.getPoints();
        binding.pointsAwarded.setText(pointStr);
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
                //startLeaderBoard();
                return true;
            case R.id.preference_menu:
                return true;
            case R.id.profile_delete_menu:
               // deleteDialog();
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    public void textToImage(String imgStr64) {
        if (imgStr64 == null) return;

        byte[] imageBytes = Base64.decode(imgStr64, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        binding.imageDisplay.setImageBitmap(bitmap);
    }
}