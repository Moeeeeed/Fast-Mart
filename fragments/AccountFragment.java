package com.example.a2.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.a2.R;
import com.example.a2.activities.SplashActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AccountFragment extends Fragment {

    // camelCase variables
    EditText etName, etEmail, etPhone, etGender, etDob, etCountry, etAddress;
    Button btnLogout;
    FirebaseAuth mAuth;
    DatabaseReference dbRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmentaccount, container, false);

        etName = view.findViewById(R.id.etProfileFullName);
        etEmail = view.findViewById(R.id.etProfileEmail);
        etPhone = view.findViewById(R.id.etProfilePhone);
        etGender = view.findViewById(R.id.etProfileGender);
        etDob = view.findViewById(R.id.etProfileDob);
        etCountry = view.findViewById(R.id.etProfileCountry);
        etAddress = view.findViewById(R.id.etProfileAddress);
        btnLogout = view.findViewById(R.id.btnLogout);

        mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getUid();

        if (uid != null) {
            dbRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);
            loadUserData();
        }

        btnLogout.setOnClickListener(v -> {
            performLogout();
        });

        return view;
    }

    private void loadUserData() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    etName.setText(snapshot.child("fullName").getValue(String.class));
                    etEmail.setText(snapshot.child("email").getValue(String.class));
                    etPhone.setText(snapshot.child("phone").getValue(String.class));
                    etGender.setText(snapshot.child("gender").getValue(String.class));
                    etDob.setText(snapshot.child("dob").getValue(String.class));
                    etCountry.setText(snapshot.child("country").getValue(String.class));
                    etAddress.setText(snapshot.child("address").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error loading profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performLogout() {
        // Sign out from Firebase
        mAuth.signOut();

        // Clear SharedPreferences
        SharedPreferences spref = getActivity().getSharedPreferences("FastMartPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = spref.edit();
        editor.putBoolean("isLogin", false);
        editor.remove("userId");
        editor.remove("userName");
        editor.remove("accountType");
        editor.apply();

        Toast.makeText(getActivity(), "Logged out successfully", Toast.LENGTH_SHORT).show();

        // Go back to Splash/Auth
        Intent intent = new Intent(getActivity(), SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }
}
