package com.example.a2.fragments;

import android.app.ProgressDialog;
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
import com.example.a2.activities.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.example.a2.activities.SellerHomeActivity;

public class LoginFragment extends Fragment {

    // camelCase naming
    EditText etEmail, etPassword;
    Button btnLogin;
    FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmentlogin, container, false);

        etEmail = view.findViewById(R.id.etLoginEmail);
        etPassword = view.findViewById(R.id.etLoginPassword);
        btnLogin = view.findViewById(R.id.btnLogin);
        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getActivity(), "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            final ProgressDialog pd = new ProgressDialog(getActivity());
            pd.setMessage("Checking credentials...");
            pd.show();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            checkAccountType(pd);
                        } else {
                            pd.dismiss();
                            Toast.makeText(getActivity(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        return view;
    }

    private void checkAccountType(ProgressDialog pd) {
        String uid = mAuth.getUid();
        if (uid == null) {
            pd.dismiss();
            return;
        }

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pd.dismiss();
                if (snapshot.exists()) {
                    String type = snapshot.child("accountType").getValue(String.class);
                    String name = snapshot.child("fullName").getValue(String.class);

                    // Save to SharedPreferences as gatekeeper
                    SharedPreferences spref = getActivity().getSharedPreferences("FastMartPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = spref.edit();
                    editor.putBoolean("isLogin", true);
                    editor.putString("userId", uid);
                    editor.putString("userName", name);
                    editor.putString("accountType", type);
                    editor.apply();

                    Toast.makeText(getActivity(), "Welcome " + name, Toast.LENGTH_SHORT).show();

                    // Navigate based on type
                    if ("Seller".equals(type)) {
                        startActivity(new Intent(getActivity(), SellerHomeActivity.class));
                    } else {
                        startActivity(new Intent(getActivity(), MainActivity.class));
                    }
                    getActivity().finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pd.dismiss();
                Toast.makeText(getActivity(), "Database error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
