package com.riddhidamani.rewardsapp.profile;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.riddhidamani.rewardsapp.R;

public class ProfileViewHolder extends RecyclerView.ViewHolder {

    public TextView fullName, positionAndDept, points;
    public ImageView portrait;

    public ProfileViewHolder(@NonNull View itemView) {
        super(itemView);
        fullName = itemView.findViewById(R.id.fullname);
        positionAndDept = itemView.findViewById(R.id.position_department);
        points = itemView.findViewById(R.id.points);
        portrait = itemView.findViewById(R.id.userImage);
    }
}
