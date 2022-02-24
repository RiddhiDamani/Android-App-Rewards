package com.riddhidamani.rewardsapp.profile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.riddhidamani.rewardsapp.LeaderboardActivity;
import com.riddhidamani.rewardsapp.MainActivity;
import com.riddhidamani.rewardsapp.ProfileActivity;
import com.riddhidamani.rewardsapp.R;
import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileViewHolder> {
    private static final String TAG = "ProfileAdaptor";

    private final List<Profile> profileListHolder;
    private final LeaderboardActivity leaderboardActivity;
    private final Profile loggedInUserDetails = ProfileActivity.loggedInUserProfile;

    public ProfileAdapter(List<Profile> profileListHolder, LeaderboardActivity leaderboardActivity) {
        this.profileListHolder = profileListHolder;
        this.leaderboardActivity = leaderboardActivity;
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: MAKING NEW profileHolder");
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.entry_profile, parent, false);
        itemView.setOnClickListener(leaderboardActivity);
        return new ProfileViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        Profile profile = profileListHolder.get(position);
        String fullName = profile.getLastName() + ", " + profile.getFirstName();
        holder.fullName.setText(fullName);
        holder.points.setText(profile.getPoints());
        String posAndDp = profile.getPosition() + ", " + profile.getDepartment();
        holder.positionAndDept.setText(posAndDp);
        String imgStr64 = profile.getImageBytes();
        Bitmap bitmap = textToImageBitmap(imgStr64);
        holder.portrait.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return profileListHolder.size();
    }

    public Bitmap textToImageBitmap(String imgStr64) {
        if (imgStr64 == null) {
            Log.d(TAG, "textToImageBitmap: imgStr64 is null" );
            return null;
        }

        byte[] imageBytes = Base64.decode(imgStr64, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        return bitmap;
    }
}
