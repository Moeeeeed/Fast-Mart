package com.example.a2.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.a2.fragments.HomeFragment;
import com.example.a2.fragments.SearchFragment;
import com.example.a2.fragments.FavouritesFragment;
import com.example.a2.fragments.CartFragment;
import com.example.a2.fragments.AccountFragment;

public class MainPagerAdapter extends FragmentStateAdapter {

    public MainPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return the correct fragment for each tab
        if (position == 0)
            return new HomeFragment();
        if (position == 1)
            return new SearchFragment();
        if (position == 2)
            return new FavouritesFragment();
        if (position == 3)
            return new CartFragment();
        if (position == 4)
            return new AccountFragment();

        return new HomeFragment(); // Default fallback
    }

    @Override
    public int getItemCount() {
        return 5;
    }}