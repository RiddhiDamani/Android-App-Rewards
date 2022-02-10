package com.riddhidamani.rewardsapp;

import java.util.ArrayList;
import java.util.List;

public class Profile implements Comparable<Profile> {
    private String username, password, firstName, lastName, department, position, story;
    private String points, pointsToAward, imageBytes;
    private String location;

    private List<Reward> listOfRewards = new ArrayList<Reward>();

    public Profile(String username) {
        this.username = username;
        this.points = "0";
    }

    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getDepartment() {
        return department;
    }
    public String getPosition() {
        return position;
    }
    public String getStory() {
        return story;
    }
    public String getPoints() {
        return points;
    }
    public String getPointsToAward() {
        return pointsToAward;
    }
    public String getImageBytes() {
        return imageBytes;
    }
    public String getLocation() {
        return location;
    }
    public List<Reward> getListOfRewards() {
        return listOfRewards;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public void setDepartment(String department) {
        this.department = department;
    }
    public void setPosition(String position) {
        this.position = position;
    }
    public void setStory(String story) {
        this.story = story;
    }
    public void setPoints(String points) {
        this.points = points;
    }
    public void setPointsToAward(String pointsToAward) {
        this.pointsToAward = pointsToAward;
    }
    public void setImageBytes(String imageBytes) {
        this.imageBytes = imageBytes;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public void setListOfRewards(List<Reward> listOfRewards) {
        this.listOfRewards = listOfRewards;
    }

    @Override
    public int compareTo(Profile profile) {
        int pointsInt = Integer.parseInt(points);
        int pointsInt_p = Integer.parseInt(profile.getPoints());
        if(pointsInt > pointsInt_p) return -1;
        else if (pointsInt < pointsInt_p) return 1;
        return 0;
    }
}