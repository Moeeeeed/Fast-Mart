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
import com.example.a2.activities.AuthActivity;
import com.example.a2.activities.MainActivity;

public class SignUpFragment extends Fragment {

    EditText etEmail, etPassword, etVerifyPassword;
    Button btnSignup;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Sets up the UI and the signup button listener.
        View view = inflater.inflate(R.layout.fragmentsignup, container, false);

        etEmail = view.findViewById(R.id.etSignupEmail);
        etPassword = view.findViewById(R.id.etSignupPassword);
        etVerifyPassword = view.findViewById(R.id.etSignupVerify);
        btnSignup = view.findViewById(R.id.btnSignup);

        btnSignup.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String verifyPassword = etVerifyPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(verifyPassword)) {
                etVerifyPassword.setError("Passwords do not match");
                return;
            }

            SharedPreferences authPrefs = getActivity().getSharedPreferences("FastMartPrefs", Context.MODE_PRIVATE);
            authPrefs.edit().putString("user.email", email).putString("user.password", password).apply();

            Toast.makeText(getActivity(), "Account created successfully!", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(getActivity(), AuthActivity.class));
            getActivity().finish();
        });

        return view;
    }
}
