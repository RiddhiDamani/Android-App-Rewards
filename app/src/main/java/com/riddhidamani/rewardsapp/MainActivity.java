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
import android.text.InputType;
import android.util.JsonWriter;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.riddhidamani.rewardsapp.profile.Profile;
import com.riddhidamani.rewardsapp.volley.GetStudentRegisterAPIKeyVolley;
import com.riddhidamani.rewardsapp.volley.LoginVolley;

import org.json.JSONObject;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    //private ActivityMainBinding binding;

    // location
    private FusedLocationProviderClient mFusedLocationClient;
    private static final int LOCATION_REQUEST = 111;
    public static String locText = "Unspecified Location";

    // Student Registration API
    public static String APIKey;
    private String stud_firstname, stud_lastname, stud_emailId, stud_id;

    EditText username, password;
    CheckBox checkbox;
    public static String logInUsername;

//    private SharedPreferences myPrefs;
//    private SharedPreferences.Editor prefsEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // binding = ActivityMainBinding.inflate(getLayoutInflater());
        // setContentView(binding.getRoot());
        HomeNav.setupHomeIndicator(getSupportActionBar());
        setTitle("Rewards");

        username = findViewById(R.id.rewards_main_username);
        password = findViewById(R.id.rewards_main_pswd);
        checkbox = findViewById(R.id.credentials_checkbox);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        determineLocation();
        readJSON();

//        myPrefs = getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);
//        String myData = myPrefs.getString("APIKey", APIKey);

        if (APIKey == null || APIKey.isEmpty() || APIKey.equals("null")) {
            requestStudentRegisterApiKey();
        }
    }

    public void performLogin(View v) {
        String usernameStr = username.getText().toString();
        String passwordStr = password.getText().toString();
        writeJSON();
        LoginVolley.getLoginDetails(this, usernameStr, passwordStr);
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

    // Saved Credentials - Read JSON Data
    private void readJSON() {
        try {
            FileInputStream fis = getApplicationContext().openFileInput(getString(R.string.studRegisterAPIKeyJSON));

            byte[] data = new byte[(int) fis.available()];
            int loaded = fis.read(data);
            Log.d(TAG, "readJSONData: Loaded " + loaded + " bytes");
            fis.close();
            String json = new String(data);

            JSONObject jsonObject = new JSONObject(json);
            if(jsonObject != null) {
                APIKey = jsonObject.getString("APIKey");
                if(jsonObject.has("saveCredentials")) {
                    checkbox.setChecked(true);
                }
                if(jsonObject.has("username")) {
                    username.setText(jsonObject.getString("username"));

                }
                if(jsonObject.has("password")) {
                    password.setText(jsonObject.getString("password"));
                }
            }

        } catch (Exception exception) {
            Log.d(TAG, "readJSON: " + exception.getMessage());
        }
    }

    // Saved Credentials - Write JSON Data
    private void writeJSON() {
        try {
            FileOutputStream fos = getApplicationContext().openFileOutput(getString(R.string.studRegisterAPIKeyJSON), Context.MODE_PRIVATE);

            JsonWriter writer = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                writer = new JsonWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8));
            }
            writer.setIndent("  ");
            writer.beginObject();
            writer.name("APIKey").value(APIKey);
            if(checkbox.isChecked()) {
                writer.name("saveCredentials").value("YES");
                writer.name("username").value(username.getText().toString());
                writer.name("password").value(password.getText().toString());
            }
            writer.endObject();
            writer.close();
        } catch (Exception exception) {
            exception.printStackTrace();
            Log.d(TAG, "writeJSON: "+ exception.getMessage());

        }
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

                if(stud_firstname.length() == 0 || stud_lastname .length() == 0 ||
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

    public void handleApiKeySucceeded(String s) {
        APIKey = s;
        writeJSON();
//        prefsEditor = myPrefs.edit();
//        prefsEditor.putString("APIKey", s);
        //prefsEditor.apply();

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
        //prefsEditor.clear();
        try {
            FileOutputStream fos = getApplicationContext().openFileOutput(getString(R.string.studRegisterAPIKeyJSON), Context.MODE_PRIVATE);
            JsonWriter writer = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                writer = new JsonWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8));
            }
            writer.setIndent("  ");
            writer.beginObject();
            writer.name("APIKey").value("");
            writer.endObject();
            writer.close();
            requestStudentRegisterApiKey();

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "writeJSONData: " + e.getMessage());
        }
    }

    public void createProfile(View v) {
        Intent intent = new Intent(this, CreateProfileActivity.class);
        startActivity(intent);
    }

    public void displayLoginProfile(Profile profile) {
        logInUsername = profile.getUsername();
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("LOGIN_PROFILE", profile);
        startActivity(intent);
    }
}