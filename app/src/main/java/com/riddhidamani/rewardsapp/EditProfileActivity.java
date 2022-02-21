package com.riddhidamani.rewardsapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
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
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.riddhidamani.rewardsapp.databinding.ActivityEditProfileBinding;
import com.riddhidamani.rewardsapp.profile.Profile;
import com.riddhidamani.rewardsapp.volley.UpdateProfileVolley;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "EditProfileActivity";
    private ActivityEditProfileBinding binding;
    private Profile profileHolder;

    // Location
    private FusedLocationProviderClient mFusedLocationClient;
    private static final int LOCATION_REQUEST = 111;
    public static String locationData = "Unspecified Location";

    // Image
    public static Bitmap selectedImage;
    private boolean changedImageFlag = false;

    // Text Count
    private static final int MAX_LEN = 360;

    private ActivityResultLauncher<Intent> thumbActivityResultLauncher;
    private ActivityResultLauncher<Intent> galleryActivityResultLauncher;
    private ActivityResultLauncher<Intent> displayProfileResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_edit_profile);

        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        HomeNav.setupHomeIndicator(getSupportActionBar());
        setTitle("Edit Profile");

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        thumbActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleThumbResult);

        galleryActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleGalleryResult);

        displayProfileResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), this::displayUpdatedProfileHandler);

        Intent intent = getIntent();
        if (intent.hasExtra("EDIT_PROFILE")) {
            profileHolder = (Profile)intent.getSerializableExtra("EDIT_PROFILE");
            if (profileHolder != null) {
                binding.epUsername.setEnabled(false);
                binding.epUsername.setText(profileHolder.getUsername());
                binding.epPassword.setText(profileHolder.getPassword());
                binding.epFirstname.setText(profileHolder.getFirstName());
                binding.epLastname.setText(profileHolder.getLastName());
                binding.epDepartment.setText(profileHolder.getDepartment());
                binding.epPosition.setText(profileHolder.getPosition());
                binding.epUserStory.setText(profileHolder.getStory());
                int len = profileHolder.getStory().length();
                String countText = "Your Story: (" + len + " of " + MAX_LEN + ")";
                binding.epStoryTitle.setText(countText);
                textToImage(profileHolder.getImageBytes());
            }
        }
        locationData = MainActivity.locText;
        setupEditText();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editprofile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.editSave:
                saveEditDetailsDialog();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    private void saveEditDetailsDialog() {

        List<String> errorMessage = new ArrayList<>();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.icon);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String password = binding.epPassword.getText().toString();
                String firstName = binding.epFirstname.getText().toString();
                String lastName = binding.epLastname.getText().toString();
                String department = binding.epDepartment.getText().toString();
                String position = binding.epPosition.getText().toString();
                String story = binding.epUserStory.getText().toString();

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

                if(errorMessage.size() > 0) {
                    invalidEntryDialog(errorMessage);
                    return;
                }

                profileHolder.setLocation(MainActivity.locText);
                profileHolder.setPassword(password);
                profileHolder.setFirstName(firstName);
                profileHolder.setLastName(lastName);
                profileHolder.setDepartment(department);
                profileHolder.setPosition(position);
                profileHolder.setStory(story);

                if(changedImageFlag) {
                    String imageBase64 = imageToBase64();
                    profileHolder.setImageBytes(imageBase64);
                }
                UpdateProfileVolley.updateProfileData(EditProfileActivity.this, profileHolder);
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        builder.setTitle("Save Changes?");
        AlertDialog dialog = builder.create();
        dialog.show();
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

    private void setupEditText() {

        binding.epUserStory.setFilters(new InputFilter[] {
                new InputFilter.LengthFilter(MAX_LEN) // Specifies a max text length
        });

        binding.epUserStory.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void afterTextChanged(Editable s) {
                        // This one executes upon completion of typing a character
                        int len = s.toString().length();
                        String countText = "Your Story: (" + len + " of " + MAX_LEN + ")";
                        binding.epStoryTitle.setText(countText);
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

    public void getUpdatedUserProfile(String password, String firstname, String lastname, String department, String position, String story) {
        profileHolder.setPassword(password);
        profileHolder.setFirstName(firstname);
        profileHolder.setLastName(lastname);
        profileHolder.setDepartment(department);
        profileHolder.setPosition(position);
        profileHolder.setStory(story);
        Intent intent = new Intent();
        intent.putExtra("EDIT_PROFILE", profileHolder);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    public void showImageDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.icon);
        builder.setPositiveButton("CAMERA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                thumbActivityResultLauncher.launch(takePictureIntent);
            }
        });

        builder.setNegativeButton("GALLERY", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                galleryActivityResultLauncher.launch(photoPickerIntent);
            }
        });

        builder.setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setTitle("Profile Picture");
        builder.setMessage("Take picture from:");
        AlertDialog dialog = builder.create();
        dialog.show();
        changedImageFlag = true;
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
        binding.epProfilePic.setImageBitmap(selectedImage);
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
        binding.epProfilePic.setImageBitmap(selectedImage);
        //makeCustomToast(this, String.format(Locale.getDefault(),
          //      "Gallery Image Size:%n%,d bytes", selectedImage.getByteCount()));

    }

    private String imageToBase64() {
        // Remember - API requirements:
        // Profile image (as Base64 String) – Not null or empty, 100000 character maximum

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // Resize to match imageview size
        int bmW = selectedImage.getWidth();
        int bmH = selectedImage.getHeight();
        double ratio = (double) bmW / (double) bmH;

        int h = binding.epProfilePic.getHeight();
        int w = (int) (h * ratio);
        selectedImage = Bitmap.createScaledBitmap(selectedImage, w, h, false);

        selectedImage.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] byteArray = baos.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public void textToImage(String imgStr64) {
        if (imgStr64 == null) return;
        byte[] imageBytes = Base64.decode(imgStr64, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        binding.epProfilePic.setImageBitmap(bitmap);
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

    public void displayUpdatedProfileHandler(ActivityResult result) {
        Log.d(TAG, "displayProfileHandler: ");
    }

}