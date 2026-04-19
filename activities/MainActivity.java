package com.example.a2.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.badge.BadgeDrawable;

import com.example.a2.R;
import com.example.a2.adapters.MainPagerAdapter;
import com.example.a2.fragments.HomeFragment;
import com.example.a2.CartBadgeListener;

public class MainActivity extends AppCompatActivity implements HomeFragment.BadgeListener, CartBadgeListener {

    ViewPager2 mainViewPager;
    TabLayout bottomTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Sets the content view and initializes the ViewPager and TabLayout.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitymain);

        mainViewPager = findViewById(R.id.mainViewPager);
        bottomTabLayout = findViewById(R.id.bottomTabLayout);

        MainPagerAdapter pagerAdapter = new MainPagerAdapter(this);
        mainViewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(bottomTabLayout, mainViewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Home");
                    tab.setIcon(R.drawable.home);
                    break;
                case 1:
                    tab.setText("Search");
                    tab.setIcon(R.drawable.search);
                    break;
                case 2:
                    tab.setText("Favs");
                    tab.setIcon(R.drawable.heart);
                    break;
                case 3:
                    tab.setText("Cart");
                    tab.setIcon(R.drawable.cart);
                    break;
                case 4:
                    tab.setText("Profile");
                    tab.setIcon(R.drawable.person);
                    break;
            }
        }).attach();

        bottomTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 2 || tab.getPosition() == 3) {
                    tab.removeBadge();
                }
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    @Override
    public void onBadgeChange(boolean isAdded) {
        // Updates the favorite badge based on whether an item was added or removed.
        if (isAdded) {
            incrementFavBadge();
        } else {
            decrementFavBadge();
        }
    }

    @Override
    public void onCartBadgeChange() {
        // Triggers the cart badge increment from HomeFragment.
        incrementCartBadge();
    }

    @Override
    public void onCartBadgeUpdated() {
        // Triggers the cart badge increment from FavouritesFragment.
        incrementCartBadge();
    }

    public void incrementFavBadge() {
        // Increments the badge number on the favorites tab.
        if (bottomTabLayout.getSelectedTabPosition() == 2) return;
        TabLayout.Tab favTab = bottomTabLayout.getTabAt(2);
        if (favTab != null) {
            BadgeDrawable badge = favTab.getOrCreateBadge();
            badge.setVisible(true);
            badge.setNumber(badge.getNumber() + 1);
        }
    }

    public void decrementFavBadge() {
        // Decrements the badge number on the favorites tab.
        TabLayout.Tab favTab = bottomTabLayout.getTabAt(2);
        if (favTab != null && favTab.getBadge() != null) {
            BadgeDrawable badge = favTab.getBadge();
            int current = badge.getNumber();
            if (current > 1) {
                badge.setNumber(current - 1);
            } else {
                favTab.removeBadge();
            }
        }
    }

    public void incrementCartBadge() {
        // Increments the badge number on the cart tab.
        if (bottomTabLayout.getSelectedTabPosition() == 3) return;
        TabLayout.Tab cartTab = bottomTabLayout.getTabAt(3);
        if (cartTab != null) {
            BadgeDrawable badge = cartTab.getOrCreateBadge();
            badge.setVisible(true);
            badge.setNumber(badge.getNumber() + 1);
        }
    }
}
