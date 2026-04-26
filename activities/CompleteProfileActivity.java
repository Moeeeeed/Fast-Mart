package com.example.a2.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import android.app.ProgressDialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.a2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class CompleteProfileActivity extends AppCompatActivity {

    private EditText etFullName, etPhone, etDob, etCountry, etAddress;
    private Spinner spinnerAccountType;
    private RadioGroup rgGender;
    private RadioButton rbMale, rbFemale;
    private CheckBox cbTerms;
    private Button btnSaveProfile;
    private ImageButton btnBack;

    private String userEmail;
    private String userPassword;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitycompleteprofile);

        // Get email and password passed from the first sign-up screen
        userEmail = getIntent().getStringExtra("EMAIL");
        userPassword = getIntent().getStringExtra("PASSWORD");

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        
        // Simple initialization as per class notes style
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Users");

        initViews();
        setupSpinner();

        btnSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserProfile();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initViews() {
        etFullName = findViewById(R.id.etFullName);
        etPhone = findViewById(R.id.etPhone);
        etDob = findViewById(R.id.etDob);
        etCountry = findViewById(R.id.etCountry);
        etAddress = findViewById(R.id.etAddress);
        spinnerAccountType = findViewById(R.id.spinnerAccountType);
        rgGender = findViewById(R.id.rgGender);
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);
        cbTerms = findViewById(R.id.cbTerms);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupSpinner() {
        String[] accountTypes = {"Buyer", "Seller"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, accountTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAccountType.setAdapter(adapter);
    }

    private void saveUserProfile() {
        String fullName = etFullName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String dob = etDob.getText().toString().trim();
        String country = etCountry.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String accountType = spinnerAccountType.getSelectedItem().toString();

        Log.d("FastMart", "Attempting to save profile for: " + userEmail);

        String selectedGender = "Not Specified";
        if (rbMale.isChecked()) {
            selectedGender = "Male";
        } else if (rbFemale.isChecked()) {
            selectedGender = "Female";
        }
        final String gender = selectedGender;

        if (fullName.isEmpty() || phone.isEmpty() || address.isEmpty() || !cbTerms.isChecked()) {
            Toast.makeText(this, "Please fill all fields and agree to terms", Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating Account...");
        progressDialog.setCancelable(true); // Allow cancel in case it hangs
        progressDialog.show();

        // 1. Create User in Firebase Auth
        firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("FastMart", "Auth Success. Writing to Database...");

                            // 2. Save details to Firebase Realtime Database
                            String userId = firebaseAuth.getCurrentUser().getUid();

                            HashMap<String, String> userMap = new HashMap<>();
                            userMap.put("fullName", fullName);
                            userMap.put("phone", phone);
                            userMap.put("dob", dob);
                            userMap.put("country", country);
                            userMap.put("address", address);
                            userMap.put("accountType", accountType);
                            userMap.put("gender", gender);
                            userMap.put("email", userEmail);

                            databaseReference.child(userId).setValue(userMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> dbTask) {
                                            if (!isFinishing()) progressDialog.dismiss();
                                            if (dbTask.isSuccessful()) {
                                                Log.d("FastMart", "Database Write Success");

                                                // 3. Save to SharedPreferences for auto-login
                                                saveToSharedPreferences(userId, fullName, accountType);

                                                Toast.makeText(CompleteProfileActivity.this, "Account Created!", Toast.LENGTH_SHORT).show();

                                                // 4. Navigate based on account type
                                                if ("Seller".equals(accountType)) {
                                                    startActivity(new Intent(CompleteProfileActivity.this, SellerHomeActivity.class));
                                                } else {
                                                    startActivity(new Intent(CompleteProfileActivity.this, MainActivity.class));
                                                }
                                                finish();
                                            } else {
                                                String error = dbTask.getException() != null ? dbTask.getException().getMessage() : "Unknown error";
                                                Log.e("FastMart", "Database Error: " + error);
                                                Toast.makeText(CompleteProfileActivity.this, "Database Error: " + error, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            if (!isFinishing()) progressDialog.dismiss();
                            String error = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                            Log.e("FastMart", "Auth Error: " + error);
                            Toast.makeText(CompleteProfileActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void saveToSharedPreferences(String userId, String name, String accountType) {
        SharedPreferences prefs = getSharedPreferences("FastMartPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("userId", userId);
        editor.putString("userName", name);
        editor.putString("accountType", accountType);
        editor.putBoolean("isLogin", true); // Ensure key matches security gatekeeper
        editor.apply();
    }
}
