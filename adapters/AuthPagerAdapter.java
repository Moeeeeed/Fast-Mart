package com.example.a2.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

// Make sure to import your fragments!
import com.example.a2.fragments.LoginFragment;
import com.example.a2.fragments.SignUpFragment;

public class AuthPagerAdapter extends FragmentStateAdapter {

    public AuthPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Position 0 is the first tab, Position 1 is the second tab
        if (position == 0) {
            return new LoginFragment();
        } else {
            return new SignUpFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2; // We only have 2 tabs: Login and Signup
    }
}