package com.example.a2.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
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
import com.example.a2.activities.MainActivity;

public class LoginFragment extends Fragment {

    EditText etEmail, etPassword;
    Button btnLogin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragmentlogin, container, false);

        etEmail = view.findViewById(R.id.etLoginEmail);
        etPassword = view.findViewById(R.id.etLoginPassword);
        btnLogin = view.findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // 1. Error Checking & Validation
            if (email.isEmpty()) {
                etEmail.setError("Email is required");
                etEmail.requestFocus();
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.setError("Please enter a valid email");
                etEmail.requestFocus();
                return;
            }
            if (password.isEmpty()) {
                etPassword.setError("Password is required");
                etPassword.requestFocus();
                return;
            }

            // 2. Fetch stored credentials from SharedPreferences
            SharedPreferences prefs = getActivity().getSharedPreferences("FastMartPrefs", Context.MODE_PRIVATE);
            String savedEmail = prefs.getString("user.email", "");
            String savedPassword = prefs.getString("user.password", "");

            // 3. Compare inputs with stored data
            if (email.equals(savedEmail) && password.equals(savedPassword)) {
                // Success! Save login state so they don't have to log in again next time
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("user.isLoggedIn", true);
                editor.apply();

                Toast.makeText(getActivity(), "Login Successful!", Toast.LENGTH_SHORT).show();

                // Move to MainActivity
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish(); // Close the AuthActivity
            } else {
                // Failure
                Toast.makeText(getActivity(), "Invalid email or password", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}