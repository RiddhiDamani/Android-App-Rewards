package com.riddhidamani.rewardsapp.reward;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.riddhidamani.rewardsapp.ProfileActivity;
import com.riddhidamani.rewardsapp.R;
import java.util.List;

public class RewardAdapter extends RecyclerView.Adapter<RewardViewHolder> {

    private static final String TAG = "RewardAdaptor";
    private List<Reward> rewardListHolder;
    private ProfileActivity profileActivity;

    public RewardAdapter(List<Reward> rewardList, ProfileActivity profileActivity) {
        this.rewardListHolder = rewardList;
        this.profileActivity = profileActivity;
    }

    @NonNull
    @Override
    public RewardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: MAKING NEW profileHolder");
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.entry_reward, parent, false);
        itemView.setOnClickListener(profileActivity);
        return new RewardViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull RewardViewHolder holder, int position) {
        Reward reward = rewardListHolder.get(position);
        String date = reward.getAwardDate().substring(0,10); //"2021-01-29T20:46:38" to "2021-01-29"
        String name = reward.getGiverName();
        String points = reward.getAmount();
        String notes = reward.getNote();

        holder.rewardDate.setText(date);
        holder.rewardName.setText(name);
        holder.rewardPoints.setText(points);
        holder.rewardNotes.setText(notes);
    }

    @Override
    public int getItemCount() {
        return rewardListHolder.size();
    }
}
