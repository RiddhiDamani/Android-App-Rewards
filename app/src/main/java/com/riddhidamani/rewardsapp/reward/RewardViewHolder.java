package com.riddhidamani.rewardsapp.reward;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.riddhidamani.rewardsapp.R;

public class RewardViewHolder extends RecyclerView.ViewHolder {
    public TextView rewardDate;
    public TextView rewardName;
    public TextView rewardPoints;
    public TextView rewardNotes;


    public RewardViewHolder(@NonNull View itemView) {
        super(itemView);
        rewardDate = itemView.findViewById(R.id.rewardDate);
        rewardName = itemView.findViewById(R.id.rewardName);
        rewardPoints = itemView.findViewById(R.id.rewardPoints);
        rewardNotes = itemView.findViewById(R.id.rewardNotes);
    }
}
