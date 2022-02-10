package com.riddhidamani.rewardsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.riddhidamani.rewardsapp.databinding.ActivityCreateProfileBinding;

import java.io.ByteArrayOutputStream;

public class CreateProfileActivity extends AppCompatActivity {

    private static final String TAG = "CreateProfile";
    private ActivityCreateProfileBinding binding;
    private EditText username, password, firstName, lastName, department, position;
    public static Bitmap selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_create_profile);

        binding = ActivityCreateProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (getActionBar() != null) {
            // Comment out the below line to show the default home indicator
            getActionBar().setHomeAsUpIndicator(R.drawable.logo);
            getActionBar().setHomeButtonEnabled(true);
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setTitle("Create Profile");

        //textCountDisplay = findViewById(R.id.tv_yourStory);
        //profileImage = findViewById(R.id.iv_profile);
        //setupEditText();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.createprofile_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
        switch(menuItem.getItemId()){
            case R.id.save_user_profile:
                saveUserProfile();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    public void saveUserProfile() {

        String username = binding.cpUsername.getText().toString();
        String password = binding.cpPassword.getText().toString();
        String firstName = binding.cpFirstname.getText().toString();
        String lastName = binding.cpLastname.getText().toString();
        String department = binding.cpDepartment.getText().toString();
        String position = binding.cpPosition.getText().toString();
        String story = binding.cpUserStory.getText().toString();
        String remainingPoint = "1000";

        // Validate Entries
        if(username == null || username.isEmpty() || username.length() > 20) {
            invalidEntryDialog("Username");
            binding.cpUsername.setText(" ");
            return;
        }

        if(firstName == null || firstName.isEmpty() || firstName.length() > 20 ){
            invalidEntryDialog("First Name");
            binding.cpFirstname.setText(" ");
            return;
        }

        if(lastName == null || lastName.isEmpty() || lastName.length() > 20 ){
            invalidEntryDialog("Last Name");
            binding.cpLastname.setText(" ");
            return;
        }

        if(department == null || department.isEmpty() || department.length() > 20 ){
            invalidEntryDialog("Department");
            binding.cpDepartment.setText(" ");
            return;
        }

        if(story == null || story.isEmpty() || story.length() > 360 ){
            invalidEntryDialog("Story");
            binding.cpUserStory.setText(" ");
            return;
        }

        if(position == null || position.isEmpty() || position.length() > 20 ){
            invalidEntryDialog("Position");
            binding.cpPosition.setText(" ");
            return;
        }

        if(password == null || password.isEmpty() || password.length() > 20 ) {
            invalidEntryDialog("Password");
            binding.cpPassword.setText(" ");
            return;
        }

        Profile newUserProfile = new Profile(username);
        newUserProfile.setPassword(password);
        newUserProfile.setFirstName(firstName);
        newUserProfile.setLastName(lastName);
        newUserProfile.setDepartment(department);
        newUserProfile.setPosition(position);
        newUserProfile.setStory(story);
        newUserProfile.setPointsToAward(remainingPoint);
        newUserProfile.setLocation(MainActivity.locText);

        String imageBase64 = makeImageBase64();
        newUserProfile.setImageBytes(imageBase64);

    }

    public void invalidEntryDialog(String invalidInfo) {
        // Simple Ok & Cancel dialog - no view used.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.icon);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        builder.setMessage("Invalid " + invalidInfo + " format, please enter again");
        builder.setTitle("Invalid Input");

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private String makeImageBase64() {
        // Remember - API requirements:
        // Profile image (as Base64 String) – Not null or empty, 100000 character maximum
        ByteArrayOutputStream byteArrayOutputStream;
        int value = 50;
        while (value > 0) {
            byteArrayOutputStream = new ByteArrayOutputStream();
            selectedImage.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
            String b64 = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
            Log.d(TAG, "makeImageBase64: " + b64.length());
            if (b64.length() > 100000) {
                value -= 10;
            } else {
                Log.d(TAG, "makeImageBase64: " + value);
                return b64;
            }
        }
        return null;
    }
}