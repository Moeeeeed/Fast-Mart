package com.example.a2.fragments;

import android.content.Intent;
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
import com.example.a2.activities.CompleteProfileActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpFragment extends Fragment {

    // Use camelCase naming
    EditText etEmail, etPassword, etVerifyPassword;
    Button btnSignup;
    FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmentsignup, container, false);

        etEmail = view.findViewById(R.id.etSignupEmail);
        etPassword = view.findViewById(R.id.etSignupPassword);
        etVerifyPassword = view.findViewById(R.id.etSignupVerify);
        btnSignup = view.findViewById(R.id.btnSignup);
        mAuth = FirebaseAuth.getInstance();

        btnSignup.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String verifyPassword = etVerifyPassword.getText().toString().trim();

            // Simple validation
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(verifyPassword)) {
                etVerifyPassword.setError("Passwords do not match");
                return;
            }

            // Move to profile completion to save details later
            Intent intent = new Intent(getActivity(), CompleteProfileActivity.class);
            intent.putExtra("EMAIL", email);
            intent.putExtra("PASSWORD", password);
            startActivity(intent);
        });

        return view;
    }
}
