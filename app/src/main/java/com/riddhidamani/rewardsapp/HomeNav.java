package com.riddhidamani.rewardsapp;

import androidx.appcompat.app.ActionBar;

public class HomeNav {
    static void setupHomeIndicator(ActionBar actionBar) {
        if (actionBar != null) {
            // Comment out the below line to show the default home indicator
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setLogo(R.drawable.icon);
            actionBar.setDisplayUseLogoEnabled(true);
        }
    }
}
