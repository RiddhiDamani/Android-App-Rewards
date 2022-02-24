package com.riddhidamani.rewardsapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.riddhidamani.rewardsapp.profile.Profile;
import com.riddhidamani.rewardsapp.volley.GetStudentRegisterAPIKeyVolley;
import com.riddhidamani.rewardsapp.volley.UserLoginVolley;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity  {

    private static final String TAG = "MainActivity";
    private static final String FILE_NAME = "myCredentials";
    //private ActivityMainBinding binding;

    // location
    private FusedLocationProviderClient mFusedLocationClient;
    private static final int LOCATION_REQUEST = 111;
    public static String locText = "Unspecified Location";

    // Student Registration API
    private String stud_firstname, stud_lastname, stud_emailId, stud_id;
    public static String logInUsername;

    // Shared Preferences
    private SharedPreferencesConfig myPrefs;
    public static String APIKey;

    // Remember Me Functionality
    private EditText username, password;
    private CheckBox rem_user_pass;
    SharedPreferences rememberMeSP;
    SharedPreferences.Editor rememberMeSPEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // binding = ActivityMainBinding.inflate(getLayoutInflater());
        // setContentView(binding.getRoot());
        HomeNav.setupHomeIndicator(getSupportActionBar());
        setTitle("Rewards");

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        determineLocation();

        myPrefs = new SharedPreferencesConfig(this);
        APIKey = myPrefs.getValue("APIKey");

        if ( APIKey == null || APIKey.isEmpty() || APIKey.equals("null")) {
            requestStudentRegisterApiKey();
        }

        username = findViewById(R.id.rewards_main_username);
        password = findViewById(R.id.rewards_main_pswd);
        rem_user_pass = findViewById(R.id.credentials_checkbox);

        rememberMeSP = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        String un = rememberMeSP.getString("username", "");
        String pwd = rememberMeSP.getString("password", "");

        username.setText(un);
        password.setText(pwd);

    }


    public void performLogin(View v) {
        APIKey = myPrefs.getValue("APIKey");
        if ( APIKey != "") {
            String usernameStr = username.getText().toString();
            String passwordStr = password.getText().toString();
            if(rem_user_pass.isChecked()) {
                storeDataUsingSP(usernameStr, passwordStr);
            }
            UserLoginVolley.getLoginDetails(this, usernameStr, passwordStr, APIKey);
        }
        else {
            Toast.makeText(this, "Need to Register for API", Toast.LENGTH_LONG).show();
        }
    }

    private void storeDataUsingSP(String usernameStr, String passwordStr) {
        rememberMeSPEditor = getSharedPreferences(FILE_NAME, MODE_PRIVATE).edit();
        rememberMeSPEditor.putString("username", usernameStr);
        rememberMeSPEditor.putString("password", passwordStr);
        rememberMeSPEditor.apply();
    }

    public void displayLoginProfile(Profile profile) {
        Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();
        //logInUsername = profile.getUsername();
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("LOGIN_PROFILE", profile);
        startActivity(intent);
    }

    // Initial Location setup
    private void determineLocation() {
        if (checkPermission()) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            locText = getGeoLocation(location);
                            Log.d(TAG, "determineLocation: " + locText);
                        }
                    })
                    .addOnFailureListener(this, e -> Toast.makeText(MainActivity.this,
                            e.getMessage(), Toast.LENGTH_LONG).show());
        }
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION
                    }, LOCATION_REQUEST);
            return false;
        }
        return true;
    }

    private String getGeoLocation(Location loc) {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            return city + ", " + state;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    // Request API key for student registration
    private void requestStudentRegisterApiKey() {

        LayoutInflater inflater = LayoutInflater.from(this);
        @SuppressLint("InflateParams")
        final View view = inflater.inflate(R.layout.student_register_api, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final EditText firstName = new EditText(this);
        firstName.setGravity(Gravity.CENTER_HORIZONTAL);
        firstName.setInputType(InputType.TYPE_CLASS_TEXT);


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText fName = view.findViewById(R.id.student_firstname);
                EditText lName = view.findViewById(R.id.student_lastname);
                EditText email = view.findViewById(R.id.student_emailid);
                EditText id = view.findViewById(R.id.student_id);

                stud_firstname = fName.getText().toString();
                stud_lastname = lName.getText().toString();
                stud_emailId = email.getText().toString();
                stud_id = id.getText().toString();

                if(stud_firstname.length() == 0 || stud_lastname.length() == 0 ||
                        stud_emailId.length()  == 0 || stud_id.length() == 0) {
                    requestStudentRegisterApiKey();
                }

                if (Patterns.EMAIL_ADDRESS.matcher(stud_emailId).matches())
                {
                    String emailDomain = stud_emailId.substring(stud_emailId.length() - 3);
                    if(emailDomain.equals("edu")){
                        Log.d(TAG, "email domain: " + emailDomain);
                    }else{
                        Toast.makeText(MainActivity.this, "Invalid Email Address!", Toast.LENGTH_SHORT).show();
                        requestStudentRegisterApiKey();
                    }
                }else{
                    Toast.makeText(MainActivity.this, "Invalid Email Address!", Toast.LENGTH_SHORT).show();
                    requestStudentRegisterApiKey();
                }

                if(stud_id.length() > 20) {
                    Toast.makeText(MainActivity.this, "Invalid Student ID!", Toast.LENGTH_SHORT).show();
                    requestStudentRegisterApiKey();
                }

                GetStudentRegisterAPIKeyVolley.getApiKey(MainActivity.this, stud_firstname, stud_lastname, stud_emailId, stud_id);
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(MainActivity.this, "You have to request an API Key!", Toast.LENGTH_SHORT).show();

            }
        });

        builder.setTitle("API Key Needed");
        builder.setMessage("You need to request an API key:");
        builder.setIcon(R.drawable.icon);

        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void handleApiKeySucceeded(String APIKey) {
        myPrefs.save("APIKey", APIKey);
        Toast.makeText(MainActivity.this, "Information Saved!", Toast.LENGTH_LONG).show();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        builder.setTitle("API Key Received and Stored");
        String fullName = "Name: " + stud_firstname + " " + stud_lastname;
        String id = "Student ID: " + stud_id;
        String email = "Email: " + stud_emailId;
        String apiKey = "API Key: " + APIKey;
        String fullMessage = fullName + "\n" + id + "\n" + email + "\n" + apiKey;

        builder.setMessage(fullMessage);
        builder.setIcon(R.drawable.icon);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void handleError(String s) {
        Toast.makeText(MainActivity.this, s,
                Toast.LENGTH_LONG).show();
    }

    public void deleteStudentRegisterAPIKey(View v) {
        Log.d(TAG, "clearAll: ");
        myPrefs.removeValue("APIKey");
        myPrefs.clearAll();
        requestStudentRegisterApiKey();
    }

    public void createProfile(View v) {
        Toast.makeText(this, "Profile Creation Successful!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, CreateProfileActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}