package com.riddhidamani.rewardsapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.riddhidamani.rewardsapp.databinding.ActivityCreateProfileBinding;
import com.riddhidamani.rewardsapp.profile.Profile;
import com.riddhidamani.rewardsapp.volley.CreateProfileVolley;
import com.riddhidamani.rewardsapp.volley.GetStudentRegisterAPIKeyVolley;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CreateProfileActivity extends AppCompatActivity {

    private static final String TAG = "CreateProfileActivity";
    private ActivityCreateProfileBinding binding;
    public static Bitmap selectedImage;
    private ActivityResultLauncher<Intent> thumbActivityResultLauncher;
    private ActivityResultLauncher<Intent> galleryActivityResultLauncher;
    private ActivityResultLauncher<Intent> displayProfileResultLauncher;
    Profile newUserProfile;

    // Story Count
    private static final int MAX_LEN = 360;

    // Shared Preferences
    private SharedPreferencesConfig myPrefs;
    public static String APIKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_create_profile);

        binding = ActivityCreateProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        HomeNav.setupHomeIndicator(getSupportActionBar());
        setTitle(" Create Profile");

        myPrefs = new SharedPreferencesConfig(this);
        APIKey = myPrefs.getValue("APIKey");

        setupEditText();

        thumbActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleThumbResult);

        galleryActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleGalleryResult);

        displayProfileResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), this::displayProfileHandler);
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

    private void saveUserProfile() {

        List<String> errorMessage = new ArrayList<>();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String username = binding.cpUsername.getText().toString();
                String password = binding.cpPassword.getText().toString();
                String firstName = binding.cpFirstname.getText().toString();
                String lastName = binding.cpLastname.getText().toString();
                String department = binding.cpDepartment.getText().toString();
                String position = binding.cpPosition.getText().toString();
                String story = binding.cpUserStory.getText().toString();
                String location = MainActivity.locText;
                String remainingPointToAward = "1000";

                // Query Parameters Check - Validations of Input

                if(username == null || username.isEmpty() || username.length() > 20) {
                    errorMessage.add("Username");
                }

                if(password == null || password.isEmpty()) {
                    errorMessage.add("Password");
                }

                if (!((password.length() >= 8)
                        && (password.length() <= 40))) {
                    errorMessage.add("Valid Length Password");
                }

                if(firstName == null || firstName.isEmpty() || firstName.length() > 20 ) {
                    errorMessage.add("First Name");
                }

                if(lastName == null || lastName.isEmpty() || lastName.length() > 20 ){
                    errorMessage.add("Last Name");
                }

                if(department == null || department.isEmpty() || department.length() > 30 ){
                    errorMessage.add("Department");
                }

                if(position == null || position.isEmpty() || position.length() > 20 ){
                    errorMessage.add("Position");
                }

                if(story == null || story.isEmpty() || story.length() > 360 ){
                    errorMessage.add("User Story");
                }

                if(location == null || location.isEmpty() || location.length() > 50) {
                    //invalidEntryDialog("Location");
                    return;
                }

                int value = Integer.parseInt(remainingPointToAward);
                if(value < 0 || remainingPointToAward.length() >= 11) {
                    //invalidEntryDialog("RemainingPointToAward");
                    return;
                }

                if(errorMessage.size() > 0) {
                    invalidEntryDialog(errorMessage);
                    return;
                }

                newUserProfile = new Profile(username);
                newUserProfile.setPassword(password);
                newUserProfile.setFirstName(firstName);
                newUserProfile.setLastName(lastName);
                newUserProfile.setDepartment(department);
                newUserProfile.setPosition(position);
                newUserProfile.setStory(story);
                newUserProfile.setPointsToAward(remainingPointToAward);
                newUserProfile.setLocation(location);

                String imageString64 = imageToBase64(); // Convert image to base64
                Log.d(TAG, "doApiCall: base64 size " + imageString64.length());
                newUserProfile.setImageBytes(imageString64);

                CreateProfileVolley.createProfile(CreateProfileActivity.this, firstName, lastName, username, password, department, position, story, remainingPointToAward, location, imageString64);

                Intent intent = new Intent(CreateProfileActivity.this, ProfileActivity.class);
                intent.putExtra("NEW_PROFILE",  newUserProfile);
                displayProfileResultLauncher.launch(intent);
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Do Nothing
            }
        });

        builder.setIcon(R.drawable.logo);
        builder.setTitle("Save Changes?");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setupEditText() {

        binding.cpUserStory.setFilters(new InputFilter[] {
                new InputFilter.LengthFilter(MAX_LEN) // Specifies a max text length
        });

        binding.cpUserStory.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void afterTextChanged(Editable s) {
                        // This one executes upon completion of typing a character
                        int len = s.toString().length();
                        String countText = "Your Story: (" + len + " of " + MAX_LEN + ")";
                        binding.cpStoryTitle.setText(countText);
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start,
                                                  int count, int after) {
                        // Nothing to do here
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start,
                                              int before, int count) {
                        // Nothing to do here
                    }
                });
    }

    public void invalidEntryDialog(List invalidInfo) {
        // Simple Ok & Cancel dialog - no view used.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.icon);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        String msg = "";
        builder.setIcon(R.drawable.logo);
        builder.setTitle("Data Problem");
        for(int i=0; i < invalidInfo.size(); i++) {
            if(i >= 1) {
                msg = msg + ", " + invalidInfo.get(i);
            }
            else {
                msg = (String) invalidInfo.get(i);
            }
        }
        builder.setMessage("You must specify: " + "\n" + "\n" + msg);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void addPicOnCreateProfileDialog(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.icon);

        // Camera Selection
        builder.setPositiveButton("CAMERA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                thumbActivityResultLauncher.launch(takePictureIntent);
            }
        });

        // Gallery Selection
        builder.setNegativeButton("GALLERY", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                galleryActivityResultLauncher.launch(photoPickerIntent);
            }
        });

        // Cancel Button
        builder.setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.setIcon(R.drawable.logo);
        builder.setTitle("Profile Picture");
        builder.setMessage("Take picture from:");

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Process Camera Thumb
    public void handleThumbResult(ActivityResult result) {
        if (result == null || result.getData() == null) {
            Log.d(TAG, "handleResult: NULL ActivityResult received");
            return;
        }

        if (result.getResultCode() == RESULT_OK) {
            try {
                Intent data = result.getData();
                processCameraThumb(data.getExtras());
            } catch (Exception e) {
                Toast.makeText(this, "onActivityResult: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    private void processCameraThumb(Bundle extras) {
        selectedImage = (Bitmap) extras.get("data");
        binding.profilePic.setImageBitmap(selectedImage);
    }

    // Process Gallery Image
    public void handleGalleryResult(ActivityResult result) {
        if (result == null || result.getData() == null) {
            Log.d(TAG, "handleResult: NULL ActivityResult received");
            return;
        }

        if (result.getResultCode() == RESULT_OK) {
            try {
                Intent data = result.getData();
                processGallery(data);
            } catch (Exception e) {
                Toast.makeText(this, "onActivityResult: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    private void processGallery(Intent data) {
        Uri galleryImageUri = data.getData();
        if (galleryImageUri == null)
            return;

        InputStream imageStream = null;
        try {
            imageStream = getContentResolver().openInputStream(galleryImageUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        selectedImage = BitmapFactory.decodeStream(imageStream);
        binding.profilePic.setImageBitmap(selectedImage);
//        makeCustomToast(this, String.format(Locale.getDefault(),
//                "Gallery Image Size:%n%,d bytes", selectedImage.getByteCount()));

    }

    private String imageToBase64() {
        // Remember - API requirements:
        // Profile image (as Base64 String) – Not null or empty, 100000 character maximum

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // Resize to match imageview size
        int bmW = selectedImage.getWidth();
        int bmH = selectedImage.getHeight();
        double ratio = (double) bmW / (double) bmH;

        int h = binding.profilePic.getHeight();
        int w = (int) (h * ratio);
        selectedImage = Bitmap.createScaledBitmap(selectedImage, w, h, false);

        selectedImage.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] byteArray = baos.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public static void makeCustomToast(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);

        TextView tv = new TextView(context);
        tv.setText(message);
        tv.setTextSize(18.0f);

        tv.setPadding(50, 25, 50, 25);
        tv.setTextColor(Color.BLACK);
        tv.setBackgroundColor(Color.WHITE);
        toast.setView(tv);

        toast.show();
    }

    public void displayProfileHandler(ActivityResult activityResult) {
        Log.d(TAG, "displayProfileHandler: ");
        if(activityResult.getResultCode() == 1) {
            Intent data = activityResult.getData();
            if(data != null) {
                Profile profile = (Profile)data.getSerializableExtra("EDIT_PROFILE");
                if(profile != null) {
                    //updateProfile(profile);
                }
            }
        }
    }
}